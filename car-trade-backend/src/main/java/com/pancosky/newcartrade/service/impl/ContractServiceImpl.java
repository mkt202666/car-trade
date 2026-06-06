package com.pancosky.newcartrade.service.impl;

import com.pancosky.newcartrade.entity.Contract;
import com.pancosky.newcartrade.exception.BusinessException;
import com.pancosky.newcartrade.mapper.ContractMapper;
import com.pancosky.newcartrade.service.ContractService;
import com.pancosky.newcartrade.vo.ContractDetailVO;
import com.pancosky.newcartrade.vo.ContractVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractMapper contractMapper;

    private String generateContractNo() {
        return "CT" + System.currentTimeMillis() + new Random().nextInt(1000);
    }

    @Override
    @Transactional
    public ContractVO create(String orderId) {
        Contract contract = new Contract();
        contract.setOrderId(orderId);
        contract.setContractNo(generateContractNo());
        contract.setStatus("DRAFT");
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
        return vo;
    }

    @Override
    @Transactional
    public void sign(Long id) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) throw new BusinessException("Contract not found");
    }

    @Override
    public void download(Long id) {
    }
}
