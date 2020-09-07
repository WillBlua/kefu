package com.chatopera.cc.util.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <p> ClassName: com.framework.core.config.minio.MinioProperties.java </p>
 * <p> Description : MinioProperties.java miniIO配置文件</p>
 * <p> Author : jfwu </p>
 * <p> Version : 1.0 </p>
 * <p> Create Time : 2020/4/24 16:43 </p>
 * <p> Author Email: <a href="mailTo:2391923921@qq.com">jfwu</a> </p>
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    /**
     * minio 服务地址 http://ip:port
     */
    private String url;
    /**
     * 用户名
     */
    private String accessKey;
    /**
     * 密码
     */
    private String secretKey;
    /**
     * 域名称
     */
    private String bucketName;
}
