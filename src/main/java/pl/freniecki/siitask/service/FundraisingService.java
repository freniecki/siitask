package pl.freniecki.siitask.service;

import org.springframework.stereotype.Service;
import pl.freniecki.siitask.dto.BoxDto;
import pl.freniecki.siitask.dto.EventDto;
import pl.freniecki.siitask.model.Box;
import pl.freniecki.siitask.model.Event;
import pl.freniecki.siitask.repository.BoxRepository;
import pl.freniecki.siitask.repository.EventAccountRepository;
import pl.freniecki.siitask.repository.EventRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FundraisingService {
    private final BoxRepository boxRepository;
    private final EventRepository eventRepository;
    private final EventAccountRepository eventAccountRepository;

    public FundraisingService(BoxRepository boxRepository, EventRepository eventRepository, EventAccountRepository eventAccountRepository) {
        this.boxRepository = boxRepository;
        this.eventRepository = eventRepository;
        this.eventAccountRepository = eventAccountRepository;
    }

    // =========== EVENT ============

    public Long createEvent(EventDto eventDto) {
        Event event = eventRepository.save(Event.builder()
                .name(eventDto.getName())
                .build());

        return event.getId();
    }

    public List<EventDto> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(Event::getName)
                .map(name -> EventDto.builder().name(name).build())
                .toList();
    }

    // ========= BOX =========

    public List<BoxDto> getAllBoxes() {
        List<BoxDto> boxesDto = new ArrayList<>();
        for (Box box : boxRepository.findAll()) {
            BigDecimal sum = box.getVault().values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            boolean isEmpty = sum.equals(BigDecimal.ZERO);

            if (box.getEventId() == null) {
                boxesDto.add(BoxDto.builder()
                        .isAssigned(false)
                        .isEmpty(isEmpty)
                        .build());
            } else {
                boxesDto.add(BoxDto.builder()
                        .isAssigned(true)
                        .isEmpty(isEmpty)
                        .build());
            }
        }
        return boxesDto;
    }

    public UUID registerBox() {
        Box box = boxRepository.save(Box.builder().build());
        return box.getId();
    }
}
