package www.ucforward.com.manager.impl;

import org.springframework.stereotype.Service;
import org.utils.StringUtil;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.entity.HsHousingOrder;
import www.ucforward.com.entity.HsHousingOrderPaymentRecord;
import www.ucforward.com.entity.HsMainHouse;
import www.ucforward.com.manager.PayManager;
import www.ucforward.com.serviceInter.HousesService;
import www.ucforward.com.serviceInter.OrderService;
import www.ucforward.com.serviceInter.PayService;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/7/9
 */
@Service("payManager")
public class PayManagerImpl implements PayManager {

    @Resource
    private OrderService orderService;
    @Resource
    private PayService payService;
    @Resource
    private HousesService housesService;

    /**
     * 测试订单支付接口
     * @param condition
     * @return
     */
    @Override
    public ResultVo testOrderPay(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            int memberId = StringUtil.getAsInt(StringUtil.trim(condition.get("memberId")));
            result = orderService.selectList(new HsHousingOrder(), condition, 1);
            if(ResultConstant.SYS_REQUIRED_SUCCESS == result.getResult()){
                HsHousingOrder order = ((List<HsHousingOrder>) result.getDataSet()).get(0);
                if(order==null){
                    result.setResult(ResultConstant.ORDER_DATA_EXCEPTION);
                    result.setMessage(ResultConstant.ORDER_DATA_EXCEPTION_VALUE);
                    return result;
                }
                Date nowTime = new Date();
                if(memberId==order.getMemberId()){
                    //插入支付记录
                    HsHousingOrderPaymentRecord payment = new HsHousingOrderPaymentRecord();
                    payment.setOrderSerialCode("test_pay");
                    payment.setPayWay(1);
                    payment.setProceedsAmout(order.getOrderAmount());
                    payment.setOrderId(order.getId());
                    payment.setCreateTime(nowTime);
                    payment.setCreateBy(memberId);
                    payment.setUpdateTime(nowTime);
                    payment.setRemarks("用户完成线上支付");

                    result = payService.insert(payment);
                    if(ResultConstant.SYS_REQUIRED_SUCCESS == result.getResult()){
                        //修改订单状态
                        HsHousingOrder orderUpdate = new HsHousingOrder();
                        orderUpdate.setId(order.getId());
                        orderUpdate.setOrderStatus(1);
                        orderUpdate.setOrderStatusTitle("已支付");
                        orderUpdate.setPayWay(1);
                        orderUpdate.setPayStatus(1);
                        orderUpdate.setUpdateBy(memberId);
                        orderUpdate.setUpdateTime(nowTime);
                        orderUpdate.setVersionNo(order.getVersionNo());
                        result = orderService.update(orderUpdate);
                        if(ResultConstant.SYS_REQUIRED_SUCCESS == result.getResult()){
                            HsMainHouse house =  new HsMainHouse();
                            house.setId(order.getHouseId());
                            house.setUpdateTime(nowTime);
                            house.setIsLock(1);
                            house.setRemark("房源已完成支付，正常下架");
                            result = housesService.update(house);
                        }
                    }
                }else{
                    //订单数据异常
                    result.setResult(ResultConstant.ORDER_DATA_EXCEPTION);
                    result.setMessage(ResultConstant.ORDER_DATA_EXCEPTION_VALUE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }
}
