package pl.freniecki.siitask.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.freniecki.siitask.dto.*;
import pl.freniecki.siitask.service.FundraisingService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api")
public class FundraisingController {

    private final FundraisingService fundraisingService;

    public FundraisingController(FundraisingService fundraisingService) {
        this.fundraisingService = fundraisingService;
    }

    // ========== EVENT ==========

    @PostMapping("/event")
    public ResponseEntity<String> createEvent(@RequestBody EventDto eventDto) {
        Long eventId = fundraisingService.createEvent(eventDto);
        return ResponseEntity.ok("Event created with ID: " + eventId);
    }

    @GetMapping("/event")
    public ResponseEntity<List<EventDto>> getEvents() {
        return ResponseEntity.ok(fundraisingService.getAllEvents());
    }

    // =========== BOX ==========

    @PostMapping("/box")
    public ResponseEntity<String> registerBox() {
        UUID boxId = fundraisingService.registerBox();
        return ResponseEntity.ok("Box registered with UUID: " + boxId);
    }

    @GetMapping("/box")
    public ResponseEntity<List<BoxDto>> getBoxes() {
        return ResponseEntity.ok(fundraisingService.getAllBoxes());
    }

    @DeleteMapping("/box/{id}")
    public ResponseEntity<String> removeBox(@PathVariable UUID id) {
        boolean isRemoved = fundraisingService.removeBox(id);
        if (!isRemoved) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Box removed with UUID: " + id);

    }

    @PutMapping("/box/{id}/assign")
    public ResponseEntity<String> assignBox(@PathVariable UUID id, @RequestBody AssignDto assignDto) {
        boolean isAssigned = fundraisingService.assignBox(id, assignDto);
        if (!isAssigned) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Box with UUID: " + id + "assigned to event with ID: " + assignDto.eventId());
    }

    @PutMapping("/box/{id}/donate")
    public void donate(@PathVariable UUID id, @RequestBody DonationDto donationDto) {
        // TODO document why this method is empty
    }

    @PutMapping("/box/{id}/transfer")
    public void transferMoney(@PathVariable UUID id, @RequestBody TransferDto transfer) {
        // TODO document why this method is empty
    }

    // ========= UTIL =========

    @GetMapping("/raport")
    public List<RaportDto> reportOnEvents() {
        return new ArrayList<>();
    }
}

