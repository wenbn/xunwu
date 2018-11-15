package www.ucforward.com.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;
import net.sf.json.JSONObject;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.utils.StringUtil;
import www.ucforward.com.constants.AppRquestParamsConstant;
import www.ucforward.com.constants.IndexConstant;
import www.ucforward.com.constants.MessageConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.entity.*;
import www.ucforward.com.index.entity.GoogleMapLocation;
import www.ucforward.com.index.entity.HouseBucketDTO;
import www.ucforward.com.index.entity.HouseSearchCondition;
import www.ucforward.com.index.entity.HouseSuggest;
import www.ucforward.com.index.form.MapSearch;
import www.ucforward.com.index.key.HouseIndexKey;
import www.ucforward.com.index.message.HouseIndexMessage;
import www.ucforward.com.index.template.HouseIndexTemplate;
import www.ucforward.com.serviceInter.HousesService;
import www.ucforward.com.serviceInter.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import www.ucforward.com.utils.ElasticsearchUtil;
import www.ucforward.com.utils.GoogleMapUtil;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.utils.ModelMapperUtil;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/6/25
 */
@Service("searchService")
public class SearchServiceImpl implements SearchService {

    private static Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class); // 日志记录

    @Resource
    private HousesService housesService;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 监听消息
     * @param content
     */
    @KafkaListener(topics = MessageConstant.BUILD_HOUSE_INDEX_TOPIC_MESSAGE)
    private void handleMessage(String content) {
        HouseIndexMessage message = JsonUtil.jsonToObjectT(content,HouseIndexMessage.class);
        switch (message.getOperation()) {
            case HouseIndexMessage.INDEX:
                  this.createOrUpdateIndex(message);
                break;
            case HouseIndexMessage.REMOVE:
                this.removeIndex(message);
                break;
            default:
                logger.warn("Not support message content " + content);
                break;
        }
    }

    /**
     * 创建索引
     * @param message
     * @throws Exception
     */
    private void createOrUpdateIndex(HouseIndexMessage message){
        int houseId = message.getHouseId();
        HsMainHouse house = null;
        try {
            ResultVo houseVo = housesService.select(houseId, new HsMainHouse());
            if(houseVo.getResult()== ResultConstant.SYS_REQUIRED_SUCCESS ){
                house = (HsMainHouse) houseVo.getDataSet();
            }
            if (house == null) {
                logger.error("Index house {} dose not exist!", houseId);
                this.index(houseId, message.getRetry() + 1);
                return;
            }
            //创建索引模板
            HouseIndexTemplate houseIndexTemplate = new HouseIndexTemplate();
            ModelMapperUtil.getInstance().map(house,houseIndexTemplate);
            //判断是否有坐标信息
            GoogleMapLocation location = null;
            if(StringUtil.hasText(house.getLatitude())&& StringUtil.hasText(house.getLongitude())){
                location = new GoogleMapLocation();
                location.setLat(StringUtil.getDouble(house.getLatitude()));
                location.setLon(StringUtil.getDouble(house.getLongitude()));
                houseIndexTemplate.setLocation(location);
            }else{
                //查询google坐标信息
                String address =  house.getCity() + house.getCommunity() + house.getSubCommunity()+house.getAddress();
                try{
                    Map<String, Object> mapLocation = GoogleMapUtil.getMapLocation(address);
                    int state = StringUtil.getAsInt(StringUtil.trim(mapLocation.get("state")));
                    if(state == 0){
                        GoogleMapLocation obj = (GoogleMapLocation) mapLocation.get("obj");
                        //修改房源坐标信息
                        HsMainHouse updateHouse = new HsMainHouse();
                        updateHouse.setId(houseId);
                        updateHouse.setVersionNo(house.getVersionNo());
                        updateHouse.setLatitude(StringUtil.trim(obj.getLat()));
                        updateHouse.setLongitude(StringUtil.trim(obj.getLon()));
                        housesService.update(updateHouse);

                        houseIndexTemplate.setLocation(obj);
                    }else{
                        this.index(message.getHouseId(), message.getRetry() + 1);
                        return;
                    }
                } catch (Exception e) {
                    logger.error("Failed to connect to Google!", e);
                }
            }
            boolean success = true;
            //查询索引，是否存在
            SearchRequestBuilder requestBuilder = ElasticsearchUtil.getConnection().prepareSearch(IndexConstant.INDEX_NAME).setTypes(IndexConstant.INDEX_TYPE).setQuery(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId));
            SearchResponse searchResponse = requestBuilder.get();
            long totalHit = searchResponse.getHits().getTotalHits();
            if (totalHit == 0) {
                success = create(houseIndexTemplate);
            } else if (totalHit == 1) {
                String esId = searchResponse.getHits().getAt(0).getId();
                success = update(esId, houseIndexTemplate);
            } else {
                success = deleteAndCreate(totalHit, houseIndexTemplate);
            }
            if (!success) {
                this.index(message.getHouseId(), message.getRetry() + 1);
            } else {
                logger.debug("Index success with house " + houseId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 先删除再新增索引
     * @param totalHit
     * @param houseIndexTemplate
     * @return
     */
    private boolean deleteAndCreate(long totalHit, HouseIndexTemplate houseIndexTemplate) {
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(ElasticsearchUtil.getConnection())
                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseIndexTemplate.getId()))
                .source(IndexConstant.INDEX_NAME);
        logger.debug("Delete by query for house: " + builder);
        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        if (deleted != totalHit) {
            logger.warn("Need delete {}, but {} was deleted!", totalHit, deleted);
            return false;
        } else {
            return create(houseIndexTemplate);
        }
    }

    /**
     * 创建索引
     * @param houseIndexTemplate
     * @return
     */
    private boolean create(HouseIndexTemplate houseIndexTemplate) {
        if (!updateSuggest(houseIndexTemplate)) {
            return false;
        }
        IndexResponse response = ElasticsearchUtil.getConnection().prepareIndex(IndexConstant.INDEX_NAME, IndexConstant.INDEX_TYPE).setSource(JsonUtil.toJson(houseIndexTemplate), XContentType.JSON).get();
        logger.debug("Create index with house: " + houseIndexTemplate.getId());
        if (response.status() == RestStatus.CREATED) {
            return true;
        }
        return false;
    }

    /**
     * 修改索引
     * @param esId
     * @param houseIndexTemplate
     * @return
     */
    private boolean update(String esId, HouseIndexTemplate houseIndexTemplate) {
        if (!updateSuggest(houseIndexTemplate)) {
            return false;
        }
        UpdateResponse response = ElasticsearchUtil.getConnection().prepareUpdate(IndexConstant.INDEX_NAME, IndexConstant.INDEX_TYPE, esId).setDoc(JsonUtil.toJson(houseIndexTemplate), XContentType.JSON).get();
        logger.debug("Update index with house: " + houseIndexTemplate.getId());
        if (response.status() == RestStatus.OK) {
            return true;
        }
        logger.debug("Update failure index with house: " + houseIndexTemplate.getId());
        return false;
    }


    /**
     * 填充关键字（在create or update时更新）
     * @param indexTemplate
     * @return
     */
    private boolean updateSuggest(HouseIndexTemplate indexTemplate) {
        //将需要分词的数据进行查询，得到权重
        AnalyzeRequestBuilder requestBuilder = new AnalyzeRequestBuilder(
                ElasticsearchUtil.getConnection(), AnalyzeAction.INSTANCE, IndexConstant.INDEX_NAME,
                indexTemplate.getHouseName(),
                indexTemplate.getBuildingName(),
                indexTemplate.getCity(),
                indexTemplate.getCommunity(),
                indexTemplate.getSubCommunity(),
                //indexTemplate.getProperty(),
                indexTemplate.getAddress()
//                indexTemplate.getHouseSituation()
        );

        //设置分词规则
        requestBuilder.setAnalyzer("ik_smart");
        List<AnalyzeResponse.AnalyzeToken> tokens = requestBuilder.execute().actionGet().getTokens();
        if (tokens == null) {
            logger.warn("Can not analyze token for house: " + indexTemplate.getId());
            return false;
        }

        List<HouseSuggest> suggests = new ArrayList<>();
        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            // 排除数字类型 & 小于2个字符的分词结果
            if ("<NUM>".equals(token.getType()) || token.getTerm().length() < 2) {
                continue;
            }
            //填充词汇
            HouseSuggest suggest = new HouseSuggest();
            suggest.setInput(token.getTerm());
            suggests.add(suggest);
        }
        //定制化小区自动补全
//        HouseSuggest suggest = new HouseSuggest();
//        suggest.setInput(indexTemplate.getVillageName());
//        suggests.add(suggest);
        indexTemplate.setSuggest(suggests);
        return true;
    }

    /**
     * 索引目标房源
     * @param houseId
     */
    @Override
    public ResultVo index(int houseId) {
        return this.index(houseId, 0);
    }

    /**
     * 索引目标房源
     * @param houseId
     * @param retry 重试次数
     */
    private ResultVo index(int houseId, int retry) {
        ResultVo vo = null;
        if (retry > HouseIndexMessage.MAX_RETRY) {//
            logger.error("Retry index times over 3 for house: " + houseId + " Please check it!");
            return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"Retry index times over 3 for house");
        }
        //发送创建索引消息
        HouseIndexMessage message = new HouseIndexMessage(houseId, HouseIndexMessage.INDEX, retry);
        kafkaTemplate.send(MessageConstant.BUILD_HOUSE_INDEX_TOPIC_MESSAGE, JsonUtil.toJson(message));
        return ResultVo.success();
    }

    /**
     * 移除索引
     * @param houseId
     */
    @Override
    public ResultVo remove(int houseId) {
        return this.remove(houseId, 0);
    }

    /**
     * 移除索引
     * @param houseId 房源ID
     * @param retry 尝试次数
     */
    private ResultVo remove(int houseId, int retry) {
        if (retry > HouseIndexMessage.MAX_RETRY) {
            logger.error("Retry remove times over 3 for house: " + houseId + " Please check it!");
            return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        HouseIndexMessage message = new HouseIndexMessage(houseId, HouseIndexMessage.REMOVE, retry);
        this.kafkaTemplate.send(MessageConstant.BUILD_HOUSE_INDEX_TOPIC_MESSAGE, JsonUtil.toJson(message));
        return ResultVo.success();
    }

    /**
     * 移除索引
     * @param message
     */
    private void removeIndex(HouseIndexMessage message) {
        int houseId = message.getHouseId();
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(ElasticsearchUtil.getConnection())
                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId))
                .source(IndexConstant.INDEX_NAME);
        logger.debug("Delete by query for house: " + builder);
        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        logger.debug("Delete total " + deleted);
        if (deleted <= 0) {
            logger.warn("Did not remove data from es for response: " + response);
            // 重新加入消息队列
            this.remove(houseId, message.getRetry() + 1);
        }
    }

    /**
     * 查询索引
     * @param condition
     */
    public ResultVo query(HouseSearchCondition condition) {
        ResultVo vo = new ResultVo();
        try {
            //创建查询条件
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            //过滤城市
            boolQuery.filter(
                    QueryBuilders.termQuery(HouseIndexKey.CITY, condition.getCity())
            );

            //过滤出租出售
            if (StringUtil.getAsInt(StringUtil.trim(condition.getLeaseType()))!= -1) {
                boolQuery.filter(
                        QueryBuilders.termQuery(HouseIndexKey.LEASE_TYPE, condition.getLeaseType())
                );
            }

            if (StringUtil.hasText(StringUtil.trim(condition.getCommunity()))) {
                boolQuery.filter(
                        QueryBuilders.termQuery(HouseIndexKey.COMMUNITY, condition.getCommunity())
                );//添加社区
            }

            String houseConfigDictcode = condition.getHouseConfigDictcode();
            if (StringUtil.hasText(StringUtil.trim(houseConfigDictcode))) {
                String[] houseConfigDictcodes = houseConfigDictcode.split(",");
                boolQuery.filter(
                        QueryBuilders.termsQuery(HouseIndexKey.HOUSE_CONFIG_DICTCODE, houseConfigDictcodes)
                );//房源配置
            }


            String housingTypeDictcode = condition.getHousingTypeDictcode();
            if (StringUtil.hasText(StringUtil.trim(housingTypeDictcode))) {
                String[] housingTypeDictcodes = housingTypeDictcode.split(",");
                boolQuery.filter(
                        QueryBuilders.termsQuery(HouseIndexKey.HOUSING_TYPE_DICTCODE,housingTypeDictcodes)
                );//房屋类型
            }

            //支付节点
            String payNode = condition.getPayNode();
            if (StringUtil.hasText(payNode)){
                String[] payNodes = payNode.split(",");
                boolQuery.filter(
                        QueryBuilders.termsQuery(HouseIndexKey.PAY_NODE,payNodes)
                );//支付节点
            }

            //查询价格
            if ( StringUtil.getAsInt(StringUtil.trim(condition.getMinPrice()))!=-1  && StringUtil.getAsInt(StringUtil.trim(condition.getMaxPrice()))!=-1 ) {
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(HouseIndexKey.HOUSE_RENT);
                rangeQueryBuilder.lte(condition.getMaxPrice());//最小价格
                rangeQueryBuilder.gte(condition.getMinPrice());//最大价格
                boolQuery.filter(rangeQueryBuilder);
            }

            //面积价格
            if ( StringUtil.getAsInt(StringUtil.trim(condition.getMinArea()))!=-1 && StringUtil.getAsInt(StringUtil.trim(condition.getMaxArea())) !=-1){
//            if ( StringUtil.hasText(StringUtil.trim(condition.getMinArea())) && StringUtil.hasText(StringUtil.trim(condition.getMinArea()))){
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(HouseIndexKey.HOUSE_ACREAGE);
                rangeQueryBuilder.lte(condition.getMaxArea());//最小面积
                rangeQueryBuilder.gte(condition.getMinArea());//最大面积
                boolQuery.filter(rangeQueryBuilder);
            }

            //浴室数量
            if ( StringUtil.getAsInt(StringUtil.trim(condition.getMinBathroom()))!=-1 && StringUtil.getAsInt(StringUtil.trim(condition.getMaxBathroom())) !=-1){
//            if ( StringUtil.hasText(StringUtil.trim(condition.getMinArea())) && StringUtil.hasText(StringUtil.trim(condition.getMinArea()))){
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(HouseIndexKey.BATHROOM_NUM);
                rangeQueryBuilder.lte(condition.getMaxBathroom());//最小浴室数量
                rangeQueryBuilder.gte(condition.getMinBathroom());//最大浴室数量
                boolQuery.filter(rangeQueryBuilder);
            }

            //卧室数量
            if ( StringUtil.getAsInt(StringUtil.trim(condition.getMinBedroom()))!=-1 && StringUtil.getAsInt(StringUtil.trim(condition.getMaxBedroom())) !=-1){
//            if ( StringUtil.hasText(StringUtil.trim(condition.getMinArea())) && StringUtil.hasText(StringUtil.trim(condition.getMinArea()))){
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(HouseIndexKey.BEDROOM_NUM);
                rangeQueryBuilder.lte(condition.getMaxBedroom());//最小卧室数量
                rangeQueryBuilder.gte(condition.getMinBedroom());//最大卧室数量
                boolQuery.filter(rangeQueryBuilder);
            }

            if (StringUtil.hasText(StringUtil.trim(condition.getKeywords()))) {
                boolQuery.must(
                        QueryBuilders.multiMatchQuery(condition.getKeywords(),
                                HouseIndexKey.HOUSE_NAME,
                                HouseIndexKey.COMMUNITY,
                                HouseIndexKey.SUBCOMMUNITY,
                                HouseIndexKey.ADDRESS,
                                HouseIndexKey.BUILDING_NAME
                        )
                );
            }

            SearchRequestBuilder requestBuilder = ElasticsearchUtil.getConnection().prepareSearch(IndexConstant.INDEX_NAME)
                    .setTypes(IndexConstant.INDEX_TYPE)
                    .setQuery(boolQuery)
                    .addSort(
                            condition.getOrderBy(),
                            SortOrder.fromString(condition.getOrderDirection())
                    )
                    .setFrom((condition.getPageIndex()-1)*condition.getPageSize())
                    .setSize(condition.getPageSize())
                    .setFetchSource(HouseIndexKey.HOUSE_ID, null);

            logger.debug(requestBuilder.toString());
            System.out.println(requestBuilder.toString());

            List<Long> houseIds = new ArrayList<>();
            SearchResponse response = requestBuilder.get();
            if (response.status() != RestStatus.OK) {
                logger.warn("Search status is no ok for " + requestBuilder);
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"Search status is no ok for "+ requestBuilder);
            }
            SearchHits hits = response.getHits();
            for (SearchHit hit : hits) {
                System.out.println(hit.getSourceAsMap());
                houseIds.add(Longs.tryParse(String.valueOf(hit.getSourceAsMap().get(HouseIndexKey.HOUSE_ID))));
            }
            PageInfo pageInfo = new PageInfo();
            pageInfo.setTotal(hits.totalHits);
            pageInfo.setPageNum(condition.getPageIndex());
            if(hits.totalHits>0){
                pageInfo.setHasNextPage(hits.totalHits>condition.getPageIndex()*condition.getPageSize());
            }else{
                pageInfo.setHasNextPage(false);
            }
            pageInfo.setPageSize(condition.getPageSize());
            pageInfo.setSize(houseIds.size());
            vo.setPageInfo(pageInfo);
            vo.setDataSet(houseIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;
    }


    /**
     * 获取补全建议关键词
     */
    @Override
    public ResultVo suggest(String prefix) {
        ResultVo vo = null;
        CompletionSuggestionBuilder suggestion = SuggestBuilders.completionSuggestion("suggest")
                .prefix(prefix)
                .size(5);//只提示5个
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("autocomplete", suggestion);
        //查询
        SearchRequestBuilder requestBuilder = ElasticsearchUtil.getConnection().prepareSearch(IndexConstant.INDEX_NAME)
                .setTypes(IndexConstant.INDEX_TYPE)
                .suggest(suggestBuilder);
        logger.debug(requestBuilder.toString());

        SearchResponse response = requestBuilder.get();
        Suggest suggest = response.getSuggest();
        if (suggest == null) {
            return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        Suggest.Suggestion result = suggest.getSuggestion("autocomplete");

        int maxSuggest = 0;//控制只推荐5个
        Set<String> suggestSet = new HashSet<>();//去重
        for (Object term : result.getEntries()) {
            if (term instanceof CompletionSuggestion.Entry) {
                CompletionSuggestion.Entry item = (CompletionSuggestion.Entry) term;
                if (item.getOptions().isEmpty()) {
                    continue;
                }
                for (CompletionSuggestion.Entry.Option option : item.getOptions()) {
                    String tip = option.getText().string();
                    if (suggestSet.contains(tip)) {//集合中是否存在
                        continue;
                    }
                    suggestSet.add(tip);
                    maxSuggest++;
                }
            }
            if (maxSuggest > 5) {
                break;
            }
        }
        List<String> suggests = Lists.newArrayList(suggestSet.toArray(new String[]{}));
        vo = ResultVo.success();
        if(suggests!=null){
            vo.setDataSet(suggests);
        }
        return vo;
    }

    /**
     * 聚合城市数据
     * @param cityNameCn
     * @return
     */
    @Override
    public ResultVo aggregateCity(String cityNameCn) throws Exception{
        //返回结果
        ResultVo resultVo =null;
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.CITY, cityNameCn));

        //定义聚合的列
        AggregationBuilder aggBuilder = AggregationBuilders.terms(HouseIndexKey.AGG_COMMUNITY)
                .field(HouseIndexKey.COMMUNITY);
        SearchRequestBuilder requestBuilder = ElasticsearchUtil.getConnection().prepareSearch(IndexConstant.INDEX_NAME)
                .setTypes(IndexConstant.INDEX_TYPE)
                .setQuery(boolQuery)
                .addAggregation(aggBuilder);
        logger.debug(requestBuilder.toString());

        SearchResponse response = requestBuilder.get();
        List<HouseBucketDTO> buckets = new ArrayList<>();
        if (response.status() != RestStatus.OK) {
            logger.warn("Aggregate status is not ok for " + requestBuilder);
            return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        Terms terms = response.getAggregations().get(HouseIndexKey.AGG_COMMUNITY);
        for (Terms.Bucket bucket : terms.getBuckets()) {
            buckets.add(new HouseBucketDTO(bucket.getKeyAsString(), bucket.getDocCount()));
        }
        resultVo = ResultVo.success();
        resultVo.setDataSet(buckets);

        return resultVo;
    }

    /**
     * 聚合子社区下的房源数据
     * @param cityName
     * @param communityName
     * @param subCommunityName
     * @return
     * @throws Exception
     */
    @Override
    public ResultVo aggregateDistrictHouse(String cityName, String communityName, String subCommunityName) throws Exception {
        //返回结果
        ResultVo resultVo =null;
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.CITY, cityName));
        boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.COMMUNITY, communityName));
        boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.SUBCOMMUNITY, subCommunityName));

        //定义聚合的列
        SearchRequestBuilder requestBuilder = ElasticsearchUtil.getConnection().prepareSearch(IndexConstant.INDEX_NAME)
                .setTypes(IndexConstant.INDEX_TYPE)
                .setQuery(boolQuery)
                .addAggregation(
                        AggregationBuilders.terms(HouseIndexKey.AGG_SUBCOMMUNITY)
                                .field(HouseIndexKey.SUBCOMMUNITY)
                ).setSize(0);

        SearchResponse response = requestBuilder.get();
        if (response.status() == RestStatus.OK) {
            Terms terms = response.getAggregations().get(HouseIndexKey.AGG_SUBCOMMUNITY);
            if (terms.getBuckets() != null && !terms.getBuckets().isEmpty()) {
                resultVo = ResultVo.success();
                resultVo.setDataSet(terms.getBucketByKey(subCommunityName).getDocCount());
                return resultVo;
            }
        } else {
            logger.warn("Failed to Aggregate for " + HouseIndexKey.AGG_SUBCOMMUNITY);
            ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"Failed to Aggregate for "+HouseIndexKey.AGG_SUBCOMMUNITY);
        }
        return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"Failed to Aggregate for "+HouseIndexKey.AGG_SUBCOMMUNITY);
    }

    /**
     * 城市级别查询
     * @return
     */
    @Override
    public ResultVo mapQuery(String cityEnName, String orderBy, String orderDirection, int pageIndex, int pageSize) {
        ResultVo resultVo = new ResultVo();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.CITY, cityEnName));
        SearchRequestBuilder searchRequestBuilder = ElasticsearchUtil.getConnection().prepareSearch(IndexConstant.INDEX_NAME)
                .setTypes(IndexConstant.INDEX_TYPE)
                .setQuery(boolQuery)
                .addSort(
                        orderBy,
                        SortOrder.fromString(orderDirection)
                )
                .setFrom(pageIndex*pageSize)
                .setSize(pageSize);
        List<Long> houseIds = new ArrayList<>();
        SearchResponse response = searchRequestBuilder.get();
        if (response.status() != RestStatus.OK) {
            logger.warn("Search status is not ok for " + searchRequestBuilder);
            return resultVo;
        }
        for (SearchHit hit : response.getHits()) {
            houseIds.add(Longs.tryParse(String.valueOf(hit.getSourceAsMap().get(HouseIndexKey.HOUSE_ID))));
        }
        resultVo.setDataSet(houseIds);
        return resultVo;

    }

    /**
     * 精确范围数据查询
     * @param mapSearch
     * @return
     */
    @Override
    public ResultVo mapQuery(MapSearch mapSearch) {
        ResultVo resultVo = new ResultVo();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.CITY, mapSearch.getCityEnName()));

        boolQuery.filter(
                QueryBuilders.geoBoundingBoxQuery("location")
                        .setCorners(
                                new GeoPoint(mapSearch.getLeftLatitude(), mapSearch.getLeftLongitude()),
                                new GeoPoint(mapSearch.getRightLatitude(), mapSearch.getRightLongitude())
        ));

        SearchRequestBuilder builder = ElasticsearchUtil.getConnection().prepareSearch(IndexConstant.INDEX_NAME)
                .setTypes(IndexConstant.INDEX_TYPE)
                .setQuery(boolQuery)
                .addSort(
                        mapSearch.getOrderBy(),
                        SortOrder.fromString(mapSearch.getOrderDirection())
                )
                .setFrom(mapSearch.getPageIndex()*mapSearch.getPageSize())
                .setSize(mapSearch.getPageSize());

        List<Long> houseIds = new ArrayList<>();
        SearchResponse response = builder.get();
        if (RestStatus.OK != response.status()) {
            logger.warn("Search status is not ok for " + builder);
            return resultVo;
        }

        for (SearchHit hit : response.getHits()) {
            houseIds.add(Longs.tryParse(String.valueOf(hit.getSourceAsMap().get(HouseIndexKey.HOUSE_ID))));
        }
        resultVo.setDataSet(houseIds);
        return resultVo;
    }


}
