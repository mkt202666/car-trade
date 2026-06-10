package com.pancosky.newcartrade.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储服务接口
 * 当前实现：本地存储
 * 生产对接：阿里云 OSS / 腾讯云 COS / MinIO
 */
public interface FileStorageService {

    /**
     * 上传文件
     * @param file 文件
     * @param directory 存储目录（如 avatars, contracts, car-images）
     * @return 可访问的文件 URL
     */
    String upload(MultipartFile file, String directory);

    /**
     * 删除文件
     * @param fileUrl 文件 URL
     */
    void delete(String fileUrl);

    /**
     * 获取文件访问 URL（可能涉及签名 URL）
     * @param fileUrl 文件标识
     * @return 可访问的 URL
     */
    String getUrl(String fileUrl);
}
