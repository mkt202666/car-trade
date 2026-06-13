package com.pancosky.cartradeadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pancosky.cartradeadmin.common.BusinessException;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.DepositAccountQueryDTO;
import com.pancosky.cartradeadmin.dto.DepositManualDTO;
import com.pancosky.cartradeadmin.dto.DepositRecordQueryDTO;
import com.pancosky.cartradeadmin.entity.AppDepositAccount;
import com.pancosky.cartradeadmin.entity.AppDepositRecord;
import com.pancosky.cartradeadmin.entity.AppUser;
import com.pancosky.cartradeadmin.event.AdminEvent;
import com.pancosky.cartradeadmin.event.AdminEventPublisher;
import com.pancosky.cartradeadmin.event.MobileNotification;
import com.pancosky.cartradeadmin.mapper.AppDepositAccountMapper;
import com.pancosky.cartradeadmin.mapper.AppDepositRecordMapper;
import com.pancosky.cartradeadmin.mapper.AppUserMapper;
import com.pancosky.cartradeadmin.vo.DepositAccountVO;
import com.pancosky.cartradeadmin.vo.DepositRecordVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminDepositService {

    @Autowired
    private AppDepositAccountMapper appDepositAccountMapper;

    @Autowired
    private AppDepositRecordMapper appDepositRecordMapper;

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private AdminEventPublisher adminEventPublisher;

    @Autowired
    private AdminNotificationService adminNotificationService;

    public PageResult<DepositAccountVO> getAccountList(DepositAccountQueryDTO query) {
        LambdaQueryWrapper<AppDepositAccount> wrapper = new LambdaQueryWrapper<AppDepositAccount>();

        // 如果有关键词，先查用户ID
        List<Long> userIds = null;
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            LambdaQueryWrapper<AppUser> userWrapper = new LambdaQueryWrapper<AppUser>()
                    .like(AppUser::getShopName, query.getKeyword())
                    .or()
                    .like(AppUser::getNickname, query.getKeyword())
                    .or()
                    .like(AppUser::getPhone, query.getKeyword());
            List<AppUser> users = appUserMapper.selectList(userWrapper);
            userIds = users.stream().map(AppUser::getId).collect(Collectors.toList());
            if (userIds.isEmpty()) {
                return new PageResult<>(List.of(), 0, query.getPage(), query.getSize());
            }
            wrapper.in(AppDepositAccount::getUserId, userIds);
        }

        wrapper.orderByDesc(AppDepositAccount::getCreatedAt);

        Page<AppDepositAccount> page = appDepositAccountMapper.selectPage(
                new Page<>(query.getPage(), query.getSize()), wrapper);

        List<DepositAccountVO> voList = page.getRecords().stream().map(account -> {
            DepositAccountVO vo = new DepositAccountVO();
            vo.setId(account.getId());
            vo.setUserId(account.getUserId());
            vo.setBalance(account.getBalance());
            vo.setFrozenAmount(account.getFrozenAmount());
            vo.setTotalDeposit(account.getTotalDeposit());
            vo.setStatus(account.getStatus());

            // 联表查询用户信息
            if (account.getUserId() != null) {
                AppUser user = appUserMapper.selectById(account.getUserId());
                if (user != null) {
                    vo.setUserName(user.getNickname());
                    vo.setUserPhone(maskPhone(user.getPhone()));
                }
            }

            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    public PageResult<DepositRecordVO> getRecordList(DepositRecordQueryDTO query) {
        LambdaQueryWrapper<AppDepositRecord> wrapper = new LambdaQueryWrapper<AppDepositRecord>();

        if (query.getType() != null && !query.getType().isEmpty()) {
            wrapper.eq(AppDepositRecord::getType, query.getType());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(AppDepositRecord::getStatus, query.getStatus());
        }
        if (query.getUserId() != null) {
            wrapper.eq(AppDepositRecord::getUserId, query.getUserId());
        }

        wrapper.orderByDesc(AppDepositRecord::getCreatedAt);

        Page<AppDepositRecord> page = appDepositRecordMapper.selectPage(
                new Page<>(query.getPage(), query.getSize()), wrapper);

        List<DepositRecordVO> voList = page.getRecords().stream().map(record -> {
            DepositRecordVO vo = new DepositRecordVO();
            vo.setId(record.getId());
            vo.setUserId(record.getUserId());
            vo.setType(record.getType());
            vo.setAmount(record.getAmount());
            vo.setBalanceAfter(record.getBalanceAfter());
            vo.setRemark(record.getRemark());
            vo.setCreatedAt(record.getCreatedAt());

            // 联表查询用户信息
            if (record.getUserId() != null) {
                AppUser user = appUserMapper.selectById(record.getUserId());
                if (user != null) {
                    vo.setUserName(user.getNickname());
                }
            }

            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    public List<DepositRecordVO> getDepositExportList(DepositRecordQueryDTO query) {
        LambdaQueryWrapper<AppDepositRecord> wrapper = new LambdaQueryWrapper<AppDepositRecord>();

        if (query.getType() != null && !query.getType().isEmpty()) {
            wrapper.eq(AppDepositRecord::getType, query.getType());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(AppDepositRecord::getStatus, query.getStatus());
        }
        if (query.getUserId() != null) {
            wrapper.eq(AppDepositRecord::getUserId, query.getUserId());
        }

        wrapper.orderByDesc(AppDepositRecord::getCreatedAt);

        List<AppDepositRecord> records = appDepositRecordMapper.selectList(wrapper);

        return records.stream().map(record -> {
            DepositRecordVO vo = new DepositRecordVO();
            vo.setId(record.getId());
            vo.setUserId(record.getUserId());
            vo.setType(record.getType());
            vo.setAmount(record.getAmount());
            vo.setBalanceAfter(record.getBalanceAfter());
            vo.setRemark(record.getRemark());
            vo.setCreatedAt(record.getCreatedAt());

            // 联表查询用户信息
            if (record.getUserId() != null) {
                AppUser user = appUserMapper.selectById(record.getUserId());
                if (user != null) {
                    vo.setUserName(user.getNickname());
                }
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void manualAdjust(DepositManualDTO dto, Long operatorId) {
        Long userId = dto.getUserId();

        // 查找账户
        AppDepositAccount account = appDepositAccountMapper.selectOne(
                new LambdaQueryWrapper<AppDepositAccount>().eq(AppDepositAccount::getUserId, userId));
        if (account == null) {
            throw new BusinessException(404, "保证金账户不存在");
        }

        BigDecimal amount = dto.getAmount();
        String type = dto.getType();
        BigDecimal balanceAfter;

        switch (type) {
            case "MANUAL":
            case "CHARGE":
                account.setBalance(account.getBalance().add(amount));
                account.setTotalDeposit(account.getTotalDeposit().add(amount));
                break;
            case "WITHDRAW":
            case "REFUND":
                if (account.getBalance().compareTo(amount) < 0) {
                    throw new BusinessException(400, "余额不足");
                }
                account.setBalance(account.getBalance().subtract(amount));
                break;
            case "FREEZE":
                if (account.getBalance().compareTo(amount) < 0) {
                    throw new BusinessException(400, "可用余额不足");
                }
                account.setBalance(account.getBalance().subtract(amount));
                account.setFrozenAmount(account.getFrozenAmount().add(amount));
                break;
            case "UNFREEZE":
                if (account.getFrozenAmount().compareTo(amount) < 0) {
                    throw new BusinessException(400, "冻结金额不足");
                }
                account.setFrozenAmount(account.getFrozenAmount().subtract(amount));
                account.setBalance(account.getBalance().add(amount));
                break;
            default:
                throw new BusinessException(400, "无效的操作类型");
        }

        appDepositAccountMapper.updateById(account);
        balanceAfter = account.getBalance();

        // 插入流水记录
        AppDepositRecord record = new AppDepositRecord();
        record.setAccountId(account.getId());
        record.setUserId(userId);
        record.setType(type);
        record.setAmount(amount);
        record.setBalanceAfter(balanceAfter);
        record.setRemark(dto.getRemark());
        record.setOperatorId(operatorId);
        record.setStatus("SUCCESS");
        appDepositRecordMapper.insert(record);

        // 发布 Redis 事件
        adminEventPublisher.publish(AdminEvent.EventType.DEPOSIT_ADJUSTED, operatorId,
                String.valueOf(operatorId), "deposit", String.valueOf(account.getId()),
                "手动调整保证金：" + type + " " + amount);

        // 发送移动端通知给用户
        String typeText = switch (type) {
            case "MANUAL", "CHARGE" -> "充值";
            case "WITHDRAW", "REFUND" -> "扣减";
            case "FREEZE" -> "冻结";
            case "UNFREEZE" -> "解冻";
            default -> type;
        };
        adminNotificationService.notify(
                MobileNotification.NotifyType.DEPOSIT_CHANGED, userId,
                "保证金变动通知", "您的保证金账户发生" + typeText + "，金额：" + amount + "元。备注：" + dto.getRemark(),
                "deposit", String.valueOf(account.getId()));
    }

    public Map<String, Object> getSummary() {
        List<AppDepositAccount> accounts = appDepositAccountMapper.selectList(null);

        BigDecimal totalBalance = BigDecimal.ZERO;
        BigDecimal totalFrozen = BigDecimal.ZERO;
        BigDecimal totalDeposit = BigDecimal.ZERO;
        long activeAccounts = 0;

        for (AppDepositAccount account : accounts) {
            totalBalance = totalBalance.add(account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO);
            totalFrozen = totalFrozen.add(account.getFrozenAmount() != null ? account.getFrozenAmount() : BigDecimal.ZERO);
            totalDeposit = totalDeposit.add(account.getTotalDeposit() != null ? account.getTotalDeposit() : BigDecimal.ZERO);
            if ("ACTIVE".equals(account.getStatus())) {
                activeAccounts++;
            }
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalBalance", totalBalance);
        summary.put("totalFrozen", totalFrozen);
        summary.put("totalDeposit", totalDeposit);
        summary.put("activeAccounts", activeAccounts);
        return summary;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
