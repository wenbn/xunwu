package com.example.demo.generator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/11/23
 */
public class WenbnConfigBuilder {

    private final WenbnTemplateConfig template;
    private final DataSourceConfig dataSourceConfig;
    private Connection connection;
    private IDbQuery dbQuery;
    private String superEntityClass;
    private String superMapperClass;
    private String superServiceClass;
    private String superServiceImplClass;
    private String superManagerClass;
    private String superManagerImplClass;
    private String superControllerClass;
    private List<WenbnTableInfo> tableInfoList;
    private Map<String, String> packageInfo;
    private Map<String, String> pathInfo;
    private StrategyConfig strategyConfig;
    private WenbnGlobalConfig globalConfig;
    private WenbnInjectionConfig injectionConfig;

    public WenbnConfigBuilder(WenbnPackageConfig packageConfig, DataSourceConfig dataSourceConfig, StrategyConfig strategyConfig, WenbnTemplateConfig template, WenbnGlobalConfig globalConfig) {
        if (null == globalConfig) {
            this.globalConfig = new WenbnGlobalConfig();
        } else {
            this.globalConfig = globalConfig;
        }

        if (null == template) {
            this.template = new WenbnTemplateConfig();
        } else {
            this.template = template;
        }

        if (null == packageConfig) {
            this.handlerPackage(this.template, this.globalConfig.getOutputDir(), new WenbnPackageConfig());
        } else {
            this.handlerPackage(this.template, this.globalConfig.getOutputDir(), packageConfig);
        }

        this.dataSourceConfig = dataSourceConfig;
        this.handlerDataSource(dataSourceConfig);
        if (null == strategyConfig) {
            this.strategyConfig = new StrategyConfig();
        } else {
            this.strategyConfig = strategyConfig;
        }

        this.handlerStrategy(this.strategyConfig);
    }

    public Map<String, String> getPackageInfo() {
        return this.packageInfo;
    }

    public Map<String, String> getPathInfo() {
        return this.pathInfo;
    }

    public String getSuperEntityClass() {
        return this.superEntityClass;
    }

    public String getSuperMapperClass() {
        return this.superMapperClass;
    }

    public String getSuperServiceClass() {
        return this.superServiceClass;
    }

    public String getSuperServiceImplClass() {
        return this.superServiceImplClass;
    }

    public String getSuperManagerClass() {
        return superManagerClass;
    }

    public String getSuperManagerImplClass() {
        return superManagerImplClass;
    }

    public String getSuperControllerClass() {
        return this.superControllerClass;
    }

    public List<WenbnTableInfo> getTableInfoList() {
        return this.tableInfoList;
    }

    public WenbnConfigBuilder setTableInfoList(List<WenbnTableInfo> tableInfoList) {
        this.tableInfoList = tableInfoList;
        return this;
    }

    public WenbnTemplateConfig getTemplate() {
        return this.template == null ? new WenbnTemplateConfig() : this.template;
    }

    private void handlerPackage(WenbnTemplateConfig template, String outputDir, WenbnPackageConfig config) {
        this.packageInfo = new HashMap(6);
        this.packageInfo.put("ModuleName", config.getModuleName());
        this.packageInfo.put("Entity", this.joinPackage(config.getParent(), config.getEntity()));
        this.packageInfo.put("Mapper", this.joinPackage(config.getParent(), config.getMapper()));
        this.packageInfo.put("Xml", this.joinPackage(config.getParent(), config.getXml()));
        this.packageInfo.put("Service", this.joinPackage(config.getParent(), config.getService()));
        this.packageInfo.put("ServiceImpl", this.joinPackage(config.getParent(), config.getServiceImpl()));
        this.packageInfo.put("Manager", this.joinPackage(config.getParent(), config.getManager()));
        this.packageInfo.put("ManagerImpl", this.joinPackage(config.getParent(), config.getManagerImpl()));
        this.packageInfo.put("Controller", this.joinPackage(config.getParent(), config.getController()));
        Map<String, String> configPathInfo = config.getPathInfo();
        if (null != configPathInfo) {
            this.pathInfo = configPathInfo;
        } else {
            this.pathInfo = new HashMap(6);
            this.setPathInfo(this.pathInfo, template.getEntity(this.getGlobalConfig().isKotlin()), outputDir, "entity_path", "Entity");
            this.setPathInfo(this.pathInfo, template.getMapper(), outputDir, "mapper_path", "Mapper");
            this.setPathInfo(this.pathInfo, template.getXml(), outputDir, "xml_path", "Xml");
            this.setPathInfo(this.pathInfo, template.getService(), outputDir, "service_path", "Service");
            this.setPathInfo(this.pathInfo, template.getServiceImpl(), outputDir, "service_impl_path", "ServiceImpl");
            this.setPathInfo(this.pathInfo, template.getManager(), outputDir, "manager_path", "Manager");
            this.setPathInfo(this.pathInfo, template.getManagerImpl(), outputDir, "manager_impl_path", "ManagerImpl");
            this.setPathInfo(this.pathInfo, template.getController(), outputDir, "controller_path", "Controller");
        }

    }

    private void setPathInfo(Map<String, String> pathInfo, String template, String outputDir, String path, String module) {
        if (StringUtils.isNotEmpty(template)) {
            pathInfo.put(path, this.joinPath(outputDir, (String)this.packageInfo.get(module)));
        }

    }

    private void handlerDataSource(DataSourceConfig config) {
        this.connection = config.getConn();
        this.dbQuery = config.getDbQuery();
    }

    private void handlerStrategy(StrategyConfig config) {
        this.processTypes(config);
        this.tableInfoList = this.getTablesInfo(config);
    }

    private void processTypes(StrategyConfig config) {
        if (StringUtils.isEmpty(config.getSuperServiceClass())) {
            this.superServiceClass = "com.baomidou.mybatisplus.extension.service.IService";
        } else {
            this.superServiceClass = config.getSuperServiceClass();
        }

        if (StringUtils.isEmpty(config.getSuperServiceImplClass())) {
            this.superServiceImplClass = "com.baomidou.mybatisplus.extension.service.impl.ServiceImpl";
        } else {
            this.superServiceImplClass = config.getSuperServiceImplClass();
        }

        if (StringUtils.isEmpty(config.getSuperMapperClass())) {
            this.superMapperClass = "com.baomidou.mybatisplus.core.mapper.BaseMapper";
        } else {
            this.superMapperClass = config.getSuperMapperClass();
        }

        this.superEntityClass = config.getSuperEntityClass();
        this.superControllerClass = config.getSuperControllerClass();
    }

    private List<WenbnTableInfo> processTable(List<WenbnTableInfo> tableList, NamingStrategy strategy, StrategyConfig config) {
        String[] tablePrefix = config.getTablePrefix();

        WenbnTableInfo tableInfo;
        for(Iterator var5 = tableList.iterator(); var5.hasNext(); this.checkImportPackages(tableInfo)) {
            tableInfo = (WenbnTableInfo)var5.next();
            String entityName = NamingStrategy.capitalFirst(this.processName(tableInfo.getName(), strategy, tablePrefix));
            if (StringUtils.isNotEmpty(this.globalConfig.getEntityName())) {
                tableInfo.setConvert(true);
                tableInfo.setEntityName(String.format(this.globalConfig.getEntityName(), entityName));
            } else {
                tableInfo.setEntityName(this.strategyConfig, entityName);
            }

            if (StringUtils.isNotEmpty(this.globalConfig.getMapperName())) {
                tableInfo.setMapperName(String.format(this.globalConfig.getMapperName(), entityName));
            } else {
                tableInfo.setMapperName(entityName + "Mapper");
            }

            if (StringUtils.isNotEmpty(this.globalConfig.getXmlName())) {
                tableInfo.setXmlName(String.format(this.globalConfig.getXmlName(), entityName));
            } else {
                tableInfo.setXmlName(entityName + "Mapper");
            }

            if (StringUtils.isNotEmpty(this.globalConfig.getServiceName())) {
                tableInfo.setServiceName(String.format(this.globalConfig.getServiceName(), entityName));
            } else {
                tableInfo.setServiceName("I" + entityName + "Service");
            }

            if (StringUtils.isNotEmpty(this.globalConfig.getServiceImplName())) {
                tableInfo.setServiceImplName(String.format(this.globalConfig.getServiceImplName(), entityName));
            } else {
                tableInfo.setServiceImplName(entityName + "ServiceImpl");
            }

            if (StringUtils.isNotEmpty(this.globalConfig.getManagerName())) {
                tableInfo.setManagerName(String.format(this.globalConfig.getManagerName(), entityName));
            } else {
                tableInfo.setManagerName("I" + entityName + "Manager");
            }

            if (StringUtils.isNotEmpty(this.globalConfig.getManagerImplName())) {
                tableInfo.setManagerImplName(String.format(this.globalConfig.getManagerImplName(), entityName));
            } else {
                tableInfo.setServiceImplName(entityName + "ManagerImpl");
            }

            if (StringUtils.isNotEmpty(this.globalConfig.getControllerName())) {
                tableInfo.setControllerName(String.format(this.globalConfig.getControllerName(), entityName));
            } else {
                tableInfo.setControllerName(entityName + "Controller");
            }
        }

        return tableList;
    }

    private void checkImportPackages(TableInfo tableInfo) {
        if (StringUtils.isNotEmpty(this.strategyConfig.getSuperEntityClass())) {
            tableInfo.getImportPackages().add(this.strategyConfig.getSuperEntityClass());
        } else if (this.globalConfig.isActiveRecord()) {
            tableInfo.getImportPackages().add(Model.class.getCanonicalName());
        }

        if (null != this.globalConfig.getIdType()) {
            tableInfo.getImportPackages().add(IdType.class.getCanonicalName());
            tableInfo.getImportPackages().add(TableId.class.getCanonicalName());
        }

        if (StringUtils.isNotEmpty(this.strategyConfig.getVersionFieldName())) {
            tableInfo.getFields().forEach((f) -> {
                if (this.strategyConfig.getVersionFieldName().equals(f.getName())) {
                    tableInfo.getImportPackages().add(Version.class.getCanonicalName());
                }

            });
        }

    }

    private List<WenbnTableInfo> getTablesInfo(StrategyConfig config) {
        boolean isInclude = null != config.getInclude() && config.getInclude().length > 0;
        boolean isExclude = null != config.getExclude() && config.getExclude().length > 0;
        if (isInclude && isExclude) {
            throw new RuntimeException("<strategy> 标签中 <include> 与 <exclude> 只能配置一项！");
        } else {
            List<WenbnTableInfo> tableList = new ArrayList();
            List<WenbnTableInfo> includeTableList = new ArrayList();
            List<WenbnTableInfo> excludeTableList = new ArrayList();
            Set<String> notExistTables = new HashSet();
            PreparedStatement preparedStatement = null;

            try {
                String tablesSql = this.dbQuery.tablesSql();
                String schema;
                if (DbType.POSTGRE_SQL == this.dbQuery.dbType()) {
                    schema = this.dataSourceConfig.getSchemaName();
                    if (schema == null) {
                        schema = "public";
                        this.dataSourceConfig.setSchemaName(schema);
                    }

                    tablesSql = String.format(tablesSql, schema);
                } else if (DbType.ORACLE == this.dbQuery.dbType()) {
                    schema = this.dataSourceConfig.getSchemaName();
                    if (schema == null) {
                        schema = this.dataSourceConfig.getUsername().toUpperCase();
                        this.dataSourceConfig.setSchemaName(schema);
                    }

                    tablesSql = String.format(tablesSql, schema);
                    StringBuilder sb;
                    if (isInclude) {
                        sb = new StringBuilder(tablesSql);
                        sb.append(" AND ").append(this.dbQuery.tableName()).append(" IN (");
                        Arrays.stream(config.getInclude()).forEach((tbname) -> {
                            sb.append("'").append(tbname.toUpperCase()).append("',");
                        });
                        sb.replace(sb.length() - 1, sb.length(), ")");
                        tablesSql = sb.toString();
                    } else if (isExclude) {
                        sb = new StringBuilder(tablesSql);
                        sb.append(" AND ").append(this.dbQuery.tableName()).append(" NOT IN (");
                        Arrays.stream(config.getExclude()).forEach((tbname) -> {
                            sb.append("'").append(tbname.toUpperCase()).append("',");
                        });
                        sb.replace(sb.length() - 1, sb.length(), ")");
                        tablesSql = sb.toString();
                    }
                }

                preparedStatement = this.connection.prepareStatement(tablesSql);
                ResultSet results = preparedStatement.executeQuery();

                while(true) {
                    String tableName;
                    String tableComment;
                    label307:
                    do {
                        while(results.next()) {
                            tableName = results.getString(this.dbQuery.tableName());
                            if (StringUtils.isNotEmpty(tableName)) {
                                tableComment = results.getString(this.dbQuery.tableComment());
                                continue label307;
                            }

                            System.err.println("当前数据库为空！！！");
                        }

                        Iterator var30 = tableList.iterator();

                        while(var30.hasNext()) {
                            TableInfo tabInfo = (TableInfo)var30.next();
                            notExistTables.remove(tabInfo.getName());
                        }

                        if (notExistTables.size() > 0) {
                            System.err.println("表 " + notExistTables + " 在数据库中不存在！！！");
                        }

                        if (isExclude) {
                            tableList.removeAll(excludeTableList);
                            includeTableList = tableList;
                        }

                        if (!isInclude && !isExclude) {
                            includeTableList = tableList;
                        }

                        includeTableList.forEach((ti) -> {
                            this.convertTableFields(ti, config.getColumnNaming());
                        });
                        return this.processTable(includeTableList, config.getNaming(), config);
                    } while(config.isSkipView() && "VIEW".equals(tableComment));

                    WenbnTableInfo tableInfo = new WenbnTableInfo();
                    tableInfo.setName(tableName);
                    tableInfo.setComment(tableComment);
                    String[] var14;
                    int var15;
                    int var16;
                    String excludeTable;
                    if (isInclude) {
                        var14 = config.getInclude();
                        var15 = var14.length;

                        for(var16 = 0; var16 < var15; ++var16) {
                            excludeTable = var14[var16];
                            if (this.tableNameMatches(excludeTable, tableName)) {
                                includeTableList.add(tableInfo);
                            } else {
                                notExistTables.add(excludeTable);
                            }
                        }
                    } else if (isExclude) {
                        var14 = config.getExclude();
                        var15 = var14.length;

                        for(var16 = 0; var16 < var15; ++var16) {
                            excludeTable = var14[var16];
                            if (this.tableNameMatches(excludeTable, tableName)) {
                                excludeTableList.add(tableInfo);
                            } else {
                                notExistTables.add(excludeTable);
                            }
                        }
                    }

                    tableList.add(tableInfo);
                }
            } catch (SQLException var26) {
                var26.printStackTrace();
            } finally {
                try {
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }

                    if (this.connection != null) {
                        this.connection.close();
                    }
                } catch (SQLException var25) {
                    var25.printStackTrace();
                }

            }

            return this.processTable(includeTableList, config.getNaming(), config);
        }
    }

    private boolean tableNameMatches(String setTableName, String dbTableName) {
        return setTableName.equals(dbTableName) || StringUtils.matches(setTableName, dbTableName);
    }

    private WenbnTableInfo convertTableFields(WenbnTableInfo tableInfo, NamingStrategy strategy) {
        boolean haveId = false;
        List<TableField> fieldList = new ArrayList();
        ArrayList commonFieldList = new ArrayList();

        try {
            String tableFieldsSql = this.dbQuery.tableFieldsSql();
            if (DbType.POSTGRE_SQL == this.dbQuery.dbType()) {
                tableFieldsSql = String.format(tableFieldsSql, this.dataSourceConfig.getSchemaName(), tableInfo.getName());
            } else if (DbType.ORACLE == this.dbQuery.dbType()) {
                tableFieldsSql = String.format(tableFieldsSql.replace("#schema", this.dataSourceConfig.getSchemaName()), tableInfo.getName());
            } else {
                tableFieldsSql = String.format(tableFieldsSql, tableInfo.getName());
            }

            PreparedStatement preparedStatement = this.connection.prepareStatement(tableFieldsSql);
            ResultSet results = preparedStatement.executeQuery();

            while(results.next()) {
                TableField field = new TableField();
                String key = results.getString(this.dbQuery.fieldKey());
                boolean isId;
                if (DbType.DB2 == this.dbQuery.dbType()) {
                    isId = StringUtils.isNotEmpty(key) && "1".equals(key);
                } else {
                    isId = StringUtils.isNotEmpty(key) && "PRI".equals(key.toUpperCase());
                }

                if (isId && !haveId) {
                    field.setKeyFlag(true);
                    if (this.dbQuery.isKeyIdentity(results)) {
                        field.setKeyIdentityFlag(true);
                    }

                    haveId = true;
                } else {
                    field.setKeyFlag(false);
                }

                String[] fcs = this.dbQuery.fieldCustom();
                if (null != fcs) {
                    Map<String, Object> customMap = new HashMap();
                    String[] var14 = fcs;
                    int var15 = fcs.length;

                    for(int var16 = 0; var16 < var15; ++var16) {
                        String fc = var14[var16];
                        customMap.put(fc, results.getObject(fc));
                    }

                    field.setCustomMap(customMap);
                }

                field.setName(results.getString(this.dbQuery.fieldName()));
                field.setType(results.getString(this.dbQuery.fieldType()));
                field.setPropertyName(this.strategyConfig, this.processName(field.getName(), strategy));
                field.setColumnType(this.dataSourceConfig.getTypeConvert().processTypeConvert(this.globalConfig, field.getType()));
                field.setComment(results.getString(this.dbQuery.fieldComment()));
                if (this.strategyConfig.includeSuperEntityColumns(field.getName())) {
                    commonFieldList.add(field);
                } else {
                    List<TableFill> tableFillList = this.getStrategyConfig().getTableFillList();
                    if (null != tableFillList) {
                        tableFillList.stream().filter((tf) -> {
                            return tf.getFieldName().equalsIgnoreCase(field.getName());
                        }).findFirst().ifPresent((tf) -> {
                            field.setFill(tf.getFieldFill().name());
                        });
                    }

                    fieldList.add(field);
                }
            }
        } catch (SQLException var18) {
            System.err.println("SQL Exception：" + var18.getMessage());
        }

        tableInfo.setFields(fieldList);
        tableInfo.setCommonFields(commonFieldList);
        return tableInfo;
    }

    private String joinPath(String parentDir, String packageName) {
        if (StringUtils.isEmpty(parentDir)) {
            parentDir = System.getProperty("java.io.tmpdir");
        }

        if (!StringUtils.endsWith(parentDir, File.separator)) {
            parentDir = parentDir + File.separator;
        }

        packageName = packageName.replaceAll("\\.", "\\" + File.separator);
        return parentDir + packageName;
    }

    private String joinPackage(String parent, String subPackage) {
        return StringUtils.isEmpty(parent) ? subPackage : parent + "." + subPackage;
    }

    private String processName(String name, NamingStrategy strategy) {
        return this.processName(name, strategy, this.strategyConfig.getFieldPrefix());
    }

    private String processName(String name, NamingStrategy strategy, String[] prefix) {
        boolean removePrefix = false;
        if (prefix != null && prefix.length >= 1) {
            removePrefix = true;
        }

        String propertyName;
        if (removePrefix) {
            if (strategy == NamingStrategy.underline_to_camel) {
                propertyName = NamingStrategy.removePrefixAndCamel(name, prefix);
            } else {
                propertyName = NamingStrategy.removePrefix(name, prefix);
            }
        } else if (strategy == NamingStrategy.underline_to_camel) {
            propertyName = NamingStrategy.underlineToCamel(name);
        } else {
            propertyName = name;
        }

        return propertyName;
    }

    public StrategyConfig getStrategyConfig() {
        return this.strategyConfig;
    }

    public WenbnConfigBuilder setStrategyConfig(StrategyConfig strategyConfig) {
        this.strategyConfig = strategyConfig;
        return this;
    }

    public WenbnGlobalConfig getGlobalConfig() {
        return this.globalConfig;
    }

    public WenbnConfigBuilder setGlobalConfig(WenbnGlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
        return this;
    }

    public WenbnInjectionConfig getInjectionConfig() {
        return this.injectionConfig;
    }

    public WenbnConfigBuilder setInjectionConfig(WenbnInjectionConfig injectionConfig) {
        this.injectionConfig = injectionConfig;
        return this;
    }
}
