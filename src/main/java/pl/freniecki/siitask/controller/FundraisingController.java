package pl.freniecki.siitask.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.freniecki.siitask.dto.*;
import pl.freniecki.siitask.service.FundraisingService;

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
    public ResponseEntity<String> createEvent(@RequestBody EventCreateDto eventCreateDto) {
        Long eventId = fundraisingService.createEvent(eventCreateDto);
        return ResponseEntity.ok("Event created with ID: " + eventId);
    }

    @GetMapping("/event")
    public ResponseEntity<List<EventInfoDto>> getEvents() {
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
        fundraisingService.removeBox(id);
        return ResponseEntity.ok("Box removed with UUID: " + id);

    }

    @PutMapping("/box/{id}/assign")
    public ResponseEntity<String> assignBox(@PathVariable UUID id, @RequestBody AssignDto assignDto) {
        fundraisingService.assignBox(id, assignDto);
        return ResponseEntity.ok("Box with UUID: " + id + " assigned to event with ID: " + assignDto.eventId());
    }

    @PutMapping("/box/{id}/donate")
    public ResponseEntity<String> donate(@PathVariable UUID id, @RequestBody DonationDto donationDto) {
        fundraisingService.donate(id, donationDto);
        return ResponseEntity.ok("Box with UUID: " + id + "donated " + donationDto.value() + " " + donationDto.currency());
    }

    @PutMapping("/box/{id}/transfer")
    public ResponseEntity<String> transferMoney(@PathVariable UUID id) {
        fundraisingService.transferMoney(id);
        return ResponseEntity.ok("Money transferred from box with UUID: " + id);
    }

    // ========= UTIL =========

    @GetMapping("/raport")
    public ResponseEntity<List<RaportDto>> reportOnEvents() {
        return ResponseEntity.ok().body(fundraisingService.reportOnEvents());
    }
}

