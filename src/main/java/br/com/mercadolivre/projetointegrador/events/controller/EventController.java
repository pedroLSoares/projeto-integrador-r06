package br.com.mercadolivre.projetointegrador.events.controller;

import br.com.mercadolivre.projetointegrador.events.dto.request.NewWarehouseEventDTO;
import br.com.mercadolivre.projetointegrador.events.model.WarehouseEvent;
import br.com.mercadolivre.projetointegrador.events.service.WarehouseEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/warehouse")
@RequiredArgsConstructor
public class EventController {

    private final WarehouseEventService eventService;

    @PostMapping("/events")
    public ResponseEntity<?> registerWarehouseEvent(@RequestBody @Valid NewWarehouseEventDTO body){
        WarehouseEvent createdEvent = eventService.registerEventToWarehouse(body);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/events")
    public ResponseEntity<?> getRegisteredWarehouseEvents(){
        return ResponseEntity.ok(eventService.getAllWarehouseEvents());
    }

    @GetMapping("{warehouseId}/events")
    public ResponseEntity<?> getWarehouseEventsDetails(@PathVariable Long warehouseId){
        return ResponseEntity.ok(eventService.getWarehouseEvents(warehouseId));
    }

    @PostMapping("/events/execute")
    public ResponseEntity<?> executeEvents(){
        eventService.executeEvents();

        return ResponseEntity.ok().build();
    }
}
