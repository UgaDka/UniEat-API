package foorun.unieat.api.model.database.member.entity;

import foorun.unieat.api.model.base.security.UniEatUserDetails;
import foorun.unieat.api.model.database.member.entity.primary_key.UniEatMemberId;
import foorun.unieat.common.rules.ManagedStatusType;
import foorun.unieat.common.rules.MemberRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * UNIEAT 회원정보
 */
@Entity
@Table(name = "member_base")
@Validated
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
@DynamicUpdate
@IdClass(UniEatMemberId.class)
public class UniEatMemberEntity extends UniEatUserDetails {
    /**
     * 회원 접속경로
     */
    @Id
    @Column(name = "member_provider", updatable = false)
    private String provider;

    /**
     * 회원 ID
     */
    @Id
    @Column(name = "member_id", updatable = false)
    private String primaryId;

    /**
     * 회원 REFRESH TOKEN 관리
     */
    @Setter
    @Column(name = "refresh_token")
    private String refreshToken;

    /**
     * 최종 로그인 일시
     */
    @Column(name = "last_sign_in_at")
    @Builder.Default
    private LocalDateTime lastSignInAt = null;

    /**
     * 계정 상태 관리
     */
    @Column(name = "manage_status", length = 10)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ManagedStatusType status = ManagedStatusType.ACTIVE;

    /**
     * 계정 권한 정보
     */
    @Column(name = "member_role", length = 10)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MemberRole role = MemberRole.GUEST;

    /**
     * 계정 잠금 기간
     */
    @Column(name = "locked_date")
    @Builder.Default
    private LocalDateTime lockedDate = null;

    /**
     * 계정 만료 기간
     */
    @Column(name = "expired_date")
    @Builder.Default
    private LocalDateTime expiredDate = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

    /**
     * 회원 마이페이지 연결 ID
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "member_provider", referencedColumnName = "member_provider"),
            @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    })
    private UniEatMemberMyPageEntity myPage;

    /**
     * 최종 로그인 일시 갱신
     */
    public UniEatMemberEntity updateSignInNow() {
        this.lastSignInAt = LocalDateTime.now();
        return this;
    }

    /**
     * 계정 잠금 갱신
     */
    public UniEatMemberEntity updateLocked(LocalDateTime date) {
        this.lockedDate = date;
        return this;
    }

    public UniEatMemberEntity updateLocked(int year, int month, int day, int hour, int minute, int second) {
        this.lockedDate = LocalDateTime.of(year, month - 1, day, hour, minute, second);
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return primaryId;
    }

    /* 만료일자 경과 여부 */
    @Override
    public boolean isAccountNonExpired() {
        return LocalDateTime.now().isBefore(expiredDate);
    }

    /* 잠금일자 경과 여부 */
    @Override
    public boolean isAccountNonLocked() {
        return lockedDate == null || LocalDateTime.now().isAfter(lockedDate);
    }

    /* 인증유효 경과 여부 */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /* 계정 활성 여부 */
    @Override
    public boolean isEnabled() {
        return status == ManagedStatusType.ACTIVE && isAccountNonExpired() && isAccountNonLocked() && isCredentialsNonExpired();
    }
}