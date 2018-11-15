package www.ucforward.com.index.entity;

import java.io.Serializable;

/**
 * 房源查询条件
 * @author wenbn
 * @version 1.0
 * @date 2018/7/2
 */
public class HouseSearchCondition implements Serializable {

    private String keywords;//搜索关键字

    private Integer leaseType;//预约类型（0：出租，1：出售）

    private Integer minPrice;//最小价格
    private Integer maxPrice;//最大价格
    private Integer minArea;//最小面积
    private Integer maxArea;//最大面积
    private Integer minBedroom;//最小卧室数量
    private Integer maxBedroom;//最大卧室数量
    private Integer minBathroom;//最小浴室数量
    private Integer maxBathroom;//最大浴室数量

    private String houseOrientationDictcode;//房屋朝向 0：东 1：南 2：西 3：北 4：南北
    private String houseDecorationDictcode;//房屋装修 0：毛坯 1：普通装修 2：精装修 3：豪华装修
    private String houseLabelDictcode;//房源特色，查询数据字典，逗号分割
    private String houseFloorDictcode;//房源楼层，0：低楼层 1：中楼层 2：高楼层
    private String houseConfigDictcode;//房源楼层，0：低楼层 1：中楼层 2：高楼层
    private String housingTypeDictcode;//房屋类型 0:小区 1:公寓 2:民房 3:别墅
    private String payNode;//支付节点 , 1....12/月


    private String orderBy = "updateTime";
    private String orderDirection = "desc";
    private String city;//城市
    private String community;//社区
    private String subCommunity;//子社区
    private String property;//城市
    private String address;//详细地址
    private String houseName;//房源名称
    private String buildingName;//楼名/别墅名
    private String villageName;//小区名称

    private Integer pageIndex = 1;//当前页
    private Integer pageSize = 10;//查询数据条数

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Integer getLeaseType() {
        return leaseType;
    }

    public void setLeaseType(Integer leaseType) {
        this.leaseType = leaseType;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getMinArea() {
        return minArea;
    }

    public void setMinArea(Integer minArea) {
        this.minArea = minArea;
    }

    public Integer getMaxArea() {
        return maxArea;
    }

    public void setMaxArea(Integer maxArea) {
        this.maxArea = maxArea;
    }

    public Integer getMinBedroom() {
        return minBedroom;
    }

    public void setMinBedroom(Integer minBedroom) {
        this.minBedroom = minBedroom;
    }

    public Integer getMaxBedroom() {
        return maxBedroom;
    }

    public void setMaxBedroom(Integer maxBedroom) {
        this.maxBedroom = maxBedroom;
    }

    public Integer getMinBathroom() {
        return minBathroom;
    }

    public void setMinBathroom(Integer minBathroom) {
        this.minBathroom = minBathroom;
    }

    public Integer getMaxBathroom() {
        return maxBathroom;
    }

    public void setMaxBathroom(Integer maxBathroom) {
        this.maxBathroom = maxBathroom;
    }

    public String getHouseOrientationDictcode() {
        return houseOrientationDictcode;
    }

    public void setHouseOrientationDictcode(String houseOrientationDictcode) {
        this.houseOrientationDictcode = houseOrientationDictcode;
    }

    public String getHouseDecorationDictcode() {
        return houseDecorationDictcode;
    }

    public void setHouseDecorationDictcode(String houseDecorationDictcode) {
        this.houseDecorationDictcode = houseDecorationDictcode;
    }

    public String getHouseLabelDictcode() {
        return houseLabelDictcode;
    }

    public void setHouseLabelDictcode(String houseLabelDictcode) {
        this.houseLabelDictcode = houseLabelDictcode;
    }

    public String getHouseFloorDictcode() {
        return houseFloorDictcode;
    }

    public void setHouseFloorDictcode(String houseFloorDictcode) {
        this.houseFloorDictcode = houseFloorDictcode;
    }

    public String getHouseConfigDictcode() {
        return houseConfigDictcode;
    }

    public void setHouseConfigDictcode(String houseConfigDictcode) {
        this.houseConfigDictcode = houseConfigDictcode;
    }

    public String getHousingTypeDictcode() {
        return housingTypeDictcode;
    }

    public void setHousingTypeDictcode(String housingTypeDictcode) {
        this.housingTypeDictcode = housingTypeDictcode;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(String orderDirection) {
        this.orderDirection = orderDirection;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getSubCommunity() {
        return subCommunity;
    }

    public void setSubCommunity(String subCommunity) {
        this.subCommunity = subCommunity;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getPayNode() {
        return payNode;
    }

    public void setPayNode(String payNode) {
        this.payNode = payNode;
    }
}
