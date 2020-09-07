package com.chatopera.cc.util.minio;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p> ClassName: com.framework.core.config.minio.MinioAutoConfiguration.java </p>
 * <p> Description : MinioAutoConfiguration.java minio 初始化client</p>
 * <p> Author : jfwu </p>
 * <p> Version : 1.0 </p>
 * <p> Create Time : 2020/4/24 17:57 </p>
 * <p> Author Email: <a href="mailTo:2391923921@qq.com">jfwu</a> </p>
 */
@Configuration
public class MinioAutoConfiguration {

    @Autowired
    private MinioProperties properties;

    @Bean
    public MinioClient minioClient() throws InvalidPortException, InvalidEndpointException {
        MinioClient client = new MinioClient(properties.getUrl(), properties.getAccessKey(), properties.getSecretKey());
        return client;
    }

}