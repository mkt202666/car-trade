package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.common.RequiresAuth;
import com.pancosky.newcartrade.common.AuthLevel;
import com.pancosky.newcartrade.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 * 描述：提供车源图片、证件材料、检测报告等文件上传接口
 * 基础路径：/api/v1/uploads
 * 认证要求：必须登录
 */
@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
@Slf4j
// @RequiresAuth(AuthLevel.PROTECTED) // 临时注释以排查路由问题
public class UploadController {

    private final FileStorageService fileStorageService;

    /**
     * 上传车源图片
     * POST /api/v1/uploads/car-image
     * 请求参数：file（表单文件字段），支持 jpg/png/webp，最大 5MB
     * 响应：{ code: 200, data: { url: "https://..." } }
     */
    @PostMapping("/car-image")
    @RequiresAuth(AuthLevel.PROTECTED)
    public ApiResponse<Map<String, String>> uploadCarImage(@RequestParam MultipartFile file) {
        try {
            // 验证文件
            validateImageFile(file);
            
            // 上传到 car-images 目录
            String fileUrl = fileStorageService.upload(file, "car-images");
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            
            log.info("车源图片上传成功: {}", fileUrl);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("车源图片上传失败", e);
            return ApiResponse.error(500, "图片上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传车源文档(检测报告/证件材料)
     * POST /api/v1/uploads/car-document?type=report|certificate_driving_license|...
     * 请求参数：file（表单文件字段）, type（查询参数，文档类型）
     * 支持格式：jpg/png/webp/pdf，最大 10MB
     */
    @PostMapping("/car-document")
    public ApiResponse<Map<String, String>> uploadCarDocument(
            @RequestParam MultipartFile file,
            @RequestParam String type) {
        try {
            // 根据type确定存储目录
            String directory = determineDirectory(type);
            
            // 验证文件
            validateDocumentFile(file, type);
            
            // 上传到对应目录
            String fileUrl = fileStorageService.upload(file, directory);
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("type", type);
            
            log.info("车源文档上传成功: type={}, url={}", type, fileUrl);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("车源文档上传失败: type={}", type, e);
            return ApiResponse.error(500, "文档上传失败: " + e.getMessage());
        }
    }

    /**
     * 根据文档类型确定存储目录
     */
    private String determineDirectory(String type) {
        if (type == null || type.isEmpty()) {
            return "car-documents";
        }
        
        if (type.contains("report") || type.contains("inspection")) {
            return "car-documents/reports";
        } else if (type.contains("driving_license")) {
            return "car-documents/certificates/driving-licenses";
        } else if (type.contains("vehicle_nameplate")) {
            return "car-documents/certificates/nameplates";
        } else if (type.contains("certificate")) {
            return "car-documents/certificates";
        } else {
            return "car-documents/" + type;
        }
    }

    /**
     * 验证图片文件
     */
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        // 检查文件大小（5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("图片大小不能超过5MB");
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("只支持图片文件(jpg/png/webp)");
        }
    }

    /**
     * 验证文档文件
     */
    private void validateDocumentFile(MultipartFile file, String type) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        // 检查文件大小（10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("文件大小不能超过10MB");
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("无法识别文件类型");
        }
        
        // 允许图片和PDF
        boolean isImage = contentType.startsWith("image/");
        boolean isPdf = "application/pdf".equals(contentType);
        
        if (!isImage && !isPdf) {
            throw new IllegalArgumentException("只支持图片(jpg/png/webp)和PDF文件");
        }
    }
}
