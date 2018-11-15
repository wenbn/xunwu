package www.ucforward.com.utils;

import com.google.common.collect.Maps;
import net.sf.json.JSONObject;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.utils.StringUtil;
import www.ucforward.com.constants.IndexConstant;
import www.ucforward.com.index.template.HouseIndexTemplate;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author wenbn
 * @date 2018/4/17
 */
public class ElasticsearchUtil {

    private static Properties properties = new Properties();
    static {
        try{
            properties.load(ElasticsearchUtil.class.getResourceAsStream("/es/es.properties"));
        }catch (Exception e){
            e.getMessage();
        }
    }

    public final static String HOST = "192.168.1.196";
    public final static int PORT = 9300;//http请求的端口是9200，客户端是9300
    public final static String ANALYZE = "smartcn";

    private static TransportClient client = null;

    public ElasticsearchUtil(){
        try{
            Settings settings = Settings.builder()
                    .put("client.transport.sniff", true)
                    .put("cluster.name", "elasticsearch").build();//集群名称
//            client = new PreBuiltTransportClient(settings)集群配置
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(properties.getProperty("es.host")), StringUtil.getAsInt(properties.getProperty("es.port"))));

        }catch (Exception ex){
            client.close();
        }finally {

        }
    }

    //创建
    public static TransportClient getConnection(){
        if (client==null){
            synchronized (ElasticsearchUtil.class){
                if (client==null){
                    new ElasticsearchUtil();
                }
            }
        }
        return  client;
    }

    public static void closeClient(){
        if (client != null){
            client.close();
        }
    }

    /**
     * 添加索引数据
     * @param condition
     *      index
     *      type
     *      data 格式为 Map<String,Object>
     * @return
     * @throws Exception
     */
    public static int createIndex4Map(Map<Object,Object> condition) throws Exception {
        int state = -1;
        try{
            if(null == client){
                getConnection();
            }
            if(null==condition){
                return state;
            }
            String index = StringUtil.trim(condition.get("index"));
            String type = StringUtil.trim(condition.get("type"));
            Map<String,Object> data = (Map<String, Object>) condition.get("data");
            if(data!=null && data.size()>0){
                ActionFuture<IndexResponse> response = client.index(Requests.indexRequest(index).type(type).source(data));
                RestStatus status = response.get().status();
                if ("OK".equals(status.name())) {
                    state = 0;
                    System.out.println("更新文档成功！");
                } else {
                    System.out.println("更新文档失败！");
                }
            }
            return state;
        }catch (Exception e){
            return state;
        }finally {
           // closeClient();
        }
    }

    /**
     * 批量添加索引数据
     *  此方法是通过map建设索引
     * @param condition
     *      index
     *      type
     *      data 格式为 List<Map<String,Object>>
     * @return
     * @throws Exception
     */
    public static int batchCreateIndex4Map(Map<Object,Object> condition) throws Exception {
        int state = -1;
        try{
            if(null == client){
                getConnection();
            }
            if(null==condition){
                return state;
            }
            String index = StringUtil.trim(condition.get("index"));
            String type = StringUtil.trim(condition.get("type"));
            List<Map<String,Object>> datas = (List<Map<String, Object>>) condition.get("data");
            if(datas!=null && datas.size()>0){
                BulkRequest request = Requests.bulkRequest();
                for (Map<String, Object> data : datas) {
                    request.add(Requests.indexRequest(index).type(type).source(data));
                }
                ActionFuture<BulkResponse> response = client.bulk(request);
                RestStatus status = response.get().status();
                if ("OK".equals(status.name())) {
                    state = 0;
                    System.out.println("更新文档成功！");
                } else {
                    System.out.println("更新文档失败！");
                }
            }
            return state;
        }catch (Exception e){
            return state;
        }finally {
            //closeClient();
        }
    }

    /**
     * 批量添加索引数据
     *  此方法是通过json建设索引
     * @param condition
     *      index
     *      type
     *      data 格式为 Map<Object,Object>
     * @return
     * @throws Exception
     */
    public static int createIndex4Json(Map<Object,Object> condition) throws Exception {
        int state = -1;
        try {
            if(null == client){
                getConnection();
            }
            if (null == condition) {
                return state;
            }
            String index = StringUtil.trim(condition.get("index"));
            String type = StringUtil.trim(condition.get("type"));
            Map<Object, Object> data = (Map<Object, Object>) condition.get("data");
            if (data != null && data.size() > 0) {
                IndexResponse indexResponse = indexResponse = client.prepareIndex(index, type)
                            .setSource(JSONObject.fromObject(data), XContentType.JSON).get();
                RestStatus status = indexResponse.status();
                if ("CREATED".equals(status.name())) {
                    state = 0;
                    System.out.println("更新文档成功！");
                } else {
                    System.out.println("更新文档失败！");
                }
            }
            return state;
        }catch (Exception e){
            return state;
        }finally {
            //closeClient();
        }
    }

    /**
     * 批量添加索引数据
     *  此方法是通过json建设索引
     * @param condition
     *      index
     *      type
     *      data 格式为 Map<String,Object>
     * @return
     * @throws Exception
     */
    public static int batchCreateIndex4Json(Map<Object,Object> condition) throws Exception {
        int state = -1;
        try {
            if(null == client){
                getConnection();
            }
            if (null == condition) {
                return state;
            }
            String index = StringUtil.trim(condition.get("index"));
            String type = StringUtil.trim(condition.get("type"));
            List<Map<Object, Object>> list = (List<Map<Object, Object>>) condition.get("data");
            if (list != null && list.size() > 0) {
                IndexResponse indexResponse = null;
                for (int i = 0; i < list.size(); i++) {
                    indexResponse = client.prepareIndex(index, type)
                            .setSource(JSONObject.fromObject(list.get(i)), XContentType.JSON).get();
                }
                RestStatus status = indexResponse.status();
                if ("CREATED".equals(status.name())) {
                    state = 0;
                    System.out.println("更新文档成功！");
                } else {
                    System.out.println("更新文档失败！");
                }
            }
            return state;
        }catch (Exception e){
            return state;
        }finally {
            //closeClient();
        }
    }

    //查询商品
    public static List<Map<String,Object>> searchProduct(Map<Object,Object> condition){
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        try {
            if(null == client){
                getConnection();
            }
            String index = StringUtil.trim(condition.get("index"));
            String type = StringUtil.trim(condition.get("type"));
            String keywords = StringUtil.trim(condition.get("keywords"));
            int pageIndex = StringUtil.getAsInt(StringUtil.trim(condition.get("pageIndex")));
            int pageSize = StringUtil.getAsInt(StringUtil.trim(condition.get("pageSize")));
            HighlightBuilder highlightBuilder = new HighlightBuilder();//创建高亮
            highlightBuilder.preTags("<em>");//添加前缀
            highlightBuilder.postTags("</em>");//添加后缀
            highlightBuilder.field("productName");
            highlightBuilder.field("shortDesc");
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type);
            SearchResponse searchResponse = searchRequestBuilder.setQuery(QueryBuilders.multiMatchQuery(keywords, "productName", "shortDesc"))//多字段搜索
                    .highlighter(highlightBuilder)//设置高亮
                   // .addSort("productType", SortOrder.DESC)
                    .addSort("createTime", SortOrder.DESC)
                    .setFrom(pageIndex)//从第一条开始查询
                    .setSize(pageSize)//连续查询2条
                    .execute()
                    .actionGet();
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                Map<String, Object> data = hit.getSourceAsMap();
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                for (Map.Entry<String, HighlightField> entry : highlightFields.entrySet()) {
                    String key = entry.getKey();
                    HighlightField describeField = highlightFields.get(key);
                    if (describeField != null) {
                        Text[] fragments = describeField.fragments();
                        String describe = "";
                        for (Text text : fragments) {
                            describe += text;
                        }
                        data.put(key, describe);
                    }
                }
                result.add(data);
            }
            return result;
        }catch (Exception e){
            return result;
        }finally {
            //closeClient();
        }
    }

    /**
     * 判断索引是否存在
     * @return
     */
    public static boolean indexIsExists(String index_alias) {
        if(null == client){
            getConnection();
        }
        ActionFuture<IndicesExistsResponse> response = client.admin().indices().exists(Requests.indicesExistsRequest(index_alias));
        return response.actionGet().isExists();
    }

    /**
     * 删除索引
     * @param index_alias 索引名称
     * @return
     * @throws Exception
     */
    public static int deleteIndexByName(String index_alias) throws Exception {
        int state = -1;
        try {
            if(null == client){
                getConnection();
            }
            if(!StringUtil.hasText(StringUtil.trim(index_alias))){
                return state;
            }
            if(indexIsExists(index_alias)){
                ActionFuture<DeleteIndexResponse> response = client.admin().indices().delete(Requests.deleteIndexRequest(index_alias));
                state = 0;
                System.out.println("isContextEmpty: " + response.actionGet().isFragment());
            }
            return state;
        }catch (Exception e){
            return state;
        }finally {
            //closeClient();
        }
    }


    /**
     * 修改索引数据
     * @param condition
     *      index
     *      type
     *      data 格式为 Map
     * @return
     * @throws Exception
     */
    public static int updateIndex(Map<Object,Object> condition) throws Exception {
        int state = -1;
        try {
            if(null == client){
                getConnection();
            }
            if (null == condition) {
                return state;
            }
            String index = StringUtil.trim(condition.get("index"));
            String type = StringUtil.trim(condition.get("type"));
            String docId = StringUtil.trim(condition.get("docId"));
            Map<Object, Object> data = (Map<Object, Object>) condition.get("data");
            UpdateRequest updateRequest = new UpdateRequest(index, type, docId).doc(data);
            UpdateResponse response = client.update(updateRequest).get();
            if(200 == response.status().getStatus()){
                state = 0;
            }
            return state;
        }catch (Exception e){
            return state;
        }finally {
            //closeClient();
        }
    }


    /**
     * 批量修改索引数据
     * @param condition
     *      index
     *      type
     *      data 格式为 Map
     * @return
     * @throws Exception
     */
    public static int batchUpdateIndex(Map<Object,Object> condition) throws Exception {
        int state = -1;
        try {
            if(null == client){
                getConnection();
            }
            if (null == condition) {
                return state;
            }
            String index = StringUtil.trim(condition.get("index"));
            String type = StringUtil.trim(condition.get("type"));
            List<Map<Object, Object>> data = (List<Map<Object, Object>>) condition.get("data");
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            for (Map<Object, Object> _data : data) {
                String docId = StringUtil.trim(_data.get("docId"));
                _data.remove("docId");
                bulkRequest.add(client.prepareUpdate(index, type, docId)
                        .setDoc(_data)
                );
            }
            BulkResponse bulkResponse = bulkRequest.get();
            if(200 == bulkResponse.status().getStatus()){
                state = 0;
            }
            return state;
        }catch (Exception e){
            return state;
        }finally {
            //closeClient();
        }
    }

    public static void main(String[] args) throws Exception {
//        int wm = deleteIndexByName("wm");
//        System.out.println("删除索引："+wm);


        Map<Object,Object> condition = Maps.newHashMap();
        Map<Object,Object> data = Maps.newHashMap();
//        condition.put("index","wm");
//        condition.put("type","product");
//        condition.put("docId","90h63GIBhDCvoToP2lQW");
//        data.put("shortDesc","红豆杉123");
//        data.put("productName","红豆杉123");
//        condition.put("data",data);
//        updateIndex(condition);

        //测试批量修改
//        condition.clear();
//        List<Map<Object,Object>> _data = new ArrayList<Map<Object,Object>>();
//        condition.put("index","wm");
//        condition.put("type","product");
//
//        Map<Object,Object> data1 = Maps.newHashMap();
//        data1.put("docId","90h63GIBhDCvoToP2lQW");
//        data1.put("shortDesc","红豆杉");
//        data1.put("productName","红豆杉");
//        _data.add(data1);
//
//        Map<Object,Object> data2 = Maps.newHashMap();
//        data2.put("docId","70h63GIBhDCvoToP2lQW");
//        data2.put("shortDesc","华洋基酒");
//        data2.put("productName","华洋基酒");
//        _data.add(data2);
//
//        condition.put("data",_data);
//
//        int i = batchUpdateIndex(condition);
//        System.out.println("批量删除索引："+i);


        HouseIndexTemplate houseIndexTemplate = new HouseIndexTemplate();
        houseIndexTemplate.setId(49);
        houseIndexTemplate.setAddress("12312312");
        UpdateRequest updateRequest = new UpdateRequest(IndexConstant.INDEX_NAME, IndexConstant.INDEX_TYPE, "49").doc(JSONObject.fromObject(houseIndexTemplate), XContentType.JSON);
        UpdateResponse response = ElasticsearchUtil.getConnection().update(updateRequest).get();
        if(200 == response.status().getStatus()){
            System.out.println("修改成功");
        }else{
            System.out.println("修改失败");
        }

       // System.out.println(deleteIndexByName("wm"));

    }

}
