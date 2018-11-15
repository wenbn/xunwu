package www.ucforward.com.serviceInter;

import com.google.common.primitives.Longs;
import net.sf.json.JSONObject;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.utils.StringUtil;
import www.ucforward.com.constants.IndexConstant;
import www.ucforward.com.index.entity.HouseSearchCondition;
import www.ucforward.com.index.key.HouseIndexKey;
import www.ucforward.com.index.template.HouseIndexTemplate;
import www.ucforward.com.utils.ElasticsearchUtil;
import www.ucforward.com.utils.JsonUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/6/28
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/ApplicationContext.xml")
public class SearchServiceTests {


//    @Resource
//    private SearchService searchService;

    @Test
    public void testIndex(){
        //searchService.index(48);
    }


    @Test
    public void get(){
        SearchRequestBuilder requestBuilder = ElasticsearchUtil.getConnection().prepareSearch(IndexConstant.INDEX_NAME).setTypes(IndexConstant.INDEX_TYPE)
                .setQuery(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, 1));
        SearchHits hits = requestBuilder.get().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> data = hit.getSourceAsMap();
            System.out.println(data);
        }

//        if (response.status() == RestStatus.CREATED) {
//            System.out.println("新增成功");
//        }else{
//            System.out.println("新增失败");
//        }
    }

    @Test
    public void insert() throws JsonProcessingException {
        HouseIndexTemplate houseIndexTemplate = new HouseIndexTemplate();
        houseIndexTemplate.setId(49);
        houseIndexTemplate.setAddress("dfasdfasdfasdf");
        houseIndexTemplate.setCreateTime(new Date());
        houseIndexTemplate.setUpdateTime(new Date());
        JSONObject jsonObject = JSONObject.fromObject(houseIndexTemplate);
        System.out.println(JsonUtil.toJson(houseIndexTemplate));
        IndexResponse response = ElasticsearchUtil.getConnection().prepareIndex(IndexConstant.INDEX_NAME, IndexConstant.INDEX_TYPE).setSource(JsonUtil.toJson(houseIndexTemplate), XContentType.JSON).get();
        if (response.status() == RestStatus.CREATED) {
            System.out.println("新增成功");
        }else{
            System.out.println("新增失败");
        }
    }

    @Test
    public void update() throws ExecutionException, InterruptedException {
        HouseIndexTemplate houseIndexTemplate = new HouseIndexTemplate();
        houseIndexTemplate.setId(1);
        houseIndexTemplate.setAddress("12312312");
        UpdateRequest updateRequest = new UpdateRequest(IndexConstant.INDEX_NAME, IndexConstant.INDEX_TYPE, "1")
                .doc(JSONObject.fromObject(houseIndexTemplate), XContentType.JSON);
        UpdateResponse response = ElasticsearchUtil.getConnection().update(updateRequest).get();
        if(200 == response.status().getStatus()){
            System.out.println("修改成功");
        }else{
            System.out.println("修改失败");
        }
    }

    @Test
    public void deleteAndCreate(){
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(ElasticsearchUtil.getConnection())
                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, 49))
                .source(IndexConstant.INDEX_NAME);
        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        if (deleted>0) {
            System.out.println("删除成功");
        } else {
            System.out.println("删除失败");
        }
    }


    @Test
    public void testQuery(){
        HouseSearchCondition condition = new HouseSearchCondition();
        condition.setKeywords("大三房");
        //创建查询条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        boolQuery.filter(
                QueryBuilders.termQuery(HouseIndexKey.CITY, "深圳市")
        );

        if (StringUtil.hasText(StringUtil.trim(condition.getCommunity()))) {
            boolQuery.filter(
                    QueryBuilders.termQuery(HouseIndexKey.COMMUNITY, condition.getCommunity())
            );//添加社区
        }

        //查询价格
        if (StringUtil.hasText(StringUtil.trim(condition.getMinPrice())) && StringUtil.hasText(StringUtil.trim(condition.getMaxPrice()))) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(HouseIndexKey.HOUSE_ACREAGE);
            rangeQueryBuilder.lte(condition.getMinPrice());//最小价格
            rangeQueryBuilder.gte(condition.getMaxPrice());//最大价格
            boolQuery.filter(rangeQueryBuilder);
        }

        //面积价格
        if (StringUtil.hasText(StringUtil.trim(condition.getMinArea())) && StringUtil.hasText(StringUtil.trim(condition.getMinArea()))) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(HouseIndexKey.HOUSE_ACREAGE);
            rangeQueryBuilder.lte(condition.getMinArea());//最小面积
            rangeQueryBuilder.gte(condition.getMinArea());//最大面积
            boolQuery.filter(rangeQueryBuilder);
        }

        if (StringUtil.hasText(StringUtil.trim(condition.getKeywords()))){
            boolQuery.must(
                    QueryBuilders.multiMatchQuery(condition.getKeywords(),
                            HouseIndexKey.COMMUNITY,
                            HouseIndexKey.SUBCOMMUNITY,
                            HouseIndexKey.ADDRESS,
                            HouseIndexKey.HOUSE_NAME,
                            HouseIndexKey.BUILDING_NAME,
                            HouseIndexKey.VILLAGE_NAME
                    )
            );
        }

        SearchRequestBuilder requestBuilder = ElasticsearchUtil.getConnection().prepareSearch(IndexConstant.INDEX_NAME)
                .setTypes(IndexConstant.INDEX_TYPE)
                .setQuery(boolQuery)
//                .addSort(
//                        condition.getOrderBy(),
//                        SortOrder.fromString(condition.getOrderDirection())
//                )
                .setFrom(condition.getPageIndex())
                .setSize(condition.getPageSize())
                .setFetchSource(HouseIndexKey.HOUSE_ID, null);


        List<Long> houseIds = new ArrayList<>();
        SearchResponse response = requestBuilder.get();
        if (response.status() != RestStatus.OK) {

        }

        for (SearchHit hit : response.getHits()) {
            System.out.println(hit.getSourceAsMap());
            houseIds.add(Longs.tryParse(String.valueOf(hit.getSourceAsMap().get(HouseIndexKey.HOUSE_ID))));
        }
        System.out.println("查询到的房源数据："+houseIds.size());

    }

    /**
     * 删除索引
     * @return
     * @throws Exception
     */
    public void deleteIndexByName() throws Exception {
        int state = -1;
        try {
            String index_alias = "sandy_house";
            TransportClient client = ElasticsearchUtil.getConnection();
            if(!StringUtil.hasText(StringUtil.trim(index_alias))){
                System.out.println("删除失败");
            }
            if(ElasticsearchUtil.indexIsExists(index_alias)){
                ActionFuture<DeleteIndexResponse> response = client.admin().indices().delete(Requests.deleteIndexRequest(index_alias));
                state = 0;
                System.out.println("isContextEmpty: " + response.actionGet().isFragment());
            }
            System.out.println("删除成功");
        }catch (Exception e){
            System.out.println("删除失败");
        }finally {
            //closeClient();
        }
    }


}
