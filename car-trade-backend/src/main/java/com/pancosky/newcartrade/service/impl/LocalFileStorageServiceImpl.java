package com.pancosky.newcartrade.service.impl;

import com.pancosky.newcartrade.exception.BusinessException;
import com.pancosky.newcartrade.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 文件存储 - 本地实现
 * 生产环境替换为 OSS 实现时，只需新建 OssFileStorageService 并加 @Primary 注解即可
 */
@Slf4j
@Service("localFileStorageService")
@Primary
public class LocalFileStorageServiceImpl implements FileStorageService {

    private static final String BASE_DIR = "uploads";

    @Override
    public String upload(MultipartFile file, String directory) {
        if (file == null || file.isEmpty()) throw new BusinessException("文件不能为空");

        try {
            String originalName = file.getOriginalFilename();
            String ext = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : ".bin";
            String fileName = directory + "_" + System.currentTimeMillis() + ext;

            File dir = new File(BASE_DIR + "/" + directory);
            if (!dir.exists()) dir.mkdirs();

            File dest = new File(dir, fileName);
            file.transferTo(dest);

            String url = "/" + BASE_DIR + "/" + directory + "/" + fileName;
            log.info("File uploaded to local: {}", url);
            return url;
        } catch (IOException e) {
            log.error("File upload failed", e);
            throw new BusinessException("文件上传失败");
        }
    }

    @Override
    public void delete(String fileUrl) {
        if (fileUrl == null) return;
        try {
            File file = new File(fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl);
            if (file.exists()) {
                file.delete();
                log.info("File deleted: {}", fileUrl);
            }
        } catch (Exception e) {
            log.warn("File delete failed: {}", fileUrl, e);
        }
    }

    @Override
    public String getUrl(String fileUrl) {
        // 本地存储直接返回路径
        return fileUrl;
    }
}
