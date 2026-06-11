package com.pancosky.newcartrade.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 启动时补充数据库迁移（当前表结构缺失列时才会触发）。
 * <p>
 * 与 MyBatis-Plus 的启动顺序无关：
 * 只是在应用完全启动后执行若干条
 * ALTER TABLE ... ADD COLUMN IF NOT EXISTS 语句，
 * 保证实体所需的字段在数据库中存在。
 * <p>
 * 可通过 car-trade.init-db=false 关闭。
 */
@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class StartupRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Value("${car-trade.init-db:true}")
    private boolean initDb;

    public StartupRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        if (!initDb) {
            log.info("DB init disabled (car-trade.init-db=false)");
            return;
        }

        // 只执行轻量级的"补列/补表"语句 —— 所有语句必须是幂等的
        // (ALTER TABLE ... ADD COLUMN IF NOT EXISTS / CREATE TABLE IF NOT EXISTS 是 PostgreSQL 语法)
        String[] migrations = {
            "ALTER TABLE users ADD COLUMN IF NOT EXISTS password VARCHAR(100)",
            "ALTER TABLE messages ADD COLUMN IF NOT EXISTS sender_id BIGINT",
            "ALTER TABLE user_coupons ADD COLUMN IF NOT EXISTS received_at TIMESTAMP",
            "ALTER TABLE user_membership ADD COLUMN IF NOT EXISTS end_at TIMESTAMP",
            "CREATE TABLE IF NOT EXISTS chat_messages (id BIGSERIAL PRIMARY KEY, conversation_id BIGINT NOT NULL REFERENCES chat_conversations(id), sender_id BIGINT, content TEXT, message_type VARCHAR(20), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)",
            "CREATE INDEX IF NOT EXISTS idx_chat_messages_conv ON chat_messages(conversation_id)",
            "CREATE INDEX IF NOT EXISTS idx_chat_messages_sender ON chat_messages(sender_id)",
            "CREATE INDEX IF NOT EXISTS idx_chat_messages_created ON chat_messages(created_at DESC)"
        };

        int applied = 0;
        for (String sql : migrations) {
            try {
                jdbcTemplate.execute(sql);
                applied++;
                log.info("DB migration OK: {}", sql);
            } catch (Exception e) {
                // Postgres 如果列已经存在会返回错误，只打印不中断启动
                log.warn("DB migration skipped ({}): {}", e.getClass().getSimpleName(), sql);
            }
        }
        log.info("DB migration finished. applied={}", applied);
    }
}
