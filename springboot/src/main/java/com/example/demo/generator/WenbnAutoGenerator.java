package com.example.demo.generator;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/11/23
 */
public class WenbnAutoGenerator {

    private static final Logger logger = LoggerFactory.getLogger(WenbnAutoGenerator.class);
    protected WenbnConfigBuilder config;
    protected WenbnInjectionConfig injectionConfig;
    private DataSourceConfig dataSource;
    private StrategyConfig strategy;
    private WenbnPackageConfig packageInfo;
    private WenbnTemplateConfig template;
    private WenbnGlobalConfig globalConfig;
    private WenbnAbstractTemplateEngine templateEngine;

    public WenbnAutoGenerator() {
    }

    public void execute() {
        logger.debug("==========================准备生成文件...==========================");
        if (null == this.config) {
            this.config = new WenbnConfigBuilder(this.packageInfo, this.dataSource, this.strategy, this.template, this.globalConfig);
            if (null != this.injectionConfig) {
                this.injectionConfig.setConfig(this.config);
            }
        }

        if (null == this.templateEngine) {
            this.templateEngine = new WenbnVelocityTemplateEngine();
        }

        this.templateEngine.init(this.pretreatmentConfigBuilder(this.config)).mkdirs().batchOutput().open();
        logger.debug("==========================文件生成完成！！！==========================");
    }

    protected List<WenbnTableInfo> getAllTableInfoList(WenbnConfigBuilder config) {
        return config.getTableInfoList();
    }

    protected WenbnConfigBuilder pretreatmentConfigBuilder(WenbnConfigBuilder config) {
        if (null != this.injectionConfig) {
            this.injectionConfig.initMap();
            config.setInjectionConfig(this.injectionConfig);
        }

        List<WenbnTableInfo> tableList = this.getAllTableInfoList(config);
        Iterator var3 = tableList.iterator();

        while(var3.hasNext()) {
            TableInfo tableInfo = (TableInfo)var3.next();
            if (config.getGlobalConfig().isActiveRecord()) {
                tableInfo.setImportPackages(Model.class.getCanonicalName());
            }

            if (tableInfo.isConvert()) {
                tableInfo.setImportPackages(TableName.class.getCanonicalName());
            }

            if (config.getStrategyConfig().getLogicDeleteFieldName() != null && tableInfo.isLogicDelete(config.getStrategyConfig().getLogicDeleteFieldName())) {
                tableInfo.setImportPackages(TableLogic.class.getCanonicalName());
            }

            if (StringUtils.isNotEmpty(config.getStrategyConfig().getVersionFieldName())) {
                tableInfo.setImportPackages(Version.class.getCanonicalName());
            }

            if (StringUtils.isNotEmpty(config.getSuperEntityClass())) {
                tableInfo.setImportPackages(config.getSuperEntityClass());
            } else {
                tableInfo.setImportPackages(Serializable.class.getCanonicalName());
            }

            if (config.getStrategyConfig().isEntityBooleanColumnRemoveIsPrefix()) {
                tableInfo.getFields().stream().filter((field) -> {
                    return "boolean".equalsIgnoreCase(field.getPropertyType());
                }).filter((field) -> {
                    return field.getPropertyName().startsWith("is");
                }).forEach((field) -> {
                    field.setPropertyName(config.getStrategyConfig(), StringUtils.removePrefixAfterPrefixToLower(field.getPropertyName(), 2));
                });
            }
        }

        return config.setTableInfoList(tableList);
    }

    public DataSourceConfig getDataSource() {
        return this.dataSource;
    }

    public WenbnAutoGenerator setDataSource(DataSourceConfig dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public StrategyConfig getStrategy() {
        return this.strategy;
    }

    public WenbnAutoGenerator setStrategy(StrategyConfig strategy) {
        this.strategy = strategy;
        return this;
    }

    public PackageConfig getPackageInfo() {
        return this.packageInfo;
    }

    public WenbnAutoGenerator setPackageInfo(WenbnPackageConfig packageInfo) {
        this.packageInfo = packageInfo;
        return this;
    }

    public TemplateConfig getTemplate() {
        return this.template;
    }

    public WenbnAutoGenerator setTemplate(WenbnTemplateConfig template) {
        this.template = template;
        return this;
    }

    public WenbnConfigBuilder getConfig() {
        return this.config;
    }

    public WenbnAutoGenerator setConfig(WenbnConfigBuilder config) {
        this.config = config;
        return this;
    }

    public WenbnGlobalConfig getGlobalConfig() {
        return this.globalConfig;
    }

    public WenbnAutoGenerator setGlobalConfig(WenbnGlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
        return this;
    }

    public WenbnInjectionConfig getCfg() {
        return this.injectionConfig;
    }

    public WenbnAutoGenerator setCfg(WenbnInjectionConfig injectionConfig) {
        this.injectionConfig = injectionConfig;
        return this;
    }

    public WenbnAbstractTemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }

    public WenbnAutoGenerator setTemplateEngine(WenbnAbstractTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        return this;
    }
}
