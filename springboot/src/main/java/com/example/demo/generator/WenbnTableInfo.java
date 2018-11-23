package com.example.demo.generator;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/11/23
 */
public class WenbnTableInfo extends TableInfo {

    private boolean convert;
    private String name;
    private String comment;
    private String entityName;
    private String mapperName;
    private String xmlName;
    private String serviceName;
    private String serviceImplName;
    private String managerName;
    private String managerImplName;
    private String controllerName;
    private List<TableField> fields;
    private List<TableField> commonFields;
    private final Set<String> importPackages = new HashSet();
    private String fieldNames;

    public WenbnTableInfo() {
    }

    public boolean isConvert() {
        return this.convert;
    }

    protected void setConvert(StrategyConfig strategyConfig) {
        if (strategyConfig.containsTablePrefix(this.name)) {
            this.convert = true;
        } else if (strategyConfig.isCapitalModeNaming(this.name)) {
            this.convert = false;
        } else if (NamingStrategy.underline_to_camel == strategyConfig.getColumnNaming()) {
            if (StringUtils.containsUpperCase(this.name)) {
                this.convert = true;
            }
        } else if (!this.entityName.equalsIgnoreCase(this.name)) {
            this.convert = true;
        }

    }

    public void setConvert(boolean convert) {
        this.convert = convert;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getEntityPath() {
        StringBuilder ep = new StringBuilder();
        ep.append(this.entityName.substring(0, 1).toLowerCase());
        ep.append(this.entityName.substring(1));
        return ep.toString();
    }

    public String getEntityName() {
        return this.entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setEntityName(StrategyConfig strategyConfig, String entityName) {
        this.entityName = entityName;
        this.setConvert(strategyConfig);
    }

    public String getMapperName() {
        return this.mapperName;
    }

    public void setMapperName(String mapperName) {
        this.mapperName = mapperName;
    }

    public String getXmlName() {
        return this.xmlName;
    }

    public void setXmlName(String xmlName) {
        this.xmlName = xmlName;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceImplName() {
        return this.serviceImplName;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerImplName() {
        return managerImplName;
    }

    public void setManagerImplName(String managerImplName) {
        this.managerImplName = managerImplName;
    }

    public void setServiceImplName(String serviceImplName) {
        this.serviceImplName = serviceImplName;
    }

    public String getControllerName() {
        return this.controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    public List<TableField> getFields() {
        return this.fields;
    }

    public void setFields(List<TableField> fields) {
        if (CollectionUtils.isNotEmpty(fields)) {
            this.fields = fields;
            Iterator var2 = fields.iterator();

            while(var2.hasNext()) {
                TableField field = (TableField)var2.next();
                if (null != field.getColumnType() && null != field.getColumnType().getPkg()) {
                    this.importPackages.add(field.getColumnType().getPkg());
                }

                if (!field.isKeyFlag()) {
                    if (field.isConvert()) {
                        this.importPackages.add(com.baomidou.mybatisplus.annotation.TableField.class.getCanonicalName());
                    }
                } else {
                    if (field.isConvert() || field.isKeyIdentityFlag()) {
                        this.importPackages.add(TableId.class.getCanonicalName());
                    }

                    if (field.isKeyIdentityFlag()) {
                        this.importPackages.add(IdType.class.getCanonicalName());
                    }
                }

                if (null != field.getFill()) {
                    this.importPackages.add(com.baomidou.mybatisplus.annotation.TableField.class.getCanonicalName());
                    this.importPackages.add(FieldFill.class.getCanonicalName());
                }
            }
        }

    }

    public List<TableField> getCommonFields() {
        return this.commonFields;
    }

    public void setCommonFields(List<TableField> commonFields) {
        this.commonFields = commonFields;
    }

    public Set<String> getImportPackages() {
        return this.importPackages;
    }

    public void setImportPackages(String pkg) {
        this.importPackages.add(pkg);
    }

    public boolean isLogicDelete(String logicDeletePropertyName) {
        return this.fields.stream().anyMatch((tf) -> {
            return tf.getName().equals(logicDeletePropertyName);
        });
    }

    public String getFieldNames() {
        if (StringUtils.isEmpty(this.fieldNames)) {
            StringBuilder names = new StringBuilder();
            IntStream.range(0, this.fields.size()).forEach((i) -> {
                TableField fd = (TableField)this.fields.get(i);
                if (i == this.fields.size() - 1) {
                    names.append(fd.getName());
                } else {
                    names.append(fd.getName()).append(", ");
                }

            });
            this.fieldNames = names.toString();
        }

        return this.fieldNames;
    }
}
