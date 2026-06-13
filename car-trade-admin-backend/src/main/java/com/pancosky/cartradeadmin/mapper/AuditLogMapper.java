package com.pancosky.cartradeadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pancosky.cartradeadmin.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {
}
