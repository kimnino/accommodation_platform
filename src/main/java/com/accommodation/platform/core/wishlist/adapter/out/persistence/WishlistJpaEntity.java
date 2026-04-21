package com.accommodation.platform.core.wishlist.adapter.out.persistence;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.accommodation.platform.common.adapter.out.persistence.BaseJpaEntity;

import static lombok.AccessLevel.PROTECTED;

/**
 * 위시리스트 테이블.
 * 회원이 즐겨찾기로 저장한 숙소 목록을 관리한다.
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(
    name = "wishlist",
    indexes = {
        @Index(name = "idx_wishlist_member_id", columnList = "memberId"),
        @Index(name = "idx_wishlist_accommodation_id", columnList = "accommodationId")
    }
)
public class WishlistJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 회원 ID (member 테이블 참조, FK 미사용 — 인덱스로 대체)
     */
    @Column(nullable = false)
    private Long memberId;

    /**
     * 숙소 ID (accommodation 테이블 참조, FK 미사용 — 인덱스로 대체)
     */
    @Column(nullable = false)
    private Long accommodationId;

    /**
     * 소프트 삭제 여부
     */
    @Column(nullable = false)
    private boolean isDeleted = false;

    /**
     * 소프트 삭제 일시
     */
    private Instant deletedAt;

    public WishlistJpaEntity(Long id, Long memberId, Long accommodationId) {
        this.id = id;
        this.memberId = memberId;
        this.accommodationId = accommodationId;
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = Instant.now();
    }
}
