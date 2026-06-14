package com.pancosky.newcartrade.service.impl;

import com.pancosky.newcartrade.entity.Contract;
import com.pancosky.newcartrade.entity.User;
import com.pancosky.newcartrade.exception.BusinessException;
import com.pancosky.newcartrade.mapper.ContractMapper;
import com.pancosky.newcartrade.mapper.UserMapper;
import com.pancosky.newcartrade.security.OwnerAssert;
import com.pancosky.newcartrade.service.ContractService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.ContractDetailVO;
import com.pancosky.newcartrade.vo.ContractVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractMapper contractMapper;
    private final UserMapper userMapper;

    @Value("${contract.file.url.prefix:http://localhost:8080/files/contracts/}")
    private String fileUrlPrefix;

    private String generateContractNo() {
        return "CT" + System.currentTimeMillis() + new Random().nextInt(1000);
    }

    @Override
    @Transactional
    public ContractVO create(String orderId) {
        Contract contract = new Contract();
        contract.setOrderId(orderId);
        contract.setContractNo(generateContractNo());
        contract.setStatus("PENDING_SIGN");
        contract.setBuyerSigned(false);
        contract.setSellerSigned(false);
        contractMapper.insert(contract);
        ContractVO vo = new ContractVO();
        vo.setId(contract.getId());
        vo.setContractNo(contract.getContractNo());
        vo.setOrderId(contract.getOrderId());
        vo.setStatus(contract.getStatus());
        vo.setBuyerSigned(contract.getBuyerSigned());
        vo.setSellerSigned(contract.getSellerSigned());
        vo.setCreatedAt(contract.getCreatedAt());
        return vo;
    }

    @Override
    public ContractDetailVO detail(Long id) {
        OwnerAssert.assertValidId(id);
        Contract contract = contractMapper.selectById(id);
        if (contract == null) throw new BusinessException("Contract not found");
        // IDOR 防护：仅买家、卖家、平台 admin 可读合同
        Long currentUserId = SecurityUtils.getCurrentUserId();
        OwnerAssert.assertBuyerOrSeller(currentUserId, contract.getBuyerId(), contract.getSellerId());
        ContractDetailVO vo = new ContractDetailVO();
        vo.setId(contract.getId());
        vo.setContractNo(contract.getContractNo());
        vo.setOrderId(contract.getOrderId());
        vo.setTitle(contract.getTitle());
        vo.setStatus(contract.getStatus());
        vo.setBuyerSigned(contract.getBuyerSigned());
        vo.setSellerSigned(contract.getSellerSigned());
        vo.setCreatedAt(contract.getCreatedAt());
        vo.setBuyerId(contract.getBuyerId());
        vo.setSellerId(contract.getSellerId());
        vo.setContent(contract.getContent());
        vo.setFileUrl(contract.getFileUrl());
        
        if (contract.getBuyerId() != null) {
            User buyer = userMapper.selectById(contract.getBuyerId());
            if (buyer != null) {
                vo.setBuyerName(buyer.getNickname());
            }
        }
        if (contract.getSellerId() != null) {
            User seller = userMapper.selectById(contract.getSellerId());
            if (seller != null) {
                vo.setSellerName(seller.getNickname());
            }
        }
        
        return vo;
    }

    @Override
    @Transactional
    public void sign(Long id, String role, Long userId, String signatureUrl) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) throw new BusinessException("Contract not found");
        
        // Verify user matches the role
        if ("BUYER".equalsIgnoreCase(role)) {
            if (contract.getBuyerId() != null && !contract.getBuyerId().equals(userId)) {
                throw new BusinessException("You are not the buyer of this contract");
            }
            if (contract.getBuyerSigned()) {
                throw new BusinessException("Buyer has already signed");
            }
            contract.setBuyerSigned(true);
            contract.setBuyerSignedAt(LocalDateTime.now());
            if (signatureUrl != null && !signatureUrl.isEmpty()) {
                contract.setBuyerSignatureUrl(signatureUrl);
                log.info("Contract {} buyer signature saved: {}", id, signatureUrl);
            }
            log.info("Contract {} signed by buyer {}", id, userId);
        } else if ("SELLER".equalsIgnoreCase(role)) {
            if (contract.getSellerId() != null && !contract.getSellerId().equals(userId)) {
                throw new BusinessException("You are not the seller of this contract");
            }
            if (contract.getSellerSigned()) {
                throw new BusinessException("Seller has already signed");
            }
            contract.setSellerSigned(true);
            contract.setSellerSignedAt(LocalDateTime.now());
            if (signatureUrl != null && !signatureUrl.isEmpty()) {
                contract.setSellerSignatureUrl(signatureUrl);
                log.info("Contract {} seller signature saved: {}", id, signatureUrl);
            }
            log.info("Contract {} signed by seller {}", id, userId);
        } else {
            throw new BusinessException("Invalid role, must be BUYER or SELLER");
        }
        
        // Update contract status: SIGNED only when both parties have signed
        if (contract.getBuyerSigned() && contract.getSellerSigned()) {
            contract.setStatus("SIGNED");
        }
        
        contractMapper.updateById(contract);
    }

    @Override
    public String download(Long id) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) throw new BusinessException("Contract not found");

        // 生成合同文件（HTML 格式，可浏览器打印为 PDF）
        if (contract.getFileUrl() == null || contract.getFileUrl().isEmpty()) {
            try {
                java.io.File dir = new java.io.File("uploads/contracts");
                if (!dir.exists()) dir.mkdirs();

                String fileName = contract.getContractNo() + ".html";
                java.io.File file = new java.io.File(dir, fileName);

                User buyer = contract.getBuyerId() != null ? userMapper.selectById(contract.getBuyerId()) : null;
                User seller = contract.getSellerId() != null ? userMapper.selectById(contract.getSellerId()) : null;

                String html = buildContractHtml(contract, buyer, seller);
                java.nio.file.Files.write(file.toPath(), html.getBytes(java.nio.charset.StandardCharsets.UTF_8));

                String fileUrl = "/static-uploads/contracts/" + fileName;
                contract.setFileUrl(fileUrl);
                contractMapper.updateById(contract);
                log.info("Generated contract file: {}", fileUrl);
            } catch (java.io.IOException e) {
                log.error("Failed to generate contract file for {}", contract.getContractNo(), e);
                throw new BusinessException("合同文件生成失败");
            }
        }

        return contract.getFileUrl();
    }

    private String buildContractHtml(Contract contract, User buyer, User seller) {
        String buyerName = buyer != null ? buyer.getNickname() : "未指定";
        String sellerName = seller != null ? seller.getNickname() : "未指定";
        String content = contract.getContent() != null ? contract.getContent() : "（合同内容待补充）";

        return "<!DOCTYPE html><html><head><meta charset='UTF-8'>" +
                "<title>电子合同 - " + contract.getContractNo() + "</title>" +
                "<style>body{font-family:sans-serif;max-width:800px;margin:40px auto;padding:20px;line-height:1.8}" +
                "h1{text-align:center;border-bottom:2px solid #333;padding-bottom:10px}" +
                ".info{margin:10px 0}.sign{margin-top:30px;display:flex;justify-content:space-between}" +
                ".sign div{width:45%;border-top:1px solid #999;padding-top:10px;text-align:center}" +
                "@media print{body{margin:0}}</style></head><body>" +
                "<h1>5D好车 电子合同</h1>" +
                "<div class='info'><strong>合同编号：</strong>" + contract.getContractNo() + "</div>" +
                "<div class='info'><strong>合同状态：</strong>" + contract.getStatus() + "</div>" +
                "<div class='info'><strong>创建时间：</strong>" + contract.getCreatedAt() + "</div>" +
                "<hr>" +
                "<div class='info'><strong>卖方：</strong>" + sellerName + "</div>" +
                "<div class='info'><strong>买方：</strong>" + buyerName + "</div>" +
                "<hr>" +
                "<h3>合同条款</h3>" +
                "<div>" + content + "</div>" +
                "<hr>" +
                "<div class='sign'>" +
                "<div>卖方签署：" + (contract.getSellerSigned() ? "✅ 已签署 " + contract.getSellerSignedAt() : "❌ 待签署") + "</div>" +
                "<div>买方签署：" + (contract.getBuyerSigned() ? "✅ 已签署 " + contract.getBuyerSignedAt() : "❌ 待签署") + "</div>" +
                "</div>" +
                "<p style='margin-top:40px;color:#666;font-size:12px;text-align:center'>本合同由5D好车平台生成，具有电子签约法律效力</p>" +
                "</body></html>";
    }
}
