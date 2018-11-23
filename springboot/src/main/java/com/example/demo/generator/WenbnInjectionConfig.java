package com.example.demo.generator;

import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;

import java.util.List;
import java.util.Map;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/11/23
 */
public abstract class WenbnInjectionConfig {

    private WenbnConfigBuilder config;
    private Map<String, Object> map;
    private List<FileOutConfig> fileOutConfigList;
    private WenbnIFileCreate fileCreate;

    public abstract void initMap();

    public WenbnInjectionConfig() {
    }

    public WenbnConfigBuilder getConfig() {
        return this.config;
    }

    public Map<String, Object> getMap() {
        return this.map;
    }

    public List<FileOutConfig> getFileOutConfigList() {
        return this.fileOutConfigList;
    }

    public WenbnIFileCreate getFileCreate() {
        return this.fileCreate;
    }


    public WenbnInjectionConfig setConfig(WenbnConfigBuilder config) {
        this.config = config;
        return this;
    }

    public WenbnInjectionConfig setMap(Map<String, Object> map) {
        this.map = map;
        return this;
    }

    public WenbnInjectionConfig setFileOutConfigList(List<FileOutConfig> fileOutConfigList) {
        this.fileOutConfigList = fileOutConfigList;
        return this;
    }

    public WenbnInjectionConfig setFileCreate(WenbnIFileCreate fileCreate) {
        this.fileCreate = fileCreate;
        return this;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof WenbnInjectionConfig)) {
            return false;
        } else {
            WenbnInjectionConfig other = (WenbnInjectionConfig)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label59: {
                    Object this$config = this.getConfig();
                    Object other$config = other.getConfig();
                    if (this$config == null) {
                        if (other$config == null) {
                            break label59;
                        }
                    } else if (this$config.equals(other$config)) {
                        break label59;
                    }

                    return false;
                }

                Object this$map = this.getMap();
                Object other$map = other.getMap();
                if (this$map == null) {
                    if (other$map != null) {
                        return false;
                    }
                } else if (!this$map.equals(other$map)) {
                    return false;
                }

                Object this$fileOutConfigList = this.getFileOutConfigList();
                Object other$fileOutConfigList = other.getFileOutConfigList();
                if (this$fileOutConfigList == null) {
                    if (other$fileOutConfigList != null) {
                        return false;
                    }
                } else if (!this$fileOutConfigList.equals(other$fileOutConfigList)) {
                    return false;
                }

                Object this$fileCreate = this.getFileCreate();
                Object other$fileCreate = other.getFileCreate();
                if (this$fileCreate == null) {
                    if (other$fileCreate != null) {
                        return false;
                    }
                } else if (!this$fileCreate.equals(other$fileCreate)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof WenbnInjectionConfig;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $config = this.getConfig();
        result = result * 59 + ($config == null ? 43 : $config.hashCode());
        Object $map = this.getMap();
        result = result * 59 + ($map == null ? 43 : $map.hashCode());
        Object $fileOutConfigList = this.getFileOutConfigList();
        result = result * 59 + ($fileOutConfigList == null ? 43 : $fileOutConfigList.hashCode());
        Object $fileCreate = this.getFileCreate();
        result = result * 59 + ($fileCreate == null ? 43 : $fileCreate.hashCode());
        return result;
    }

    public String toString() {
        return "InjectionConfig(config=" + this.getConfig() + ", map=" + this.getMap() + ", fileOutConfigList=" + this.getFileOutConfigList() + ", fileCreate=" + this.getFileCreate() + ")";
    }
}
