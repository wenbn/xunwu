package www.ucforward.com.serviceInter;

import org.springframework.stereotype.Service;
import www.ucforward.com.vo.ResultVo;

import java.util.List;
import java.util.Map;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/5/23
 */
@Service
public interface OrderService<T>  {

    //所有库公用的方法 start

    ResultVo delete(Integer id, Object o) throws Exception;

    ResultVo insert(Object o) throws Exception;

    ResultVo select(Integer id, Object o) throws Exception;

    /**
     * 查询列表数据
     * @param o 查询的实体，用于控制查询的dao
     * @param condition 查询参数
     * @param returnType 返回值类型，0 List<Map>  1 list<Entity>
     * @return
     * @throws Exception
     */
    ResultVo selectList(Object o, Map<Object,Object> condition, int returnType) throws Exception;

    /**
     * 自定义查询列数据
     * @param condition 查询条件 List<String> columns
     * @return
     * @throws Exception
     */
    ResultVo selectCustomColumnNamesList(T t, Map<Object,Object> condition) throws Exception;

    ResultVo update(Object o) throws Exception;

    ResultVo batchUpdate(Object o ,Map<Object,Object> condition) throws Exception;

    //所有库公用的方法 end

    //加入系统订单池
    ResultVo addSystemOrderPoolTx(Map<Object, Object> condition) throws Exception ;

    /**
     * 获取订单状态 字典表dict_order_status
     * @return
     */
    ResultVo getOrderStatus();

    /**
     * 根据条件更新
     * @param condition
     * @return
     */
    ResultVo updateByCondition(Object o,Map<Object, Object> condition);

}