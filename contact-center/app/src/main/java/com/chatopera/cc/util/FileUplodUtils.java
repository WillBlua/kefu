package com.chatopera.cc.util;

import com.alibaba.fastjson.JSONObject;
import com.chatopera.cc.util.minio.MinioProperties;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p> ClassName: com.framework.core.utils.FileUplodUtils.java </p>
 * <p> Description : FileUplodUtils.java 文件上传工具类</p>
 * <p> Author : jfwu </p>
 * <p> Version : 1.0 </p>
 * <p> Create Time : 2020/4/29 17:22 </p>
 * <p> Author Email: <a href="mailTo:2391923921@qq.com">jfwu</a> </p>
 */
@Slf4j
public class FileUplodUtils {

    private final static MinioClient client = SpringContextUtils.getBean(MinioClient.class);
    private final static MinioProperties properties = SpringContextUtils.getBean(MinioProperties.class);

    /**
     * 文件上传
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static Map upload(MultipartFile file) throws Exception {
        String originalFilename = file.getOriginalFilename();
        //得到文件流
        InputStream in = file.getInputStream();
        //类型
        String contentType = file.getContentType();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID() + suffix;
        //判断文件夹是否存在
        boolean exists = client.bucketExists(properties.getBucketName());
        //不存在则创建文件夹
        if (!exists) {
            client.makeBucket(properties.getBucketName());
        }
        //上传
        client.putObject(properties.getBucketName(), fileName, in, contentType);
        Map<String, Object> data = new HashMap<>();
        String url = client.getObjectUrl(properties.getBucketName(),fileName);
        data.put("originalFilename", originalFilename);
        data.put("fileName", fileName);
        data.put("url", fileName);
//        data.put("url",baseUrl + fileName);
        log.debug("上传成功：{}--{}", fileName, originalFilename);
        return data;
    }


    public static JSONObject upload(InputStream inputStream, String ext, String originalFilename, String baseUrl) throws Exception {
//        String originalFilename = file.getOriginalFilename();
        //得到文件流
//        InputStream in = file.getInputStream();
        //类型

//        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID() + "."+ext;
        //判断文件夹是否存在
        boolean exists = client.bucketExists(properties.getBucketName());
        //不存在则创建文件夹
        if (!exists) {
            client.makeBucket(properties.getBucketName());
        }
        //上传
        client.putObject(properties.getBucketName(), fileName, inputStream, ext);
        JSONObject data = new JSONObject();
        String url=client.getObjectUrl(properties.getBucketName(),fileName);
        data.put("originalFilename", originalFilename);
        data.put("fileName", fileName);
        data.put("url",baseUrl + fileName);
        log.debug("上传成功：{}--{}", fileName, originalFilename);
        return data;
    }

    /**
     *  根据文件名获取下载地址
     * @param fileName
     * @return
     * @throws Exception
     */
    public static String getFileUrl(String fileName) throws Exception{
        String url=client.getObjectUrl(properties.getBucketName(),fileName);
        return  url;
    }

    /**
     * 文件下载
     *
     * @param response
     * @param fileName
     */
    public static void download(HttpServletResponse response, String fileName) {
        InputStream in = null;
        try {
            //获取文件对象 stat原信息
            ObjectStat stat = client.statObject(properties.getBucketName(), fileName);
            in = client.getObject(properties.getBucketName(), fileName);
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setContentType(stat.contentType());
            response.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
//            response.setContentType("application/force-download; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            IOUtils.copy(in, response.getOutputStream());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public static void deleteFile(String fileName) throws Exception{
        client.removeObject(properties.getBucketName(),fileName);
    }

    /**
     * 文件预览
     *
     * @param response
     * @param fileName
     */
    public static void view(HttpServletResponse response, String fileName){
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            ObjectStat stat = client.statObject(properties.getBucketName(), fileName);
            inputStream = client.getObject(properties.getBucketName(), fileName);
            response.setContentType(stat.contentType());
            outputStream = response.getOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            response.flushBuffer();
        } catch (Exception e) {
            log.error("预览图片失败" + e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
}
