package com.pancosky.newcartrade.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 * 描述：处理 createdAt 和 updatedAt 字段的自动填充，避免在插入或更新时这些字段为 null。
 * 当实体类中对应的字段标注了 @TableField(fill = FieldFill.INSERT) 或 FieldFill.INSERT_UPDATE 时，
 * MyBatis-Plus 会调用此处理器来填充字段值。
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入填充，填充 createdAt 和 updatedAt 字段");
        // 插入时填充 createdAt 和 updatedAt
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始更新填充，填充 updatedAt 字段");
        // 更新时仅填充 updatedAt
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }
}
