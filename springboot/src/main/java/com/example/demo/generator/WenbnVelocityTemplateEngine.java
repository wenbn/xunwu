package com.example.demo.generator;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Properties;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/11/23
 */
public class WenbnVelocityTemplateEngine extends WenbnAbstractTemplateEngine {

    private static final String DOT_VM = ".vm";
    private VelocityEngine velocityEngine;

    public WenbnVelocityTemplateEngine() {
    }

    public WenbnVelocityTemplateEngine init(WenbnConfigBuilder configBuilder) {
        super.init(configBuilder);
        if (null == this.velocityEngine) {
            Properties p = new Properties();
            p.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            p.setProperty("file.resource.loader.path", "");
            p.setProperty("UTF-8", ConstVal.UTF8);
            p.setProperty("input.encoding", ConstVal.UTF8);
            p.setProperty("file.resource.loader.unicode", "true");
            this.velocityEngine = new VelocityEngine(p);
        }

        return this;
    }

    public void writer(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception {
        if (!StringUtils.isEmpty(templatePath)) {
            Template template = this.velocityEngine.getTemplate(templatePath, ConstVal.UTF8);
            FileOutputStream fos = new FileOutputStream(outputFile);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, ConstVal.UTF8));
            template.merge(new VelocityContext(objectMap), writer);
            writer.close();
            logger.debug("模板:" + templatePath + ";  文件:" + outputFile);
        }
    }

    public String templateFilePath(String filePath) {
        if (null != filePath && !filePath.contains(".vm")) {
            StringBuilder fp = new StringBuilder();
            fp.append(filePath).append(".vm");
            return fp.toString();
        } else {
            return filePath;
        }
    }
}
