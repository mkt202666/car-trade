package com.pancosky.newcartrade.controller;
import com.pancosky.newcartrade.common.RequiresAuth;
import com.pancosky.newcartrade.common.AuthLevel;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.service.ContractService;
import com.pancosky.newcartrade.service.FileStorageService;
import com.pancosky.newcartrade.vo.ContractDetailVO;
import com.pancosky.newcartrade.vo.ContractVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 合同管理控制器
 * 描述：提供独立合同的创建、详情、签署与下载接口（适用于独立于订单流程之外的合同场景）。
 * 基础路径：/api/v1/contracts
 * 认证要求：全部接口必须登录。
 */
@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
@Slf4j
@RequiresAuth(AuthLevel.PROTECTED)
public class ContractController {

    private final ContractService contractService;
    private final FileStorageService fileStorageService;

    /**
     * 创建合同
     * HTTP: POST /api/v1/contracts
     * 请求参数：body.orderId（关联订单号）
     * 响应：ApiResponse&lt;ContractVO&gt; —— 根据订单及车源信息生成合同后返回基本信息。
     * 认证要求：必须登录；一般由卖家或平台发起。
     */
    @PostMapping
    public ApiResponse<ContractVO> create(@RequestBody Map<String, String> body) {
        return ApiResponse.success(contractService.create(body.get("orderId")));
    }

    /**
     * 合同详情
     * HTTP: GET /api/v1/contracts/{id}
     * 请求参数：id（path，合同ID）
     * 响应：ApiResponse&lt;ContractDetailVO&gt; —— 合同正文、签署状态、签署时间、签署人信息等。
     * 认证要求：必须登录；仅合同当事人或管理员可见。
     */
    @GetMapping("/{id}")
    public ApiResponse<ContractDetailVO> detail(@PathVariable Long id) {
        return ApiResponse.success(contractService.detail(id));
    }

    /**
     * 签署合同
     * HTTP: PUT /api/v1/contracts/{id}/sign?role=BUYER&userId=123&signatureUrl=https://...
     * 请求参数：id（path，合同ID）；role（query，BUYER/SELLER/PLATFORM）；
     *         userId（可选，指定签署人ID，若不指定则使用当前登录用户）；
     *         signatureUrl（可选，手写签名图片URL）。
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；必须是合同对应角色才能签署。
     */
    @PutMapping("/{id}/sign")
    public ApiResponse<Void> sign(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String signatureUrl) {
        contractService.sign(id, role, userId, signatureUrl);
        return ApiResponse.success();
    }

    /**
     * 下载合同文件
     * HTTP: GET /api/v1/contracts/{id}/download
     * 请求参数：id（path，合同ID）
     * 响应：ApiResponse&lt;String&gt; —— 返回合同文件的临时下载 URL（PDF/图片）。
     * 认证要求：必须登录；仅合同当事人或管理员可见。
     */
    @GetMapping("/{id}/download")
    public ApiResponse<String> download(@PathVariable Long id) {
        return ApiResponse.success(contractService.download(id));
    }

    /**
     * 上传合同签名图片
     * HTTP: POST /api/v1/contracts/{id}/upload-signature
     * 请求参数：id（path，合同ID）；file（multipart file，签名图片PNG/JPG）
     * 响应：ApiResponse&lt;Map&gt; —— {signatureUrl: "https://..."}
     * 认证要求：必须登录；必须是合同当事人
     */
    @PostMapping("/{id}/upload-signature")
    public ApiResponse<Map<String, String>> uploadSignature(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return ApiResponse.error(400, "签名图片不能为空");
            }
            if (!file.getContentType().startsWith("image/")) {
                return ApiResponse.error(400, "只支持图片格式");
            }
            if (file.getSize() > 5 * 1024 * 1024) {
                return ApiResponse.error(400, "签名图片不能超过5MB");
            }

            // 上传签名图片
            String signatureUrl = fileStorageService.upload(file, "contract-signatures");
            
            Map<String, String> result = new HashMap<>();
            result.put("signatureUrl", signatureUrl);
            
            log.info("合同签名上传成功: contractId={}, url={}", id, signatureUrl);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("合同签名上传失败: contractId={}", id, e);
            return ApiResponse.error(500, "签名上传失败: " + e.getMessage());
        }
    }
}
