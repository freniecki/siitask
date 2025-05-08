package pl.freniecki.siitask.service;

import org.springframework.stereotype.Service;
import pl.freniecki.siitask.dto.AssignDto;
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
import java.util.Optional;
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

    /**
     * Dto contains only name of an existing event.
     * @return List of events names
     */
    public List<EventDto> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(Event::getName)
                .map(name -> EventDto.builder().name(name).build())
                .toList();
    }

    // ========= BOX =========

    /**
     * Dto contains information about box state: is it assigned to any event, is it empty. Despite the fact that
     * box should always be empty when not assigned, it is checked anyway.
     * @return List of dto boxes
     */
    public List<BoxDto> getAllBoxes() {
        List<BoxDto> boxesDto = new ArrayList<>();
        for (Box box : boxRepository.findAll()) {
            BigDecimal sum = box.getVault().values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

            boxesDto.add(BoxDto.builder()
                    .isAssigned(box.getEventId() == null)
                    .isEmpty(sum.equals(BigDecimal.ZERO))
                    .build());
        }
        return boxesDto;
    }

    /**
     * Registration of a new box does not require any parameters, so it creates an empty box, not assigned to any event
     * instance. Box is identified by UUID, so it will always be unique.
     * @return ID of created box
     */
    public UUID registerBox() {
        Box box = boxRepository.save(Box.builder().build());
        return box.getId();
    }

    public boolean removeBox(UUID boxId) {
        Optional<Box> box = boxRepository.findById(boxId);
        if (box.isPresent()) {
            boxRepository.delete(box.get());
            return true;
        }
        return false;
    }

    public boolean assignBox(UUID boxId, AssignDto assignDto) {
        Optional<Box> box = boxRepository.findById(boxId);
        Optional<Event> event = eventRepository.findById(assignDto.getEventId());
        if (box.isPresent() && event.isPresent()) {
            box.get().setEventId(event.get().getId());
            boxRepository.save(box.get());
            return true;
        }
        return false;
    }
}
