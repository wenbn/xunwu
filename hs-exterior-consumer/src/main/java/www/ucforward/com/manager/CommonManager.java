package www.ucforward.com.manager;

import org.springframework.stereotype.Service;
import www.ucforward.com.vo.ResultVo;

import java.util.Map;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/5/28
 */
@Service
public interface CommonManager {

    /**
     * 获取首页信息
     * @return
     */
    ResultVo getIndexDatas(Map<Object, Object> condition);

    /**
     * 猜你喜欢（推荐房源）
     * @param token
     * @return
     * @throws Exception
     */
    Map<String, Object> recommendHouses(String token) throws Exception;

    /**
     * 获取支持的城市信息
     * @return
     */
    ResultVo getSupportCities(Map<Object, Object> condition);


}
