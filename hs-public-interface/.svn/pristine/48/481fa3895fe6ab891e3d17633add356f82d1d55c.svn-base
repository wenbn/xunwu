package www.ucforward.com.utils;

import org.junit.Test;

public class MapUtil {

    private static final  double EARTH_RADIUS = 6371000;//赤道半径(单位m)

    /**
     * 转化为弧度(rad)
     * */
    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }
    /**
     * 基于googleMap中的算法得到两经纬度之间的距离,计算精度与谷歌地图的距离精度差不多，相差范围在0.2米以下
     * @param lon1 第一点的精度
     * @param lat1 第一点的纬度
     * @param lon2 第二点的精度
     * @param lat2 第二点的纬度
     * @return 返回的距离，单位m
     * */
    public static double GetDistance(double lon1,double lat1,double lon2, double lat2)
    {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    /**
     *
     * @param longitude 经度
     * @param latitude 纬度
     */
    @Test
    public static void findNeighPosition(double longitude,double latitude){
//        longitude = 12683996;
//        latitude = 2557729;
        //先计算查询点的经纬度范围
        double r = 6371;//地球半径千米
        double dis = 0.5;//0.5千米距离
        double dlng =  2*Math.asin(Math.sin(dis/(2*r))/Math.cos(latitude*Math.PI/180));
        dlng = dlng*180/Math.PI;//角度转为弧度
        double dlat = dis/r;
        dlat = dlat*180/Math.PI;
        double minlat =latitude-dlat;
        double maxlat = latitude+dlat;
        double minlng = longitude -dlng;
        double maxlng = longitude + dlng;

        System.out.println("minLatitude="+minlat+"   maxLatitude="+maxlat+"   minLongitude="+minlng+"   maxLongitude="+maxlng);

    }


    public static void main(String []args){
        double lon1=109.0145193757;
        double lat1=34.236080797698;
        double lon2=108.9644583556;
        double lat2=34.286439088548;
        double dist;
        String geocode;
        dist=MapUtil.GetDistance(lon1, lat1, lon2, lat2);
        System.out.println("两点相距：" + dist + " 米");
        Geohash geohash = new Geohash();
        geocode=geohash.encode(lat1, lon1);
        System.out.println("当前位置编码：" + geocode);

        geocode=geohash.encode(lat2, lon2);
        System.out.println("远方位置编码：" + geocode);
        double x=(double)(Math.random());
        System.out.println("ssss：" + x);
        //现在所在地址
        double longitude = 22.535596476175666;
        double latitude = 113.9480730800027;
        findNeighPosition(longitude,latitude);
    }
}
