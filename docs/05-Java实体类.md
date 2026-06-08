## 五、Java 实体类

### 5.1 项目结构

```
src/main/java/com/pancosky/newcartrade/
├── entity/
│   ├── User.java
│   ├── CarSource.java
│   ├── CarImage.java
│   ├── CarTag.java
│   ├── CarInspection.java
│   ├── Order.java
│   ├── OrderInspection.java
│   ├── OrderLog.java
│   ├── DepositAccount.java
│   ├── DepositRecord.java
│   ├── CreditAccount.java
│   ├── Message.java
│   ├── UserFavorite.java
│   ├── Brand.java
│   ├── Series.java
│   ├── Model.java
│   ├── Dispute.java
│   ├── ShopMember.java
│   ├── UserFollow.java
│   ├── BrowsingHistory.java
│   ├── Coupon.java
│   ├── UserCoupon.java
│   ├── MemberPlan.java
│   ├── UserMembership.java
│   ├── CustomerServiceTicket.java
│   ├── ChatConversation.java
│   ├── ChatConversationMember.java
│   └── Contract.java
├── mapper/
│   ├── CarMapper.java
│   ├── OrderMapper.java
│   ├── UserMapper.java
│   ├── UserFavoriteMapper.java
│   ├── MessageMapper.java
│   ├── DepositAccountMapper.java
│   ├── CreditAccountMapper.java
│   └── ... (每张表对应一个 Mapper)
├── enums/
│   ├── CarStatus.java
│   ├── EnergyType.java
│   ├── AuctionStatus.java
│   ├── OrderStatus.java
│   ├── DepositType.java
│   ├── CreditGrade.java
│   ├── CertificationStatus.java
│   ├── MessageType.java
│   ├── ShopMemberRole.java
│   ├── ShopMemberStatus.java
│   ├── MemberLevel.java
│   ├── ContractStatus.java
│   ├── CsTicketStatus.java
│   ├── CsTicketPriority.java
│   ├── CouponType.java
│   └── ChatConversationType.java
└── base/
    ├── BaseEntity.java
    └── MyMetaObjectHandler.java (自动填充)
```

### 5.2 MyBatis-Plus 配置

#### 自动填充处理器

```java
package com.pancosky.newcartrade.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }
}
```

### 5.3 实体类规范

所有实体类继承 BaseEntity，使用 MyBatis-Plus 注解：

```java
package com.pancosky.newcartrade.base;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public abstract class BaseEntity implements Serializable {
    // 由 MyBatis-Plus MetaObjectHandler 自动填充
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### 5.4 核心实体类

#### User.java
```java
package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.pancosky.newcartrade.enums.CreditGrade;
import com.pancosky.newcartrade.enums.CertificationStatus;
import com.pancosky.newcartrade.enums.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("users")
public class User extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String phone;

    private String nickname;

    @TableField("real_name")
    private String realName;

    @TableField("avatar_url")
    private String avatarUrl;

    @TableField("shop_name")
    private String shopName;

    @TableField("credit_grade")
    private CreditGrade creditGrade = CreditGrade.C;

    @TableField("credit_score")
    private Integer creditScore = 60;

    @TableField("deal_count")
    private Integer dealCount = 0;

    @TableField("on_sale_count")
    private Integer onSaleCount = 0;

    @TableField("view_count")
    private Long viewCount = 0L;

    @TableField("view_count_today")
    private Integer viewCountToday = 0;

    @TableField("message_count")
    private Long messageCount = 0L;

    @TableField("message_count_today")
    private Integer messageCountToday = 0;

    @TableField("follower_count")
    private Integer followerCount = 0;

    @TableField("follower_count_today")
    private Integer followerCountToday = 0;

    @TableField("member_expire_at")
    private LocalDateTime memberExpireAt;

    @TableField("certification_status")
    private CertificationStatus certificationStatus = CertificationStatus.UNCERTIFIED;

    private UserStatus status = UserStatus.ACTIVE;
}
```

#### CarSource.java
```java
package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.pancosky.newcartrade.enums.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("car_sources")
public class CarSource extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("brand_id")
    private Integer brandId;

    @TableField("series_id")
    private Integer seriesId;

    @TableField("model_id")
    private Integer modelId;

    private String title;

    private Integer year;

    private Integer mileage;

    private BigDecimal price;

    private BigDecimal deposit;

    private String color;

    @TableField("city_code")
    private String cityCode;

    @TableField("city_name")
    private String cityName;

    @TableField("energy_type")
    private EnergyType energyType;

    @TableField("usage_type")
    private UsageType usageType;

    @TableField("owner_type")
    private OwnerType ownerType;

    @TableField("is_mortgaged")
    private Boolean isMortgaged = false;

    @TableField("is_inherited")
    private Boolean isInherited = false;

    @TableField("registration_date")
    private LocalDate registrationDate;

    @TableField("insurance_expiry")
    private LocalDate insuranceExpiry;

    @TableField("inspection_expiry")
    private LocalDate inspectionExpiry;

    @TableField("production_date")
    private String productionDate;

    @TableField("key_count")
    private Integer keyCount;

    private String description;

    @TableField("auction_status")
    private AuctionStatus auctionStatus = AuctionStatus.NONE;

    @TableField("auction_end_time")
    private LocalDateTime auctionEndTime;

    @TableField("view_count")
    private Long viewCount = 0L;

    @TableField("favorite_count")
    private Integer favoriteCount = 0;

    private CarStatus status = CarStatus.ACTIVE;

    @TableField("published_at")
    private LocalDateTime publishedAt;

    @TableField("export_countries")
    private String exportCountries;
}
```

#### Order.java
```java
package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.pancosky.newcartrade.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order extends BaseEntity {

    @TableId
    private String id;

    @TableField("car_id")
    private Long carId;

    @TableField("buyer_id")
    private Long buyerId;

    @TableField("seller_id")
    private Long sellerId;

    @TableField("total_price")
    private BigDecimal totalPrice;

    @TableField("deposit_amount")
    private BigDecimal depositAmount;

    @TableField("buyer_deposit_paid")
    private Boolean buyerDepositPaid = false;

    @TableField("buyer_deposit_paid_at")
    private LocalDateTime buyerDepositPaidAt;

    @TableField("seller_deposit_paid")
    private Boolean sellerDepositPaid = false;

    @TableField("seller_deposit_paid_at")
    private LocalDateTime sellerDepositPaidAt;

    private OrderStatus status = OrderStatus.PENDING_CONFIRM;

    @TableField("contract_no")
    private String contractNo;

    private String remark;

    @TableField("cancel_reason")
    private String cancelReason;

    @TableField("completed_at")
    private LocalDateTime completedAt;

    @TableField("cancelled_at")
    private LocalDateTime cancelledAt;
}
```

### 5.5 枚举类（MyBatis-Plus IEnum 接口）

所有枚举实现 `IEnum<Integer>` 便于存储整型值到数据库，配合 `typeEnumsPackage` 扫描。

#### OrderStatus.java
```java
package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus implements IEnum<Integer> {
    PENDING_CONFIRM(0, "待确认"),
    TRADING(1, "交易中"),
    DISPUTE(2, "争议中"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已终止");

    private final Integer value;
    private final String description;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
```

#### EnergyType.java
```java
package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnergyType implements IEnum<Integer> {
    GASOLINE(0, "燃油"),
    PURE_ELECTRIC(1, "纯电"),
    HYBRID(2, "混动");

    private final Integer value;
    private final String description;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
```

#### CreditGrade.java
```java
package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CreditGrade implements IEnum<Integer> {
    S(5, "极佳"),
    A(4, "优秀"),
    B(3, "良好"),
    C(2, "一般"),
    D(1, "较差");

    private final Integer value;
    private final String description;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
```
