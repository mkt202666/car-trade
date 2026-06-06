## 六、SpringBoot 接口

### 6.1 项目依赖 pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.14</version>
    </parent>

    <groupId>com.pancosky</groupId>
    <artifactId>new-car-trade</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <properties>
        <java.version>21</java.version>
        <mybatis-plus.version>3.5.6</mybatis-plus.version>
        <jwt.version>0.12.5</jwt.version>
    </properties>

    <dependencies>
        <!-- Spring Boot 3.5.14 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>

        <!-- MyBatis-Plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>

        <!-- Database -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jwt.version}</version>
            <scope>runtime</scope>
        </dependency>

        <!-- WebSocket -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>

        <!-- RocketMQ -->
        <dependency>
            <groupId>org.apache.rocketmq</groupId>
            <artifactId>rocketmq-spring-boot-starter</artifactId>
            <version>2.3.2</version>
        </dependency>

        <!-- Tools -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.8.26</version>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### 6.2 配置文件 application.yml

```yaml
server:
  port: 8080
  servlet:
    context-path: /

spring:
  application:
    name: new-car-trade
  
  # PostgreSQL
  datasource:
    url: jdbc:postgresql://localhost:5432/new_car_trade
    username: postgres
    password: ${DB_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 300000
      max-lifetime: 1800000
      connection-timeout: 30000
  
  # MyBatis-Plus
  mybatis-plus:
    mapper-locations: classpath:mapper/**/*.xml
    type-aliases-package: com.pancosky.newcartrade.entity
    type-enums-package: com.pancosky.newcartrade.enums
    configuration:
      map-underscore-to-camel-case: true
      cache-enabled: false
      log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    global-config:
      db-config:
        id-type: auto
        logic-delete-field: deletedAt
        logic-delete-value: now()
        logic-not-delete-value: null
  
  # Redis
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    database: 0
    timeout: 10000
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
        max-wait: -1ms

# JWT
jwt:
  secret: ${JWT_SECRET:your-secret-key-here-must-be-at-least-256-bits}
  expiration: 7200  # 2小时
  refresh-expiration: 604800  # 7天

# Logging - 阿里云 SLS
logging:
  level:
    root: INFO
    com.pancosky.newcartrade: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n"
  file:
    name: ./logs/new-car-trade.log
    max-size: 100MB
    max-history: 30

# 阿里云 SLS 日志配置
alibaba:
  sls:
    endpoint: ap-shanghai.cls.tencentcs.com
    project: new-car-trade
    logstore: application-log
    access-key-id: ${SLS_ACCESS_KEY_ID}
    access-key-secret: ${SLS_ACCESS_KEY_SECRET}
    topic: spring-boot-app

# RocketMQ
rocketmq:
  name-server: ${ROCKETMQ_NAMESRV:localhost:9876}
  producer:
    group: new-car-trade-producer
    send-message-timeout: 3000
    retry-times-when-send-failed: 2

# API
api:
  v1:
    prefix: /api/v1

# 文件上传
file:
  upload:
    max-size: 10MB
    allowed-types: image/jpeg,image/png,image/gif,video/mp4
    storage-type: local  # local/oss
    local:
      path: ./uploads
    oss:
      endpoint: oss-cn-hangzhou.aliyuncs.com
      bucket: new-car-trade
      access-key-id: ${OSS_ACCESS_KEY_ID}
      access-key-secret: ${OSS_ACCESS_KEY_SECRET}
```

### 6.3 核心控制器

#### CarController.java
```java
package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.common.PageResult;
import com.pancosky.newcartrade.dto.CarCreateDTO;
import com.pancosky.newcartrade.dto.CarQueryDTO;
import com.pancosky.newcartrade.dto.CarUpdateDTO;
import com.pancosky.newcartrade.service.CarService;
import com.pancosky.newcartrade.vo.CarDetailVO;
import com.pancosky.newcartrade.vo.CarVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping
    public ApiResponse<PageResult<CarVO>> list(CarQueryDTO query) {
        return ApiResponse.success(carService.list(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<CarDetailVO> detail(@PathVariable Long id) {
        return ApiResponse.success(carService.detail(id));
    }

    @PostMapping
    public ApiResponse<CarVO> create(@Validated @RequestBody CarCreateDTO dto) {
        return ApiResponse.success(carService.create(dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<CarVO> update(
            @PathVariable Long id,
            @Validated @RequestBody CarUpdateDTO dto) {
        return ApiResponse.success(carService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        carService.delete(id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/favorite")
    public ApiResponse<Void> favorite(@PathVariable Long id) {
        carService.favorite(id);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}/favorite")
    public ApiResponse<Void> unfavorite(@PathVariable Long id) {
        carService.unfavorite(id);
        return ApiResponse.success();
    }

    @GetMapping("/recommend")
    public ApiResponse<PageResult<CarVO>> recommend(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ApiResponse.success(carService.recommend(page, size));
    }
}
```

#### OrderController.java
```java
package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.common.PageResult;
import com.pancosky.newcartrade.dto.OrderCreateDTO;
import com.pancosky.newcartrade.dto.OrderQueryDTO;
import com.pancosky.newcartrade.service.OrderService;
import com.pancosky.newcartrade.vo.OrderDetailVO;
import com.pancosky.newcartrade.vo.OrderLogVO;
import com.pancosky.newcartrade.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ApiResponse<PageResult<OrderVO>> list(OrderQueryDTO query) {
        return ApiResponse.success(orderService.list(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDetailVO> detail(@PathVariable String id) {
        return ApiResponse.success(orderService.detail(id));
    }

    @PostMapping
    public ApiResponse<OrderVO> create(@Validated @RequestBody OrderCreateDTO dto) {
        return ApiResponse.success(orderService.create(dto));
    }

    @PutMapping("/{id}/confirm")
    public ApiResponse<OrderVO> confirm(@PathVariable String id) {
        return ApiResponse.success(orderService.confirm(id));
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<OrderVO> cancel(
            @PathVariable String id,
            @RequestParam String reason) {
        return ApiResponse.success(orderService.cancel(id, reason));
    }

    @PutMapping("/{id}/pay-deposit")
    public ApiResponse<OrderVO> payDeposit(
            @PathVariable String id,
            @RequestParam String role) {
        return ApiResponse.success(orderService.payDeposit(id, role));
    }

    @PutMapping("/{id}/complete")
    public ApiResponse<OrderVO> complete(@PathVariable String id) {
        return ApiResponse.success(orderService.complete(id));
    }

    @GetMapping("/{id}/logs")
    public ApiResponse<List<OrderLogVO>> logs(@PathVariable String id) {
        return ApiResponse.success(orderService.logs(id));
    }
}
```

#### UserController.java
```java
package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.dto.LoginDTO;
import com.pancosky.newcartrade.dto.RegisterDTO;
import com.pancosky.newcartrade.dto.UserUpdateDTO;
import com.pancosky.newcartrade.service.UserService;
import com.pancosky.newcartrade.vo.LoginVO;
import com.pancosky.newcartrade.vo.UserStatsVO;
import com.pancosky.newcartrade.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Validated @RequestBody LoginDTO dto) {
        return ApiResponse.success(userService.login(dto));
    }

    @PostMapping("/register")
    public ApiResponse<UserVO> register(@Validated @RequestBody RegisterDTO dto) {
        return ApiResponse.success(userService.register(dto));
    }

    @GetMapping("/me")
    public ApiResponse<UserVO> me() {
        return ApiResponse.success(userService.getCurrentUser());
    }

    @PutMapping("/me")
    public ApiResponse<UserVO> updateMe(@Validated @RequestBody UserUpdateDTO dto) {
        return ApiResponse.success(userService.updateUser(dto));
    }

    @PostMapping("/me/avatar")
    public ApiResponse<String> uploadAvatar(@RequestParam MultipartFile file) {
        return ApiResponse.success(userService.uploadAvatar(file));
    }

    @GetMapping("/me/stats")
    public ApiResponse<UserStatsVO> stats() {
        return ApiResponse.success(userService.getStats());
    }
}
```

### 6.4 核心服务类

#### CarService.java
```java
package com.pancosky.newcartrade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pancosky.newcartrade.common.PageResult;
import com.pancosky.newcartrade.dto.CarCreateDTO;
import com.pancosky.newcartrade.dto.CarQueryDTO;
import com.pancosky.newcartrade.dto.CarUpdateDTO;
import com.pancosky.newcartrade.entity.CarSource;
import com.pancosky.newcartrade.entity.UserFavorite;
import com.pancosky.newcartrade.exception.BusinessException;
import com.pancosky.newcartrade.mapper.CarMapper;
import com.pancosky.newcartrade.mapper.UserFavoriteMapper;
import com.pancosky.newcartrade.service.cache.CarCacheService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.CarDetailVO;
import com.pancosky.newcartrade.vo.CarVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarService {

    private final CarMapper carMapper;
    private final UserFavoriteMapper favoriteMapper;
    private final CarCacheService carCacheService;
    private final BrowsingHistoryService browsingHistoryService;

    public PageResult<CarVO> list(CarQueryDTO query) {
        String cacheKey = buildCacheKey(query);
        PageResult<CarVO> cached = carCacheService.getList(cacheKey);
        if (cached != null) {
            return cached;
        }

        Page<CarSource> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<CarSource> wrapper = buildQueryWrapper(query);
        IPage<CarSource> result = carMapper.selectPage(page, wrapper);

        List<CarVO> list = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        PageResult<CarVO> pageResult = PageResult.of(
                list, result.getTotal(), query.getPage(), query.getSize());

        carCacheService.setList(cacheKey, pageResult);

        return pageResult;
    }

    public CarDetailVO detail(Long id) {
        CarDetailVO cached = carCacheService.getDetail(id);
        if (cached != null) {
            return cached;
        }

        CarSource car = carMapper.selectById(id);
        if (car == null) {
            throw new BusinessException("车源不存在");
        }

        carMapper.incrementViewCount(id);
        // 记录浏览历史
        Long currentUserId = SecurityUtils.getCurrentUserId();
        browsingHistoryService.record(currentUserId, id);

        CarDetailVO vo = convertToDetailVO(car);

        carCacheService.setDetail(id, vo);

        return vo;
    }

    @Transactional
    public CarVO create(CarCreateDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();

        CarSource car = new CarSource();
        car.setUserId(userId);
        car.setBrandId(dto.getBrandId());
        car.setSeriesId(dto.getSeriesId());
        car.setModelId(dto.getModelId());
        car.setYear(dto.getYear());
        car.setMileage(dto.getMileage());
        car.setPrice(dto.getPrice());
        car.setDeposit(dto.getDeposit());
        car.setColor(dto.getColor());
        car.setCityCode(dto.getCityCode());
        car.setCityName(dto.getCityName());
        car.setEnergyType(dto.getEnergyType());
        car.setUsageType(dto.getUsageType());
        car.setOwnerType(dto.getOwnerType());
        car.setIsMortgaged(dto.getIsMortgaged());
        car.setIsInherited(dto.getIsInherited());
        car.setDescription(dto.getDescription());
        car.setPublishedAt(LocalDateTime.now());

        carMapper.insert(car);

        carCacheService.clearListCache();

        log.info("用户{}发布车源{}", userId, car.getId());

        return convertToVO(car);
    }

    @Transactional
    public void favorite(Long carId) {
        Long userId = SecurityUtils.getCurrentUserId();

        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setCarId(carId);
        favoriteMapper.insert(favorite);

        carMapper.incrementFavoriteCount(carId);
        carCacheService.clearDetailCache(carId);
    }

    @Transactional
    public void unfavorite(Long carId) {
        Long userId = SecurityUtils.getCurrentUserId();

        favoriteMapper.delete(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .eq(UserFavorite::getCarId, carId));

        carMapper.decrementFavoriteCount(carId);
        carCacheService.clearDetailCache(carId);
    }
}
```

### 6.5 通用响应类

#### ApiResponse.java
```java
package com.pancosky.newcartrade.common;

import lombok.Data;

@Data
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;
    private long timestamp;
    private String traceId;

    private ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}
```

#### PageResult.java
```java
package com.pancosky.newcartrade.common;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private List<T> list;
    private long total;
    private int page;
    private int size;
    private int totalPages;

    public static <T> PageResult<T> of(List<T> list, long total, int page, int size) {
        PageResult<T> result = new PageResult<>();
        result.setList(list);
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);
        result.setTotalPages((int) Math.ceil((double) total / size));
        return result;
    }
}
```

### 6.6 MyBatis-Plus 配置类

#### MyBatisPlusConfig.java
```java
package com.pancosky.newcartrade.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.pancosky.newcartrade.mapper")
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.POSTGRE_SQL);
        pagination.setOverflow(true);
        pagination.setMaxLimit(500L);
        interceptor.addInnerInterceptor(pagination);
        return interceptor;
    }
}
```

### 6.7 Mapper 层

#### CarMapper.java（接口）
```java
package com.pancosky.newcartrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pancosky.newcartrade.entity.CarSource;
import org.apache.ibatis.annotations.Update;

public interface CarMapper extends BaseMapper<CarSource> {

    @Update("UPDATE car_sources SET view_count = view_count + 1 WHERE id = #{carId}")
    void incrementViewCount(Long carId);

    @Update("UPDATE car_sources SET favorite_count = favorite_count + 1 WHERE id = #{carId}")
    void incrementFavoriteCount(Long carId);

    @Update("UPDATE car_sources SET favorite_count = favorite_count - 1 WHERE id = #{carId}")
    void decrementFavoriteCount(Long carId);
}
```

#### UserFavoriteMapper.java
```java
package com.pancosky.newcartrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pancosky.newcartrade.entity.UserFavorite;

public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {
}
```

#### src/main/resources/mapper/CarMapper.xml（复杂查询示例）
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.pancosky.newcartrade.mapper.CarMapper">

    <resultMap id="carDetailMap" type="com.pancosky.newcartrade.entity.CarSource">
        <id column="id" property="id"/>
        <result column="user_id" property="userId"/>
        <result column="brand_id" property="brandId"/>
        <result column="series_id" property="seriesId"/>
        <result column="model_id" property="modelId"/>
        <result column="city_code" property="cityCode"/>
        <result column="city_name" property="cityName"/>
        <result column="energy_type" property="energyType"/>
        <result column="auction_status" property="auctionStatus"/>
        <result column="published_at" property="publishedAt"/>
        <result column="created_at" property="createdAt"/>
        <result column="updated_at" property="updatedAt"/>
    </resultMap>

    <!-- 复杂多表关联查询示例 -->
    <select id="searchExportCars" resultMap="carDetailMap">
        SELECT cs.*
        FROM car_sources cs
        INNER JOIN car_tags ct ON cs.id = ct.car_id
        WHERE ct.tag_type = 'EXPORT'
          AND ct.tag_value = #{countryCode}
          AND cs.status = 'ACTIVE'
        ORDER BY cs.created_at DESC
    </select>

</mapper>
```

### 6.8 消息推送（WebSocket + RocketMQ）

#### 架构图

```
┌──────────────┐     ┌──────────────────┐     ┌───────────────┐
│  发送方       │     │   后端服务         │     │  接收方        │
│ ┌──────────┐ │     │ ┌──────────────┐  │     │ ┌──────────┐  │
│ │ 业务请求  │ │────▶│ │ PushManager  │  │────▶│ │WebSocket │  │
│ └──────────┘ │     │ └──────┬───────┘  │     │ └──────────┘  │
└──────────────┘     │        │          │     └───────────────┘
                     │ ┌──────▼───────┐  │
                     │ │RocketMQ     │  │
                     │ │Producer     │  │
                     │ └──────┬───────┘  │
                     │        │          │
                     │ ┌──────▼───────┐  │
                     │ │RocketMQ     │  │
                     │ │Consumer     │  │
                     │ └──────┬───────┘  │
                     │        │          │
                     │ ┌──────▼───────┐  │
                     │ │  Message     │  │
                     │ │  Service    │  │
                     │ └──────┬───────┘  │
                     │        │          │
                     │ ┌──────▼───────┐  │
                     │ │  DB (消息持久化)│  │
                     │ └──────────────┘  │
                     └──────────────────┘
```

#### 6.8.1 WebSocket 配置

```java
package com.pancosky.newcartrade.config;

import com.pancosky.newcartrade.interceptor.WebSocketAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 服务端推送前缀，客户端订阅 /topic/ 或 /queue/ 开头的地址
        registry.enableSimpleBroker("/topic", "/queue");
        // 客户端发消息前缀，如 /app/chat/send
        registry.setApplicationDestinationPrefixes("/app");
        // 点对点前缀
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new WebSocketAuthInterceptor());
    }
}
```

#### 6.8.2 WebSocket 连接鉴权拦截器

```java
package com.pancosky.newcartrade.interceptor;

import com.pancosky.newcartrade.util.JwtUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token == null || !JwtUtil.validateToken(token)) {
                throw new IllegalArgumentException("WebSocket 鉴权失败");
            }
            Long userId = JwtUtil.getUserId(token);
            accessor.setUser(() -> String.valueOf(userId));
        }

        return message;
    }
}
```

#### 6.8.3 消息推送抽象层 — PushManager

```java
package com.pancosky.newcartrade.manager;

import com.pancosky.newcartrade.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushManager {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 推送给指定用户（点对点）
     */
    public void pushToUser(Long userId, MessageVO message) {
        String destination = "/queue/messages";
        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId), destination, message);
        log.info("推送消息给用户 {}, type={}", userId, message.getType());
    }

    /**
     * 推送给所有在线用户（广播）
     */
    public void pushToAll(MessageVO message) {
        messagingTemplate.convertAndSend("/topic/announcements", message);
        log.info("广播消息 type={}", message.getType());
    }
}
```

#### 6.8.4 RocketMQ 消息生产者

```java
package com.pancosky.newcartrade.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pancosky.newcartrade.entity.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProducer {

    private final RocketMQTemplate rocketMQTemplate;
    private final ObjectMapper objectMapper;

    /** 系统消息 topic */
    private static final String TOPIC_SYSTEM = "message-system";
    /** 聊天消息 topic */
    private static final String TOPIC_CHAT = "message-chat";

    public void sendSystemMessage(Message message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            rocketMQTemplate.send(
                    TOPIC_SYSTEM,
                    MessageBuilder.withPayload(json).build());
            log.info("MQ 发送系统消息 messageId={}", message.getId());
        } catch (Exception e) {
            log.error("MQ 发送系统消息失败", e);
        }
    }

    public void sendChatMessage(Message message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            // 聊天消息按接收者 hash 选择队列，保证同一会话有序
            rocketMQTemplate.syncSendOrderly(
                    TOPIC_CHAT,
                    MessageBuilder.withPayload(json).build(),
                    String.valueOf(message.getReceiverId()));
            log.info("MQ 发送聊天消息 messageId={}", message.getId());
        } catch (Exception e) {
            log.error("MQ 发送聊天消息失败", e);
        }
    }
}
```

#### 6.8.5 RocketMQ 消息消费者

```java
package com.pancosky.newcartrade.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pancosky.newcartrade.entity.Message;
import com.pancosky.newcartrade.service.MessageService;
import com.pancosky.newcartrade.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageConsumer {

    private final ObjectMapper objectMapper;
    private final MessageService messageService;
    private final PushManager pushManager;

    @Component
    @RocketMQMessageListener(
            topic = "message-system",
            consumerGroup = "new-car-trade-system")
    public class SystemMessageListener implements RocketMQListener<String> {

        @Override
        public void onMessage(String json) {
            try {
                Message msg = objectMapper.readValue(json, Message.class);
                // 先落库
                messageService.save(msg);
                // 再推送
                MessageVO vo = messageService.convertToVO(msg);
                pushManager.pushToUser(msg.getUserId(), vo);
            } catch (Exception e) {
                log.error("消费系统消息失败", e);
            }
        }
    }

    @Component
    @RocketMQMessageListener(
            topic = "message-chat",
            consumerGroup = "new-car-trade-chat")
    public class ChatMessageListener implements RocketMQListener<String> {

        @Override
        public void onMessage(String json) {
            try {
                Message msg = objectMapper.readValue(json, Message.class);
                // 聊天消息落库
                messageService.save(msg);
                // 推送给接收方
                MessageVO vo = messageService.convertToVO(msg);
                pushManager.pushToUser(msg.getReceiverId(), vo);
            } catch (Exception e) {
                log.error("消费聊天消息失败", e);
            }
        }
    }
}
```

#### 6.8.6 消息服务类

```java
package com.pancosky.newcartrade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pancosky.newcartrade.common.PageResult;
import com.pancosky.newcartrade.entity.Message;
import com.pancosky.newcartrade.mapper.MessageMapper;
import com.pancosky.newcartrade.manager.MessageProducer;
import com.pancosky.newcartrade.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper messageMapper;
    private final MessageProducer messageProducer;

    /**
     * 发送系统消息：落库 + MQ 推送
     */
    @Transactional
    public void sendSystem(Long userId, String title, String content, String relatedId, String relatedType) {
        Message msg = new Message();
        msg.setUserId(userId);
        msg.setType("SYSTEM");
        msg.setTitle(title);
        msg.setContent(content);
        msg.setRelatedId(relatedId);
        msg.setRelatedType(relatedType);
        msg.setIsRead(false);

        messageMapper.insert(msg);
        messageProducer.sendSystemMessage(msg);
    }

    /**
     * 发送聊天消息：落库 + MQ 推送
     */
    @Transactional
    public void sendChat(Long senderId, Long receiverId, String content) {
        Message msg = new Message();
        msg.setUserId(receiverId);
        msg.setSenderId(senderId);
        msg.setType("CHAT");
        msg.setTitle("");
        msg.setContent(content);
        msg.setIsRead(false);

        messageMapper.insert(msg);
        messageProducer.sendChatMessage(msg);
    }

    /**
     * 分页查询用户消息
     */
    public PageResult<MessageVO> listMessages(Long userId, String type, Integer page, Integer size) {
        Page<Message> p = new Page<>(page, size);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, userId)
                .eq(type != null, Message::getType, type)
                .orderByDesc(Message::getCreatedAt);

        IPage<Message> result = messageMapper.selectPage(p, wrapper);

        List<MessageVO> list = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(list, result.getTotal(), page, size);
    }

    /**
     * 标记已读
     */
    public void markRead(Long messageId) {
        Message msg = messageMapper.selectById(messageId);
        if (msg != null) {
            msg.setIsRead(true);
            messageMapper.updateById(msg);
        }
    }

    /**
     * 未读数量
     */
    public long countUnread(Long userId) {
        return messageMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getUserId, userId)
                        .eq(Message::getIsRead, false));
    }

    public MessageVO convertToVO(Message msg) {
        MessageVO vo = new MessageVO();
        vo.setId(msg.getId());
        vo.setType(msg.getType());
        vo.setTitle(msg.getTitle());
        vo.setContent(msg.getContent());
        vo.setIsRead(msg.getIsRead());
        vo.setSenderId(msg.getSenderId());
        vo.setRelatedId(msg.getRelatedId());
        vo.setRelatedType(msg.getRelatedType());
        vo.setCreatedAt(msg.getCreatedAt());
        return vo;
    }
}

### 6.9 新增模块服务示例

#### 6.9.1 关注控制器

```java
package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.service.UserFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/follows")
@RequiredArgsConstructor
public class UserFollowController {

    private final UserFollowService userFollowService;

    @PostMapping("/{userId}")
    public ApiResponse<Void> follow(@PathVariable Long userId) {
        userFollowService.follow(userId);
        return ApiResponse.success();
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<Void> unfollow(@PathVariable Long userId) {
        userFollowService.unfollow(userId);
        return ApiResponse.success();
    }

    @GetMapping
    public ApiResponse<PageResult<UserPublicVO>> list(@RequestParam int page, @RequestParam int size) {
        return ApiResponse.success(userFollowService.listFollows(page, size));
    }

    @GetMapping("/{userId}/check")
    public ApiResponse<Map<String, Boolean>> check(@PathVariable Long userId) {
        boolean followed = userFollowService.isFollowed(userId);
        return ApiResponse.success(Map.of("followed", followed));
    }
}
```

#### 6.9.2 车行控制器

```java
package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.service.ShopMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
public class ShopMemberController {

    private final ShopMemberService shopMemberService;

    @GetMapping("/members")
    public ApiResponse<PageResult<ShopMemberVO>> listMembers(@RequestParam int page, @RequestParam int size) {
        return ApiResponse.success(shopMemberService.listMembers(page, size));
    }

    @PostMapping("/members/invite")
    public ApiResponse<Void> invite(@RequestBody Map<String, Long> body) {
        shopMemberService.invite(body.get("userId"));
        return ApiResponse.success();
    }

    @PutMapping("/members/{id}/approve")
    public ApiResponse<Void> approve(@PathVariable Long id, @RequestParam boolean approve) {
        shopMemberService.approve(id, approve);
        return ApiResponse.success();
    }

    @DeleteMapping("/members/{id}")
    public ApiResponse<Void> remove(@PathVariable Long id) {
        shopMemberService.remove(id);
        return ApiResponse.success();
    }
}
```

#### 6.9.3 聊天控制器

```java
package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/conversations")
    public ApiResponse<List<ConversationVO>> listConversations() {
        return ApiResponse.success(chatService.listConversations());
    }

    @PostMapping("/conversations")
    public ApiResponse<ConversationVO> createConversation(@RequestBody CreateConversationDTO dto) {
        return ApiResponse.success(chatService.createConversation(dto));
    }

    @GetMapping("/conversations/{id}/messages")
    public ApiResponse<PageResult<ChatMessageVO>> listMessages(
            @PathVariable Long id, @RequestParam int page, @RequestParam int size) {
        return ApiResponse.success(chatService.listMessages(id, page, size));
    }

    @PostMapping("/conversations/{id}/read")
    public ApiResponse<Void> markRead(@PathVariable Long id) {
        chatService.markRead(id);
        return ApiResponse.success();
    }
}
```

#### 6.9.4 电子合同控制器

```java
package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @PostMapping
    public ApiResponse<ContractVO> create(@RequestBody Map<String, String> body) {
        return ApiResponse.success(contractService.create(body.get("orderId")));
    }

    @GetMapping("/{id}")
    public ApiResponse<ContractDetailVO> detail(@PathVariable Long id) {
        return ApiResponse.success(contractService.detail(id));
    }

    @PutMapping("/{id}/sign")
    public ApiResponse<Void> sign(@PathVariable Long id) {
        contractService.sign(id);
        return ApiResponse.success();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        return contractService.download(id);
    }
}
```

#### 6.9.5 在线客服控制器

```java
package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cs")
@RequiredArgsConstructor
public class CustomerServiceController {

    private final CustomerService customerService;

    @PostMapping("/tickets")
    public ApiResponse<TicketVO> createTicket(@RequestBody TicketCreateDTO dto) {
        return ApiResponse.success(customerService.createTicket(dto));
    }

    @GetMapping("/tickets")
    public ApiResponse<PageResult<TicketVO>> listTickets(
            @RequestParam(required = false) String status,
            @RequestParam int page, @RequestParam int size) {
        return ApiResponse.success(customerService.listTickets(status, page, size));
    }

    @GetMapping("/tickets/{id}")
    public ApiResponse<TicketDetailVO> ticketDetail(@PathVariable Long id) {
        return ApiResponse.success(customerService.ticketDetail(id));
    }
}
```

#### 6.9.6 浏览记录服务

```java
package com.pancosky.newcartrade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pancosky.newcartrade.common.PageResult;
import com.pancosky.newcartrade.entity.BrowsingHistory;
import com.pancosky.newcartrade.mapper.BrowsingHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrowsingHistoryService {

    private final BrowsingHistoryMapper browsingHistoryMapper;

    @Async
    public void record(Long userId, Long carId) {
        BrowsingHistory history = new BrowsingHistory();
        history.setUserId(userId);
        history.setCarId(carId);
        browsingHistoryMapper.insert(history);
    }

    public PageResult<BrowsingHistoryVO> list(Long userId, int page, int size) {
        Page<BrowsingHistory> p = new Page<>(page, size);
        LambdaQueryWrapper<BrowsingHistory> wrapper = new LambdaQueryWrapper<BrowsingHistory>()
                .eq(BrowsingHistory::getUserId, userId)
                .orderByDesc(BrowsingHistory::getCreatedAt);
        IPage<BrowsingHistory> result = browsingHistoryMapper.selectPage(p, wrapper);
        // 转换为 VO 并入 car info
        return PageResult.of(convertToVO(result.getRecords()), result.getTotal(), page, size);
    }

    public void clear(Long userId) {
        browsingHistoryMapper.delete(
                new LambdaQueryWrapper<BrowsingHistory>().eq(BrowsingHistory::getUserId, userId));
    }
}
```

### 6.10 消息类型扩展

在 `MessageType.java` 枚举中新增类型：

```java
public enum MessageType implements IEnum<Integer> {
    SYSTEM(0),
    TRADE(1),
    ACTIVITY(2),
    AUTO_PROMOTION(3),     // 自动推广通知
    CHAT(4),               // 聊天消息
    TEAM_APPLICATION(5),   // 车行成员申请
    DEPOSIT_WARNING(6);    // 保证金不足提醒

    private final int value;

    MessageType(int value) { this.value = value; }

    @Override
    public Integer getValue() { return value; }
}
```
```
