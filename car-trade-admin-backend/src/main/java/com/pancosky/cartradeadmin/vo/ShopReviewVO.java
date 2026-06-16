package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShopReviewVO {
    private Long id;
    private String shopName;
    private String realName;
    private String phone;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private String certificationStatus;

    // --- Aliased fields for frontend compatibility ---

    /** Alias of realName — applicant real name */
    private String applicantName;

    /** Unmasked phone for admin view */
    private String applicantPhone;

    /** Alias of certificationStatus — frontend reads this field */
    private String status;

    // --- New audit-related fields ---

    /** 营业执照号或URL */
    private String businessLicense;

    /** 身份证正面图片URL */
    private String idCardFront;

    /** 身份证反面图片URL */
    private String idCardBack;

    /** 营业执照图片URL (distinct from businessLicense which is the number) */
    private String licenseImage;

    /** 申请人身份证号码 */
    private String idCardNumber;

    /** 最近一次驳回原因 */
    private String rejectReason;

    /** 审核人ID */
    private Long reviewerId;

    /** 审核人姓名 */
    private String reviewerName;

    /** 最近审核时间 */
    private LocalDateTime reviewedAt;
}
