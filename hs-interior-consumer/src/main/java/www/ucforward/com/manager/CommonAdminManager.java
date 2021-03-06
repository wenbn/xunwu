package www.ucforward.com.manager;

import org.springframework.stereotype.Service;
import www.ucforward.com.vo.ResultVo;

import java.util.Map;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/8/27
 */
@Service
public interface CommonAdminManager {

    /**
     * 获取支持的城市信息
     * @return
     */
    ResultVo getSupportCities(Map<Object, Object> condition);

    /**
     * 获取地址编码
     * @return
     * @throws Exception
     */
    ResultVo loadCityCode(Map<Object,Object> condition);


}
