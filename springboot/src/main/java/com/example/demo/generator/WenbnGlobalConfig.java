package com.example.demo.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/11/23
 */
public class WenbnGlobalConfig extends GlobalConfig {

    private String outputDir = "D://";
    private boolean fileOverride = false;
    private boolean open = true;
    private boolean enableCache = false;
    private String author;
    private boolean kotlin = false;
    private boolean swagger2 = false;
    private boolean activeRecord = false;
    private boolean baseResultMap = false;
    private DateType dateType;
    private boolean baseColumnList;
    private String entityName;
    private String mapperName;
    private String xmlName;
    private String serviceName;
    private String serviceImplName;
    private String managerName;
    private String managerImplName;
    private String controllerName;
    private IdType idType;

    public WenbnGlobalConfig() {
        this.dateType = DateType.TIME_PACK;
        this.baseColumnList = false;
    }

    public String getOutputDir() {
        return this.outputDir;
    }

    public boolean isFileOverride() {
        return this.fileOverride;
    }

    public boolean isOpen() {
        return this.open;
    }

    public boolean isEnableCache() {
        return this.enableCache;
    }

    public String getAuthor() {
        return this.author;
    }

    public boolean isKotlin() {
        return this.kotlin;
    }

    public boolean isSwagger2() {
        return this.swagger2;
    }

    public boolean isActiveRecord() {
        return this.activeRecord;
    }

    public boolean isBaseResultMap() {
        return this.baseResultMap;
    }

    public DateType getDateType() {
        return this.dateType;
    }

    public boolean isBaseColumnList() {
        return this.baseColumnList;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public String getMapperName() {
        return this.mapperName;
    }

    public String getXmlName() {
        return this.xmlName;
    }

    public String getServiceName() {
        return this.serviceName;
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

    public String getControllerName() {
        return this.controllerName;
    }

    public IdType getIdType() {
        return this.idType;
    }

    public WenbnGlobalConfig setOutputDir(String outputDir) {
        this.outputDir = outputDir;
        return this;
    }

    public WenbnGlobalConfig setFileOverride(boolean fileOverride) {
        this.fileOverride = fileOverride;
        return this;
    }

    public WenbnGlobalConfig setOpen(boolean open) {
        this.open = open;
        return this;
    }

    public WenbnGlobalConfig setEnableCache(boolean enableCache) {
        this.enableCache = enableCache;
        return this;
    }

    public WenbnGlobalConfig setAuthor(String author) {
        this.author = author;
        return this;
    }

    public WenbnGlobalConfig setKotlin(boolean kotlin) {
        this.kotlin = kotlin;
        return this;
    }

    public WenbnGlobalConfig setSwagger2(boolean swagger2) {
        this.swagger2 = swagger2;
        return this;
    }

    public WenbnGlobalConfig setActiveRecord(boolean activeRecord) {
        this.activeRecord = activeRecord;
        return this;
    }

    public WenbnGlobalConfig setBaseResultMap(boolean baseResultMap) {
        this.baseResultMap = baseResultMap;
        return this;
    }

    public WenbnGlobalConfig setDateType(DateType dateType) {
        this.dateType = dateType;
        return this;
    }

    public WenbnGlobalConfig setBaseColumnList(boolean baseColumnList) {
        this.baseColumnList = baseColumnList;
        return this;
    }

    public WenbnGlobalConfig setEntityName(String entityName) {
        this.entityName = entityName;
        return this;
    }

    public WenbnGlobalConfig setMapperName(String mapperName) {
        this.mapperName = mapperName;
        return this;
    }

    public WenbnGlobalConfig setXmlName(String xmlName) {
        this.xmlName = xmlName;
        return this;
    }

    public WenbnGlobalConfig setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public WenbnGlobalConfig setServiceImplName(String serviceImplName) {
        this.serviceImplName = serviceImplName;
        return this;
    }

    public WenbnGlobalConfig setControllerName(String controllerName) {
        this.controllerName = controllerName;
        return this;
    }

    public WenbnGlobalConfig setIdType(IdType idType) {
        this.idType = idType;
        return this;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof WenbnGlobalConfig)) {
            return false;
        } else {
            WenbnGlobalConfig other = (WenbnGlobalConfig)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$outputDir = this.getOutputDir();
                Object other$outputDir = other.getOutputDir();
                if (this$outputDir == null) {
                    if (other$outputDir != null) {
                        return false;
                    }
                } else if (!this$outputDir.equals(other$outputDir)) {
                    return false;
                }

                if (this.isFileOverride() != other.isFileOverride()) {
                    return false;
                } else if (this.isOpen() != other.isOpen()) {
                    return false;
                } else if (this.isEnableCache() != other.isEnableCache()) {
                    return false;
                } else {
                    label152: {
                        Object this$author = this.getAuthor();
                        Object other$author = other.getAuthor();
                        if (this$author == null) {
                            if (other$author == null) {
                                break label152;
                            }
                        } else if (this$author.equals(other$author)) {
                            break label152;
                        }

                        return false;
                    }

                    if (this.isKotlin() != other.isKotlin()) {
                        return false;
                    } else if (this.isSwagger2() != other.isSwagger2()) {
                        return false;
                    } else if (this.isActiveRecord() != other.isActiveRecord()) {
                        return false;
                    } else if (this.isBaseResultMap() != other.isBaseResultMap()) {
                        return false;
                    } else {
                        Object this$dateType = this.getDateType();
                        Object other$dateType = other.getDateType();
                        if (this$dateType == null) {
                            if (other$dateType != null) {
                                return false;
                            }
                        } else if (!this$dateType.equals(other$dateType)) {
                            return false;
                        }

                        if (this.isBaseColumnList() != other.isBaseColumnList()) {
                            return false;
                        } else {
                            Object this$entityName = this.getEntityName();
                            Object other$entityName = other.getEntityName();
                            if (this$entityName == null) {
                                if (other$entityName != null) {
                                    return false;
                                }
                            } else if (!this$entityName.equals(other$entityName)) {
                                return false;
                            }

                            label124: {
                                Object this$mapperName = this.getMapperName();
                                Object other$mapperName = other.getMapperName();
                                if (this$mapperName == null) {
                                    if (other$mapperName == null) {
                                        break label124;
                                    }
                                } else if (this$mapperName.equals(other$mapperName)) {
                                    break label124;
                                }

                                return false;
                            }

                            Object this$xmlName = this.getXmlName();
                            Object other$xmlName = other.getXmlName();
                            if (this$xmlName == null) {
                                if (other$xmlName != null) {
                                    return false;
                                }
                            } else if (!this$xmlName.equals(other$xmlName)) {
                                return false;
                            }

                            label110: {
                                Object this$serviceName = this.getServiceName();
                                Object other$serviceName = other.getServiceName();
                                if (this$serviceName == null) {
                                    if (other$serviceName == null) {
                                        break label110;
                                    }
                                } else if (this$serviceName.equals(other$serviceName)) {
                                    break label110;
                                }

                                return false;
                            }

                            label103: {
                                Object this$serviceImplName = this.getServiceImplName();
                                Object other$serviceImplName = other.getServiceImplName();
                                if (this$serviceImplName == null) {
                                    if (other$serviceImplName == null) {
                                        break label103;
                                    }
                                } else if (this$serviceImplName.equals(other$serviceImplName)) {
                                    break label103;
                                }

                                return false;
                            }

                            ///
                            label111: {
                                Object this$managerName = this.getManagerName();
                                Object other$managerName = other.getManagerName();
                                if (this$managerName == null) {
                                    if (other$managerName == null) {
                                        break label111;
                                    }
                                } else if (this$managerName.equals(other$managerName)) {
                                    break label111;
                                }

                                return false;
                            }

                            label104: {
                                Object this$managerImplName = this.getManagerImplName();
                                Object other$managerImplName = other.getManagerImplName();
                                if (this$managerImplName == null) {
                                    if (other$managerImplName == null) {
                                        break label104;
                                    }
                                } else if (this$managerImplName.equals(other$managerImplName)) {
                                    break label104;
                                }

                                return false;
                            }

                            Object this$controllerName = this.getControllerName();
                            Object other$controllerName = other.getControllerName();
                            if (this$controllerName == null) {
                                if (other$controllerName != null) {
                                    return false;
                                }
                            } else if (!this$controllerName.equals(other$controllerName)) {
                                return false;
                            }

                            Object this$idType = this.getIdType();
                            Object other$idType = other.getIdType();
                            if (this$idType == null) {
                                if (other$idType != null) {
                                    return false;
                                }
                            } else if (!this$idType.equals(other$idType)) {
                                return false;
                            }

                            return true;
                        }
                    }
                }
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof WenbnGlobalConfig;
    }

    public int hashCode() {
        boolean PRIME = true;
//        int PRIME = true;
        int result = 1;
        Object $outputDir = this.getOutputDir();
        result = result * 59 + ($outputDir == null ? 43 : $outputDir.hashCode());
        result = result * 59 + (this.isFileOverride() ? 79 : 97);
        result = result * 59 + (this.isOpen() ? 79 : 97);
        result = result * 59 + (this.isEnableCache() ? 79 : 97);
        Object $author = this.getAuthor();
        result = result * 59 + ($author == null ? 43 : $author.hashCode());
        result = result * 59 + (this.isKotlin() ? 79 : 97);
        result = result * 59 + (this.isSwagger2() ? 79 : 97);
        result = result * 59 + (this.isActiveRecord() ? 79 : 97);
        result = result * 59 + (this.isBaseResultMap() ? 79 : 97);
        Object $dateType = this.getDateType();
        result = result * 59 + ($dateType == null ? 43 : $dateType.hashCode());
        result = result * 59 + (this.isBaseColumnList() ? 79 : 97);
        Object $entityName = this.getEntityName();
        result = result * 59 + ($entityName == null ? 43 : $entityName.hashCode());
        Object $mapperName = this.getMapperName();
        result = result * 59 + ($mapperName == null ? 43 : $mapperName.hashCode());
        Object $xmlName = this.getXmlName();
        result = result * 59 + ($xmlName == null ? 43 : $xmlName.hashCode());
        Object $serviceName = this.getServiceName();
        result = result * 59 + ($serviceName == null ? 43 : $serviceName.hashCode());
        Object $serviceImplName = this.getServiceImplName();
        result = result * 59 + ($serviceImplName == null ? 43 : $serviceImplName.hashCode());
        Object $controllerName = this.getControllerName();
        result = result * 59 + ($controllerName == null ? 43 : $controllerName.hashCode());
        Object $idType = this.getIdType();
        result = result * 59 + ($idType == null ? 43 : $idType.hashCode());
        return result;
    }

    public String toString() {
        return "WenbnGlobalConfig(outputDir=" + this.getOutputDir() + ", fileOverride=" + this.isFileOverride() + ", open=" + this.isOpen() + ", enableCache=" + this.isEnableCache() + ", author=" + this.getAuthor() + ", kotlin=" + this.isKotlin() + ", swagger2=" + this.isSwagger2() + ", activeRecord=" + this.isActiveRecord() + ", baseResultMap=" + this.isBaseResultMap() + ", dateType=" + this.getDateType() + ", baseColumnList=" + this.isBaseColumnList() + ", entityName=" + this.getEntityName() + ", mapperName=" + this.getMapperName() + ", xmlName=" + this.getXmlName() + ", serviceName=" + this.getServiceName() + ", serviceImplName=" + this.getServiceImplName() + ", controllerName=" + this.getControllerName() + ", idType=" + this.getIdType() + ")";
    }
}
