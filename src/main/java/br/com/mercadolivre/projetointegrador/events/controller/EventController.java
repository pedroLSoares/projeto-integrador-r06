package br.com.mercadolivre.projetointegrador.events.controller;

import br.com.mercadolivre.projetointegrador.events.dto.request.NewWarehouseEventDTO;
import br.com.mercadolivre.projetointegrador.events.dto.response.EventsExecutedDTO;
import br.com.mercadolivre.projetointegrador.events.dto.response.WarehouseEventCreatedDTO;
import br.com.mercadolivre.projetointegrador.events.dto.response.WarehouseEventDTO;
import br.com.mercadolivre.projetointegrador.events.dto.response.WarehouseListEventsResponseDTO;
import br.com.mercadolivre.projetointegrador.events.model.WarehouseEvent;
import br.com.mercadolivre.projetointegrador.events.service.WarehouseEventService;
import br.com.mercadolivre.projetointegrador.events.assembler.EventsAssembler;
import br.com.mercadolivre.projetointegrador.events.view.WarehouseEventView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/warehouse")
@RequiredArgsConstructor
public class EventController {

    private final WarehouseEventService eventService;
    private final EventsAssembler assembler;

    // cadastrar um evento na warehouse
    @PostMapping("/events")
    public ResponseEntity<WarehouseEventCreatedDTO> registerWarehouseEvent(@RequestBody @Valid NewWarehouseEventDTO body){
        WarehouseEvent createdEvent = eventService.registerEventToWarehouse(body);

        return assembler.toEventCreatedResponse(createdEvent, HttpStatus.CREATED);
    }

    // GET eventos cadastrados
    @GetMapping("/events")
    //@JsonView(WarehouseEventView.WarehouseEventListResponse.class)
    public ResponseEntity<List<WarehouseListEventsResponseDTO>> getRegisteredWarehouseEvents(){
        return assembler.toWarehousesEventsResponse(eventService.getAllWarehouseEvents(), HttpStatus.OK);
    }

    // GET eventos cadastros em uma warehouse
    @GetMapping("{warehouseId}/events")
    @JsonView(WarehouseEventView.WarehouseEventListResponse.class)
    public ResponseEntity<List<WarehouseEventDTO>> getWarehouseEventsDetails(@PathVariable Long warehouseId){
        List<WarehouseEvent> warehouseEvents = eventService.getWarehouseEvents(warehouseId);
        return assembler.toWarehouseEventResponse(warehouseEvents, HttpStatus.OK);
    }

    // Executar eventos
    @PostMapping("/events/execute")
    @JsonView(WarehouseEventView.WarehouseEventResponse.class)
    public ResponseEntity<List<EventsExecutedDTO>> executeEvents(){
        List<EventsExecutedDTO> response = eventService.executeEvents();

        return ResponseEntity.ok().body(response);
    }

    // buscar pelo evento de warehouse por id, para visualizar os produtos cadastrados e etc
    @GetMapping("/events/detail/{warehouseEventId}")
    @JsonView(WarehouseEventView.WarehouseEventDetailsResponse.class)
    public ResponseEntity<WarehouseEventDTO> getWarehouseEventDetail(@PathVariable Long warehouseEventId){
        WarehouseEvent result = eventService.getEvent(warehouseEventId);

        return assembler.toWarehouseEventResponse(result, HttpStatus.OK);

    }

    @PostMapping( "/events/{warehouseEventId}/products/{productId}")
    @JsonView(WarehouseEventView.WarehouseEventDetailsResponse.class)
    public ResponseEntity<WarehouseEventDTO> addProductIntoEvent(@PathVariable Long warehouseEventId,@PathVariable Long productId){
        WarehouseEvent result = eventService.addProductIntoEvent(warehouseEventId, productId);

        return assembler.toWarehouseEventResponse(result, HttpStatus.OK);
    }

    // Remover um evento da warehouse
    @DeleteMapping("/events/{warehouseEventId}")
    public ResponseEntity<Void> removeEventFromWarehouse(@PathVariable Long warehouseEventId){

        eventService.removeWarehouseEvent(warehouseEventId);

        return ResponseEntity.noContent().build();
    }

    // Remover produto do evento
    @DeleteMapping("/events/{warehouseEventID}/products/{productId}")
    @JsonView(WarehouseEventView.WarehouseEventDetailsResponse.class)
    public ResponseEntity<WarehouseEventDTO> removeProductFromEvent(@PathVariable Long warehouseEventID, @PathVariable Long productId){
        WarehouseEvent result = eventService.removeProductFromEvent(warehouseEventID, productId);

        return assembler.toWarehouseEventResponse(result, HttpStatus.OK);
    }
}
