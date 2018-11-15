package www.ucforward.com.manager;

import org.springframework.stereotype.Service;
import www.ucforward.com.vo.ResultVo;

import java.util.Map;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/7/9
 */
@Service
public interface PayManager {

    /**
     * 测试订单支付接口
     * @param condition
     * @return
     */
    ResultVo testOrderPay(Map<Object,Object> condition);
}
