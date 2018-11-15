package www.ucforward.com.manager.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.utils.StringUtil;
import www.ucforward.com.constants.MessageConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.dto.HsOrderContract;
import www.ucforward.com.entity.*;
import www.ucforward.com.index.message.HouseIndexMessage;
import www.ucforward.com.manager.OrderManager;
import www.ucforward.com.serviceInter.CommonService;
import www.ucforward.com.serviceInter.HousesService;
import www.ucforward.com.serviceInter.MemberService;
import www.ucforward.com.serviceInter.OrderService;
import www.ucforward.com.utils.BigDecimalUtil;
import www.ucforward.com.utils.ImageUtil;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.utils.ModelMapperUtil;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auther: lsq
 * @Date: 2018/8/23 10:35
 * @Description:
 */
@Service("OrderManager")
public class OrderManagerImpl implements OrderManager {

    @Resource
    private OrderService orderService;
    @Resource
    private HousesService housesService;
    @Resource
    private MemberService memberService;
    @Resource
    private CommonService commonService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 获取订单列表
     * TODO 如果没有获取到订单对应的房源信息，不会返回这条订单信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo getOrderList(Map<Object, Object> condition){
        ResultVo result = new ResultVo();
        List<Map<Object,Object>> resultMap = new ArrayList<>();
        try{
            //1.获取订单信息
            ResultVo orderListResultVo = orderService.selectList(new HsHousingOrder(), condition, 0);
            if(orderListResultVo == null || orderListResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || orderListResultVo.getDataSet() == null){
                //查询订单列表失败
                return result;
            }
            //订单信息
            List<Map<Object, Object>> orderList = (List<Map<Object, Object>>) orderListResultVo.getDataSet();
            //2.在订单信息中获取房源id列表
            List<Integer> houseIds = new ArrayList<>();
            orderList.forEach(map ->{
                int houseId = StringUtil.getAsInt(StringUtil.trim(map.get("houseId")));
                houseIds.add(houseId);
            });
            //3.根据上面获取的房源id列表获取房源列表
            condition.clear();
            condition.put("houseIds",houseIds);
            ResultVo houseResultVo = housesService.selectList(new HsMainHouse(), condition, 0);
            if(houseResultVo == null || houseResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || houseResultVo.getDataSet() == null){
                //未查房源信息失败
                return orderListResultVo;
            }
            //房源信息
            List<Map<Object, Object>> houseList = (List<Map<Object, Object>>) houseResultVo.getDataSet();
            //4.遍历订单信息，根据房源id将房源信息封装到订单信息中，形成返回结果集
            for (Map<Object, Object> orderMap : orderList) {
                int houseId = StringUtil.getAsInt(StringUtil.trim(orderMap.get("houseId")));
                //订单服务费
                BigDecimal platformServiceAmount = new BigDecimal(StringUtil.trim(orderMap.get("platformServiceAmount"),"0.0"));
                //银行手续费
                BigDecimal poundage = new BigDecimal(StringUtil.trim(orderMap.get("standby3"),"0.0"));
                //支付总额
                double add = BigDecimalUtil.add(platformServiceAmount.doubleValue(), poundage.doubleValue());
                orderMap.put("platformServiceAmount",add);
                for (Map<Object, Object> houseMap : houseList) {
                    //房源ID
                    int id = StringUtil.getAsInt(StringUtil.trim(houseMap.get("id")));
                    //房源名称
                    String houseName = StringUtil.trim(houseMap.get("houseName"));
                    //房屋面积
                    String houseAcreage = StringUtil.trim(houseMap.get("houseAcreage"));
                    // 城市
                    String city = StringUtil.trim(houseMap.get("city"));
                    //社区
                    String community = StringUtil.trim(houseMap.get("community"));
                    //子社区
                    String subCommunity = StringUtil.trim(houseMap.get("subCommunity"));
                    //项目
                    String property = StringUtil.trim(houseMap.get("property"));
                    //房源所在区域名称
                    String address = StringUtil.trim(houseMap.get("address"));
                    //期望租金/或出售价
                    String houseRent = StringUtil.trim(houseMap.get("houseRent"));
                    //房源主图
                    String houseMainImg = StringUtil.trim(houseMap.get("houseMainImg"));
                    if(houseId == id){
                        //封装结果集
                        orderMap.put("houseName",houseName);
                        orderMap.put("houseAcreage",houseAcreage);
                        orderMap.put("city",city);
                        orderMap.put("community",community);
                        orderMap.put("subCommunity",subCommunity);
                        orderMap.put("property",property);
                        orderMap.put("address",address);
                        orderMap.put("houseRent",houseRent);
                        orderMap.put("houseMainImg", ImageUtil.imgResultUrl(houseMainImg));
                        resultMap.add(orderMap);
                        break;
                    }
                }
            }
            result.setDataSet(resultMap);
            result.setPageInfo(orderListResultVo.getPageInfo());
        }catch (Exception e){
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 获取合同信息
     * @param orderId
     * @return
     */
    @Override
    public ResultVo getContract(int orderId){
        ResultVo result = new ResultVo();
        HsOrderContract orderContract = new HsOrderContract();
        try{
            //1.获取合同信息  合同附加条款、交易类型、订单信息等
            ResultVo orderResultVo = orderService.select(orderId, new HsHousingOrder());
            if(orderResultVo != null && orderResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && orderResultVo.getDataSet() != null){
                HsHousingOrder order = (HsHousingOrder) orderResultVo.getDataSet();
                //议价id
                Integer bargainId = order.getBargainId();
                //房源id
                Integer houseId = order.getHouseId();
                //订单类型 0-租房->1-买房
                Integer orderType = order.getOrderType();
                //合同附加条款
                String additionalTermsStr = order.getAdditionalTerms();
                JSONArray additionalTerms = JSON.parseArray(additionalTermsStr);
                //2.获取议价信息  获取议价最终的订单信息，价格、起租时间等
                ResultVo bargainResultVo = memberService.select(bargainId, new HsMemberHousingBargain());
                if(bargainResultVo == null || bargainResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || bargainResultVo.getDataSet() == null){
                    result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                    result.setMessage("获取议价信息失败！");
                    return result;
                }
                HsMemberHousingBargain bargain = (HsMemberHousingBargain) bargainResultVo.getDataSet();
                //租金价格 单位 迪拉姆/年
                Integer leasePrice = bargain.getLeasePrice();
                //租赁时长,单位年
                Integer leaseDurationYear = bargain.getLeaseDurationYear();
                //起租日期
                Date leaseStartDate = bargain.getLeaseStartDate();
                //3.获取房源信息
                ResultVo houseResultVo = housesService.select(houseId, new HsMainHouse());
                if(houseResultVo == null || houseResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || houseResultVo.getDataSet() == null){
                    result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                    result.setMessage("获取房源信息失败！");
                    return result;
                }
                HsMainHouse house = (HsMainHouse) houseResultVo.getDataSet();
                //房屋装修 0：带家具，1：不带家具
                String houseDecorationDictcode = house.getHouseDecorationDictcode();
                //4,根据订单类型封装不同的合同返回值
                //封装房源信息
                ModelMapperUtil.getInstance().map(house,orderContract);
                //封装议价信息
                ModelMapperUtil.getInstance().map(bargain,orderContract);
                //订单服务费
                BigDecimal platformServiceAmount = order.getPlatformServiceAmount();
                //银行手续费
                BigDecimal poundage = new BigDecimal(StringUtil.trim(order.getStandby3(), "0.00"));
                //支付总额
                double add = BigDecimalUtil.add(platformServiceAmount.doubleValue(), poundage.doubleValue());
                orderContract.setPlatformServiceAmount(new BigDecimal(StringUtil.trim(add)));
                //合同附加条款
                orderContract.setAdditionalTerms(additionalTerms);
                if(orderType == 0){
                    //出租
                    //根据起租日期及租赁时长，算出租赁结束日期
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(leaseStartDate);
                    calendar.add(Calendar.YEAR, leaseDurationYear);
                    Date contractPeriodTo = calendar.getTime();
                    orderContract.setContractPeriodTo(contractPeriodTo);
                    /**
                     * 根据房屋装修情况、租金价格、租赁时长计算保证金，有家具为总租金的百分之10 没家具为总租金的百分之5
                     * 保证金 = 总租金 * 百分比
                     */
                    //获取总租金  防止结果溢出，将租金转为Long再进行计算
                    double leasePriceDouble = leasePrice.doubleValue();
                    double mul = BigDecimalUtil.mul(leasePriceDouble, leaseDurationYear);
                    //百分比
                    double percent = 0.05;
                    //是否有家具
                    String possess = "0";
                    if(possess.equals(houseDecorationDictcode)){
                        //有家具
                        percent = 0.1;
                    }
                    Double db = BigDecimalUtil.mul(mul,percent);
                    //防止将Double转换为BigDecimal后精度太高的问题。先将Double转换为字符串
                    String trim = StringUtil.trim(db);
                    BigDecimal securityDepositAmount = new BigDecimal(trim);
                    orderContract.setSecurityDepositAmount(securityDepositAmount);
                }else if(orderType == 1){
                    //出售


                }
                //获取业主信息
                String memberMoble = "";
                String familyName = "";
                String name = "";
                Integer memberId = house.getMemberId();
                ResultVo memberResultVo = memberService.select(memberId, new HsMember());
                if(memberResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && memberResultVo.getDataSet() != null){
                    HsMember member = (HsMember) memberResultVo.getDataSet();
                    memberMoble = member.getMemberMoble();
                    familyName = member.getFamilyName();
                    name = member.getName();
                }
                orderContract.setMemberMoble(memberMoble);
                orderContract.setMemberName(StringUtil.trim(familyName) + StringUtil.trim(name));
            }
            result.setDataSet(orderContract);
        }catch (Exception e){
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 客户确认合同
     * @param memberId 当前登陆人员id
     * @param orderId 合同订单id
     * @return
     */
    @Override
    public ResultVo confirmationContract(Integer memberId, Integer orderId){
        ResultVo result = new ResultVo();
        try {
            //1.获取订单信息
            ResultVo orderResultVo = orderService.select(orderId, new HsHousingOrder());
            if(orderResultVo != null && orderResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && orderResultVo.getDataSet() != null){
                HsHousingOrder order = (HsHousingOrder) orderResultVo.getDataSet();
                Integer id = order.getMemberId();
                if(!memberId.equals(id)){
                    result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                    result.setMessage("Current user is not a contract customer！");
                    return result;
                }
                Integer orderStatus = order.getOrderStatus();
                if(orderStatus != -2){
                    result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                    result.setMessage("Contract status is incorrect！");
                    return result;
                }
                //2.修改订单信息
                order.setOrderStatus(0);
                result = orderService.update(order);
            }
        }catch (Exception e){
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 获取订单状态 字典表dict_order_status
     * @return
     */
    @Override
    public ResultVo getOrderStatus(){
        ResultVo result = new ResultVo();
        try {
            result = orderService.getOrderStatus();
        }catch (Exception e){
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 取消订单
     * @param condition
     * @return
     */
    @Override
    public ResultVo cancelOrder(Map<Object, Object> condition){
        ResultVo vo;
        try {
            int id = StringUtil.getAsInt(StringUtil.trim(condition.get("id")));
            String orderCode = StringUtil.trim(condition.get("orderCode"));
            int userId = StringUtil.getAsInt(StringUtil.trim(condition.get("userId")));
            int isCancel = StringUtil.getAsInt(StringUtil.trim(condition.get("isCancel")));
            String remark = StringUtil.trim(condition.get("remark"));
            condition.clear();
            condition.put("orderCode",orderCode);
            vo = orderService.selectList(new HsHousingOrder(), condition, 1);
            if(vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                //订单不存在
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage("Order does not exist");
                return vo;
            }
            Date nowTime = new Date();
            List<HsHousingOrder> orderList = (List<HsHousingOrder>) vo.getDataSet();
            HsHousingOrder order = null;
            if(orderList == null || orderList.size() < 1){
                //订单不存在
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage("Order does not exist");
                return vo;
            }
            order = orderList.get(0);
            //交易状态 0:交易中 1:交易成功 2:交易失败
            Integer tradingStatus = order.getTradingStatus();
            if(tradingStatus != 0){
                //订单状态错误
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage("Order status error");
                return vo;
            }

            order.setIsCancel(isCancel);
            order.setUpdateBy(userId);
            order.setUpdateTime(nowTime);
            if(StringUtil.hasText(remark)){
                order.setRemark(remark);
            }
            //更新订单信息
            vo = orderService.update(order);
            if(vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                Integer houseId = order.getHouseId();
                //插入日志信息
                HsHousingOrderLog orderLog = new HsHousingOrderLog();
                orderLog.setOrderId(id);
                if(StringUtil.hasText(remark)){
                    orderLog.setRemarks(remark);
                }else{
                    orderLog.setRemarks("取消订单");
                }
                orderLog.setCreateTime(nowTime);
                orderLog.setPostTime(nowTime);
                orderLog.setCreateBy(userId);
                orderLog.setOperatorUid(userId);
                //操作人类型1:普通会员 2:商家 3:系统管理员
                orderLog.setOperatorType(1);
                orderService.insert(orderLog);
                /*更新房源进度*/
                //1.封装进度信息
                HsHouseProgress hsHouseProgress = new HsHouseProgress();
                //房源ID
                hsHouseProgress.setHouseId(houseId);
                //创建人
                hsHouseProgress.setCreateBy(userId);
                //进度
                hsHouseProgress.setProgressCode("102");
                //创建日期
                hsHouseProgress.setCreateTime(nowTime);
                //3.插入数据
                ResultVo insert = housesService.insert(hsHouseProgress);
                if (insert.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                    /*删除发布房源之后的进度*/
                    //角色类型，逗号（,）分隔 1 业主；10业主(出租)；11业主(出售)；2 租客 3 买家 40客服(出租) 41客服(出售) 50内勤(出租) 51内勤(出售)
                    String type = "50";
                    //订单类型 0-租房->1-买房
                    Integer orderType = order.getOrderType();
                    if(orderType == 1){
                        type = "51";
                    }
                    //获取进度列表
                    List<Map<String, Object>> progressList = housesService.findProgressList(type);
                    Optional<Map<String, Object>> codeOptional = progressList.stream().filter(map -> "102".equals(StringUtil.trim(map.get("code")))).findFirst();
                    if(codeOptional.isPresent()){
                        Map<String, Object> progressMap = codeOptional.get();
                        //102进度的顺序位置
                        int orderInt = StringUtil.getAsInt(StringUtil.trim(progressMap.get("order")));
                        //大于102进度位置的进度
                        List<String> codes = new ArrayList<>();
                        for (Map<String, Object> map : progressList) {
                            if(StringUtil.getAsInt(StringUtil.trim(map.get("order"))) > orderInt){
                                String code = StringUtil.trim(map.get("code"));
                                codes.add(code);
                            }
                        }
                        condition.clear();
                        List<String> setColumn = new ArrayList<>();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String format = sdf.format(nowTime);
                        setColumn.add("DELETE_TIME = '" + format + "'");
                        condition.put("setColumn",setColumn);
                        condition.put("progressCodes",codes);
                        condition.put("houseId",houseId);
                        int i = housesService.updateByCondition(condition);
                    }
                    /*修改房源状态*/
                    ResultVo houseResultVo = housesService.select(houseId, new HsMainHouse());
                    if(houseResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                        HsMainHouse mainHouse = (HsMainHouse) houseResultVo.getDataSet();
                        if(mainHouse != null){
                            Integer isLock = mainHouse.getIsLock();
                            if(isLock == 1){
                                //是否锁定：0:未锁定，1：锁定（议价成功后，锁定房源）
                                mainHouse.setIsLock(0);
                                housesService.update(mainHouse);
                                //创建索引
                                HouseIndexMessage message = new HouseIndexMessage(houseId, HouseIndexMessage.REMOVE, 0);
                                kafkaTemplate.send(MessageConstant.BUILD_HOUSE_INDEX_TOPIC_MESSAGE, JsonUtil.toJson(message));
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

}
