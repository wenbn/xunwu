package www.ucforward.com.manager.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.utils.StringUtil;
import www.ucforward.com.constants.RedisConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.entity.*;
import www.ucforward.com.manager.ICommonManager;
import www.ucforward.com.serviceInter.CommonService;
import www.ucforward.com.serviceInter.HousesService;
import www.ucforward.com.utils.*;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/8/20
 */
@Service
public class ICommonManagerImpl implements ICommonManager {

    @Resource
    private CommonService commonService;
    @Resource
    private HousesService housesService;

    // 日志记录器
    private static Logger logger = LoggerFactory.getLogger(ICommonManagerImpl.class);

    /**
     * 获取频道列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getChannelList(Map<Object, Object> condition) {
        ResultVo vo = null;
        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        queryColumn.add("ID channelId");//主键ID
        queryColumn.add("LANGUAGE_VERSION languageVersion");//语言版本（0：中文，1：英文）默认为0
        queryColumn.add("CHANNEL_NAME channelName");//频道名称
        queryColumn.add("CHANNEL_ALIAS_NAME channelAliasName");//频道别名唯一
        queryColumn.add("CHANNEL_DESC channelDesc");//频道描述
        queryColumn.add("CHANNEL_TYPE channelType");//频道类型（1：PC网站，2：触屏端，3：App）
        queryColumn.add("CHANNEL_STATE channelState");//频道的状态（0：不启用，1：启用）
        queryColumn.add("CHANNEL_SORT channelSort");//排序
        queryColumn.add("IS_DEL isDel");//是否删除0:不删除，1：删除
        queryColumn.add("CREATE_TIME createTime");//创建时间
        condition.put("queryColumn",queryColumn);
        try {
            vo = commonService.selectCustomColumnNamesList(HsChannel.class,condition);
        } catch (Exception e) {
            logger.warn("Remote call to CommonService fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        queryColumn = null;
        condition = null;
        return vo;
    }

    /**
     * 获取展位列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getBoothList(Map<Object, Object> condition) {
        ResultVo vo = null;
        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        queryColumn.add("ID boothId");//主键ID
        queryColumn.add("LANGUAGE_VERSION languageVersion");//语言版本（0：中文，1：英文）默认为0
        queryColumn.add("BOOTH_NAME boothName");//展位的名称
        queryColumn.add("BOOTH_ALIAS_NAME boothAliasName");//展位别名
        queryColumn.add("BOOTH_DESC boothDesc");//展位描述
        queryColumn.add("BOOTH_TYPE boothType");//展位类型（1：广告，2：买家出租问题 ，3：买家出售问题，4：卖家出租问题 ，5：卖家出售问题，6：友情链接，7：出租，8：出售，9：新楼盘）
        queryColumn.add("BOOTH_STATE boothState");//展位状态（0：启用，1：禁用）
        queryColumn.add("BOOTH_SORT boothSort");//排序
        queryColumn.add("IS_DEL isDel");//是否删除0:不删除，1：删除
        queryColumn.add("DATA_SIZE dataSize");//该栏位限制的数据条数0标识不限制
        queryColumn.add("BOOTH_EXCLUSIVE boothExclusive");//该展位的专属(1:PC, 2:TC, 3:APP)
        queryColumn.add("CREATE_TIME createTime");//创建时间
        condition.put("queryColumn",queryColumn);
        try {
            vo = commonService.selectCustomColumnNamesList(HsBooth.class,condition);
        } catch (Exception e) {
            logger.warn("Remote call to getBoothList fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        queryColumn = null;
        condition = null;
        return vo;
    }

    /**
     * 获取该展位已经绑定的数据
     * @param boothId
     * @return
     */
    @Override
    public ResultVo getBoothBindingData(Integer boothId) {
        ResultVo vo = null;
        Map<Object,Object> condition = Maps.newHashMap();
        try {
            //查询展位是否存在
            HsBooth booth = null;
            vo = commonService.select(boothId, new HsBooth());
            if (vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                booth = (HsBooth) vo.getDataSet();
            }
            if(booth == null ){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"展位信息不存在");
            }
            Integer boothType = booth.getBoothType();//展位类型（1：广告，2：买家出租问题 ，3：买家出售问题，4：卖家出租问题 ，5：卖家出售问题，6：友情链接，7：出租，8：出售，9：新楼盘）
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            if(boothType == 1 ){//广告类型
                queryColumn.add("ID boothAdvertRelId");//广告类型数据与展位进行关联表ID
                queryColumn.add("ADVERT_ID advertId");//广告_ID
                queryColumn.add("BOOTH_ID boothId");//展位ID
                queryColumn.add("AD_SN adSn");//排序序号
                condition.put("queryColumn",queryColumn);
                condition.put("isDel",0);//未删除
                condition.put("boothId",boothId);//展位ID
                vo = commonService.selectCustomColumnNamesList(HsBoothAdvertRel.class,condition);
                if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                }
                List<Map<Object,Object>> hsBoothAdvertRels = (List<Map<Object, Object>>) vo.getDataSet();
                if(hsBoothAdvertRels==null || hsBoothAdvertRels.size()==0){//如果查询到的内容为空，则直接返回
                    return vo;
                }
                //保存广告ids
                List<Integer> advertIds = Lists.newArrayList();
                for (Map<Object, Object> hsBoothAdvertRel : hsBoothAdvertRels) {
                    advertIds.add(StringUtil.getAsInt(StringUtil.trim(hsBoothAdvertRel.get("advertId"))));
                }
                queryColumn.clear();
                condition.clear();
                queryColumn.add("ID advertId");//广告ID
                queryColumn.add("AD_FULL_TITLE adFullTitle");//广告长标题
                queryColumn.add("AD_SHORT_TITLE adShortTitle");//广告短标题
                queryColumn.add("AD_TYPE adType");//广告类型(1:图片类型, 2:文字类型)
                queryColumn.add("AD_IMG_URL adImgUrl");//广告图片上传名称(当ad_type=1有效)
                queryColumn.add("AD_CONTENT adContent");//广告文字内容(当ad_type=2有效)
                queryColumn.add("AD_DESC adDesc");//广告内容的备注描述
                queryColumn.add("AD_LINK adLink");//广告内容点击链接的URL地址
                condition.put("queryColumn",queryColumn);
                condition.put("isDel",0);//未删除
                condition.put("advertIds",advertIds);//展位IDs
                vo = commonService.selectCustomColumnNamesList(HsAdvertData.class,condition);

            }else {//文章类型
                queryColumn.add("ID boothArticleRelId");//广告类型数据与展位进行关联表ID
                queryColumn.add("ARTICLE_ID articleId");//文章ID
                queryColumn.add("BOOTH_ID boothId");//展位ID
                queryColumn.add("AD_SN adSn");//排序序号
                condition.put("queryColumn",queryColumn);
                condition.put("isDel",0);//未删除
                condition.put("boothId",boothId);//展位ID
                vo = commonService.selectCustomColumnNamesList(HsBoothArticleRel.class,condition);

                if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询失败
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                }
                List<Map<Object,Object>> hsBoothArticleRels = (List<Map<Object, Object>>) vo.getDataSet();
                if(hsBoothArticleRels==null || hsBoothArticleRels.size()==0){//如果查询到的内容为空，则直接返回
                    return vo;
                }
                //保存文章ids
                List<Integer> articleIds = Lists.newArrayList();
                for (Map<Object, Object> hsBoothArticleRel : hsBoothArticleRels) {
                    articleIds.add(StringUtil.getAsInt(StringUtil.trim(hsBoothArticleRel.get("articleId"))));
                }
                queryColumn.clear();
                condition.clear();
                queryColumn.add("ID articleId");//广告类型数据与展位进行关联表ID
                queryColumn.add("ARTICLE_NAME articleName");//文章名称
                queryColumn.add("ARTICLE_DESC articleDesc");//文章描述
                queryColumn.add("ARTICLE_TYPE articleType");//0 买家租房 1 买家买房 2 卖家出租 3 卖家出售
                queryColumn.add("SEO_TITLE seoTitle");//seo标题
                queryColumn.add("SEO_KEYWORDS seoKeywords");//seo关键字
                queryColumn.add("SEO_DESCRIPTION seoDescription");//seo描述
                condition.put("queryColumn",queryColumn);
                condition.put("isDel",0);//是否删除0:不删除，1：删除
                condition.put("articleIds",articleIds);//展位IDs
                vo = commonService.selectCustomColumnNamesList(HsArticleData.class,condition);
            }

        } catch (Exception e) {
            logger.warn("Remote call to getChannelBindData fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取该展位未绑定的数据
     * @param boothId
     * @return
     */
    @Override
    public ResultVo getBoothUnBindData(Integer boothId) {
        ResultVo vo = null;
        Map<Object,Object> condition = Maps.newHashMap();
        try {
            //查询展位是否存在
            HsBooth booth = null;
            vo = commonService.select(boothId, new HsBooth());
            if (vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                booth = (HsBooth) vo.getDataSet();
            }
            if(booth == null ){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"展位信息不存在");
            }
            Integer boothType = booth.getBoothType();//展位类型（1：广告，2：买家出租问题 ，3：买家出售问题，4：卖家出租问题 ，5：卖家出售问题，6：友情链接，7：出租，8：出售，9：新楼盘）
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            if(boothType == 1 ){//广告类型
                queryColumn.add("ID boothAdvertRelId");//广告类型数据与展位进行关联表ID
                queryColumn.add("ADVERT_ID advertId");//广告ID
                queryColumn.add("BOOTH_ID boothId");//展位ID
                queryColumn.add("AD_SN adSn");//排序序号
                condition.put("queryColumn",queryColumn);
                condition.put("isDel",0);//未删除
                condition.put("boothId",boothId);//展位ID
                vo = commonService.selectCustomColumnNamesList(HsBoothAdvertRel.class,condition);
                if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                }
                //获取已经绑定的数据
                List<Map<Object,Object>> hsBoothAdvertRels = (List<Map<Object, Object>>) vo.getDataSet();
                //保存广告ids
                List<Integer> advertIds = Lists.newArrayList();
                for (Map<Object, Object> hsBoothAdvertRel : hsBoothAdvertRels) {
                    advertIds.add(StringUtil.getAsInt(StringUtil.trim(hsBoothAdvertRel.get("advertId"))));
                }
                queryColumn.clear();
                condition.clear();
                queryColumn.add("ID advertId");//广告ID
                queryColumn.add("AD_FULL_TITLE adFullTitle");//广告长标题
                queryColumn.add("AD_SHORT_TITLE adShortTitle");//广告短标题
                queryColumn.add("AD_TYPE adType");//广告类型(1:图片类型, 2:文字类型)
                queryColumn.add("AD_IMG_URL adImgUrl");//广告图片上传名称(当ad_type=1有效)
                queryColumn.add("AD_CONTENT adContent");//广告文字内容(当ad_type=2有效)
                queryColumn.add("AD_DESC adDesc");//广告内容的备注描述
                queryColumn.add("AD_LINK adLink");//广告内容点击链接的URL地址
                condition.put("queryColumn",queryColumn);
                condition.put("isDel",0);//未删除
                if(advertIds!=null && advertIds.size()>0){
                    condition.put("notInAdvertIds",advertIds);//广告IDs
                }
                vo = commonService.selectCustomColumnNamesList(HsAdvertData.class,condition);

            }else {//文章类型
                queryColumn.add("ID boothArticleRelId");//广告类型数据与展位进行关联表ID
                queryColumn.add("ARTICLE_ID articleId");//文章ID
                queryColumn.add("BOOTH_ID boothId");//展位ID
                queryColumn.add("AD_SN adSn");//排序序号
                condition.put("queryColumn",queryColumn);
                condition.put("isDel",0);//未删除
                condition.put("boothId",boothId);//展位ID
                vo = commonService.selectCustomColumnNamesList(HsBoothArticleRel.class,condition);

                if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询失败
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                }
                List<Map<Object,Object>> hsBoothArticleRels = (List<Map<Object, Object>>) vo.getDataSet();

                //保存文章ids
                List<Integer> articleIds = Lists.newArrayList();
                for (Map<Object, Object> hsBoothArticleRel : hsBoothArticleRels) {
                    articleIds.add(StringUtil.getAsInt(StringUtil.trim(hsBoothArticleRel.get("articleId"))));
                }
                queryColumn.clear();
                condition.clear();
                queryColumn.add("ID articleId");//广告类型数据与展位进行关联表ID
                queryColumn.add("ARTICLE_NAME articleName");//文章名称
                queryColumn.add("ARTICLE_DESC articleDesc");//文章描述
                queryColumn.add("ARTICLE_TYPE articleType");//0 买家租房 1 买家买房 2 卖家出租 3 卖家出售
                queryColumn.add("SEO_TITLE seoTitle");//seo标题
                queryColumn.add("SEO_KEYWORDS seoKeywords");//seo关键字
                queryColumn.add("SEO_DESCRIPTION seoDescription");//seo描述
                condition.put("queryColumn",queryColumn);
                condition.put("isDel",0);//是否删除0:不删除，1：删除
                if(articleIds!=null && articleIds.size()>0){
                    condition.put("notInArticleIds",articleIds);//展位IDs
                }
                vo = commonService.selectCustomColumnNamesList(HsArticleData.class,condition);
            }
            //自定义查询列名
            condition =null;
            queryColumn =null;
        } catch (Exception e) {
            logger.warn("Remote call to getBoothUnBindData fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 展位绑定数据
     * @param boothId
     * @param dataList
     * @return
     */
    @Override
    public ResultVo bindingBoothData(Integer boothId, List<Integer> dataList) {
        ResultVo vo = null;
        Map<Object,Object> condition = Maps.newHashMap();
        try {

            //查询展位是否存在
            HsBooth booth = null;
            vo = commonService.select(boothId, new HsBooth());
            if (vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                booth = (HsBooth) vo.getDataSet();
            }
            if(booth == null ){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"展位信息不存在");
            }
            Integer boothType = booth.getBoothType();//展位类型（1：广告，2：买家出租问题 ，3：买家出售问题，4：卖家出租问题 ，5：卖家出售问题，6：友情链接，7：出租，8：出售，9：新楼盘）
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            if(boothType == 1 ) {//广告类型
                //查询该展位下已绑定的数据
                condition.put("isDel",0);//未删除
                condition.put("boothId",boothId);//展位ID
                queryColumn.add("ID boothAdvertRelId");//广告类型数据与展位进行关联表ID
                queryColumn.add("ADVERT_ID advertId");//广告ID
                queryColumn.add("BOOTH_ID boothId");//展位ID
                queryColumn.add("AD_SN adSn");//排序序号
                condition.put("queryColumn",queryColumn);
                vo = commonService.selectCustomColumnNamesList(HsBoothAdvertRel.class,condition);
                if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                    return vo;
                }
                List<Map<Object,Object>> bindingAdverts = (List<Map<Object, Object>>) vo.getDataSet();
                int maxSort = -1;
                if(bindingAdverts!=null && bindingAdverts.size()>0){
                    for (Map<Object, Object> advert : bindingAdverts) {//遍历已绑定的展位数,将已绑定的Id过滤掉
                        Integer advertId = StringUtil.getAsInt(StringUtil.trim(advert.get("advertId")));
                        int sort = StringUtil.getAsInt(StringUtil.trim(advert.get("adSn")));
                        if(sort>maxSort){//保存最大的排序数
                            maxSort = sort;
                        }
                        if(dataList.contains(advertId)){//如果包含已绑定的广告
                            dataList.remove(advertId);
                        }
                    }
                }

                //绑定过滤掉的展位数
                if(dataList==null || dataList.size()==0){//如果要绑定的展位都已存在,则直接提示绑定成功
                    return ResultVo.success();
                }
                int length = dataList.size();
                List<HsBoothAdvertRel> hsBoothAdvertRels = Lists.newArrayListWithCapacity(length);
                HsBoothAdvertRel hsBoothAdvertRel = null;
                Date date = new Date();
                for (int i =0 ;i < length ; i++) {
                    hsBoothAdvertRel = new HsBoothAdvertRel();
                    hsBoothAdvertRel.setAdvertId(dataList.get(i));
                    hsBoothAdvertRel.setBoothId(boothId);
                    hsBoothAdvertRel.setAdSn(maxSort+i+1);
                    hsBoothAdvertRel.setIsDel(0);
                    hsBoothAdvertRel.setCreateBy(-1);
                    hsBoothAdvertRel.setUpdateBy(-1);
                    hsBoothAdvertRel.setCreateTime(date);
                    hsBoothAdvertRel.setUpdateTime(date);
                    hsBoothAdvertRel.setBid(57);
                    hsBoothAdvertRels.add(hsBoothAdvertRel);
                }
                vo = commonService.batchInsert(HsBoothAdvertRel.class,hsBoothAdvertRels);
            }else{
                //查询该展位下已绑定的数据
                condition.put("isDel",0);//未删除
                condition.put("boothId",boothId);//展位ID
                queryColumn.add("ID boothArticleRelId");//广告类型数据与展位进行关联表ID
                queryColumn.add("ARTICLE_ID articleId");//广告ID
                queryColumn.add("BOOTH_ID boothId");//展位ID
                queryColumn.add("AD_SN adSn");//排序序号
                condition.put("queryColumn",queryColumn);
                vo = commonService.selectCustomColumnNamesList(HsBoothAdvertRel.class,condition);
                if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                    return vo;
                }
                List<Map<Object,Object>> bindingArticles = (List<Map<Object, Object>>) vo.getDataSet();
                int maxSort = -1;
                if(bindingArticles!=null && bindingArticles.size()>0){
                    for (Map<Object, Object> article : bindingArticles) {//遍历已绑定的展位数,将已绑定的Id过滤掉
                        Integer articleId = StringUtil.getAsInt(StringUtil.trim(article.get("articleId")));
                        int sort = StringUtil.getAsInt(StringUtil.trim(article.get("adSn")));
                        if(sort>maxSort){//保存最大的排序数
                            maxSort = sort;
                        }
                        if(dataList.contains(articleId)){//如果包含已绑定的文章
                            dataList.remove(articleId);
                        }
                    }
                }

                //绑定过滤掉的文章数据
                if(dataList==null || dataList.size()==0){//如果要绑定的展位都已存在,则直接提示绑定成功
                    return ResultVo.success();
                }
                int length = dataList.size();
                List<HsBoothArticleRel> hsBoothArticleRels = Lists.newArrayListWithCapacity(length);
                HsBoothArticleRel hsBoothArticleRel = null;
                Date date = new Date();
                for (int i =0 ;i < length ; i++) {
                    hsBoothArticleRel = new HsBoothArticleRel();
                    hsBoothArticleRel.setArticleId(dataList.get(i));
                    hsBoothArticleRel.setBoothId(boothId);
                    hsBoothArticleRel.setAdSn(maxSort+i+1);
                    hsBoothArticleRel.setIsDel(0);
                    hsBoothArticleRel.setCreateBy(-1);
                    hsBoothArticleRel.setUpdateBy(-1);
                    hsBoothArticleRel.setCreateTime(date);
                    hsBoothArticleRel.setUpdateTime(date);
                    hsBoothArticleRel.setBid(57);
                    hsBoothArticleRels.add(hsBoothArticleRel);
                }
                vo = commonService.batchInsert(HsBoothArticleRel.class,hsBoothArticleRels);
            }

            booth = null;
            condition =null;
            queryColumn =null;
        } catch (Exception e) {
            logger.warn("Remote call to bindingBoothData fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 解除该展位下已经绑定的数据
     * @param boothId
     * @param dataList
     * @return
     */
    @Override
    public ResultVo relieveBoothDataBind(Integer boothId, List<Integer> dataList) {
        ResultVo vo = ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        Map<Object,Object> condition = Maps.newHashMap();
        try {
            //查询展位是否存在
            HsBooth booth = null;
            vo = commonService.select(boothId, new HsBooth());
            if (vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                booth = (HsBooth) vo.getDataSet();
            }
            if(booth == null ){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"展位信息不存在");
            }
            Integer boothType = booth.getBoothType();//展位类型（1：广告，2：买家出租问题 ，3：买家出售问题，4：卖家出租问题 ，5：卖家出售问题，6：友情链接，7：出租，8：出售，9：新楼盘）
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            if(boothType == 1 ){//广告类型
                queryColumn.add("ID boothAdvertRelId");//广告类型数据与展位进行关联表ID
                queryColumn.add("ADVERT_ID advertId");//广告_ID
                queryColumn.add("BOOTH_ID boothId");//展位ID
                condition.put("queryColumn",queryColumn);
                condition.put("isDel",0);//未删除
                condition.put("boothId",boothId);//展位ID
                vo = commonService.selectCustomColumnNamesList(HsBoothAdvertRel.class,condition);
                if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                }
                List<Map<Object,Object>> hsBoothAdvertRels = (List<Map<Object, Object>>) vo.getDataSet();
                if(hsBoothAdvertRels==null || hsBoothAdvertRels.size()==0){//如果查询到的内容为空，则直接返回
                    return vo;
                }
                //保存删除广告ids
                List<Integer> deleteIds = Lists.newArrayList();
                for (Map<Object, Object> hsBoothAdvertRel : hsBoothAdvertRels) {
                    Integer boothAdvertRelId = StringUtil.getAsInt(StringUtil.trim(hsBoothAdvertRel.get("boothAdvertRelId")));
                    Integer advertId = StringUtil.getAsInt(StringUtil.trim(hsBoothAdvertRel.get("advertId")));
                    if(dataList.contains(advertId)){//如果广告ID已绑定，则加入删除列表
                        deleteIds.add(boothAdvertRelId);
                    }
                }
                if(deleteIds!=null && deleteIds.size()>0){
                    condition.clear();
                    condition.put("ids",deleteIds);
                    vo = commonService.batchDelete(HsBoothAdvertRel.class, condition);
                }
            }else {//文章类型
                queryColumn.add("ID boothArticleRelId");//广告类型数据与展位进行关联表ID
                queryColumn.add("ARTICLE_ID articleId");//文章ID
                queryColumn.add("BOOTH_ID boothId");//展位ID
                queryColumn.add("AD_SN adSn");//排序序号
                condition.put("queryColumn",queryColumn);
                condition.put("isDel",0);//未删除
                condition.put("boothId",boothId);//展位ID
                vo = commonService.selectCustomColumnNamesList(HsBoothArticleRel.class,condition);

                if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询失败
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                }
                List<Map<Object,Object>> hsBoothArticleRels = (List<Map<Object, Object>>) vo.getDataSet();
                if(hsBoothArticleRels==null || hsBoothArticleRels.size()==0){//如果查询到的内容为空，则直接返回
                    return vo;
                }
                //保存删除文章ids
                List<Integer> deleteIds = Lists.newArrayList();
                for (Map<Object, Object> hsBoothArticleRel : hsBoothArticleRels) {
                    Integer boothAdvertRelId = StringUtil.getAsInt(StringUtil.trim(hsBoothArticleRel.get("boothAdvertRelId")));
                    Integer articleId = StringUtil.getAsInt(StringUtil.trim(hsBoothArticleRel.get("articleId")));
                    if(dataList.contains(articleId)){//如果广告ID已绑定，则忽略
                        deleteIds.add(boothAdvertRelId);
                    }
                }

                if(deleteIds!=null && deleteIds.size()>0){
                    condition.clear();
                    condition.put("ids",deleteIds);
                    vo = commonService.batchDelete(HsBoothArticleRel.class, condition);
                }
            }
        } catch (Exception e) {
            logger.warn("Remote call to relieveBoothDataBind fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取展位详情
     * @param id
     * @return
     */
    @Override
    public ResultVo getBoothDetail(Integer id) {
        ResultVo vo = null;
        try {
            vo = commonService.select(id,new HsBooth());
        } catch (Exception e) {
            logger.warn("Remote call to getBoothDetail fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 修改展位
     * @param booth
     * @return
     */
    @Override
    public ResultVo updateBooth(HsBooth booth) {
        ResultVo vo = null;
        Map<Object,Object> condition = Maps.newHashMap();
            try {
                //自定义查询列名
                List<String> queryColumn = new ArrayList<>();
                //查询该展位下已绑定的数据
                String boothAliasName = booth.getBoothAliasName();
                Integer id = booth.getId();
                queryColumn.add("ID boothId");//频道ID
                condition.put("queryColumn",queryColumn);
                condition.put("id",id);//展位别名
                vo = commonService.selectCustomColumnNamesList(HsBooth.class,condition);
                if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                    return vo;
                }
                List<Map<Object,Object>> existBooths = (List<Map<Object, Object>>) vo.getDataSet();
                if(existBooths==null || existBooths.size()==0){//判断展位是否存在，存在则直接修改，否则不
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"展位信息不存在");
                }
                Map<Object,Object> boothMap = existBooths.get(0);
                if(StringUtil.getAsInt(StringUtil.trim(boothMap.get("boothId"))) != id){
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"展位别名已存在");
                }
                booth.setBoothAliasName(null);
                booth.setUpdateTime(new Date());
                vo = commonService.update(booth);
                condition =null;
                queryColumn =null;
        } catch (Exception e) {
            logger.warn("Remote call to updateBooth fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 新增展位
     * @param booth
     * @return
     */
    @Override
    public ResultVo addBooth(HsBooth booth) {
        ResultVo vo = null;
        Map<Object,Object> condition = Maps.newHashMap();
        try {
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            //查询该频道下已绑定的数据
            condition.put("boothAliasName",booth.getBoothAliasName());//展位别名
            queryColumn.add("ID boothId");//展位ID
            condition.put("queryColumn",queryColumn);
            vo = commonService.selectCustomColumnNamesList(HsBooth.class,condition);
            if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                return vo;
            }
            List<Map<Object,Object>> existChannels = (List<Map<Object, Object>>) vo.getDataSet();
            if(existChannels!=null && existChannels.size()>0){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"展位别名已存在");
            }
            Date date = new Date();
            booth.setCreateTime(date);
            booth.setUpdateTime(date);
            vo = commonService.insert(booth);
            condition =null;
            queryColumn =null;
            date =null;
        } catch (Exception e) {
            logger.warn("Remote call to addBooth fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取该频道下已经绑定的展位数据
     * @param channelId 频道ID
     * @return
     */
    @Override
    public ResultVo getChannelBindData(Integer channelId) {
        ResultVo vo = null;
        Map<Object,Object> condition = Maps.newHashMap();
        try {
            condition.put("isDel",0);//未删除
            condition.put("channelId",channelId);//频道ID
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID boothChannelRelId");//资源和频道的关联表ID
            queryColumn.add("CHANNEL_ID channelId");//频道ID
            queryColumn.add("BOOTH_ID boothId");//展位ID
            queryColumn.add("SORT sort");//展位ID
            condition.put("queryColumn",queryColumn);
            vo = commonService.selectCustomColumnNamesList(HsBoothChannelRel.class,condition);
            if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                return vo;
            }
            List<Map<Object,Object>> boothChannelRelList = (List<Map<Object, Object>>) vo.getDataSet();
            if(boothChannelRelList== null || boothChannelRelList.size()==0) return vo;//如果数据为空直接返回
            List<Integer> boothIds = Lists.newArrayList();//保存展位ID
            boothChannelRelList.forEach((boothChannelRel)->{
                boothIds.add(StringUtil.getAsInt(StringUtil.trim(boothChannelRel.get("boothId"))));
            });
            condition.clear();
            queryColumn.clear();
            queryColumn.add("ID boothId");//展位ID
            queryColumn.add("BOOTH_NAME boothName");//展位的名称
            queryColumn.add("BOOTH_ALIAS_NAME boothAliasName");//展位别名
            queryColumn.add("BOOTH_DESC boothDesc");//展位描述
            queryColumn.add("BOOTH_TYPE boothType");//展位类型（1：广告，2：买家出租问题 ，3：买家出售问题，4：卖家出租问题 ，5：卖家出售问题，6：友情链接，7：出租，8：出售，9：新楼盘）
            queryColumn.add("BOOTH_EXCLUSIVE boothExclusive");//该展位的专属(1:PC, 2:TC, 3:APP)
            condition.put("queryColumn",queryColumn);
            condition.put("boothState",0);//展位状态（0：启用，1：禁用）
            condition.put("isDel",0);//是否删除0:未删除，1：删除
            condition.put("boothIds",boothIds);//展位Ids
            vo = commonService.selectCustomColumnNamesList(HsBooth.class,condition);
            if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                return vo;
            }
            List<Map<Object,Object>> sortBoothList = Lists.newArrayList();
            List<Map<Object,Object>> boothList = (List<Map<Object, Object>>) vo.getDataSet();
            if(boothList==null || boothList.size()==0){//数据展位数据为空时
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
            for (Integer boothId : boothIds) {
                for (Map<Object, Object> booth : boothList) {
                    if(boothId == StringUtil.getAsInt(StringUtil.trim(booth.get("boothId")))){
                        sortBoothList.add(booth);
                        break;
                    }
                }
            }
            vo.setDataSet(sortBoothList);
            boothChannelRelList =null;
            boothList =null;
            sortBoothList =null;
        } catch (Exception e) {
            logger.warn("Remote call to getChannelBindData fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 解除该频道下已经绑定的展位数据
     * @param channelId
     * @param boothIdsList
     * @return
     */
    @Override
    public ResultVo relieveChannelBind(Integer channelId, List<Integer> boothIdsList) {
        ResultVo vo = null;
        Map<Object,Object> condition = Maps.newHashMap();
        try {
            //查询该展位下已经绑定的数据,
            condition.put("isDel",0);//未删除
            condition.put("channelId",channelId);//频道ID
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID boothChannelRelId");//资源和频道的关联表ID
            queryColumn.add("CHANNEL_ID channelId");//频道ID
            queryColumn.add("BOOTH_ID boothId");//展位ID
            condition.put("queryColumn",queryColumn);
            vo = commonService.selectCustomColumnNamesList(HsBoothChannelRel.class,condition);
            if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                return vo;
            }
            List<Map<Object,Object>> boothChannelRelList = (List<Map<Object, Object>>) vo.getDataSet();
            if(boothChannelRelList== null || boothChannelRelList.size()==0) return vo;//如果数据为空直接返回
            List<Integer> boothChannelRelIds = Lists.newArrayList();//记录要删除的关联展位IDs
            for (Map<Object, Object> boothChannelRel : boothChannelRelList) {
                Integer boothId = StringUtil.getAsInt(StringUtil.trim(boothChannelRel.get("boothId")));
                if(boothIdsList.contains(boothId)){//判断传入的Ids是否有绑定,绑定则加入要删除的集合中
                    boothChannelRelIds.add(StringUtil.getAsInt(StringUtil.trim(boothChannelRel.get("boothChannelRelId"))));
                }
            }
            //
            if(boothChannelRelIds==null || boothChannelRelIds.size()==0){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
            //删除关联表信息
            condition.clear();
            condition.put("ids",boothChannelRelIds);
            vo = commonService.batchDelete(HsBoothChannelRel.class,condition);
            boothChannelRelList =null;
            condition =null;
            queryColumn =null;
            boothChannelRelIds =null;
        } catch (Exception e) {
            logger.warn("Remote call to relieveChannelBind fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取该频道下未绑定的展位数据
     * @param channelId 频道ID
     * @param channelType 频道类型
     * @return
     */
    @Override
    public ResultVo getChannelUnBindData(Integer channelId,Integer channelType) {
        ResultVo vo = null;
        Map<Object,Object> condition = Maps.newHashMap();
        try {
            condition.put("isDel",0);//未删除
            condition.put("channelId",channelId);//频道ID
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID boothChannelRelId");//资源和频道的关联表ID
            queryColumn.add("CHANNEL_ID channelId");//频道ID
            queryColumn.add("BOOTH_ID boothId");//展位ID
            queryColumn.add("SORT sort");//展位ID
            condition.put("queryColumn",queryColumn);
            vo = commonService.selectCustomColumnNamesList(HsBoothChannelRel.class,condition);
            if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                return vo;
            }
            List<Map<Object,Object>> boothChannelRelList = (List<Map<Object, Object>>) vo.getDataSet();
            if(boothChannelRelList== null || boothChannelRelList.size()==0) return vo;//如果数据为空直接返回
            List<Integer> boothIds = Lists.newArrayList();//保存展位ID
            boothChannelRelList.forEach((boothChannelRel)->{
                boothIds.add(StringUtil.getAsInt(StringUtil.trim(boothChannelRel.get("boothId"))));
            });
            condition.clear();
            queryColumn.clear();
            queryColumn.add("ID boothId");//展位ID
            queryColumn.add("BOOTH_NAME boothName");//展位的名称
            queryColumn.add("BOOTH_ALIAS_NAME boothAliasName");//展位别名
            queryColumn.add("BOOTH_DESC boothDesc");//展位描述
            queryColumn.add("BOOTH_TYPE boothType");//展位类型（1：广告，2：买家出租问题 ，3：买家出售问题，4：卖家出租问题 ，5：卖家出售问题，6：友情链接，7：出租，8：出售，9：新楼盘）
            queryColumn.add("BOOTH_EXCLUSIVE boothExclusive");//该展位的专属(1:PC, 2:TC, 3:APP)
            condition.put("queryColumn",queryColumn);
            condition.put("boothState",0);//展位状态（0：启用，1：禁用）
            condition.put("isDel",0);//是否删除0:未删除，1：删除
            condition.put("boothExclusive",channelType);//频道类型（1：PC网站，2：触屏端，3：App）
            condition.put("noInbindboothIds",boothIds);//查询未绑定的展位Ids
            vo = commonService.selectCustomColumnNamesList(HsBooth.class,condition);
            boothChannelRelList =null;
            condition =null;
            queryColumn =null;
        } catch (Exception e) {
            logger.warn("Remote call to getChannelUnBindData fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 频道绑定展位数据
     * @param channelId
     * @param boothIdsList
     * @return
     */
    @Override
    public ResultVo bindingChannelData(Integer channelId, List<Integer> boothIdsList) {
        ResultVo vo = null;
        Map<Object,Object> condition = Maps.newHashMap();
        try {
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            //查询该频道下已绑定的数据
            condition.put("isDel",0);//未删除
            condition.put("channelId",channelId);//频道ID
            queryColumn.add("ID boothChannelRelId");//资源和频道的关联表ID
            queryColumn.add("CHANNEL_ID channelId");//频道ID
            queryColumn.add("BOOTH_ID boothId");//展位ID
            queryColumn.add("SORT sort");//展位ID
            condition.put("queryColumn",queryColumn);
            vo = commonService.selectCustomColumnNamesList(HsBoothChannelRel.class,condition);
            if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                return vo;
            }
            List<Map<Object,Object>> bindingBooths = (List<Map<Object, Object>>) vo.getDataSet();
            int maxSort = 0;
            if(bindingBooths!=null && bindingBooths.size()>0){
                for (Map<Object, Object> booth : bindingBooths) {//遍历已绑定的展位数,将已绑定的Id过滤掉
                    Integer boothId = StringUtil.getAsInt(StringUtil.trim(booth.get("boothId")));
                    int sort = StringUtil.getAsInt(StringUtil.trim(booth.get("sort")));
                    if(sort>maxSort){//保存最大的排序数
                        maxSort = sort;
                    }
                    if(boothIdsList.contains(boothId)){//如果包含已绑定的展位
                        boothIdsList.remove(boothId);
                    }
                }
            }

            //绑定过滤掉的展位数
            if(boothIdsList==null || boothIdsList.size()==0){//如果要绑定的展位都已存在,则直接提示绑定成功
                return ResultVo.success();
            }
            int length = boothIdsList.size();
            List<HsBoothChannelRel> boothChannelRels = Lists.newArrayListWithCapacity(length);
            HsBoothChannelRel boothChannelRel = null;
            Date date = new Date();
            for (int i =0 ;i < length ; i++) {
                boothChannelRel = new HsBoothChannelRel();
                boothChannelRel.setChannelId(channelId);
                boothChannelRel.setBoothId(boothIdsList.get(i));
                boothChannelRel.setSort(maxSort+i+1);
                boothChannelRel.setIsDel(0);
                boothChannelRel.setCreateTime(date);
                boothChannelRel.setUpdateTime(date);
                boothChannelRel.setBid(57);
                boothChannelRels.add(boothChannelRel);
            }
            vo = commonService.batchInsert(HsBoothChannelRel.class,boothChannelRels);
            condition =null;
            boothChannelRels =null;
            bindingBooths =null;
            queryColumn =null;
        } catch (Exception e) {
            logger.warn("Remote call to bindingChannelData fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 新增频道
     * @param channel
     * @return
     */
    @Override
    public ResultVo addChannel(HsChannel channel) {
        ResultVo vo = null;
        Map<Object,Object> condition = Maps.newHashMap();
        try {
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            //查询该频道下已绑定的数据
            condition.put("channelAliasName",channel.getChannelAliasName());//频道别名
            queryColumn.add("ID channelId");//频道ID
            condition.put("queryColumn",queryColumn);
            vo = commonService.selectCustomColumnNamesList(HsChannel.class,condition);
            if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                return vo;
            }
            List<Map<Object,Object>> existChannels = (List<Map<Object, Object>>) vo.getDataSet();
            if(existChannels!=null && existChannels.size()>0){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"频道别名已存在");
            }
            Date date = new Date();
            channel.setCreateTime(date);
            channel.setUpdateTime(date);
            vo = commonService.insert(channel);
            condition =null;
            queryColumn =null;
            date =null;
        } catch (Exception e) {
            logger.warn("Remote call to addChannel fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取频道详情
     * @param id
     * @return
     */
    @Override
    public ResultVo getChannelDetail(Integer id) {
        ResultVo vo = null;
        try {
            vo = commonService.select(id,new HsChannel());
        } catch (Exception e) {
            logger.warn("Remote call to getChannelDetail fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 修改频道
     * @param channel
     * @return
     */
    @Override
    public ResultVo updateChannel(HsChannel channel) {
        ResultVo vo = null;
        Map<Object,Object> condition = Maps.newHashMap();
        try {
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            //查询该频道下已绑定的数据
            Integer id = channel.getId();
            queryColumn.add("ID channelId");//频道ID
            condition.put("queryColumn",queryColumn);
            condition.put("id",id);//频道别名
            vo = commonService.selectCustomColumnNamesList(HsChannel.class,condition);
            if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                return vo;
            }
            List<Map<Object,Object>> existChannels = (List<Map<Object, Object>>) vo.getDataSet();
            if(existChannels==null || existChannels.size()==0){//判断频道是否存在，存在则直接修改，否则不
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"频道信息不存在");
            }
            channel.setChannelAliasName(null);
            channel.setUpdateTime(new Date());
            vo = commonService.update(channel);
            condition =null;
            queryColumn =null;
        } catch (Exception e) {
            logger.warn("Remote call to updateChannel fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 添加广告
     * @param advertData
     * @return
     */
    @Override
    public ResultVo addAdvert(HsAdvertData advertData) {
        ResultVo vo = null;
        try {
            vo = commonService.insert(advertData);
        } catch (Exception e) {
            logger.warn("Remote call to addAdvert fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 删除广告
     * @param advertId
     * @return
     */
    @Override
    public ResultVo deleteAdvert(Integer advertId) {
        ResultVo vo = null;
        try {
            HsAdvertData advertData = new HsAdvertData();
            advertData.setId(advertId);
            advertData.setIsDel(1);
            advertData.setUpdateTime(new Date());
            vo = commonService.update(advertData);
        } catch (Exception e) {
            logger.warn("Remote call to deleteAdvert fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 修改广告
     * @param advertData
     * @return
     */
    @Override
    public ResultVo updateAdvert(HsAdvertData advertData) {
        ResultVo vo = null;
        try {
            Date date = new Date();
            advertData.setCreateTime(date);
            advertData.setUpdateTime(date);
            vo = commonService.update(advertData);
        } catch (Exception e) {
            logger.warn("Remote call to updateAdvert fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 查看广告列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getAdvertList(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID advertId");//广告ID
            queryColumn.add("LANGUAGE_VERSION languageVersion");//语言版本（0：中文，1：英文）默认为0
            queryColumn.add("AD_FULL_TITLE adFullTitle");//广告长标题
            queryColumn.add("AD_SHORT_TITLE adShortTitle");//广告短标题
            queryColumn.add("AD_TYPE adType");//广告类型(1:图片类型, 2:文字类型)
            queryColumn.add("AD_IMG_URL adImgUrl");//广告图片上传名称(当ad_type=1有效)
            queryColumn.add("AD_CONTENT adContent");//广告图片上传名称(当ad_type=1有效)
            queryColumn.add("AD_SN adSn");//排序序号
            queryColumn.add("AD_C_COUNT adCCount");//点击次数
            queryColumn.add("AD_F_COUNT adFCount");//关注次数
            queryColumn.add("AD_DESC adDesc");//广告内容的备注描述
            queryColumn.add("AD_LINK adLink");//广告内容点击链接的URL地址
            queryColumn.add("AD_STATE adState");//状态(0:未启用 1:启用)
            queryColumn.add("IS_DEL isDel");//是否删除0:不删除，1：删除
            queryColumn.add("CREATE_TIME createTime");//创建时间
            condition.put("queryColumn",queryColumn);
            vo = commonService.selectCustomColumnNamesList(HsAdvertData.class,condition);
            if(vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return vo ;
            }
            List<Map<Object, Object>> advertList = (List<Map<Object, Object>>) vo.getDataSet();
            for (Map<Object, Object> advert : advertList) {
                String adImgUrl  = StringUtil.trim(advert.get("adImgUrl"));
                if(StringUtil.hasText(adImgUrl)){
                    advert.put("adImgUrl", ImageUtil.IMG_URL_PREFIX + adImgUrl);
                }
            }
            condition =null;
            queryColumn =null;
        } catch (Exception e) {
            logger.warn("Remote call to addChannel fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取广告详情
     * @param advertId
     * @return
     */
    @Override
    public ResultVo getAdvertDetail(Integer advertId) {
        ResultVo vo = null;
        try {
            vo = commonService.select(advertId,new HsAdvertData());
            if(vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return vo ;
            }
            HsAdvertData advert = (HsAdvertData) vo.getDataSet();
            if(advert!=null){
                String adImgUrl = advert.getAdImgUrl();
                if(StringUtil.hasText(adImgUrl)){
                    advert.setAdImgUrl(ImageUtil.IMG_URL_PREFIX + adImgUrl);
                }
            }
        } catch (Exception e) {
            logger.warn("Remote call to getAdvertDetail fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 添加文章
     * @param articleData
     * @return
     */
    @Override
    public ResultVo addArticle(HsArticleData articleData) {
        ResultVo vo = null;
        try {
            Date date = new Date();
            articleData.setCreateTime(date);
            articleData.setUpdateTime(date);
            vo = commonService.insert(articleData);
        } catch (Exception e) {
            logger.warn("Remote call to addAdvert fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 删除文章
     * @param articleId
     * @return
     */
    @Override
    public ResultVo deleteArticle(Integer articleId) {
        ResultVo vo = null;
        try {
            HsArticleData articleData = new HsArticleData();
            articleData.setId(articleId);
            articleData.setIsDel(1);
            articleData.setUpdateTime(new Date());
            vo = commonService.update(articleData);
        } catch (Exception e) {
            logger.warn("Remote call to deleteAdvert fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 修改文章
     * @param articleData
     * @return
     */
    @Override
    public ResultVo updateArticle(HsArticleData articleData) {
        ResultVo vo = null;
        try {
            Date date = new Date();
            articleData.setUpdateTime(date);
            vo = commonService.update(articleData);
        } catch (Exception e) {
            logger.warn("Remote call to updateArticle fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 查看文章列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getArticleList(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID articleId");//文章ID
            queryColumn.add("LANGUAGE_VERSION languageVersion");//语言版本（0：中文，1：英文）默认为0
            queryColumn.add("ARTICLE_NAME articleName");//文章名称
            queryColumn.add("ARTICLE_DESC articleDesc");//文章描述
            queryColumn.add("ARTICLE_TYPE articleType");//0 买家租房 1 买家买房 2 卖家出租 3 卖家出售
            queryColumn.add("STATUS status");//状态  0 未启用  1 启用 2删除 3 修改中  默认为1
            queryColumn.add("ARTICLE_SORT articleSort");//文章排序
            queryColumn.add("SEO_TITLE seoTitle");//seo标题
            queryColumn.add("SEO_KEYWORDS seoKeywords");//seo关键字
            queryColumn.add("SEO_DESCRIPTION seoDescription");//seo描述
            queryColumn.add("IS_DEL isDel");//是否删除0:不删除，1：删除
            queryColumn.add("CREATE_TIME createTime");//创建时间
            condition.put("queryColumn",queryColumn);
            vo = commonService.selectCustomColumnNamesList(HsArticleData.class,condition);

            condition =null;
            queryColumn =null;
        } catch (Exception e) {
            logger.warn("Remote call to addChannel fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取文章详情
     * @param articleId
     * @return
     */
    @Override
    public ResultVo getArticleDetail(Integer articleId) {
        ResultVo vo = null;
        try {
            vo = commonService.select(articleId,new HsArticleData());
        } catch (Exception e) {
            logger.warn("Remote call to getArticleDetail fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;

    }

    /**
     * 获取数据字典组列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getDictGroupList(Map<Object, Object> condition) {
        ResultVo vo = null;
        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        queryColumn.add("ID groupId");//主键ID
        queryColumn.add("GROUP_CODE groupCode");//字典编码
        queryColumn.add("GROUP_NAME groupName");//组名称
        queryColumn.add("GROUP_NAME_EN groupNameEn");//英文组名称
        queryColumn.add("REMARK remark");//备注
        queryColumn.add("IS_DEL isDel");//是否删除0:不删除，1：删除
        queryColumn.add("CREATE_TIME createTime");//创建时间
        condition.put("queryColumn",queryColumn);
        try {
            vo = commonService.selectCustomColumnNamesList(HsSysDictcodeGroup.class,condition);
        } catch (Exception e) {
            logger.warn("Remote call to getDictGroupList fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        queryColumn = null;
        condition = null;
        return vo;
    }

    /**
     * 修改据字典组
     * @return
     */
    @Override
    public ResultVo updateDictGroup(HsSysDictcodeGroup dictcodeGroup) {
        ResultVo vo = null;
        try {
            vo = commonService.select(dictcodeGroup.getId(),dictcodeGroup);
            if(ResultConstant.SYS_REQUIRED_SUCCESS != vo.getResult()){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
            if(vo.getDataSet()==null){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"ID不能为空");
            }
            dictcodeGroup.setUpdateTime(new Date());
            vo = commonService.update(dictcodeGroup);
        } catch (Exception e) {
            logger.warn("Remote call to updateDictGroup fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 新增数据字典组
     * @param dictcodeGroup
     * @return
     */
    @Override
    public ResultVo addDictGroup(HsSysDictcodeGroup dictcodeGroup) {
        ResultVo vo = null;
        try {
            Date date = new Date();
            dictcodeGroup.setCreateTime(date);
            dictcodeGroup.setUpdateTime(date);
            vo = commonService.insert(dictcodeGroup);
        } catch (Exception e) {
            logger.warn("Remote call to addDictGroup fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取数据字典组详情
     * @param dictcodeGroupId
     * @return
     */
    @Override
    public ResultVo getDictGroupDetail(Integer dictcodeGroupId) {
        ResultVo vo = null;
        try {
            vo = commonService.select(dictcodeGroupId,new HsSysDictcodeGroup());
        } catch (Exception e) {
            logger.warn("Remote call to getDictGroupDetail fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取数据字典项列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getDictItemList(Map<Object, Object> condition) {
        ResultVo vo = null;
        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        queryColumn.add("ID itemId");//主键ID
        queryColumn.add("GROUP_ID groupId");//所属组ID
        queryColumn.add("ITEM_NAME itemName");//字典项名称
        queryColumn.add("ITEM_VALUE itemValue");//字典项值中文名
        queryColumn.add("ITEM_VALUE_EN itemValueEn");//字典项值英文名
        queryColumn.add("CREATE_TIME createTime");//创建时间
        queryColumn.add("REMARK remark");//备注
        queryColumn.add("SORT sort");//排序
        queryColumn.add("BACK_IMG backImg");//背景图
        queryColumn.add("IS_DEL isDel");//是否删除0:不删除，1：删除
        condition.put("queryColumn",queryColumn);
        try {
            vo = commonService.selectCustomColumnNamesList(HsSysDictcodeItem.class,condition);
            if(ResultConstant.SYS_REQUIRED_SUCCESS != vo.getResult()){
                return vo ;
            }
            List<Map<Object,Object>> itemList = (List<Map<Object, Object>>) vo.getDataSet();
            if(itemList!=null || itemList.size()>0){
                List<Integer> groupIds = Lists.newArrayList();
                for (Map<Object, Object> item : itemList) {
                    groupIds.add(StringUtil.getAsInt(StringUtil.trim(item.get("groupId"))));
                }
                //去除重复值
                HashSet h = new HashSet(groupIds);
                groupIds.clear();
                groupIds.addAll(h);

                //查询所属字典组
                queryColumn.add("ID groupId");//组ID
                queryColumn.add("GROUP_CODE groupCode");//字典编码
                queryColumn.add("GROUP_NAME groupName");//组名称
                queryColumn.add("GROUP_NAME_EN groupNameEn");//英文组名称
                condition.put("queryColumn",queryColumn);
                vo = commonService.selectCustomColumnNamesList(HsSysDictcodeGroup.class,condition);
                if(ResultConstant.SYS_REQUIRED_SUCCESS != vo.getResult()){
                    return vo;
                }
                List<Map<Object,Object>> groupList = (List<Map<Object, Object>>) vo.getDataSet();
                if(groupList!=null || groupList.size()>0){
                    for (Map<Object, Object> item : itemList) {
                        int groupId = StringUtil.getAsInt(StringUtil.trim(item.get("groupId")));
                        for (Map<Object, Object> group : groupList) {
                            int _groupId = StringUtil.getAsInt(StringUtil.trim(group.get("groupId")));
                            if(groupId == _groupId){//
                                item.putAll(group);
                                break;
                            }
                        }
                    }
                    vo.setDataSet(itemList);
                }
                groupList =null;
            }
        } catch (Exception e) {
            logger.warn("Remote call to getDictItemList fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        queryColumn = null;
        condition = null;
        return vo;
    }

    /**
     * 修改数据字典项
     * @param dictcodeItem
     * @return
     */
    @Override
    public ResultVo updateDictItem(HsSysDictcodeItem dictcodeItem) {
        ResultVo vo = null;
        try {
            vo = commonService.select(dictcodeItem.getId(),dictcodeItem);
            if(ResultConstant.SYS_REQUIRED_SUCCESS != vo.getResult()){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
            if(vo.getDataSet()==null){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"字典项不存在");
            }
            //获取所属字典组信息
            Integer groupId = dictcodeItem.getGroupId();
            if(groupId!=null){
                vo = commonService.select(groupId,HsSysDictcodeGroup.class);
                if(ResultConstant.SYS_REQUIRED_SUCCESS != vo.getResult()){
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                }
            }
            dictcodeItem.setUpdateTime(new Date());
            vo = commonService.update(dictcodeItem);
        } catch (Exception e) {
            logger.warn("Remote call to updateDictItem fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 新增数据字典项
     * @param dictcodeItem
     * @return
     */
    @Override
    public ResultVo addDictItem(HsSysDictcodeItem dictcodeItem) {
        ResultVo vo = null;
        try {
            Date date = new Date();
            dictcodeItem.setCreateTime(date);
            dictcodeItem.setUpdateTime(date);
            vo = commonService.insert(dictcodeItem);
        } catch (Exception e) {
            logger.warn("Remote call to addDictItem fails"+e.getMessage());
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取支持的城市信息
     * @return
     */
    @Override
    public ResultVo getSupportCities(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            //自定义查询的列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID id");//城市id
            queryColumn.add("PID pid");//父级城市id
            queryColumn.add("CITY_NAME_EN cityNameEn");//行政单位英文名
            queryColumn.add("CITY_NAME_CN cityNameCn");//行政单位中文名
            queryColumn.add("CITY_LONGITUDE longitude");//google地图经度
            queryColumn.add("CITY_LATITUDE latitude");//google地图纬度
            queryColumn.add("LEVEL level");//行政级别 1:市 2:社区 3:子社区
            queryColumn.add("IS_DEL isDel");//是否删除0:不删除，1：删除
            condition.put("queryColumn", queryColumn);
            condition.putAll(condition);
            //查询支持的所有城市信息
            List<Map<String, Object>> perms =commonService.findAllCity(condition);
            vo = ResultVo.success();
            vo.setDataSet(perms);
            queryColumn = null;
        } catch (Exception e) {
            logger.warn("PC Remote call to getSupportCities fails"+e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } finally {
            condition = null;
            return vo;
        }
    }

    /**
     * 获取支持的城市信息详情
     * @param id
     * @return
     */
    @Override
    public ResultVo getCityDetail(Integer id) {
        ResultVo vo = null;
        try {
            vo = commonService.select(id,new HsSupportCity());
        } catch (Exception e) {
            logger.warn("PC Remote call to getCityDetail fails"+e.getMessage());
            vo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } finally {
            return vo;
        }
    }

    /**
     * 修改支持的城市信息
     * @param city
     * @return
     */
    @Override
    public ResultVo updateCity(HsSupportCity city) {
        ResultVo vo = null;
        try {
            Integer id = city.getId();
            vo = commonService.select(id,new HsSupportCity());
            if(vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return vo;
            }
            HsSupportCity selectCity = (HsSupportCity) vo.getDataSet();
            if(selectCity == null){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"城市信息不存在");
            }
            city.setUpdateTime(new Date());
            vo = commonService.update(city);
        } catch (Exception e) {
            logger.warn("PC Remote call to updateCity fails"+e.getMessage());
            return ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 新增支持的城市信息
     * @param city
     * @return
     */
    @Override
    public ResultVo addCity(HsSupportCity city) {
        ResultVo vo = null;
        try {
            Map<Object,Object> condition = Maps.newHashMap();
            String cityNameCn = city.getCityNameCn();
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID id");//主键ID
            condition.put("queryColumn",queryColumn);
            condition.put("cityNameCn",cityNameCn);
            vo = commonService.selectCustomColumnNamesList(HsSupportCity.class,condition);
            if(vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return vo;
            }
            List<Map<Object,Object>> citys = (List<Map<Object,Object>>) vo.getDataSet();
            if(citys != null  && citys.size()!=0){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"城市信息已存在");
            }
            city.setCreateTime(new Date());
            vo = commonService.insert(city);
        } catch (Exception e) {
            logger.warn("PC Remote call to addCity fails"+e.getMessage());
            return ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 删除城市信息
     * @param id
     * @return
     */
    @Override
    public ResultVo deleteCity(Integer id) {
        ResultVo vo = null;
        try {
            vo = commonService.select(id,new HsSupportCity());
            if(vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return vo;
            }
            HsSupportCity selectCity = (HsSupportCity) vo.getDataSet();
            if(selectCity == null){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"城市信息不存在");
            }
            HsSupportCity city = new HsSupportCity();
            city.setId(id);
            city.setIsDel(1);
            city.setUpdateTime(new Date());
            vo = commonService.update(city);
            if(vo.getResult() == 0){//修改成功，删除缓存
               RedisUtil.safeDel(RedisConstant.SYS_SUPPORT_CITIES_CACHE_KEY_KEY);
            }
        } catch (Exception e) {
            logger.warn("PC Remote call to deleteCity fails"+e.getMessage());
            return ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取积分计算列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getGoldRuleList(Map<Object, Object> condition) {
        ResultVo vo = null;
        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        queryColumn.add("ID id");//主键ID
        queryColumn.add("GOLD_CODE goldCode");//积分编码
        queryColumn.add("GOLD_TYPE goldType");//0：推荐并注册会员 1：外获上传任务完成，2：区域长上传任务完成，3：外看任务完成，4：区域长送钥匙完成，5：合同单任务完成 6 第一个业务员获取钥匙 7:业务员评价房源 8：外获超时，订单关闭 9 外看任务超时，订单关闭
        queryColumn.add("GOLD_RULE_NAME_CN goldRuleNameCn");//'积分名称中文名'
        queryColumn.add("GOLD_RULE_NAME_EN goldRuleNameEn");//'积分名称英文名'
        queryColumn.add("IS_ADD_SUBTRACT isAddSubtract");//'控制加分还是减分 0 ：加分 1减分'
        queryColumn.add("IS_SALESMAN isSalesman");//是否是业务员 0 ：业务员 1 会员
        queryColumn.add("SCORE score");//'积分值'
        queryColumn.add("REMARK remark");//''备注描述''
        queryColumn.add("IS_DEL isDel");//是否删除0:不删除，1：删除
        queryColumn.add("CREATE_TIME createTime");//创建时间
        queryColumn.add("UPDATE_TIME updateTime");//更新时间
        condition.put("queryColumn",queryColumn);
        try {
            vo = commonService.selectCustomColumnNamesList(HsGoldRule.class,condition);
        } catch (Exception e) {
            logger.warn("Remote call to CommonService fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        queryColumn = null;
        condition = null;
        return vo;
    }

    /**
     * 获取积分计算详情
     * @param id
     * @return
     */
    @Override
    public ResultVo getGoldRuleDetail(Integer id) {
        ResultVo vo = null;
        try {
            vo = commonService.select(id,new HsGoldRule());
        } catch (Exception e) {
            logger.warn("Remote call to getGoldRuleDetail fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 新增积分计算详情
     * @param hsGoldRule
     * @return
     */
    @Override
    public ResultVo addGoldRule(HsGoldRule hsGoldRule) {
        ResultVo vo = null;
        Map<Object,Object> condition = Maps.newHashMap();
        try {
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            //查询积分计算规则表数据
            queryColumn.add("ID id");//主键ID
            condition.put("queryColumn",queryColumn);
            condition.put("goldType",hsGoldRule.getGoldType());//积分类型
            condition.put("isDel",0);//未删除
            vo = commonService.selectCustomColumnNamesList(HsGoldRule.class,condition);
            if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                return vo;
            }
            List<Map<Object,Object>> existGolds = (List<Map<Object, Object>>) vo.getDataSet();
            if(existGolds!=null && existGolds.size()>0){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"积分类型已存在");
            }
            Date date = new Date();
            hsGoldRule.setCreateTime(date);
            hsGoldRule.setUpdateTime(date);
            vo = commonService.insert(hsGoldRule);
            if (vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {//修改成功
                condition = MapClassUtil.beanToMap(hsGoldRule);
                condition.remove("createBy");
                condition.remove("createTime");
                condition.remove("updateBy");
                condition.remove("updateTime");
                condition.remove("standby1");
                condition.remove("standby2");
                condition.remove("standby3");
                condition.remove("standby4");
                condition.remove("standby5");

                //修改成功添加到缓存中
                if (RedisUtil.isExistCache(RedisConstant.SYS_GOLD_RULE_CACHE_KEY)) {
                    //重置积分规则缓存
                    String cacheKey = RedisUtil.safeGet(RedisConstant.SYS_GOLD_RULE_CACHE_KEY);
                    List<Map<Object, Object>> goldList = JsonUtil.parseJSON2List(cacheKey);
                    goldList.add(condition);
                }
            }
            condition =null;
            queryColumn =null;
            date =null;
        } catch (Exception e) {
            logger.warn("Remote call to addGoldRule fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 修改积分规则
     * @param hsGoldRule
     * @return
     */
    @Override
    public ResultVo updateGoldRule(HsGoldRule hsGoldRule) {
        ResultVo vo = null;
        try {
            vo = commonService.select(hsGoldRule.getId(),new HsGoldRule());
            if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                return vo;
            }
            //数据库查询到的数据
            HsGoldRule _hsGoldRule = (HsGoldRule) vo.getDataSet();
            if(_hsGoldRule ==null){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"积分规则不存在");
            }

            hsGoldRule.setUpdateTime(new Date());
            vo = commonService.update(hsGoldRule);
            if (vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {//修改成功
                ModelMapperUtil.getInstance().map(_hsGoldRule,hsGoldRule);
                Map<Object,Object> condition = MapClassUtil.beanToMap(_hsGoldRule);
                condition.remove("createBy");
                condition.remove("createTime");
                condition.remove("updateBy");
                condition.remove("updateTime");
                condition.remove("standby1");
                condition.remove("standby2");
                condition.remove("standby3");
                condition.remove("standby4");
                condition.remove("standby5");
                //修改成功添加到缓存中
                if (RedisUtil.isExistCache(RedisConstant.SYS_GOLD_RULE_CACHE_KEY)) {
                    //重置积分规则缓存
//                    String cacheKey = RedisUtil.safeGet(RedisConstant.SYS_GOLD_RULE_CACHE_KEY);
//                    List<Map<Object, Object>> goldList = JsonUtil.parseJSON2List(cacheKey);
                    List<Map<Object, Object>> goldList = RedisUtil.safeGet(RedisConstant.SYS_GOLD_RULE_CACHE_KEY);
                    for (int i = 0 ;i<goldList.size() ; i++) {
                        Map<Object, Object> gold = goldList.get(i);
                        if(StringUtil.trim(condition.get("id")).equals(StringUtil.trim(gold.get("id")))){
                            goldList.remove(i);
                            goldList.add(i,condition);
                            break;
                        }
                    }
                    RedisUtil.safeSet(RedisConstant.SYS_GOLD_RULE_CACHE_KEY,goldList,RedisConstant.SYS_GOLD_RULE_CACHE_KEY_TIME);
//                    RedisUtil.safeSet(RedisConstant.SYS_GOLD_RULE_CACHE_KEY,JsonUtil.list2Json(goldList),RedisConstant.SYS_GOLD_RULE_CACHE_KEY_TIME);
                }
            }
        } catch (Exception e) {
            logger.warn("Remote call to updateGoldRule fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取平台设置列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getPlatformSettingList(Map<Object, Object> condition) {
        ResultVo vo = null;
        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        queryColumn.add("ID setId");//主键ID
        queryColumn.add("CALL_CENTER_ASSISTANT_RATIO callCenterAssistantRatio");//客服任务结佣比例按%分比 格式：0.35-0.45 出租-出售
        queryColumn.add("SELLER_ASSISTANT_RATIO sellerAssistantRatio");//外获任务结佣比例按%分比 格式：0.35-0.45 出租-出售
        queryColumn.add("REGION_LEADER_RATIO regionLeaderRatio");//区域长实勘任务结佣比例按%分比 格式：0.35-0.45 出租-出售
        queryColumn.add("REGION_LEADER_TAKE_KEY_RATIO regionLeaderTakeKeyRatio");//区域长送钥匙任务结佣比例按%分比 格式：0.35-0.45 出租-出售
        queryColumn.add("BUYER_ASSISTANT_RATIO buyerAssistantRatio");//外看结佣比例按%分比 格式：0.35-0.45 出租-出售
        queryColumn.add("INTERNAL_ASSISTANT_RATIO internalAssistantRatio");//内勤结佣比例按%分比 格式：0.35-0.45 出租-出售
        queryColumn.add("COMPANY_ASSISTANT_RATIO companyAssistantRatio");//公司结佣比例按%分比 格式：0.35-0.45 出租-出售
        queryColumn.add("ELSE_ASSISTANT_RATIO elseAssistantRatio");//其它结佣比例按%分比 格式：0.35-0.45 出租-出售
        condition.put("queryColumn",queryColumn);
        condition.put("isDel",0);
        try {
            vo = commonService.selectCustomColumnNamesList(HsSysPlatformSetting.class,condition);
        } catch (Exception e) {
            logger.warn("Remote call to getPlatformSettingList fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        queryColumn = null;
        condition = null;
        return vo;
    }

    /**
     * 获取平台设置详情
     * @param id
     * @return
     */
    @Override
    public ResultVo getPlatformSettingDetail(Integer id) {
        ResultVo vo = null;
        try {
            if(id == null){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"该信息不存在");
            }
            vo = commonService.select(id,new HsSysPlatformSetting());
            if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                return vo;
            }
            if(vo.getDataSet()==null){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"该信息不存在");
            }
        } catch (Exception e) {
            logger.warn("Remote call to getPlatformSettingDetail fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 修改平台设置
     * @param platformSetting
     * @return
     */
    @Override
    public ResultVo updatePlatformSetting(HsSysPlatformSetting platformSetting) {
        ResultVo vo = null;
        try {
            Integer id = platformSetting.getId();
            if(id == null){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"该信息不存在");
            }
            vo = commonService.select(id,platformSetting);
            if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                return vo;
            }
            HsSysPlatformSetting selectPlatformSetting = (HsSysPlatformSetting) vo.getDataSet();
            if(selectPlatformSetting==null){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"该信息不存在");
            }
            platformSetting.setUpdateTime(new Date());
            vo = commonService.update(platformSetting);
        } catch (Exception e) {
            logger.warn("Remote call to updatePlatformSetting fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 新增平台设置
     * @param platformSetting
     * @return
     */
    @Override
    public ResultVo addPlatformSetting(HsSysPlatformSetting platformSetting) {
        ResultVo vo = null;
        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        Map<Object,Object> condition = Maps.newHashMap();
        queryColumn.add("ID id");//主键ID
        condition.put("queryColumn",queryColumn);
        condition.put("isDel",0);
        try {
            vo = commonService.selectCustomColumnNamesList(HsSysPlatformSetting.class,condition);
            if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                return vo;
            }
            List<Map<Object,Object>> selectList = (List<Map<Object,Object>>) vo.getDataSet();
            if(selectList!=null && selectList.size()>0){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"已存在一条平台设置");
            }
            platformSetting.setCreateTime(new Date());
            vo = commonService.insert(platformSetting);
        } catch (Exception e) {
            logger.warn("Remote call to addPlatformSetting fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        queryColumn = null;
        condition = null;
        return vo;
    }

    /**
     * 删除平台设置
     * @param id
     * @return
     */
    @Override
    public ResultVo deletePlatformSetting(Integer id) {
        ResultVo vo = null;
        try {
            HsSysPlatformSetting _platformSetting = new HsSysPlatformSetting();
            vo = commonService.select(id,_platformSetting);
            if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                return vo;
            }
            HsSysPlatformSetting platformSetting = (HsSysPlatformSetting) vo.getDataSet();
            if(platformSetting==null){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"该信息不存在");
            }
            _platformSetting.setId(id);
            _platformSetting.setIsDel(1);
            _platformSetting.setUpdateTime(new Date());
            vo = commonService.update(_platformSetting);
        } catch (Exception e) {
            logger.warn("Remote call to deletePlatformSetting fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取地址编码
     * @return
     * @throws Exception
     */
    @Override
    public ResultVo loadCityCode(Map<Object,Object> condition) {
        ResultVo result = new ResultVo();
        try {
            if(RedisUtil.existKey(RedisConstant.SYS_REGIONNAME_CACHE_KEY)) {//判断缓存中是否有值
                List<Map<Object,Object>> regionList =  RedisUtil.safeGet(RedisConstant.SYS_REGIONNAME_CACHE_KEY);
                result.setDataSet(regionList);
                return result;
            }
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID id");//主键ID
            queryColumn.add("REGION_NAME_CN regionNameCn");//房源名称
            queryColumn.add("REGION_NAME_EN region_name_en");//房源主图
            queryColumn.add("REGION_CODE regionCode"); //房屋面积
            condition.put("queryColumn",queryColumn);
            result = commonService.selectCustomColumnNamesList(HsRegionCode.class,condition);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object,Object>> regionList = (List<Map<Object, Object>>)result.getDataSet();
                if( regionList!=null && regionList.size()>0 ){
                    RedisUtil.safeSet(RedisConstant.SYS_REGIONNAME_CACHE_KEY,regionList,60*60*24*30);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
        }
        return result;
    }

    /**
     * 获取进度流程
     *
     * @param type
     * @return
     */
    @Override
    public List<Map<String, Object>> findProgressList(String type) {
        return housesService.findProgressList(type);
    }

}