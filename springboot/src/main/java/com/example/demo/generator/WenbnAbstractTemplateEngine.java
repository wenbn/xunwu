package com.example.demo.generator;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.toolkit.PackageHelper;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/11/23
 */
public abstract class WenbnAbstractTemplateEngine{

    protected static final Logger logger = LoggerFactory.getLogger(WenbnAbstractTemplateEngine.class);
    private WenbnConfigBuilder configBuilder;

    public WenbnAbstractTemplateEngine() {
    }

    public WenbnAbstractTemplateEngine init(WenbnConfigBuilder configBuilder) {
        this.configBuilder = configBuilder;
        return this;
    }

    public WenbnAbstractTemplateEngine batchOutput() {
        try {
            List<WenbnTableInfo> tableInfoList = this.getConfigBuilder().getTableInfoList();
            Iterator var2 = tableInfoList.iterator();

            while(var2.hasNext()) {
                WenbnTableInfo tableInfo = (WenbnTableInfo)var2.next();
                Map<String, Object> objectMap = this.getObjectMap(tableInfo);
                Map<String, String> pathInfo = this.getConfigBuilder().getPathInfo();
                WenbnTemplateConfig template = this.getConfigBuilder().getTemplate();
                WenbnInjectionConfig injectionConfig = this.getConfigBuilder().getInjectionConfig();
                if (null != injectionConfig) {
                    injectionConfig.initMap();
                    objectMap.put("cfg", injectionConfig.getMap());
                    List<FileOutConfig> focList = injectionConfig.getFileOutConfigList();
                    if (CollectionUtils.isNotEmpty(focList)) {
                        Iterator var9 = focList.iterator();

                        while(var9.hasNext()) {
                            FileOutConfig foc = (FileOutConfig)var9.next();
                            if (this.isCreate(WenbnFileType.OTHER, foc.outputFile(tableInfo))) {
                                this.writer(objectMap, foc.getTemplatePath(), foc.outputFile(tableInfo));
                            }
                        }
                    }
                }

                String entityName = tableInfo.getEntityName();
                String controllerFile;
                if (null != entityName && null != pathInfo.get("entity_path")) {
                    controllerFile = String.format((String)pathInfo.get("entity_path") + File.separator + "%s" + this.suffixJavaOrKt(), entityName);
                    if (this.isCreate(WenbnFileType.ENTITY, controllerFile)) {
                        this.writer(objectMap, this.templateFilePath(template.getEntity(this.getConfigBuilder().getGlobalConfig().isKotlin())), controllerFile);
                    }
                }

                if (null != tableInfo.getMapperName() && null != pathInfo.get("mapper_path")) {
                    controllerFile = String.format((String)pathInfo.get("mapper_path") + File.separator + tableInfo.getMapperName() + this.suffixJavaOrKt(), entityName);
                    if (this.isCreate(WenbnFileType.MAPPER, controllerFile)) {
                        this.writer(objectMap, this.templateFilePath(template.getMapper()), controllerFile);
                    }
                }

                if (null != tableInfo.getXmlName() && null != pathInfo.get("xml_path")) {
                    controllerFile = String.format((String)pathInfo.get("xml_path") + File.separator + tableInfo.getXmlName() + ".xml", entityName);
                    if (this.isCreate(WenbnFileType.XML, controllerFile)) {
                        this.writer(objectMap, this.templateFilePath(template.getXml()), controllerFile);
                    }
                }

                if (null != tableInfo.getServiceName() && null != pathInfo.get("service_path")) {
                    controllerFile = String.format((String)pathInfo.get("service_path") + File.separator + tableInfo.getServiceName() + this.suffixJavaOrKt(), entityName);
                    if (this.isCreate(WenbnFileType.SERVICE, controllerFile)) {
                        this.writer(objectMap, this.templateFilePath(template.getService()), controllerFile);
                    }
                }

                if (null != tableInfo.getServiceImplName() && null != pathInfo.get("service_impl_path")) {
                    controllerFile = String.format((String)pathInfo.get("service_impl_path") + File.separator + tableInfo.getServiceImplName() + this.suffixJavaOrKt(), entityName);
                    if (this.isCreate(WenbnFileType.SERVICE_IMPL, controllerFile)) {
                        this.writer(objectMap, this.templateFilePath(template.getServiceImpl()), controllerFile);
                    }
                }

                if (null != tableInfo.getServiceName() && null != pathInfo.get("manager_path")) {
                    controllerFile = String.format((String)pathInfo.get("manager_path") + File.separator + tableInfo.getManagerName() + this.suffixJavaOrKt(), entityName);
                    if (this.isCreate(WenbnFileType.SERVICE, controllerFile)) {
                        this.writer(objectMap, this.templateFilePath(template.getManager()), controllerFile);
                    }
                }

                if (null != tableInfo.getManagerImplName() && null != pathInfo.get("manager_impl_path")) {
                    controllerFile = String.format((String)pathInfo.get("manager_impl_path") + File.separator + tableInfo.getManagerImplName() + this.suffixJavaOrKt(), entityName);
                    if (this.isCreate(WenbnFileType.MANAGER_IMPL, controllerFile)) {
                        this.writer(objectMap, this.templateFilePath(template.getManagerImpl()), controllerFile);
                    }
                }


                if (null != tableInfo.getControllerName() && null != pathInfo.get("controller_path")) {
                    controllerFile = String.format((String)pathInfo.get("controller_path") + File.separator + tableInfo.getControllerName() + this.suffixJavaOrKt(), entityName);
                    if (this.isCreate(WenbnFileType.CONTROLLER, controllerFile)) {
                        this.writer(objectMap, this.templateFilePath(template.getController()), controllerFile);
                    }
                }
            }
        } catch (Exception var11) {
            logger.error("无法创建文件，请检查配置信息！", var11);
        }

        return this;
    }

    public abstract void writer(Map<String, Object> var1, String var2, String var3) throws Exception;

    public WenbnAbstractTemplateEngine mkdirs() {
        this.getConfigBuilder().getPathInfo().forEach((key, value) -> {
            File dir = new File(value);
            if (!dir.exists()) {
                boolean result = dir.mkdirs();
                if (result) {
                    logger.debug("创建目录： [" + value + "]");
                }
            }

        });
        return this;
    }

    public void open() {
        String outDir = this.getConfigBuilder().getGlobalConfig().getOutputDir();
        if (this.getConfigBuilder().getGlobalConfig().isOpen() && StringUtils.isNotEmpty(outDir)) {
            try {
                String osName = System.getProperty("os.name");
                if (osName != null) {
                    if (osName.contains("Mac")) {
                        Runtime.getRuntime().exec("open " + outDir);
                    } else if (osName.contains("Windows")) {
                        Runtime.getRuntime().exec("cmd /c start " + outDir);
                    } else {
                        logger.debug("文件输出目录:" + outDir);
                    }
                }
            } catch (IOException var3) {
                var3.printStackTrace();
            }
        }

    }

    public Map<String, Object> getObjectMap(WenbnTableInfo tableInfo) {
        Map<String, Object> objectMap = new HashMap(30);
        WenbnConfigBuilder config = this.getConfigBuilder();
        if (config.getStrategyConfig().isControllerMappingHyphenStyle()) {
            objectMap.put("controllerMappingHyphenStyle", config.getStrategyConfig().isControllerMappingHyphenStyle());
            objectMap.put("controllerMappingHyphen", StringUtils.camelToHyphen(tableInfo.getEntityPath()));
        }

        objectMap.put("restControllerStyle", config.getStrategyConfig().isRestControllerStyle());
        objectMap.put("config", config);
        objectMap.put("package", config.getPackageInfo());
        GlobalConfig globalConfig = config.getGlobalConfig();
        objectMap.put("author", globalConfig.getAuthor());
        objectMap.put("idType", globalConfig.getIdType() == null ? null : globalConfig.getIdType().toString());
        objectMap.put("logicDeleteFieldName", config.getStrategyConfig().getLogicDeleteFieldName());
        objectMap.put("versionFieldName", config.getStrategyConfig().getVersionFieldName());
        objectMap.put("activeRecord", globalConfig.isActiveRecord());
        objectMap.put("kotlin", globalConfig.isKotlin());
        objectMap.put("swagger2", globalConfig.isSwagger2());
        objectMap.put("date", (new SimpleDateFormat("yyyy-MM-dd")).format(new Date()));
        objectMap.put("table", tableInfo);
        objectMap.put("enableCache", globalConfig.isEnableCache());
        objectMap.put("baseResultMap", globalConfig.isBaseResultMap());
        objectMap.put("baseColumnList", globalConfig.isBaseColumnList());
        objectMap.put("entity", tableInfo.getEntityName());
        objectMap.put("entityColumnConstant", config.getStrategyConfig().isEntityColumnConstant());
        objectMap.put("entityBuilderModel", config.getStrategyConfig().isEntityBuilderModel());
        objectMap.put("entityLombokModel", config.getStrategyConfig().isEntityLombokModel());
        objectMap.put("entityBooleanColumnRemoveIsPrefix", config.getStrategyConfig().isEntityBooleanColumnRemoveIsPrefix());
        objectMap.put("superEntityClass", this.getSuperClassName(config.getSuperEntityClass()));
        objectMap.put("superMapperClassPackage", config.getSuperMapperClass());
        objectMap.put("superMapperClass", this.getSuperClassName(config.getSuperMapperClass()));
        objectMap.put("superServiceClassPackage", config.getSuperServiceClass());
        objectMap.put("superServiceClass", this.getSuperClassName(config.getSuperServiceClass()));
        objectMap.put("superServiceImplClassPackage", config.getSuperServiceImplClass());
        objectMap.put("superServiceImplClass", this.getSuperClassName(config.getSuperServiceImplClass()));

//        start ================
        objectMap.put("superManagerClassPackage", config.getSuperServiceClass());
        objectMap.put("superManagerClass", this.getSuperClassName(config.getSuperServiceClass()));
        objectMap.put("superManagerImplClassPackage", config.getSuperServiceImplClass());
        objectMap.put("superManagerImplClass", this.getSuperClassName(config.getSuperServiceImplClass()));
//       end  ================
        objectMap.put("superControllerClassPackage", config.getSuperControllerClass());
        objectMap.put("superControllerClass", this.getSuperClassName(config.getSuperControllerClass()));
        return objectMap;
    }

    private String getSuperClassName(String classPath) {
        return StringUtils.isEmpty(classPath) ? null : classPath.substring(classPath.lastIndexOf(".") + 1);
    }

    public abstract String templateFilePath(String var1);

    protected boolean isCreate(WenbnFileType fileType, String filePath) {
        WenbnConfigBuilder cb = this.getConfigBuilder();
        WenbnInjectionConfig ic = cb.getInjectionConfig();
        if (null != ic && null != ic.getFileCreate()) {
            return ic.getFileCreate().isCreate(cb, fileType, filePath);
        } else {
            File file = new File(filePath);
            boolean exist = file.exists();
            if (!exist) {
                PackageHelper.mkDir(file.getParentFile());
            }

            return !exist || this.getConfigBuilder().getGlobalConfig().isFileOverride();
        }
    }

    protected String suffixJavaOrKt() {
        return this.getConfigBuilder().getGlobalConfig().isKotlin() ? ".kt" : ".java";
    }

    public WenbnConfigBuilder getConfigBuilder() {
        return this.configBuilder;
    }

    public WenbnAbstractTemplateEngine setConfigBuilder(WenbnConfigBuilder configBuilder) {
        this.configBuilder = configBuilder;
        return this;
    }
}
