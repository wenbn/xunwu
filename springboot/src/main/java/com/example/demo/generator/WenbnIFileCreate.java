package com.example.demo.generator;

import com.baomidou.mybatisplus.extension.toolkit.PackageHelper;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.rules.FileType;

import java.io.File;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/11/23
 */
public interface WenbnIFileCreate {
    boolean isCreate(WenbnConfigBuilder var1, WenbnFileType var2, String var3);

    default void checkDir(String filePath) {
        File file = new File(filePath);
        boolean exist = file.exists();
        if (!exist) {
            PackageHelper.mkDir(file.getParentFile());
        }

    }
}
