package com.pancosky.cartradeadmin.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Admin event DTO published via Redis Pub/Sub.
 * Other services (e.g. mobile backend) subscribe to these events for real-time notifications.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminEvent {

    /** Event type enum key */
    private EventType type;

    /** Human-readable description */
    private String description;

    /** Operator admin ID */
    private Long adminId;

    /** Operator admin name */
    private String adminName;

    /** Target entity type: user, shop, car_source, order, dispute, deposit, config, coupon */
    private String targetType;

    /** Target entity ID (as string for flexibility) */
    private String targetId;

    /** Additional data payload (key-value pairs) */
    private Map<String, Object> payload;

    /** Event timestamp */
    private LocalDateTime timestamp;

    /** Pre-defined event types */
    public enum EventType {
        USER_STATUS_CHANGED,       // User enabled/disabled
        USER_DETAIL_VIEWED,         // User detail viewed
        SHOP_AUDIT_APPROVED,       // Shop audit approved
        SHOP_AUDIT_REJECTED,       // Shop audit rejected
        CAR_SOURCE_ONLINE,          // Car source listed
        CAR_SOURCE_OFFLINE,         // Car source delisted
        CAR_SOURCE_PRICE_CHANGED,   // Car source price adjusted
        ORDER_STATUS_CHANGED,      // Order status updated
        DISPUTE_RESOLVED,           // Dispute resolved
        DEPOSIT_ADJUSTED,           // Deposit amount adjusted
        DEPOSIT_WITHDRAWN,          // Deposit withdrawn
        CONFIG_UPDATED,             // System config updated
        COUPON_CREATED,             // Coupon created
        COUPON_DISABLED,           // Coupon disabled
        ADMIN_LOGIN,               // Admin logged in
        ADMIN_PASSWORD_CHANGED,     // Admin changed password
        BANNER_UPDATED,            // Banner updated
        EXPORT_TRIGGERED           // Data export triggered
    }
}
