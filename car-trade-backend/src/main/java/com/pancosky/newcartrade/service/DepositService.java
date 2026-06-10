package com.pancosky.newcartrade.service;

import com.pancosky.newcartrade.common.PageResult;
import com.pancosky.newcartrade.vo.DepositRecordVO;
import com.pancosky.newcartrade.vo.DepositVO;

public interface DepositService {
    DepositVO getDepositInfo();
    void recharge(Double amount);
    void withdraw(Double amount);
    PageResult<DepositRecordVO> getRecords(Integer page, Integer size);
}