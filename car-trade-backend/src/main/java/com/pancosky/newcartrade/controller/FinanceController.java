package com.pancosky.newcartrade.controller;
import com.pancosky.newcartrade.common.RequiresAuth;
import com.pancosky.newcartrade.common.AuthLevel;

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

/**
 * 财务/资金控制器
 * 描述：提供保证金账户查询、充值、提现、流水查询，以及信用额度查询和申请接口。
 * 基础路径：/api/v1/finance
 * 认证要求：全部接口必须登录，所有操作基于当前用户。
 */
@RestController
@RequestMapping("/api/v1/finance")
@RequiredArgsConstructor
@RequiresAuth(AuthLevel.PROTECTED)
public class FinanceController {

    private final DepositService depositService;
    private final CreditAccountMapper creditAccountMapper;

    /**
     * 查询保证金账户信息
     * HTTP: GET /api/v1/finance/deposit
     * 响应：ApiResponse&lt;DepositVO&gt; —— 余额、冻结金额、累计充值/提现等。
     * 认证要求：必须登录。
     */
    @GetMapping("/deposit")
    public ApiResponse<DepositVO> getDeposit() {
        return ApiResponse.success(depositService.getDepositInfo());
    }

    /**
     * 保证金充值
     * HTTP: POST /api/v1/finance/deposit/recharge
     * 请求参数：body.amount（Number，单位：元；最低 100 元）。
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；需通过实名认证。
     */
    @PostMapping("/deposit/recharge")
    public ApiResponse<Void> recharge(@RequestBody Map<String, Object> body) {
        Double amount = ((Number) body.get("amount")).doubleValue();
        depositService.recharge(amount);
        return ApiResponse.success();
    }

    /**
     * 保证金提现
     * HTTP: POST /api/v1/finance/deposit/withdraw
     * 请求参数：body.amount（Number，单位：元；必须为正数且 ≤ 可用余额）。
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；需完成实名认证。
     */
    @PostMapping("/deposit/withdraw")
    public ApiResponse<Void> withdraw(@RequestBody Map<String, Object> body) {
        Double amount = ((Number) body.get("amount")).doubleValue();
        depositService.withdraw(amount);
        return ApiResponse.success();
    }

    /**
     * 保证金账户流水
     * HTTP: GET /api/v1/finance/deposit/records?page=1&size=20
     * 请求参数：page、size（query）。
     * 响应：ApiResponse&lt;PageResult&lt;DepositRecordVO&gt;&gt; —— 充值、提现、冻结、解冻等流水记录。
     * 认证要求：必须登录。
     */
    @GetMapping("/deposit/records")
    public ApiResponse<PageResult<DepositRecordVO>> getRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return ApiResponse.success(depositService.getRecords(page, size));
    }

    /**
     * 查询信用额度账户
     * HTTP: GET /api/v1/finance/credit
     * 响应：ApiResponse&lt;CreditVO&gt; —— 总额度、已使用、可用额度；若尚未开通则全部为 0。
     * 认证要求：必须登录。
     */
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

    /**
     * 申请/追加信用额度
     * HTTP: POST /api/v1/finance/credit/apply
     * 请求参数：body.limit（Number，新增申请的额度，单位：元；必须为正数）。
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；首次申请将创建账户，再次申请在原额度基础上叠加。
     */
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
