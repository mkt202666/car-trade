package com.pancosky.cartradeadmin.controller;

import com.pancosky.cartradeadmin.common.ApiResponse;
import com.pancosky.cartradeadmin.common.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/uploads")
@Slf4j
public class AdminUploadController {

    @PostMapping("/image")
    public ApiResponse<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("只支持图片文件上传");
        }

        // Validate file size (max 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new BusinessException("文件大小不能超过10MB");
        }

        try {
            // Generate unique filename
            String originalName = file.getOriginalFilename();
            String ext = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : ".jpg";
            String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

            // Create upload directory
            Path uploadDir = Path.of("uploads", "images");
            Files.createDirectories(uploadDir);

            // Save file
            Path filePath = uploadDir.resolve(fileName);
            file.transferTo(filePath.toFile());

            // Return URL
            String url = "/uploads/images/" + fileName;
            return ApiResponse.success(Map.of("url", url, "fileName", fileName));
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }
}
