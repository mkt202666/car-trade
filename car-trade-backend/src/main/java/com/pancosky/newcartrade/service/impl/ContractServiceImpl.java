package com.pancosky.newcartrade.service.impl;

import com.pancosky.newcartrade.entity.Contract;
import com.pancosky.newcartrade.entity.User;
import com.pancosky.newcartrade.exception.BusinessException;
import com.pancosky.newcartrade.mapper.ContractMapper;
import com.pancosky.newcartrade.mapper.UserMapper;
import com.pancosky.newcartrade.service.ContractService;
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
        Contract contract = contractMapper.selectById(id);
        if (contract == null) throw new BusinessException("Contract not found");
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
    public void sign(Long id, String role, Long userId) {
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
        
        // Generate download URL if not exists
        if (contract.getFileUrl() == null || contract.getFileUrl().isEmpty()) {
            String fileName = contract.getContractNo() + ".pdf";
            String fileUrl = fileUrlPrefix + fileName;
            contract.setFileUrl(fileUrl);
            contractMapper.updateById(contract);
        }
        
        log.info("Download contract: {}", contract.getContractNo());
        return contract.getFileUrl();
    }
}
