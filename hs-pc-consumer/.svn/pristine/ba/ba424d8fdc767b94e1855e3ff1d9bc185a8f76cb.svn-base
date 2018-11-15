package www.ucforward.com.controller.base;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.utils.StringUtil;
import www.ucforward.com.constants.AppRquestParamsConstant;
import www.ucforward.com.entity.HsGoldRule;
import www.ucforward.com.manager.ICommonManager;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 积分计算规则
 * @author wenbn
 * @version 1.0
 * @date 2018/9/6
 */
@Controller
@ResponseBody
public class GoldRuleController {

    // 日志记录器
    private static Logger logger = LoggerFactory.getLogger(GoldRuleController.class);
    @Resource
    private ICommonManager commonManager;

    /**
     * 获取积分计算列表
     * @return
     */
    @RequestMapping(value="/gold/rule/list",method = RequestMethod.POST)
    public String getGoldRuleList(HttpServletRequest request) throws Exception {
        ResultVo vo = null;
        int pageIndex = StringUtil.getAsInt(request.getParameter("pageIndex"),0); //当前页
        Map<Object,Object> condition = Maps.newHashMap();
        condition.put("pageIndex",pageIndex);
        condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE); //页显示条数
        vo = commonManager.getGoldRuleList(condition);
        return JsonUtil.toJson(vo);
    }

    /**
     * 获取积分计算详情
     * @param id 积分ID
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/gold/rule/detail/{id}",method = RequestMethod.POST)
    public String getGoldRuleDetail(@PathVariable Integer id) throws Exception {
        ResultVo vo = commonManager.getGoldRuleDetail(id);
        return JsonUtil.toJson(vo);
    }

    /**
     * 新增积分计算详情
     * @return
     */
    @RequestMapping(value="/gold/rule/add",method = RequestMethod.POST)
    public String addGoldRule(HsGoldRule hsGoldRule) throws Exception {
        ResultVo vo = commonManager.addGoldRule(hsGoldRule);
        return JsonUtil.toJson(vo);
    }

    /**
     * 修改积分计算详情
     * @return
     */
    @RequestMapping(value="/gold/rule/update",method = RequestMethod.POST)
    public String updateGoldRule(HsGoldRule hsGoldRule) throws Exception {
        ResultVo vo = commonManager.updateGoldRule(hsGoldRule);
        return JsonUtil.toJson(vo);
    }

}
