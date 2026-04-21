package com.accommodation.platform.core.accommodation.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import com.accommodation.platform.IntegrationTestBase;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationStatus;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class AccommodationJpaAdapterTest extends IntegrationTestBase {

    @Autowired
    private AccommodationJpaAdapter adapter;

    @Autowired
    private AccommodationJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
    }

    @Test
    void 숙소를_저장하고_조회한다() {

        // given
        Accommodation accommodation = Accommodation.builder()
                .partnerId(1L)
                .name("테스트 호텔")
                .type(AccommodationType.HOTEL)
                .fullAddress("서울시 강남구 테헤란로 123")
                .latitude(37.5665)
                .longitude(126.9780)
                .locationDescription("강남역 5번 출구")
                .build();

        // when
        Accommodation saved = adapter.save(accommodation);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("테스트 호텔");
        assertThat(saved.getType()).isEqualTo(AccommodationType.HOTEL);
        assertThat(saved.getStatus()).isEqualTo(AccommodationStatus.PENDING);

        // findById
        Optional<Accommodation> found = adapter.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("테스트 호텔");
    }

    @Test
    void 파트너ID로_숙소_목록을_조회한다() {

        // given
        adapter.save(createAccommodation(1L, "호텔 A"));
        adapter.save(createAccommodation(1L, "호텔 B"));
        adapter.save(createAccommodation(2L, "호텔 C"));

        // when
        List<Accommodation> partner1Accommodations = adapter.findByPartnerId(1L);
        List<Accommodation> partner2Accommodations = adapter.findByPartnerId(2L);

        // then
        assertThat(partner1Accommodations).hasSize(2);
        assertThat(partner2Accommodations).hasSize(1);
    }

    @Test
    void 존재하지_않는_ID로_조회하면_빈_Optional을_반환한다() {

        // when
        Optional<Accommodation> found = adapter.findById(999L);

        // then
        assertThat(found).isEmpty();
    }

    private Accommodation createAccommodation(Long partnerId, String name) {

        return Accommodation.builder()
                .partnerId(partnerId)
                .name(name)
                .type(AccommodationType.HOTEL)
                .fullAddress("서울시 강남구")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();
    }
}
