package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.common.PageResult;
import com.pancosky.newcartrade.service.DepositService;
import com.pancosky.newcartrade.vo.CreditVO;
import com.pancosky.newcartrade.vo.DepositRecordVO;
import com.pancosky.newcartrade.vo.DepositVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pancosky.newcartrade.entity.CreditAccount;
import com.pancosky.newcartrade.exception.BusinessException;
import com.pancosky.newcartrade.mapper.CreditAccountMapper;
import com.pancosky.newcartrade.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final DepositService depositService;
    private final CreditAccountMapper creditAccountMapper;

    @GetMapping("/deposit")
    public ApiResponse<DepositVO> getDeposit() {
        return ApiResponse.success(depositService.getDepositInfo());
    }

    @PostMapping("/deposit/recharge")
    public ApiResponse<Void> recharge(@RequestBody Map<String, Object> body) {
        Double amount = ((Number) body.get("amount")).doubleValue();
        depositService.recharge(amount);
        return ApiResponse.success();
    }

    @PostMapping("/deposit/withdraw")
    public ApiResponse<Void> withdraw(@RequestBody Map<String, Object> body) {
        Double amount = ((Number) body.get("amount")).doubleValue();
        depositService.withdraw(amount);
        return ApiResponse.success();
    }

    @GetMapping("/deposit/records")
    public ApiResponse<PageResult<DepositRecordVO>> getRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return ApiResponse.success(depositService.getRecords(page, size));
    }

    @GetMapping("/credit")
    public ApiResponse<CreditVO> getCredit() {
        Long userId = SecurityUtils.getCurrentUserId();
        CreditAccount account = creditAccountMapper.selectOne(
                new LambdaQueryWrapper<CreditAccount>().eq(CreditAccount::getUserId, userId).last("LIMIT 1"));
        CreditVO vo = new CreditVO();
        if (account != null) {
            vo.setCreditLimit(account.getCreditLimit());
            vo.setUsedAmount(account.getUsedAmount());
            vo.setAvailableAmount(account.getAvailableAmount());
        } else {
            vo.setCreditLimit(BigDecimal.ZERO);
            vo.setUsedAmount(BigDecimal.ZERO);
            vo.setAvailableAmount(BigDecimal.ZERO);
        }
        return ApiResponse.success(vo);
    }

    @PostMapping("/credit/apply")
    public ApiResponse<Void> applyCredit(@RequestBody Map<String, Object> body) {
        Long userId = SecurityUtils.getCurrentUserId();
        BigDecimal limit = new BigDecimal(body.get("limit").toString());
        if (limit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("申请额度必须大于0");
        }
        CreditAccount account = creditAccountMapper.selectOne(
                new LambdaQueryWrapper<CreditAccount>().eq(CreditAccount::getUserId, userId).last("LIMIT 1"));
        if (account == null) {
            account = new CreditAccount();
            account.setUserId(userId);
            account.setCreditLimit(limit);
            account.setUsedAmount(BigDecimal.ZERO);
            account.setAvailableAmount(limit);
            account.setStatus("ACTIVE");
            creditAccountMapper.insert(account);
        } else {
            account.setCreditLimit(account.getCreditLimit().add(limit));
            account.setAvailableAmount(account.getAvailableAmount().add(limit));
            creditAccountMapper.updateById(account);
        }
        return ApiResponse.success();
    }
}