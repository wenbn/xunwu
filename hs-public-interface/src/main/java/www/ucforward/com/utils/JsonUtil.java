package www.ucforward.com.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;
import www.ucforward.com.entity.HsHouseAutoReplySetting;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

public class JsonUtil {

    private final static Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private static XMLSerializer xmlserializer = new XMLSerializer();

    public static Gson getInstance(){
        return gson;
    }

    /**
     * ResultVo类型的转换
     * @param object
     * @return
     */
    public static String toJson(Object object){
        if(null != object){
            return gson.toJson(object);
        }
        return "";
    }
    /**
     *
     * json转换list.
     * <br>详细说明
     * @param jsonStr json字符串
     * @return
     * @return List<Map<String,Object>> list
     * @throws
     * @author slj
     * @date 2013年12月24日 下午1:08:03
     */
    public static List<Map<Object, Object>> parseJSON2List(String jsonStr){
        JSONArray jsonArr = JSONArray.fromObject(jsonStr);
        List<Map<Object, Object>> list = new ArrayList<Map<Object,Object>>();
        Iterator<JSONObject> it = jsonArr.iterator();
        while(it.hasNext()){
            JSONObject json2 = it.next();
            list.add(parseJSON2Map(json2.toString()));
        }
        return list;
    }


    /**
     *
     * json转换map.
     * <br>详细说明
     * @param jsonStr json字符串
     * @return
     * @return Map<String,Object> 集合
     * @throws
     * @author slj
     */
    public static Map<Object, Object> parseJSON2Map(String jsonStr){
        ListOrderedMap map = new ListOrderedMap();
        //最外层解析
        JSONObject json = JSONObject.fromObject(jsonStr);
        for(Object k : json.keySet()){
            Object v = json.get(k);
            //如果内层还是数组的话，继续解析
            if(v instanceof JSONArray){
                List<Map<Object, Object>> list = new ArrayList<Map<Object,Object>>();
                Iterator<JSONObject> it = ((JSONArray)v).iterator();
                while(it.hasNext()){
                    JSONObject json2 = it.next();
                    list.add(parseJSON2Map(json2.toString()));
                }
                map.put(k.toString(), list);
            } else {
                map.put(k.toString(), v);
            }
        }
        return map;
    }

    /***
     * List 转为 JSON
     * @param list
     * @return
     */
    public static <T> String list2Json(List<T> list) {
        if(null != list && list.size() > 0){
            JSONArray jsonArray = JSONArray.fromObject(list);
            return jsonArray.toString();
        }
        return "";
    }


    /***
     * JSON 转换为 List
     * @param jsonStr
     *         [{"age":12,"createTime":null,"id":"","name":"wxw","registerTime":null,"sex":1},{...}]
     * @param objectClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> json2List(String jsonStr, Class<T> objectClass){
        try {
            if (StringUtils.isNotBlank(jsonStr)) {
                JSONArray jsonArray = JSONArray.fromObject(jsonStr);
                List<T> list = (List<T>) JSONArray.toCollection(jsonArray, objectClass);
                return list;
            }
        }catch (Exception e){
            return null;
        }
        return null;
    }


    /***
     * Object 转为  JSON
     * @param object
     * @return
     */
    public static String object2Json(Object object) {
        if(null != object){
            JSONArray jsonArray = JSONArray.fromObject(object);
            return jsonArray.toString();
        }
        return "";
    }

    /***
     *
     * JSON 转 Object
     *
     * @param jsonStr
     *         [{"age":12,"createTime":null,"id":"","name":"wxw","registerTime":null,"sex":1}]
     * @param objectClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T json2Ojbect(String jsonStr,  Class<T> objectClass){
        if(null != jsonStr){
            String leftStr = jsonStr.substring(0,2);
            String rightStr = jsonStr.substring(jsonStr.length()-2,jsonStr.length());
            if(leftStr.equals("[{")){
                jsonStr = jsonStr.substring(1,jsonStr.length());
            }
            if(rightStr.equals("}]")){
                jsonStr = jsonStr.substring(0,jsonStr.length()-1);
            }
            JSONObject jsonStu = JSONObject.fromObject(jsonStr);
            return (T) JSONObject.toBean(jsonStu,objectClass);
        }
        return null;
    }

    /***
     * JsonArray 转为 JSON
     * @param jsonArray
     * @return
     */
    public static String jsonArrayToJSONString(JSONArray jsonArray) {
        if(null != jsonArray){
            return jsonArray.toString();
        }
        return "";
    }

    /***
     * JsonObject 转为  JSON
     * @param jsonObject
     * @return
     */
    public static String jsonObjectToJSONString(JSONObject jsonObject) {
        if(null != jsonObject){
            return jsonObject.toString();
        }
        return "";
    }

    /***
     * 将Object转换为JsonObject
     * @param object
     * @return
     */
    public static JSONObject object2JsonObject(Object object) {
        if(null != object){
            return JSONObject.fromObject(object);
        }
        return null;
    }


    /***
     * XML 转为 JSON
     * @param xmlString
     *             XML字符串  例如：
     *                     <?xml version='1.0' encoding='utf-8'?><cities><province name='北京'><item>东城区</item><item>西城区</item><item>崇文区</item><item>宣武区</item><item>朝阳区</item><item>丰台区</item><item>石景山区</item><item>海淀区</item><item>门头沟区</item><item>房山区</item><item>通州区</item><item>顺义区</item><item>昌平区</item><item>大兴区</item><item>怀柔区</item><item>平谷区</item><item>密云县</item><item>延庆县</item></province></cities>
     * @return
     *
     */
    public static String xml2json(String xmlString){
        if(StringUtils.isNotBlank(xmlString)){
            try {
                return xmlserializer.read(xmlString).toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /***
     * JSON 转为      XML
     * @param jsonStr
     *             XML字符串  例如：
     *                     [{'province':{'@name':'北京','item':['东城区','西城区','崇文区','宣武区','朝阳区','丰台区','石景山区','海淀区','门头沟区','房山区','通州区','顺义区','昌平区','大兴区','怀柔区','平谷区','密云县','延庆县']}}]
     *                  或者：
     *                  {'province':{'@name':'北京','item':['东城区','西城区','崇文区','宣武区','朝阳区','丰台区','石景山区','海淀区','门头沟区','房山区','通州区','顺义区','昌平区','大兴区','怀柔区','平谷区','密云县','延庆县']}}
     * @return
     *
     */
    public static String json2xml(String jsonStr){
        if(StringUtils.isNotBlank(jsonStr)){
            try {
                if(jsonStr.contains("[{") && jsonStr.contains("}]")){
                    JSONArray jobj = JSONArray.fromObject(jsonStr);
                    return xmlserializer.write(jobj);
                }
                JSONObject jobj = JSONObject.fromObject(jsonStr);
                return xmlserializer.write(jobj);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }


    /***
     * XML/JSON 互转
     *
     * @param sourceFilePath
     *                 要解析的文件路径
     * @param directFilePath
     *              生成文件存放的路径
     * @param flag
     *             true:JSON 转为 XML
     *             false:XML转为 JSON
     * @return
     */
    public static String xml2JsonOrjson2Xml(String sourceFilePath,String directFilePath,boolean flag){
        if(StringUtils.isBlank(sourceFilePath) || StringUtils.isBlank(directFilePath)){
            return null;
        }
        FileInputStream in =null;
        BufferedReader br = null;
        FileWriter fw = null;
        String rs = null;
        try{
            File jsonFile = new File(sourceFilePath);
            in = new FileInputStream(jsonFile);
            StringBuffer sbuf = new StringBuffer();
            br = new BufferedReader(new InputStreamReader(in));
            String temp =null;

            while((temp=br.readLine())!=null){
                sbuf.append(temp);
            }
            if(flag){
                rs  = json2xml(sbuf.toString());
            }else{
                rs  = xml2json(sbuf.toString());
            }
            File test = new File(directFilePath);
            if(!test.exists()){
                test.createNewFile();
            }
            fw = new FileWriter(test);
            fw.write(rs);
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                fw.close();
                br.close();
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rs;
    }

    /**
     * 解析json对象
     *
     * @param jsonData
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T jsonToObjectT(String jsonData, Class<T> type) {
        T result = null;
        if (StringUtils.isNotBlank(jsonData) && type != null) {
            Gson gson = new Gson();
            try {
                result = gson.fromJson(jsonData, type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 输出JSON
     *
     * @param response
     * @author CYH
     * @create 2018年3月5日
     */
    public static void writeJson(Object obj, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            out = response.getWriter();
            out.write(toJson(obj));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }


    public static void main(String[] args) {

//        System.out.println(jfxfTranspose("E:/qwe.json", "E:/qwe.xml", 1));

//        System.out.println(json2xml("[{'province':{'@name':'北京','item':['东城区','西城区','崇文区','宣武区','朝阳区','丰台区','石景山区','海淀区','门头沟区','房山区','通州区','顺义区','昌平区','大兴区','怀柔区','平谷区','密云县','延庆县']}}]"));
//        System.out.println(xml2json("<?xml version='1.0' encoding='utf-8'?><cities><province name='北京'><item>东城区</item><item>西城区</item><item>崇文区</item><item>宣武区</item><item>朝阳区</item><item>丰台区</item><item>石景山区</item><item>海淀区</item><item>门头沟区</item><item>房山区</item><item>通州区</item><item>顺义区</item><item>昌平区</item><item>大兴区</item><item>怀柔区</item><item>平谷区</item><item>密云县</item><item>延庆县</item></province></cities>"));

        /*User u = new User();
        u.setName("wxw");
        u.setAge(12);
        u.setSex(1);
        System.out.println(object2JsonObject(u));*/

        /*User u = new User();
        u.setName("wxw");
        u.setAge(12);
        u.setSex(1);
        System.out.println(object2Json(u));*/

        /*User us = json2Ojbect(object2Json(u), User.class);
        System.out.println(us);
        */

        /*List<User> list = new ArrayList<User>();
        User u = new User();
        u.setName("wxw");
        u.setAge(12);
        u.setSex(1);
        list.add(u);
        u = new User();
        u.setName("zmx");
        u.setAge(12);
        u.setSex(0);
        list.add(u);
        u = new User();
        u.setName("arnold");
        u.setAge(12);
        u.setSex(1);
        list.add(u);
        String str = list2Json(list);
        System.out.println(str);*/

        /*List<User> userList = converAnswerFormString(str, User.class);
        System.out.println(userList);
        */
        List<Map<Object,Object>> listTest = new ArrayList<Map<Object, Object>>();
        Map<Object,Object> test =  new HashMap<Object, Object>();
        test.put("id",1);
        test.put("createTime",new Date());
        Map<Object,Object> test1 =  new HashMap<Object, Object>();
        test1.put("id",1);
        test1.put("createTime",new Date());

        listTest.add(test);
        listTest.add(test1);
        String list2Json =  JsonUtil.list2Json(listTest);
        System.out.println(list2Json);

        List<Map<Object,Object>> json2List =  (List<Map<Object,Object>>)JsonUtil.parseJSON2List(list2Json);
        System.out.println(json2List.toString());


        // [{"age":12,"createTime":null,"id":"","name":"wxw","registerTime":null,"sex":1}]
//        String test123 = "[{\n\t\"beginRentDate\": \"2018-09-21 17:35\",\n\t\"hasExpectApprove\": \"\",\n\t\"houseId\": \"236\",\n\t\"houseRentPrice\": \"444\",\n\t\"id\": \"\",\n\t\"isOpen\": \"1\",\n\t\"payNode\": \"1\",\n\t\"payType\": \"\",\n\t\"rentTime\": \"1\",\n\t\"type\": 0\n}, {\n\t\"beginRentDate\": \"2018-09-21 18:35\",\n\t\"hasExpectApprove\": \"\",\n\t\"houseId\": \"236\",\n\t\"houseRentPrice\": \"444\",\n\t\"id\": \"\",\n\t\"isOpen\": \"1\",\n\t\"payNode\": \"1\",\n\t\"payType\": \"\",\n\t\"rentTime\": \"1\",\n\t\"type\": 0\n}]";
        String test123 = "[{\\''beginRentDate\\'':\\''2018-09-21 18:42\\'',\\''hasExpectApprove\\'':\\''\\'',\\''houseId\\'':\\''236\\'',\\''houseRentPrice\\'':\\''444\\'',\\''id\\'':\\''\\'',\\''isOpen\\'':\\''1\\'',\\''payNode\\'':\\''1\\'',\\''payType\\'':\\''\\'',\\''rentTime\\'':\\''1\\'',\\''type\\'':0},{\\''beginRentDate\\'':\\''2018-09-21 18:42\\'',\\''hasExpectApprove\\'':\\''\\'',\\''houseId\\'':\\''236\\'',\\''houseRentPrice\\'':\\''444\\'',\\''id\\'':\\''\\'',\\''isOpen\\'':\\''1\\'',\\''payNode\\'':\\''2\\'',\\''payType\\'':\\''\\'',\\''rentTime\\'':\\''1\\'',\\''type\\'':0}]";
        //String test123 = "[{\n\t\"beginRentDate\": \"2018-09-21 17:35\",\n\t\"hasExpectApprove\": \"\",\n\t\"houseId\": \"236\",\n\t\"houseRentPrice\": \"444\",\n\t\"id\": \"\",\n\t\"isOpen\": \"1\",\n\t\"payNode\": \"1\",\n\t\"payType\": \"\",\n\t\"rentTime\": \"1\",\n\t\"type\": 0\n}, {\n\t\"beginRentDate\": \"2018-09-21 18:35\",\n\t\"hasExpectApprove\": \"\",\n\t\"houseId\": \"236\",\n\t\"houseRentPrice\": \"444\",\n\t\"id\": \"\",\n\t\"isOpen\": \"1\",\n\t\"payNode\": \"1\",\n\t\"payType\": \"\",\n\t\"rentTime\": \"1\",\n\t\"type\": 0\n}]";
        List<HsHouseAutoReplySetting> autoReplySettings = JsonUtil.json2List(test123, HsHouseAutoReplySetting.class);
        System.out.println(autoReplySettings.toString());
    }
}
