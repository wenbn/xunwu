package www.ucforward.com.controller;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.utils.StringUtil;
import www.ucforward.com.annotation.NoRequireLogin;
import www.ucforward.com.constants.AppRquestParamsConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.entity.HsHouseComplain;
import www.ucforward.com.entity.HsHouseCredentialsData;
import www.ucforward.com.entity.HsMainHouse;
import www.ucforward.com.entity.HsOwnerHousingApplication;
import www.ucforward.com.index.entity.HouseSearchCondition;
import www.ucforward.com.index.form.MapSearch;
import www.ucforward.com.manager.CommonManager;
import www.ucforward.com.manager.HousesManager;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.utils.RequestUtil;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@ResponseBody
public class SearchController {

    private static Logger logger = LoggerFactory.getLogger(SearchController.class); // 日志记录

    @Resource
    private HousesManager housesManager;
    @Resource
    private CommonManager commonManager;


    /**
     * 自动补全接口
     */
    @GetMapping("rent/house/autocomplete")
    @NoRequireLogin
    public String autocomplete(@RequestParam(value = "prefix") String prefix) {
        ResultVo result = new ResultVo();
        try {
            if(StringUtil.hasText(prefix)){
                result = housesManager.suggest(prefix);
            }
        } catch (Exception e) {
            logger.error("HousesController getHouseSettingTime Exception message:"+e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 搜索房源
     * @return
     */
    @RequestMapping(value = "/search/house",method = RequestMethod.POST)
    @NoRequireLogin
    public String searchHouse(HouseSearchCondition condition) throws Exception{
        ResultVo result = new ResultVo();
        try {
            //搜索房源
            String city = condition.getCity();
            if(!StringUtil.hasText(city)){
                condition.setCity("深圳市");
            }
            result = housesManager.searchHouse(condition);
        } catch (Exception e) {
            logger.error("HousesController searchHouse Exception message:"+e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }


    /**
     * 地图选房入口
     * @param cityNameCn 城市名
     * @return
     */
    @RequestMapping(value = "rent/house/map",method = RequestMethod.POST)
    @NoRequireLogin
    public String rentHouseMap(@RequestParam(value = "cityNameCn") String cityNameCn) {
        ResultVo result = null;
        try {
            //地图选房入口
            if(!StringUtil.hasText(cityNameCn)){
                return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,"请选择城市信息"));
            }
            Map<Object,Object> condition = Maps.newHashMap();
            condition.put("cityNameCn",cityNameCn);
            result = housesManager.rentHouseMap(condition);
        } catch (Exception e) {
            logger.error("HousesController rentHouseMap Exception message:"+e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);

    }


    /**
     * 地图选房返回房源数据
     * @return
     */
    @GetMapping("rent/house/map/houses")
    @NoRequireLogin
    public String rentMapHouses(@ModelAttribute MapSearch mapSearch) {
        ResultVo result = null;
        try {
            //地图选房入口
            if(!StringUtil.hasText(mapSearch.getCityEnName())){
                return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,"请选择城市信息"));
            }

            result = housesManager.rentMapHouses(mapSearch);
        } catch (Exception e) {
            logger.error("HousesController rentHouseMap Exception message:"+e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);

    }

}
