package www.ucforward.com.controller;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.utils.StringUtil;
import www.ucforward.com.annotation.NoRequireLogin;
import www.ucforward.com.constants.AppRquestParamsConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.entity.*;
import www.ucforward.com.manager.HousesManager;
import www.ucforward.com.umeng.util.UmengUtil;
import www.ucforward.com.utils.JsonUtil;
import www.ucforward.com.utils.RequestUtil;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


@Controller
@RequestMapping("houses")
public class HousesController {

    private static Logger logger = LoggerFactory.getLogger(HousesController.class); // 日志记录


    @Resource
    private HousesManager housesManager;

    /**
     * 业主提交预约获取房源申请
     *
     * @param request
     * @param response
     * @param ownerHousingApplication
     * @return
     */
    @RequestMapping(value = "addOwnerHousingApply", method = RequestMethod.POST)
    @ResponseBody
    public String addOwnerHousingApply(HttpServletRequest request, HttpServletResponse response, HsOwnerHousingApplication ownerHousingApplication, HsHouseCredentialsData hsHouseCredentialsData) throws IllegalAccessException {
        ResultVo result = null;
        try {
            String token = StringUtil.trim(request.getParameter("token")); //token
            String memberId = RequestUtil.analysisToken(token).getUserId(); //当前登录用户ID
            Object requestObj = RequestUtil.handleRequestBeanData(ownerHousingApplication);
            HsOwnerHousingApplication apply = (HsOwnerHousingApplication) requestObj;

            Object credentialsObj = RequestUtil.handleRequestBeanData(hsHouseCredentialsData);
            HsHouseCredentialsData houseCredentialsData = (HsHouseCredentialsData) credentialsObj;

            apply.setMemberId(Integer.parseInt(memberId));
            apply.setCreateBy(Integer.parseInt(memberId));
            result = housesManager.addOwnerHousingApply(apply, houseCredentialsData);
        } catch (Exception e) {
            logger.error("HousesController controller: addOwnerHousingApply Exception message:" + e.getMessage());
            System.out.println(e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 查询房源数据字典数据(业主申请页面)
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getHouseDictcode", method = RequestMethod.POST)
    @ResponseBody
    @NoRequireLogin
    public String getHouseDictcode(HttpServletRequest request, HttpServletResponse response) {
        ResultVo result = null;
        try {
            result = housesManager.getHouseDictcode();
        } catch (Exception e) {
            logger.error("HousesController controller: getHouseDictcode Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 查询租客/买家房源列表信息
     *
     * @param request
     * @param response
     * @param mainHouse 房源主信息實體對象
     * @return
     */
    @RequestMapping(value = "getRenterOrBuyersHousing", method = RequestMethod.POST)
    @ResponseBody
    @NoRequireLogin
    public String getRenterOrBuyersHousing(HttpServletRequest request, HttpServletResponse response, HsMainHouse mainHouse) {
        ResultVo vo = new ResultVo();
        Map<Object, Object> condition = new HashMap<Object, Object>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String leaseType = StringUtil.trim(map.get("leaseType")); //房源列表类型（0：租客，1：买家）
            if (!StringUtil.hasText(leaseType)) {
                vo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                vo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(vo);
            }
            String pageIndex = StringUtil.trim(map.get("pageIndex"), "1");//当前页
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion"), "0"));//语言版本
            int isCheck = StringUtil.getAsInt(StringUtil.trim(map.get("isCheck"), "1"));//是否审核
            condition.putAll(map);
            condition.put("pageIndex", pageIndex);//当前页
            condition.put("isCheck", isCheck);//是否审核 1审核通过
            condition.put("houseStatus", 1); //房源状态 1：审核通过
            condition.put("isDel", 0); //是否删除 0：未删除 1：已删除
            condition.put("isLock", 0);//未锁定
            condition.put("languageVersion", languageVersion);//当前页
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE);//每页显示条数

            vo = housesManager.getRenterOrBuyersHousing(condition);
        } catch (Exception e) {
            logger.error("HousesController controller: getRenterOrBuyersHousing Exception message:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 查询今日可看房源
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getTodayLookHouseList", method = RequestMethod.POST)
    @ResponseBody
    @NoRequireLogin
    public String getTodayLookHouseList(HttpServletRequest request, HttpServletResponse response) {
        ResultVo result = new ResultVo();
        Map<Object, Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String leaseType = StringUtil.trim(map.get("leaseType")); //房源类型（0：出租，1：出售）
            if (!StringUtil.hasText(leaseType)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            String pageIndex = StringUtil.trim(map.get("pageIndex"), "1"); //当前页
            //int languageVersion = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion"), "0")); //语言版本
            int isCheck = StringUtil.getAsInt(StringUtil.trim(map.get("isCheck"), "1")); //是否审核
            condition.putAll(map);
            condition.put("pageIndex", pageIndex);
            condition.put("isCheck", isCheck);//是否审核 1：审核通过
//            condition.put("languageVersion", languageVersion);//当前页
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE);//每页显示条数

            result = housesManager.getTodayLookHouseList(condition);
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return JsonUtil.toJson(result);
    }


    /**
     * 查询房源详情信息
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getHouseDetail", method = RequestMethod.POST)
    @ResponseBody
    @NoRequireLogin
    public String getHouseDetail(HttpServletRequest request, HttpServletResponse response) {
        ResultVo vo = new ResultVo();
        Map<Object, Object> condition = new HashMap<Object, Object>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String id = StringUtil.trim(map.get("id"));
            if (!StringUtil.hasText(id)) {
                vo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                vo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(vo);
            }
            condition.putAll(map);
            condition.put("channelAliasName", "hs_app_house_detail_page"); //获取房源详情
            condition.put("channelType", 3); //频道类型（1：PC,2:触屏，3：APP）
            condition.put("channelState", 1); //频道状态（0：不启用，1：启用）
            vo = housesManager.getHouseDetail(condition);
        } catch (Exception e) {
            logger.error("HousesController controller: getHouseDetail Exception message:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 获取房源图片信息
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getHousesImg", method = RequestMethod.POST)
    @ResponseBody
    @NoRequireLogin
    public String getHousesImg(HttpServletRequest request, HttpServletResponse response) {
        ResultVo vo = new ResultVo();
        Map<Object, Object> condition = new HashMap<Object, Object>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String houseId = StringUtil.trim(map.get("houseId")); //房源ID
            if (!StringUtil.hasText(houseId)) {
                vo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                vo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(vo);
            }
            condition.putAll(map);
            vo = housesManager.getHousesImg(condition);
        } catch (Exception e) {
            logger.error("HousesController controller:getHousesImg Exception message:" + e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 获取房源对比信息
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getHousingCompare", method = RequestMethod.POST)
    @ResponseBody
    @NoRequireLogin
    public String getHousingCompare(HttpServletRequest request, HttpServletResponse response) {
        ResultVo result = new ResultVo();
        Map<Object, Object> condition = new HashMap<Object, Object>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String houseId1 = StringUtil.trim(map.get("houseId1")); //房源1ID
            String houseId2 = StringUtil.trim(map.get("houseId2")); //房源2ID
            String token = StringUtil.trim(map.get("token")); //token
            String memberId = RequestUtil.analysisToken(token).getUserId(); //当前登录用户ID
            if (!StringUtil.hasText(houseId1) || !StringUtil.hasText(houseId2) || !StringUtil.hasText(memberId)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            condition.put("houseId1", houseId1);
            condition.put("houseId2", houseId2);
            result = housesManager.getHousingCompare(condition);
        } catch (Exception e) {
            logger.error("HousesController getHousingCompare Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 新增房源投诉
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "addHousingComplain", method = RequestMethod.POST)
    @ResponseBody
    public String addHousingComplain(HttpServletRequest request, HttpServletResponse response, HsHouseComplain hsHouseComplain) {
        ResultVo result = new ResultVo();
        try {
            Object requestObj = RequestUtil.handleRequestBeanData(hsHouseComplain);
            HsHouseComplain houseComplain = (HsHouseComplain) requestObj;
            String houseId = houseComplain.getHouseId() == null ? "" : houseComplain.getHouseId().toString();
            String complainReason = houseComplain.getComplainReason() == null ? "" : houseComplain.getComplainReason().toString();
            String token = StringUtil.trim(request.getParameter("token")); //token
            String memberId = RequestUtil.analysisToken(token).getUserId(); //当前登录用户ID
            //用户手机号
            String mobile = RequestUtil.analysisToken(token).getMoble();
            // String memberId = hsHouseComplain.getCreateBy().toString();

            if (!StringUtil.hasText(houseId) || !StringUtil.hasText(complainReason) || !StringUtil.hasText(memberId)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            int complainType = houseComplain.getComplainType() == null ? 0 : houseComplain.getComplainType();
            houseComplain.setComplainCode(UUID.randomUUID().toString());
            //投诉来源 (1外部 2外获 3外看 4区域长)
            houseComplain.setPlatform(1);
            houseComplain.setComplainType(complainType); //投诉类型
            houseComplain.setCreateBy(Integer.parseInt(memberId));
            houseComplain.setMobile(mobile);
            houseComplain.setCreateTime(new Date());
            result = housesManager.addHousingRelationInfo(houseComplain);
        } catch (Exception e) {
            logger.error("HousesController addHousingComplain Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 创建订单
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/order/create", method = RequestMethod.POST)
    @ResponseBody
    public String createOrder(HttpServletRequest request) {
        ResultVo vo = null;
        try {
            Map map = RequestUtil.getParameterMap(request);
            int bargainId = StringUtil.getAsInt(StringUtil.trim(map.get("bargainId")), -1);//议价ID
            int houseId = StringUtil.getAsInt(StringUtil.trim(map.get("houseId")), -1);//房源ID
            String token = StringUtil.trim(request.getParameter("token"));
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(request.getParameter("languageVersion")), 0);
            String memberId = RequestUtil.analysisToken(token).getUserId(); //会员ID
            if (houseId == -1 || bargainId == -1) {
                vo = ResultVo.error(
                        ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,
                        ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE
                );
                return JsonUtil.toJson(vo);
            }
            Map<String, Object> condition = new HashMap<>();
            condition.put("houseId", houseId);
            condition.put("memberId", StringUtil.getAsInt(memberId));
            condition.put("bargainId", 1);
            condition.put("languageVersion", languageVersion);
            vo = housesManager.createOrder(condition);
        } catch (Exception e) {
            logger.error("HousesController controller: createOrder Exception message:" + e.getMessage());
            vo = ResultVo.error(
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE
            );
        }
        return JsonUtil.toJson(vo);
    }

    /**
     * 获取进度流程
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "findProgressList", method = RequestMethod.POST)
    @ResponseBody
    public String findProgressList(HttpServletRequest request) {
        ResultVo result = new ResultVo();
        try {
            Map map = RequestUtil.getParameterMap(request);
            //人员类型 10业主(出租)；11业主(出售)；2 租客 3 买家
            String type = StringUtil.trim(map.get("type"));
            if (!StringUtil.hasText(type)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            List<Map<String, Object>> progressList = housesManager.findProgressList(type);
            result.setDataSet(progressList);
        } catch (Exception e) {
            logger.error("HousesController findProgressList Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取房源状态详细信息
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getHousingStatusDetail", method = RequestMethod.POST)
    @ResponseBody
    // @NoRequireLogin
    public String getHousingStatusDetail(HttpServletRequest request, HttpServletResponse response) {
        ResultVo result = new ResultVo();
        Map<Object, Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String id = StringUtil.trim(map.get("id")); //房源ID
            String token = StringUtil.trim(map.get("token"));
            String applyId = StringUtil.trim(map.get("applyId")); //申请ID
            String memberId = RequestUtil.analysisToken(token).getUserId(); //获取当前登录用户ID

            if (!StringUtil.hasText(id) || !StringUtil.hasText(memberId)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            condition.put("id", id);
            condition.put("applyId", applyId);
            result = housesManager.getHousingStatusInfo(condition);
        } catch (Exception e) {
            logger.error("HousesController getHousingStatusDetail Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }

        return JsonUtil.toJson(result);
    }

    /**
     * 获取自动应答设置信息
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getAutoReplySetting", method = RequestMethod.POST)
    @ResponseBody
    public String getAutoReplySetting(HttpServletRequest request, HttpServletResponse response) {
        ResultVo result = new ResultVo();
        Map<Object, Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String houseId = StringUtil.trim(map.get("houseId")); //房源ID
            if (!StringUtil.hasText(houseId)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            condition.putAll(map);

            result = housesManager.getAutoReplySetting(condition);
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }


    /**
     * 新增自动应答设置数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "addAutoReplySetting", method = RequestMethod.POST)
    @ResponseBody
    public String addAutoReplySetting(HttpServletRequest request, HttpServletResponse response, HsHouseAutoReplySetting hsHouseAutoReplySetting) {
        ResultVo result = new ResultVo();
        try {
            Object beanData = RequestUtil.handleRequestBeanData(hsHouseAutoReplySetting);
            HsHouseAutoReplySetting autoReplySetting = (HsHouseAutoReplySetting) beanData;
            //id为0时表示新增，将id置为null
            Integer id = autoReplySetting.getId() == null ? 0 : autoReplySetting.getId();
            if (id == 0) {
                autoReplySetting.setId(null);
            }
            //是否预审批
            Integer hasExpectApprove = autoReplySetting.getHasExpectApprove() == null ? -1 : autoReplySetting.getHasExpectApprove();
            //
            if (hasExpectApprove == -1) {
                autoReplySetting.setHasExpectApprove(null);
            }
            Integer houseId = autoReplySetting.getHouseId();
            if (houseId == null) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            result = housesManager.addAutoReplySetting(autoReplySetting);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return JsonUtil.toJson(result);
    }

    /**
     * 删除自动应答
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "deleteAutoReply", method = RequestMethod.POST)
    @ResponseBody
    public String deleteAutoReply(HttpServletRequest request) {
        ResultVo result = new ResultVo();
        try {
            Map map = RequestUtil.getParameterMap(request);
            //自动应答ID
            String autoReplyId = StringUtil.trim(map.get("autoReplyId"));
            if (!StringUtil.hasText(autoReplyId)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            result = housesManager.deleteAutoReply(autoReplyId);
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }


    /**
     * 检查房源钥匙是否过期，检测二维码是否被扫
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/check/key/isExpire")
    @ResponseBody
    public String checkKeyIsExpire(HttpServletRequest request) {
        ResultVo result = null;
        int houseId = StringUtil.getAsInt(request.getParameter("houseId"), -1);//房源ID
        String token = request.getParameter("token");
        String memberId = RequestUtil.analysisToken(token).getUserId(); //获取当前登录用户ID
        if (houseId == -1 || !StringUtil.hasText(memberId)) {
            return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR, ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
        }

        //业主获取房源钥匙
        result = housesManager.checkKeyIsExpire(houseId, memberId);
        return JsonUtil.toJson(result);
    }

    /**
     * 议价
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "bargain")
    @ResponseBody
    public String bargain(HttpServletRequest request) {
        ResultVo resultVo = new ResultVo();
        try {
            Map map = RequestUtil.getParameterMap(request);
            //房源id
            String houseId = StringUtil.trim(map.get("houseId"));
            //户主id
            String ownerId = StringUtil.trim(map.get("ownerId"));
            //客户id（租户或者买家id）
            String clientId = StringUtil.trim(map.get("clientId"));
            //非空验证
            if (!StringUtil.hasText(houseId) || !StringUtil.hasText(ownerId) || !StringUtil.hasText(clientId)) {
                resultVo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                resultVo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(resultVo);
            }
            String token = StringUtil.trim(request.getParameter("token"));
            //登陆用户ID
            String memberId = RequestUtil.analysisToken(token).getUserId();
            if(!clientId.equals(memberId)){
                //参数客户id与当前登陆用户id不符（议价只能是客户发起）
                resultVo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                resultVo.setMessage("参数客户id与当前登陆用户id不符");
                return JsonUtil.toJson(resultVo);
            }
            Integer languageVersion = StringUtil.getAsInt(StringUtil.trim(request.getParameter("languageVersion")), 0);
            /**
             * 组成规则
             * 房屋id_户主id_客户id
             */
            StringBuffer groupName = new StringBuffer();
            groupName.append(houseId)
                    .append("_")
                    .append(ownerId)
                    .append("_")
                    .append(clientId);
            //群组信息
            resultVo= housesManager.bargain(groupName.toString(), houseId,languageVersion);
        } catch (Exception e) {
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }

        return JsonUtil.toJson(resultVo);

    }

    /**
     * 根据用户code获取该用户下所有聊天群组
     * 只返回议价中、或者预约看房中的聊天群
     * @param request
     * @return
     */
    @RequestMapping(value = "getGroupsByMemberCode")
    @ResponseBody
    public String getGroupsByMemberCode(HttpServletRequest request) {
        ResultVo resultVo = new ResultVo();
        try {
            Map parameterMap = RequestUtil.getParameterMap(request);
            //用户code
            String memberCode = StringUtil.trim(parameterMap.get("memberCode"));
            if (!StringUtil.hasText(memberCode)) {
                resultVo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                resultVo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(resultVo);
            }
            //当前用户所在群组列表
            List<Map> groupsByMemberCode = housesManager.getGroupsByMemberCode(memberCode);
            if(groupsByMemberCode.size() < 1){
                resultVo.setDataSet(new ArrayList<>());
                return JsonUtil.toJson(resultVo);
            }
            //修改返回参数名。默认返回参数名为groupid、groupname修改为驼峰命名groupId、groupName
            List<Map> groups = new ArrayList<>();
            List<String> houseIds = new ArrayList<>();
            List<String> groupIds = new ArrayList<>();
            for (Map map : groupsByMemberCode) {
                Map groupMap = new HashMap(2);
                String groupName = StringUtil.trim(map.get("groupname"));
                String groupId = StringUtil.trim(map.get("groupid"));
                //根据群名称获取户主及客户id
                String[] split = groupName.split("_");
                if (split.length < 2) {
                    resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                    resultVo.setMessage("环信群组id错误");
                    return JsonUtil.toJson(resultVo);
                }
                String houseId = split[0];
                houseIds.add(houseId);
                groupIds.add(groupId);
                groupMap.put("groupId", groupId);
                groupMap.put("groupName", groupName);
                groups.add(groupMap);
            }
            //获取议价信息
            Map<Object,Object> condition = new HashMap<>(16);
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID id");
            queryColumn.add("BARGAIN_STATUS bargainStatus");
            queryColumn.add("GROUP_ID groupId");
            condition.put("queryColumn",queryColumn);
            condition.put("groupIds",groupIds);
            condition.put("isDel",0);
//            condition.put("bargainStatus",0);
            ResultVo bargainList = housesManager.getBargainList(condition);
            if(bargainList == null || bargainList.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                resultVo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return JsonUtil.toJson(resultVo);
            }
            List<Map<Object, Object>> housingBargainList = (List<Map<Object, Object>>) bargainList.getDataSet();
            //获取预约看房聊天记录
            condition.clear();
            condition.put("groupIds",groupIds);
            //按照groupId分组
//            condition.put("groupBy","GROUP_ID");
            ResultVo reservationMessageResultVo = housesManager.getReservationMessageList(condition);
            if(reservationMessageResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                resultVo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return JsonUtil.toJson(resultVo);
            }
            List<Map<Object, Object>> reservationMessageList = (List<Map<Object, Object>>) reservationMessageResultVo.getDataSet();
            //获取预约看房信息
            condition.clear();
            condition.put("groupIds",groupIds);
            condition.put("isDel",0);
            ResultVo reservationResultVo = housesManager.getReservationList(condition);
            if(reservationResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                resultVo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return JsonUtil.toJson(resultVo);
            }
            List<Map<Object, Object>> reservationList = (List<Map<Object, Object>>) reservationResultVo.getDataSet();
            //获取房源信息
            List<Map> resultList = new ArrayList<>();
            List<Map<Object, Object>> mainHouseList = housesManager.getMainHouseList(houseIds);
            groups.forEach(map -> {
                //Map<String,Object> resultMap = new HashMap<>(16);
                String groupName = map.get("groupName").toString();
                String groupId = map.get("groupId").toString();
                String[] split = groupName.split("_");
                String houseId = split[0];
                Map<Object, Object> mainHouseMap = null;
                for (Map<Object, Object> mainHouse : mainHouseList) {
                    String id = mainHouse.get("id").toString();
                    if(houseId.equals(id)){
                        mainHouseMap = mainHouse;
                        break;
                    }
                }
                map.put("mainHouse", mainHouseMap);
                //议价id
                Integer bargainId = -1;
                //是否议价中
                boolean isNegotiating = false;
                for (Map<Object, Object> bargainMap : housingBargainList) {
                    String id = StringUtil.trim(bargainMap.get("id"));
                    //议价状态（0 议价中 1 议价成功 2 议价失败）
                    String bargainStatus = StringUtil.trim(bargainMap.get("bargainStatus"));
                    String groupId1 = StringUtil.trim(bargainMap.get("groupId"));
                    if(groupId.equals(groupId1)){
                        bargainId = StringUtil.getAsInt(id);
                        if("0".equals(bargainStatus)){
                            isNegotiating = true;
                        }
                        break;
                    }
                }
                map.put("bargainId",bargainId);

                //大于0表示有聊天记录 说明该groupId对应的聊天群正在预约中或者聊天已经完成
                long groupIdCount = reservationMessageList.stream().filter(reservation -> groupId.equals(reservation.get("groupId"))).count();
                //小于1表示预约已经完成 说明该groupId对应的聊天群 预约看房完成
                long successCount = reservationMessageList.stream().filter(reservation ->
                        ("1".equals(StringUtil.trim(reservation.get("operateStatus"))) || "2".equals(StringUtil.trim(reservation.get("operateStatus"))))).count();
                //正在预约看房中
                boolean inChat = (groupIdCount > 0 && successCount < 1);
                //正在议价中，或者正在预约看房中才加入返回结果集
                if(isNegotiating || inChat){
                    //根据groupId获取预约消息，如果能获取到说明预约已经完成
                    resultList.add(map);
                }
            });
            resultVo.setDataSet(resultList);
        } catch (Exception e) {
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(resultVo);
    }


    /**
     * 查询业主设置的预约看房时间
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getHouseSettingTime", method = RequestMethod.POST)
    @ResponseBody
    public String getHouseSettingTime(HttpServletRequest request, HttpServletResponse response) {
        ResultVo result = new ResultVo();
        Map<Object, Object> condition = new HashMap<>();

        try {
            Map map = RequestUtil.getParameterMap(request);
            String houseId = StringUtil.trim(map.get("houseId")); //房源ID
            if (!StringUtil.hasText(houseId)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            condition.put("houseId", houseId);
            result = housesManager.getHouseSettingTime(condition);
        } catch (Exception e) {
            logger.error("HousesController getHouseSettingTime Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 新增人员申购信息
     * @param request
     * @param buildingMemberApply
     * @return
     */
    @RequestMapping(value="/addPurchase", method = RequestMethod.POST)
    @ResponseBody
    public String addPurchase(HttpServletRequest request, HsHouseNewBuildingMemberApply buildingMemberApply){
        ResultVo result = new ResultVo();
        try{
            String token = request.getParameter("token"); //登录token
            int memberId = StringUtil.getAsInt(RequestUtil.analysisToken(token).getUserId());
            buildingMemberApply.setMemberId(memberId);
            buildingMemberApply.setCreateBy(memberId);
            result = housesManager.addPurchase(buildingMemberApply);
        }catch (Exception e){
            logger.error("HousesController addPurchase Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 新增直售楼盘信息
     * @param request
     * @param hsHouseNewBuilding
     * @return
     */
    @RequestMapping(value="/addDirectSalesProperty", method = RequestMethod.POST)
    @ResponseBody
    public String addDirectSalesProperty(HttpServletRequest request, HsHouseNewBuilding hsHouseNewBuilding){
        ResultVo result = new ResultVo();
        try{
            String token = request.getParameter("token"); //登录token
            int memberId = StringUtil.getAsInt(RequestUtil.analysisToken(token).getUserId());
            hsHouseNewBuilding.setProjectCode(UUID.randomUUID().toString());
            hsHouseNewBuilding.setCreateBy(memberId);
            result = housesManager.addDirectSalesProperty(hsHouseNewBuilding);
        }catch (Exception e){
            logger.error("HousesController addDirectSalesProperty Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }


    /**
     * 获取楼盘区域(社区)
     * @return
     */
    @RequestMapping(value="/getPropertyArea", method = RequestMethod.POST)
    @ResponseBody
    public String getPropertyArea(){
        ResultVo result = new ResultVo();
        try{
            result = housesManager.getPropertyArea();
        }catch (Exception e){
            logger.error("HousesController getPropertyArea Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取开发商列表
     * @return
     */
    @RequestMapping(value="/getDevelopers", method = RequestMethod.POST)
    @ResponseBody
    public String getDevelopers(){
        ResultVo result = new ResultVo();
        try{
            result = housesManager.getDevelopers();
        }catch (Exception e){
            logger.error("HousesController getDevelopers Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取直售楼盘信息
     * @param request
     * @return
     */
    @RequestMapping(value="/getDirectSalesDetails", method = RequestMethod.POST)
    @ResponseBody
    public String getDirectSalesDetails(HttpServletRequest request){
        ResultVo result = new ResultVo();
        try{
            String id = request.getParameter("id");
            String token = request.getParameter("token");
            int memberId = StringUtil.getAsInt(RequestUtil.analysisToken(token).getUserId());
            result = housesManager.getDirectSalesDetails(StringUtil.getAsInt(id),memberId);
        }catch (Exception e){
            logger.error("HousesController getDirectSalesDetails Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取直售楼盘列表
     * @param request
     * @return
     */
    @RequestMapping(value="/getDirectSalesPropertyList", method = RequestMethod.POST)
    @ResponseBody
    public String getDirectSalesPropertyList(HttpServletRequest request){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>(16);
        try{
            Map map = RequestUtil.getParameterMap(request);
            //当前页
            int pageIndex = StringUtil.getAsInt(request.getParameter("pageIndex"), 1);
            //开发商
            String developers = StringUtil.trim(map.get("developers"));
            if(StringUtil.hasText(developers)){
                condition.put("developers",developers);
            }
            //区域(社区)
            String community = StringUtil.trim(request.getParameter("area"));
            if(StringUtil.hasText(community)){
                condition.put("community",community);
            }
            condition.put("pageIndex", pageIndex);
            //页显示条数
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE);
            condition.put("isDel",0);
            result = housesManager.getDirectSalesPropertyList(condition);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("HousesController getDirectSalesPropertyList Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

}