package com.example.demo.generator;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.PackageConfig;

import java.util.Map;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/11/23
 */
public class WenbnPackageConfig extends PackageConfig {

    private String parent = "com.baomidou";
    private String moduleName = null;
    private String entity = "entity";
    private String service = "service";
    private String serviceImpl = "service.impl";
    private String manager = "manager";
    private String managerImpl = "manager.impl";
    private String mapper = "mapper";
    private String xml = "mapper.xml";
    private String controller = "controller";
    private Map<String, String> pathInfo;

    public String getParent() {
        return StringUtils.isNotEmpty(this.moduleName) ? this.parent + "." + this.moduleName : this.parent;
    }

    public WenbnPackageConfig() {
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public String getEntity() {
        return this.entity;
    }

    public String getService() {
        return this.service;
    }

    public String getServiceImpl() {
        return this.serviceImpl;
    }

    public String getManager() {
        return manager;
    }

    public String getManagerImpl() {
        return managerImpl;
    }

    public String getMapper() {
        return this.mapper;
    }

    public String getXml() {
        return this.xml;
    }

    public String getController() {
        return this.controller;
    }

    public Map<String, String> getPathInfo() {
        return this.pathInfo;
    }

    public WenbnPackageConfig setParent(String parent) {
        this.parent = parent;
        return this;
    }

    public WenbnPackageConfig setModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    public WenbnPackageConfig setEntity(String entity) {
        this.entity = entity;
        return this;
    }

    public WenbnPackageConfig setService(String service) {
        this.service = service;
        return this;
    }

    public WenbnPackageConfig setServiceImpl(String serviceImpl) {
        this.serviceImpl = serviceImpl;
        return this;
    }
    public WenbnPackageConfig setManager(String manager) {
        this.manager = manager;
        return this;
    }

    public WenbnPackageConfig setManagerImpl(String managerImpl) {
        this.managerImpl = managerImpl;
        return this;
    }

    public WenbnPackageConfig setMapper(String mapper) {
        this.mapper = mapper;
        return this;
    }

    public WenbnPackageConfig setXml(String xml) {
        this.xml = xml;
        return this;
    }

    public WenbnPackageConfig setController(String controller) {
        this.controller = controller;
        return this;
    }

    public WenbnPackageConfig setPathInfo(Map<String, String> pathInfo) {
        this.pathInfo = pathInfo;
        return this;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof WenbnPackageConfig)) {
            return false;
        } else {
            WenbnPackageConfig other = (WenbnPackageConfig)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label119: {
                    Object this$parent = this.getParent();
                    Object other$parent = other.getParent();
                    if (this$parent == null) {
                        if (other$parent == null) {
                            break label119;
                        }
                    } else if (this$parent.equals(other$parent)) {
                        break label119;
                    }

                    return false;
                }

                Object this$moduleName = this.getModuleName();
                Object other$moduleName = other.getModuleName();
                if (this$moduleName == null) {
                    if (other$moduleName != null) {
                        return false;
                    }
                } else if (!this$moduleName.equals(other$moduleName)) {
                    return false;
                }

                label105: {
                    Object this$entity = this.getEntity();
                    Object other$entity = other.getEntity();
                    if (this$entity == null) {
                        if (other$entity == null) {
                            break label105;
                        }
                    } else if (this$entity.equals(other$entity)) {
                        break label105;
                    }

                    return false;
                }

                Object this$service = this.getService();
                Object other$service = other.getService();
                if (this$service == null) {
                    if (other$service != null) {
                        return false;
                    }
                } else if (!this$service.equals(other$service)) {
                    return false;
                }

                label91: {
                    Object this$serviceImpl = this.getServiceImpl();
                    Object other$serviceImpl = other.getServiceImpl();
                    if (this$serviceImpl == null) {
                        if (other$serviceImpl == null) {
                            break label91;
                        }
                    } else if (this$serviceImpl.equals(other$serviceImpl)) {
                        break label91;
                    }

                    return false;
                }

                Object this$manager = this.getManager();
                Object other$manager = other.getManager();
                if (this$service == null) {
                    if (other$service != null) {
                        return false;
                    }
                } else if (!this$manager.equals(other$manager)) {
                    return false;
                }

                label93: {
                    Object this$managerImpl = this.getManagerImpl();
                    Object other$managerImpl = other.getManagerImpl();
                    if (this$managerImpl == null) {
                        if (other$managerImpl == null) {
                            break label93;
                        }
                    } else if (this$managerImpl.equals(other$managerImpl)) {
                        break label93;
                    }

                    return false;
                }


                Object this$mapper = this.getMapper();
                Object other$mapper = other.getMapper();
                if (this$mapper == null) {
                    if (other$mapper != null) {
                        return false;
                    }
                } else if (!this$mapper.equals(other$mapper)) {
                    return false;
                }

                label77: {
                    Object this$xml = this.getXml();
                    Object other$xml = other.getXml();
                    if (this$xml == null) {
                        if (other$xml == null) {
                            break label77;
                        }
                    } else if (this$xml.equals(other$xml)) {
                        break label77;
                    }

                    return false;
                }

                label70: {
                    Object this$controller = this.getController();
                    Object other$controller = other.getController();
                    if (this$controller == null) {
                        if (other$controller == null) {
                            break label70;
                        }
                    } else if (this$controller.equals(other$controller)) {
                        break label70;
                    }

                    return false;
                }

                Object this$pathInfo = this.getPathInfo();
                Object other$pathInfo = other.getPathInfo();
                if (this$pathInfo == null) {
                    if (other$pathInfo != null) {
                        return false;
                    }
                } else if (!this$pathInfo.equals(other$pathInfo)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof WenbnPackageConfig;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $parent = this.getParent();
        result = result * 59 + ($parent == null ? 43 : $parent.hashCode());
        Object $moduleName = this.getModuleName();
        result = result * 59 + ($moduleName == null ? 43 : $moduleName.hashCode());
        Object $entity = this.getEntity();
        result = result * 59 + ($entity == null ? 43 : $entity.hashCode());
        Object $service = this.getService();
        result = result * 59 + ($service == null ? 43 : $service.hashCode());
        Object $serviceImpl = this.getServiceImpl();
        result = result * 59 + ($serviceImpl == null ? 43 : $serviceImpl.hashCode());

        Object $manager = this.getManager();
        result = result * 59 + ($service == null ? 43 : $manager.hashCode());
        Object $managerImpl = this.getManagerImpl();
        result = result * 59 + ($managerImpl == null ? 43 : $managerImpl.hashCode());

        Object $mapper = this.getMapper();
        result = result * 59 + ($mapper == null ? 43 : $mapper.hashCode());
        Object $xml = this.getXml();
        result = result * 59 + ($xml == null ? 43 : $xml.hashCode());
        Object $controller = this.getController();
        result = result * 59 + ($controller == null ? 43 : $controller.hashCode());
        Object $pathInfo = this.getPathInfo();
        result = result * 59 + ($pathInfo == null ? 43 : $pathInfo.hashCode());
        return result;
    }

    public String toString() {
        return "PackageConfig(parent=" + this.getParent() + ", moduleName=" + this.getModuleName() + ", entity=" + this.getEntity() + ", service=" + this.getService() + ", serviceImpl=" + this.getServiceImpl() + ", mapper=" + this.getMapper() + ", xml=" + this.getXml() + ", controller=" + this.getController() + ", pathInfo=" + this.getPathInfo() + ")";
    }

}
