package www.ucforward.com.manager;

import org.springframework.stereotype.Service;
import www.ucforward.com.entity.*;
import www.ucforward.com.vo.ResultVo;

import java.util.Map;

/**
 * 系统及订单相关管理
 * @author wenbn
 * @version 1.0
 * @date 2018/7/26
 */
@Service
public interface OrderAdminManager {

    //业务员抢单列表
    ResultVo grabOrdersList(Map<Object, Object> condition);

    //业务员抢单详情
    ResultVo getGrabOrderDetail(Map<Object, Object> condition);

    /**
     * 更新投诉订单
     * @param condition
     * @return
     */
    ResultVo updateComplaintGrabOrderDetail(Map<Object, Object> condition);

    //业务员抢单动作
    ResultVo grabOrderAction(Map<Object, Object> condition);


}