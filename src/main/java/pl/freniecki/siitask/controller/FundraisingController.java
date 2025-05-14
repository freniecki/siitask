package pl.freniecki.siitask.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.freniecki.siitask.dto.*;
import pl.freniecki.siitask.service.FundraisingService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FundraisingController {

    private final FundraisingService fundraisingService;

    public FundraisingController(FundraisingService fundraisingService) {
        this.fundraisingService = fundraisingService;
    }

    // ========== EVENT ==========

    @Operation(summary = "Create a new fundraising event",
            description = "Creates a new fundraising event using the provided event data.")
    @PostMapping("/event")
    public ResponseEntity<String> createEvent(@RequestBody EventCreateDto eventCreateDto) {
        Long eventId = fundraisingService.createEvent(eventCreateDto);
        return ResponseEntity.ok("Event created with ID: " + eventId);
    }

    @Operation(summary = "Get all fundraising events",
            description = "Retrieves a list of all registered fundraising events.")
    @GetMapping("/event")
    public ResponseEntity<List<EventInfoDto>> getEvents() {
        return ResponseEntity.ok(fundraisingService.getAllEvents());
    }

    // =========== BOX ==========

    @Operation(summary = "Register a new donation box",
            description = "Generates and registers a new donation box with a unique UUID.")
    @PostMapping("/box")
    public ResponseEntity<String> registerBox() {
        UUID boxId = fundraisingService.registerBox();
        return ResponseEntity.ok("Box registered with UUID: " + boxId);
    }

    @Operation(summary = "Get all donation boxes",
            description = "Retrieves a list of all donation boxes.")
    @GetMapping("/box")
    public ResponseEntity<List<BoxInfoDto>> getBoxes() {
        return ResponseEntity.ok(fundraisingService.getAllBoxes());
    }

    @Operation(summary = "Remove a donation box",
            description = "Removes the donation box with the specified UUID.")
    @DeleteMapping("/box/{id}")
    public ResponseEntity<String> removeBox(
            @Parameter(description = "UUID of the box to be removed") @PathVariable UUID id) {
        fundraisingService.removeBox(id);
        return ResponseEntity.ok("Box removed with UUID: " + id);

    }

    @Operation(summary = "Assign a box to an event",
            description = "Assigns a donation box to a specific fundraising event.")
    @PutMapping("/box/{id}/assign")
    public ResponseEntity<String> assignBox(
            @Parameter(description = "UUID of the box to assign") @PathVariable UUID id,
            @RequestBody AssignDto assignDto) {
        fundraisingService.assignBox(id, assignDto);
        return ResponseEntity.ok("Box with UUID: " + id + " assigned to event with ID: " + assignDto.eventId());
    }

    @Operation(summary = "Add a donation to a box",
            description = "Adds a donation entry to the specified box, including amount and currency.")
    @PutMapping("/box/{id}/donate")
    public ResponseEntity<String> donate(
            @Parameter(description = "UUID of the box receiving the donation") @PathVariable UUID id,
            @RequestBody DonationDto donationDto) {
        fundraisingService.donate(id, donationDto);
        return ResponseEntity.ok("Box with UUID: " + id + " donated " + donationDto.value() + " " + donationDto.currency());
    }

    @Operation(summary = "Transfer money from a box",
            description = "Transfers all collected donations from the specified box to the event account.")
    @PutMapping("/box/{id}/transfer")
    public ResponseEntity<String> transferMoney(
            @Parameter(description = "UUID of the box to transfer money from") @PathVariable UUID id) {
        fundraisingService.transferMoney(id);
        return ResponseEntity.ok("Money transferred from box with UUID: " + id);
    }

    // ========= UTIL =========

    @Operation(summary = "Generate event report",
            description = "Returns a report of all fundraising events, including total donations.")
    @GetMapping("/raport")
    public ResponseEntity<List<RaportDto>> reportOnEvents() {
        return ResponseEntity.ok().body(fundraisingService.reportOnEvents());
    }
}

