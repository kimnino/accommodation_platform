package com.accommodation.platform.extranet.accommodation.application.service;

import java.time.LocalTime;
import java.util.List;

import com.accommodation.platform.core.accommodation.application.port.out.PersistAccommodationPort;
import com.accommodation.platform.core.accommodation.application.port.out.PersistAccommodationTranslationPort;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationStatus;
import com.accommodation.platform.core.accommodation.domain.enums.AccommodationType;
import com.accommodation.platform.core.accommodation.domain.model.Accommodation;
import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetRegisterAccommodationUseCase.RegisterAccommodationCommand;
import com.accommodation.platform.extranet.accommodation.application.port.in.ExtranetRegisterAccommodationUseCase.TranslationCommand;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExtranetRegisterAccommodationServiceTest {

    @InjectMocks
    private ExtranetRegisterAccommodationService service;

    @Mock
    private PersistAccommodationPort persistAccommodationPort;

    @Mock
    private PersistAccommodationTranslationPort persistTranslationPort;

    @Mock
    private EntityManager entityManager;

    @Test
    void 숙소를_다국어_정보와_함께_등록한다() {

        // given
        List<TranslationCommand> translations = List.of(
                new TranslationCommand("ko", "서울 호텔", "서울시 강남구", "강남역 근처",
                        "최고의 호텔", "수영장, 사우나", "15시 입실", "24시간 전 무료 취소"),
                new TranslationCommand("en", "Seoul Hotel", "Gangnam-gu, Seoul", "Near Gangnam Station",
                        "The best hotel", "Pool, Sauna", "Check-in at 3PM", "Free cancellation 24h before"));

        RegisterAccommodationCommand command = new RegisterAccommodationCommand(
                1L, "서울 호텔", "HOTEL", "서울시 강남구 테헤란로 123",
                37.5665, 126.9780, "강남역 5번 출구 도보 3분",
                "15:00", "11:00",
                List.of("KO", "EN"),
                translations);

        given(persistAccommodationPort.save(any(Accommodation.class)))
                .willAnswer(invocation -> {
                    Accommodation acc = invocation.getArgument(0);
                    acc.setCreatedAt(acc.getCreatedAt());
                    return acc;
                });

        // when
        Accommodation result = service.register(command);

        // then
        ArgumentCaptor<Accommodation> captor = ArgumentCaptor.forClass(Accommodation.class);
        verify(persistAccommodationPort).save(captor.capture());

        Accommodation saved = captor.getValue();
        assertThat(saved.getPartnerId()).isEqualTo(1L);
        assertThat(saved.getName()).isEqualTo("서울 호텔");
        assertThat(saved.getType()).isEqualTo(AccommodationType.HOTEL);
        assertThat(saved.getStatus()).isEqualTo(AccommodationStatus.PENDING);
        assertThat(saved.getCheckInTime()).isEqualTo(LocalTime.of(15, 0));

        verify(persistTranslationPort).saveAll(any());
    }

    @Test
    void 다국어_없이_기본_정보만으로_등록한다() {

        // given
        RegisterAccommodationCommand command = new RegisterAccommodationCommand(
                1L, "서울 호텔", "HOTEL", "서울시 강남구 테헤란로 123",
                37.5665, 126.9780, null,
                null, null,
                null, null);

        given(persistAccommodationPort.save(any(Accommodation.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        Accommodation result = service.register(command);

        // then
        assertThat(result.getName()).isEqualTo("서울 호텔");
    }

    @Test
    void 잘못된_숙소유형이면_예외가_발생한다() {

        // given
        RegisterAccommodationCommand command = new RegisterAccommodationCommand(
                1L, "테스트", "INVALID_TYPE", "서울시",
                37.5, 126.9, null, null, null,
                null, null);

        // when & then
        assertThatThrownBy(() -> service.register(command))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
