package www.ucforward.com.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.dao.HsHousingOrderPaymentOnlineSerialDao;
import www.ucforward.com.dao.HsHousingOrderPaymentRecordDao;
import www.ucforward.com.entity.*;
import www.ucforward.com.serviceInter.PayService;
import www.ucforward.com.vo.ResultVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("payService")
public class PayServiceImpl implements PayService {

    @Autowired
    private HsHousingOrderPaymentOnlineSerialDao hsHousingOrderPaymentOnlineSerialDao;
    @Autowired
    private HsHousingOrderPaymentRecordDao hsHousingOrderPaymentRecordDao;

    @Override
    public ResultVo delete(Integer id, Object o) throws Exception {
        ResultVo vo = new ResultVo();
        int result = -1;//
        try{
            if (o instanceof HsHousingOrderPaymentOnlineSerial) {
                result = hsHousingOrderPaymentOnlineSerialDao.deleteByPrimaryKey(id);
            } else if (o instanceof HsHousingOrderPaymentRecord) {
                result = hsHousingOrderPaymentRecordDao.deleteByPrimaryKey(id);
            }
            if(result>0){//操作成功
                vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
                vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            }else{
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
        }catch (Exception e){
            e.printStackTrace();
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }finally {
            return vo;
        }
    }

    @Override
    public ResultVo insert(Object o) throws Exception {
        ResultVo vo = new ResultVo();
        int result = -1;//
        try{
            if (o instanceof HsHousingOrderPaymentOnlineSerial) {
                result = hsHousingOrderPaymentOnlineSerialDao.insert((HsHousingOrderPaymentOnlineSerial) o);
            } else if (o instanceof HsHousingOrderPaymentRecord) {
                result = hsHousingOrderPaymentRecordDao.insert((HsHousingOrderPaymentRecord) o);
            }
            if(result>0){//操作成功
                vo.setDataSet(o);//数据需要回显时才使用
                vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
                vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            }else{
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
        }catch (Exception e){
            e.printStackTrace();
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }finally {
            return vo;
        }
    }

    @Override
    public ResultVo select(Integer id, Object o) throws Exception {
        ResultVo vo = new ResultVo();
        Object obj = null;
        try{
            if (o instanceof HsHousingOrderPaymentOnlineSerial) {
                obj = hsHousingOrderPaymentOnlineSerialDao.selectByPrimaryKey(id);
            } else if (o instanceof HsHousingOrderPaymentRecord) {
                obj = hsHousingOrderPaymentRecordDao.selectByPrimaryKey(id);
            }
            vo.setDataSet(obj);
            vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
            vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
        }catch (Exception e){
            e.printStackTrace();
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }finally {
            return vo;
        }
    }

    /**
     * 查询列表数据
     * @param o 查询的实体，用于控制查询的dao
     * @param condition 查询参数
     * @param returnType 返回值类型，0 List<Map>  1 list<Entity>
     * @return
     * @throws Exception
     */
    @Override
    public ResultVo selectList(Object o, Map<Object, Object> condition, int returnType) throws Exception {
        ResultVo vo = new ResultVo();
        List<Object> data = null;
        Map<Object,Object> result = new HashMap<Object,Object>();
        try{
//            if (o instanceof HsSystemOrderPool) {//
//                result = hsSystemOrderPoolDao.selectListByCondition(condition,returnType);
//            } else if (o instanceof HsSystemOrderPoolLog) {
//                result = hsSystemOrderPoolLogDao.selectListByCondition(condition,returnType);
//            }
            vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
            vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            vo.setDataSet(result.get("data"));
            vo.setPageInfo(result.get("pageInfo"));
        }catch (Exception e){
            e.printStackTrace();
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }finally {
            return vo;
        }
    }

    @Override
    public ResultVo update(Object o) throws Exception {
        ResultVo vo = new ResultVo();
        int result = -1;//
        try{
            if (o instanceof HsHousingOrderPaymentOnlineSerial) {
                result = hsHousingOrderPaymentOnlineSerialDao.updateByPrimaryKeySelective((HsHousingOrderPaymentOnlineSerial) o);
            } else if (o instanceof HsHousingOrderPaymentRecord) {
                result = hsHousingOrderPaymentRecordDao.updateByPrimaryKeySelective((HsHousingOrderPaymentRecord) o);
            }
            if(result>0){//操作成功
                vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
                vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            }else{
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
        }catch (Exception e){
            e.printStackTrace();
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }finally {
            return vo;
        }
    }

    @Override
    public ResultVo batchUpdate(Object o, Map<Object, Object> condition) throws Exception {
        ResultVo vo = new ResultVo();
        int result = -1;//
        try{
            if(result>0){//操作成功
                vo.setResult(ResultConstant.SYS_REQUIRED_SUCCESS);
                vo.setMessage(ResultConstant.SYS_REQUIRED_SUCCESS_VALUE);
            }else{
                vo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                vo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
        }catch (Exception e){
            e.printStackTrace();
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }finally {
            return vo;
        }
    }
}
