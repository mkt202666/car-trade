package com.pancosky.newcartrade.service;

import com.pancosky.newcartrade.vo.ContractDetailVO;
import com.pancosky.newcartrade.vo.ContractVO;

public interface ContractService {
    ContractVO create(String orderId);
    ContractDetailVO detail(Long id);
    void sign(Long id, String role, Long userId, String signatureUrl);
    String download(Long id);
}
