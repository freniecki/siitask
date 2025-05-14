package pl.freniecki.siitask.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.freniecki.siitask.dto.AssignDto;
import pl.freniecki.siitask.dto.DonationDto;
import pl.freniecki.siitask.dto.EventCreateDto;
import pl.freniecki.siitask.dto.EventInfoDto;
import pl.freniecki.siitask.exceptions.InvalidDtoException;
import pl.freniecki.siitask.exceptions.ObjectNotFoundException;
import pl.freniecki.siitask.model.Box;
import pl.freniecki.siitask.model.Event;
import pl.freniecki.siitask.model.Currency;
import pl.freniecki.siitask.repository.BoxRepository;
import pl.freniecki.siitask.repository.EventAccountRepository;
import pl.freniecki.siitask.repository.EventRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FundraisingServiceTest {

    @Mock
    BoxRepository boxRepository;
    @Mock
    EventRepository eventRepository;
    @Mock
    EventAccountRepository eventAccountRepository;
    @Mock
    ExchangeService exchangeService;

    @InjectMocks
    FundraisingService fundraisingService;

    @Test
    void createEvent_shouldSaveEventAndEventAccount() {
        EventCreateDto dto = new EventCreateDto("testName", Currency.EUR);
        Event savedEvent = Event.builder().id(1L).name("testName").build();

        when(eventRepository.save(any())).thenReturn(savedEvent);

        Long result = fundraisingService.createEvent(dto);

        assertThat(result).isEqualTo(1L);
        verify(eventRepository).save(any());
        verify(eventAccountRepository).save(any());
    }

    @Test
    void createEvent_shouldThrowException_whenNameIsNull() {
        EventCreateDto dto = new EventCreateDto(null, Currency.EUR);

        assertThatThrownBy(() -> fundraisingService.createEvent(dto))
                .isInstanceOf(InvalidDtoException.class)
                .hasMessage("Name cannot be null");
    }

    @Test
    void getAllEvents_shouldReturnAllEvents() {
        List<Event> events = List.of(Event.builder().id(1L).name("testName").build());
        when(eventRepository.findAll()).thenReturn(events);

        List<EventInfoDto> result = fundraisingService.getAllEvents();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("testName");
    }

    @Test
    void registerBox_shouldSaveBox() {
        UUID boxId = UUID.randomUUID();
        Box box = Box.builder().id(boxId).build();

        when(boxRepository.save(any())).thenReturn(box);

        UUID result = fundraisingService.registerBox();

        assertThat(result).isEqualTo(boxId);
        verify(boxRepository).save(any());
    }

    @Test
    void removeBox_shouldRemoveIfExist() {
        UUID boxId = UUID.randomUUID();
        Box box = Box.builder().id(boxId).build();

        when(boxRepository.findById(boxId)).thenReturn(Optional.of(box));

        fundraisingService.removeBox(boxId);

        verify(boxRepository).delete(box);
    }

    @Test
    void removeBox_shouldThrowIfNotFound() {
        UUID boxId = UUID.randomUUID();
        when(boxRepository.findById(boxId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fundraisingService.removeBox(boxId))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Box not found");
    }

    @Test
    void assignBox_shouldSetEventId() {
        UUID boxId = UUID.randomUUID();
        Box box = Box.builder().id(boxId).build();
        Event event = Event.builder().id(1L).build();
        AssignDto assignDto = new AssignDto(1L);

        when(boxRepository.findById(boxId)).thenReturn(Optional.of(box));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        fundraisingService.assignBox(boxId, assignDto);

        assertThat(box.getEventId()).isEqualTo(1L);
        verify(boxRepository).save(box);
    }

    @Test
    void donate_shouldAddToAccount() {
        UUID boxId = UUID.randomUUID();
        Box box = Box.builder().id(boxId).build();
        DonationDto donationDto = new DonationDto(Currency.EUR, BigDecimal.ONE);

        when(boxRepository.findById(boxId)).thenReturn(Optional.of(box));

        fundraisingService.donate(boxId, donationDto);

        assertThat(box.getVault()).containsEntry(Currency.EUR, BigDecimal.ONE);
    }

    @Test
    void transferMoney() {
        // todo
    }

    @Test
    void reportOnEvents() {
        // todo
    }
}