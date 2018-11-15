package www.ucforward.com.utils;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import org.junit.Test;
import org.utils.StringUtil;
import www.ucforward.com.index.entity.GoogleMapLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * google地图工具类
 * @author wenbn
 * @version 1.0
 * @date 2018/6/14
 */
public class GoogleMapUtil {


    public static GeoApiContext context = new GeoApiContext.Builder()
            .apiKey("AIzaSyAlE8ysZ3l5BjWxrVsLHkjxmK37KBbqPQ4")
//                .enterpriseCredentials(clientID, clientSecret)
            .build();


    /**
     * 具体位置获取google地图的经纬度
     * @param address 具体地位
     * @return
     */
    public static Map<String,Object> getMapLocation(String address) throws InterruptedException, ApiException, IOException {
        //返回结果
        GoogleMapLocation map = null;
        Map<String,Object> result = Maps.newHashMap();
        if(!StringUtil.hasText(address)){
            result.put("state",-1);
            result.put("massege","请输入具体位置......");
            result.put("obj",map);
            return result;
        }
//        System.setProperty("proxySet", "true");
//        System.setProperty("socksProxyHost", "127.0.0.1");
//        System.setProperty("socksProxyPort", "1080");
        try{
            GeocodingResult[] results =  GeocodingApi.geocode(context,address).await();
            //System.out.println(JsonUtil.toJson(results[0].addressComponents));
            //System.out.println(JsonUtil.toJson(results[0].geometry.location));
            map = new GoogleMapLocation(results[0].geometry.location.lng,results[0].geometry.location.lat);
            result.put("state",0);
            result.put("massege","请求成功");
        }catch (Exception e){
            result.put("state",1);
            result.put("massege","请求失败");
        }
        result.put("obj",map);
        return result;
    }

    /**
     * 取消代理服务器的设置
     */
    public static void removeServiceProxy() {
        Properties properties = System.getProperties();
        properties.remove("proxySet");
        properties.remove("socksProxyHost");
        properties.remove("socksProxyPort");
//        properties.remove("http.proxyHost");
//        properties.remove("http.proxyPort");
//        properties.remove("https.proxyHost");
//        properties.remove("https.proxyPort");
    }


    @Test
    public void test() throws InterruptedException, ApiException, IOException {
        System.setProperty("proxySet", "true");
        System.setProperty("socksProxyHost", "127.0.0.1");
        System.setProperty("socksProxyPort", "1080");
        GeocodingResult[] results =  GeocodingApi.geocode(context,
                "深圳市大鹏新区").await();


        //GeocodingApi.newRequest(context).address("深圳").await();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        //System.out.println(gson.toJson(results[0].addressComponents));
        System.out.println(gson.toJson(results[0].geometry.location));
        System.out.println(gson.toJson(results[0].geometry.location.lat)+","+gson.toJson(results[0].geometry.location.lng));
    }

    @Test
    public void test1() throws InterruptedException, ApiException, IOException {
        System.setProperty("proxySet", "true");
        System.setProperty("socksProxyHost", "127.0.0.1");
        System.setProperty("socksProxyPort", "1080");

        double longitude = 114.057865;
        double latitude = 22.543096;
        LatLng _LatLng = new LatLng(latitude,longitude);

        double longitude1 = 114.051425;
        double latitude1 = 22.512326;
        LatLng LatLng1 = new LatLng(latitude1,longitude1);

        double longitude2 = 114.051425;
        double latitude2 = 22.734546;
        LatLng LatLng2 = new LatLng(latitude2,longitude2);

        LatLng destinations = new LatLng(latitude,longitude);
        List<LatLng> origins  = new ArrayList<>();
        LatLng[] _origins = new LatLng[10];
        for (int i = 0;i<10;i++) {
            LatLng LatLng = null;
            int rd=(int)(Math.random()>0.5?1:0);
            double x=(double)(Math.random());
            double y=(double)(Math.random());
            if(rd>0){
                LatLng = new LatLng(latitude+y,longitude+x);
            }else{
                LatLng = new LatLng(latitude-y,longitude-x);
            }
            _origins[i] = LatLng;
            origins.add(LatLng);
        }
        //DistanceMatrix await = DistanceMatrixApi.newRequest(context).origins(new String[]{"北京","长沙"}).destinations(new String[]{"上海","益阳"}).mode(TravelMode.DRIVING).await();
        DistanceMatrix await = DistanceMatrixApi.newRequest(context).origins(new LatLng[]{LatLng1,LatLng2}).destinations(_LatLng).mode(TravelMode.DRIVING).await();
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
       System.out.println(JsonUtil.toJson(await));
    }

    public static DistanceMatrix getDistanceMatrix(){

        return null;
    }

    @Test
    public void test2() throws InterruptedException, ApiException, IOException {
        System.setProperty("proxySet", "true");
        System.setProperty("socksProxyHost", "127.0.0.1");
        System.setProperty("socksProxyPort", "1080");
        GeolocationResult await = GeolocationApi.newRequest(context).await();
        System.out.println(JsonUtil.toJson(await));
    }


    public static void main(String[] args) throws InterruptedException, ApiException, IOException {
        String address = "深圳市龙岗区坂田街道永香路4巷8号";
        Map<String, Object> mapLocation = getMapLocation(address);
        System.out.println(mapLocation);
    }
}