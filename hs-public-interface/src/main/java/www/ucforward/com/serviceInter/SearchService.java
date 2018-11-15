package www.ucforward.com.serviceInter;

import www.ucforward.com.index.entity.HouseSearchCondition;
import www.ucforward.com.index.form.MapSearch;
import www.ucforward.com.vo.ResultVo;

import java.util.List;

/**
 * 检索接口
 * @author wenbn
 * @version 1.0
 * @date 2018/6/25
 */
public interface SearchService {

    /**
     * 索引目标房源
     * @param houseId
     */
    ResultVo index(int houseId);

    /**
     * 移除房源索引
     * @param houseId
     */
    ResultVo remove(int houseId);

    /**
     * 获取补全建议关键词
     */
    ResultVo suggest(String prefix);

    /**
     * 查询索引
     */
    ResultVo query(HouseSearchCondition condition);


    /**
     * 聚合城市数据
     * @param cityNameCn
     * @return
     */
    ResultVo aggregateCity(String cityNameCn) throws Exception;


    /**
     * 聚合子社区下的房源数据
     * @param cityName
     * @param communityName
     * @param subCommunityName
     * @return
     * @throws Exception
     */
    ResultVo aggregateDistrictHouse(String cityName, String communityName, String subCommunityName) throws Exception;


    /**
     * 城市级别查询
     * @return
     */
    ResultVo mapQuery(String cityEnName, String orderBy,String orderDirection, int pageIndex, int pageSize);

    /**
     * 精确范围数据查询
     * @param mapSearch
     * @return
     */
    ResultVo mapQuery(MapSearch mapSearch);

}
