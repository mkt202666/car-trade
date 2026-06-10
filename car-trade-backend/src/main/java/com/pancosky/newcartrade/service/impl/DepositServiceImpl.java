package com.pancosky.newcartrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pancosky.newcartrade.common.PageResult;
import com.pancosky.newcartrade.entity.DepositAccount;
import com.pancosky.newcartrade.entity.DepositRecord;
import com.pancosky.newcartrade.exception.BusinessException;
import com.pancosky.newcartrade.mapper.DepositAccountMapper;
import com.pancosky.newcartrade.mapper.DepositRecordMapper;
import com.pancosky.newcartrade.service.DepositService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.DepositRecordVO;
import com.pancosky.newcartrade.vo.DepositVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {

    private final DepositAccountMapper depositAccountMapper;
    private final DepositRecordMapper depositRecordMapper;

    @Override
    public DepositVO getDepositInfo() {
        Long userId = SecurityUtils.getCurrentUserId();
        DepositAccount account = getOrCreateAccount(userId);
        DepositVO vo = new DepositVO();
        vo.setBalance(account.getBalance());
        vo.setFrozenAmount(account.getFrozenAmount());
        vo.setTotalDeposit(account.getTotalDeposit());
        return vo;
    }

    @Override
    @Transactional
    public void recharge(Double amount) {
        if (amount == null || amount <= 0) {
            throw new BusinessException("充值金额必须大于0");
        }
        Long userId = SecurityUtils.getCurrentUserId();
        DepositAccount account = getOrCreateAccount(userId);
        BigDecimal bdAmount = BigDecimal.valueOf(amount);
        
        account.setBalance(account.getBalance().add(bdAmount));
        account.setTotalDeposit(account.getTotalDeposit().add(bdAmount));
        depositAccountMapper.updateById(account);
        
        createRecord(account.getId(), "RECHARGE", bdAmount, account.getBalance(), "保证金充值");
        log.info("User {} recharged deposit: {}", userId, amount);
    }

    @Override
    @Transactional
    public void withdraw(Double amount) {
        if (amount == null || amount <= 0) {
            throw new BusinessException("提现金额必须大于0");
        }
        Long userId = SecurityUtils.getCurrentUserId();
        DepositAccount account = getOrCreateAccount(userId);
        BigDecimal bdAmount = BigDecimal.valueOf(amount);
        
        if (account.getBalance().compareTo(bdAmount) < 0) {
            throw new BusinessException("余额不足");
        }
        
        account.setBalance(account.getBalance().subtract(bdAmount));
        depositAccountMapper.updateById(account);
        
        createRecord(account.getId(), "WITHDRAW", bdAmount, account.getBalance(), "保证金提现");
        log.info("User {} withdrew deposit: {}", userId, amount);
    }

    @Override
    public PageResult<DepositRecordVO> getRecords(Integer page, Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        DepositAccount account = getOrCreateAccount(userId);
        
        Page<DepositRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<DepositRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DepositRecord::getAccountId, account.getId());
        wrapper.orderByDesc(DepositRecord::getCreatedAt);
        
        Page<DepositRecord> recordPage = depositRecordMapper.selectPage(pageParam, wrapper);
        
        List<DepositRecordVO> vos = recordPage.getRecords().stream()
                .map(this::toRecordVO)
                .collect(Collectors.toList());
        
        return new PageResult<>(vos, recordPage.getTotal(), (int) recordPage.getCurrent(), (int) recordPage.getSize());
    }

    private DepositAccount getOrCreateAccount(Long userId) {
        LambdaQueryWrapper<DepositAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DepositAccount::getUserId, userId);
        DepositAccount account = depositAccountMapper.selectOne(wrapper);
        
        if (account == null) {
            account = new DepositAccount();
            account.setUserId(userId);
            account.setBalance(BigDecimal.ZERO);
            account.setFrozenAmount(BigDecimal.ZERO);
            account.setTotalDeposit(BigDecimal.ZERO);
            account.setStatus("ACTIVE");
            depositAccountMapper.insert(account);
            log.info("Created deposit account for user {}", userId);
        }
        return account;
    }

    private void createRecord(Long accountId, String type, BigDecimal amount, BigDecimal balanceAfter, String remark) {
        DepositRecord record = new DepositRecord();
        record.setAccountId(accountId);
        record.setType(type);
        record.setAmount(amount);
        record.setBalanceAfter(balanceAfter);
        record.setRemark(remark);
        depositRecordMapper.insert(record);
    }

    private DepositRecordVO toRecordVO(DepositRecord record) {
        DepositRecordVO vo = new DepositRecordVO();
        vo.setId(record.getId());
        vo.setType(record.getType());
        vo.setAmount(record.getAmount());
        vo.setBalanceAfter(record.getBalanceAfter());
        vo.setRemark(record.getRemark());
        vo.setCreatedAt(record.getCreatedAt());
        return vo;
    }
}