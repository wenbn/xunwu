package com.example.demo.generator;

import com.baomidou.mybatisplus.generator.config.TemplateConfig;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/11/23
 */
public class WenbnTemplateConfig extends TemplateConfig {

    private String entity = "/templates/entity.java";
    private String entityKt = "/templates/entity.kt";
    private String service = "/templates/service.java";
    private String serviceImpl = "/templates/serviceImpl.java";
    private String manager = "/templates/manager.java";
    private String managerImpl = "/templates/managerImpl.java";
    private String mapper = "/templates/mapper.java";
    private String xml = "/templates/mapper.xml";
    private String controller = "/templates/controller.java";

    public WenbnTemplateConfig() {
    }

    public String getEntity(boolean kotlin) {
        return kotlin ? this.entityKt : this.entity;
    }

    public WenbnTemplateConfig setEntityKt(String entityKt) {
        this.entityKt = entityKt;
        return this;
    }

    public WenbnTemplateConfig setEntity(String entity) {
        this.entity = entity;
        return this;
    }

    public String getService() {
        return this.service;
    }

    public WenbnTemplateConfig setService(String service) {
        this.service = service;
        return this;
    }

    public String getServiceImpl() {
        return this.serviceImpl;
    }

    public WenbnTemplateConfig setServiceImpl(String serviceImpl) {
        this.serviceImpl = serviceImpl;
        return this;
    }


    public WenbnTemplateConfig setManager(String manager) {
        this.manager = manager;
        return this;
    }

    public WenbnTemplateConfig setManagerImpl(String managerImpl) {
        this.managerImpl = managerImpl;
        return this;
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

    public WenbnTemplateConfig setMapper(String mapper) {
        this.mapper = mapper;
        return this;
    }

    public String getXml() {
        return this.xml;
    }

    public WenbnTemplateConfig setXml(String xml) {
        this.xml = xml;
        return this;
    }

    public String getController() {
        return this.controller;
    }

    public WenbnTemplateConfig setController(String controller) {
        this.controller = controller;
        return this;
    }
}
