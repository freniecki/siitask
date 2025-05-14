package pl.freniecki.siitask.service;

import org.springframework.stereotype.Service;
import pl.freniecki.siitask.dto.*;
import pl.freniecki.siitask.exceptions.InvalidDtoException;
import pl.freniecki.siitask.exceptions.ObjectNotFoundException;
import pl.freniecki.siitask.exceptions.UnregisteredBoxException;
import pl.freniecki.siitask.model.Box;
import pl.freniecki.siitask.model.Currency;
import pl.freniecki.siitask.model.Event;
import pl.freniecki.siitask.model.EventAccount;
import pl.freniecki.siitask.repository.BoxRepository;
import pl.freniecki.siitask.repository.EventAccountRepository;
import pl.freniecki.siitask.repository.EventRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class FundraisingService {
    private final BoxRepository boxRepository;
    private final EventRepository eventRepository;
    private final EventAccountRepository eventAccountRepository;

    private final ExchangeService exchangeService;

    private static final String BOX_NOT_FOUND = "Box not found";
    private static final String EVENT_NOT_FOUND = "Event not found";
    private static final String EVENT_ACCOUNT_NOT_FOUND = "Event account not found";

    public FundraisingService(BoxRepository boxRepository, EventRepository eventRepository,
                              EventAccountRepository eventAccountRepository, ExchangeService exchangeService) {
        this.boxRepository = boxRepository;
        this.eventRepository = eventRepository;
        this.eventAccountRepository = eventAccountRepository;
        this.exchangeService = exchangeService;
    }

    // =========== EVENT ============

    /**
     * Dto contains only name of an event.
     * @return ID of created event
     */
    public Long createEvent(EventCreateDto eventCreateDto) {
        if (eventCreateDto.name() == null) {
            throw new InvalidDtoException("Name cannot be null");
        }

        if (eventCreateDto.currency() == null) {
            throw new InvalidDtoException("Currency cannot be null");
        }

        Event event = eventRepository.save(
                Event.builder()
                        .name(eventCreateDto.name())
                        .build());

        eventAccountRepository.save(
                EventAccount.builder()
                        .eventId(event.getId())
                        .currency(eventCreateDto.currency())
                        .vault(BigDecimal.ZERO)
                        .build());

        return event.getId();
    }

    /**
     * Dto contains only name of an existing event.
     * @return List of events names
     */
    public List<EventInfoDto> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(event -> new EventInfoDto(event.getId().toString(), event.getName()))
                .toList();
    }

    // ========= BOX =========

    /**
     * Dto contains information about box state: is it assigned to any event, is it empty. Despite the fact that
     * box should always be empty when not assigned, it is checked anyway.
     * @return List of dto boxes
     */
    public List<BoxInfoDto> getAllBoxes() {
        List<BoxInfoDto> boxesDto = new ArrayList<>();
        for (Box box : boxRepository.findAll()) {
            String boxId = box.getId().toString();
            boolean isAssigned = box.getEventId() != null;
            boolean isEmpty = exchangeService.getAbsoluteSum(box.getVault()).compareTo(BigDecimal.ZERO) == 0;

            boxesDto.add(new BoxInfoDto(boxId, isAssigned, isEmpty));
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

    /**
     * If exists, box is removed. Else custom exception is thrown.
     * @param boxId UUID of the box
     */
    public void removeBox(UUID boxId) {
        Box box = boxRepository.findById(boxId)
                .orElseThrow(() -> new ObjectNotFoundException(BOX_NOT_FOUND));

        boxRepository.delete(box);
    }

    /**
     * Assigns exising box to an existing event, returns custom exception if box or event does not exist.
     * @param boxId UUID of the box to be assigned
     * @param assignDto contains ID of the event
     */
    public void assignBox(UUID boxId, AssignDto assignDto) {
        Box box = boxRepository.findById(boxId)
                .orElseThrow(() -> new ObjectNotFoundException(BOX_NOT_FOUND));

        Event event = eventRepository.findById(assignDto.eventId())
                .orElseThrow(() -> new ObjectNotFoundException(EVENT_NOT_FOUND));

        box.setEventId(event.getId());
        boxRepository.save(box);
    }

    /**
     * Adds given value at given currency to a box, returns custom exception if box does not exist.
     * @param boxId UUID of the box
     * @param donationDto contains currency and value
     */
    public void donate(UUID boxId, DonationDto donationDto) {
        Box box = boxRepository.findById(boxId)
                .orElseThrow(() -> new ObjectNotFoundException(BOX_NOT_FOUND));

        validateDonation(donationDto);

        box.getVault().put(
                donationDto.currency(),
                box.getVault().getOrDefault(donationDto.currency(), BigDecimal.ZERO).add(donationDto.value().setScale(2, RoundingMode.HALF_UP))
        );

        boxRepository.save(box);
    }

    /**
     * Supporting method for validation. If currency is not valid or value is negative, custom exception is thrown.
     * @param donationDto contains currency and value
     */
    private void validateDonation(DonationDto donationDto) {
        if (!EnumSet.allOf(Currency.class).contains(donationDto.currency())) {
            throw new InvalidDtoException("Invalid currency - must be one of: " + EnumSet.allOf(Currency.class));
        }

        if (donationDto.value().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDtoException("Value must be greater than zero");
        }
    }

    /**
     * For given box, checks if it exists and is registered to any event, and if so,
     * transfer money from box to event's account. Calculation is handled by exchangeService.
     * For sum grater than or equal to zero, sum is added to account and box is unassigned.
     * @param boxId UUID of the box
     */
    public void transferMoney(UUID boxId) {
        Box box = boxRepository
                .findById(boxId)
                .orElseThrow(() -> new ObjectNotFoundException(BOX_NOT_FOUND));

        if (box.getEventId() == null) {
            throw new UnregisteredBoxException("Box " + boxId + " is not registered to any event");
        }

        Event event = eventRepository
                .findById(box.getEventId())
                .orElseThrow(() -> new ObjectNotFoundException(EVENT_NOT_FOUND));

        EventAccount eventAccount = eventAccountRepository
                .findByEventId(event.getId())
                .orElseThrow(() -> new ObjectNotFoundException(EVENT_ACCOUNT_NOT_FOUND));

        BigDecimal sum = exchangeService.getConvertedSum(box.getVault(), eventAccount.getCurrency());
        if (sum.compareTo(BigDecimal.ZERO) > 0) {
            eventAccount.setVault(eventAccount.getVault().add(sum).setScale(2, RoundingMode.HALF_UP));
            eventAccountRepository.save(eventAccount);
        }

        box.setEventId(null);
        box.emptyVault();
        boxRepository.save(box);
    }

    /**
     * For every existing event, retrieves its name, currency stored money and account's currency.
     * @return list containing information about each event
     */
    public List<RaportDto> reportOnEvents() {
        List<RaportDto> raportDtoList = new ArrayList<>();
        for (Event event : eventRepository.findAll()) {
            EventAccount eventAccount = eventAccountRepository
                    .findByEventId(event.getId())
                    .orElseThrow(() -> new ObjectNotFoundException(EVENT_ACCOUNT_NOT_FOUND));

            raportDtoList.add(new RaportDto(
                    event.getName(),
                    eventAccount.getVault(),
                    eventAccount.getCurrency())
            );
        }

        return raportDtoList;
    }
}
