package com.pancosky.newcartrade.service;

import com.pancosky.newcartrade.common.PageResult;
import com.pancosky.newcartrade.dto.PurchaseDemandCreateDTO;
import com.pancosky.newcartrade.vo.PurchaseDemandVO;

public interface PurchaseDemandService {
    PurchaseDemandVO create(PurchaseDemandCreateDTO dto);
    PageResult<PurchaseDemandVO> list(Integer page, Integer size);
    PageResult<PurchaseDemandVO> myDemands(Integer page, Integer size);
    void cancel(Long id);
}
