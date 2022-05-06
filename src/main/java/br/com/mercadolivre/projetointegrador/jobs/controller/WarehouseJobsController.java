package br.com.mercadolivre.projetointegrador.jobs.controller;

import br.com.mercadolivre.projetointegrador.jobs.dto.request.UpdateJobProductsDTO;
import br.com.mercadolivre.projetointegrador.jobs.dto.request.NewWarehouseJobDTO;
import br.com.mercadolivre.projetointegrador.jobs.dto.response.*;
import br.com.mercadolivre.projetointegrador.jobs.model.WarehouseJob;
import br.com.mercadolivre.projetointegrador.jobs.service.WarehouseJobService;
import br.com.mercadolivre.projetointegrador.jobs.assembler.EventsAssembler;
import br.com.mercadolivre.projetointegrador.jobs.view.WarehouseEventView;
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
public class WarehouseJobsController {

    private final WarehouseJobService eventService;
    private final EventsAssembler assembler;

    // cadastrar um evento na warehouse
    @PostMapping("/jobs")
    public ResponseEntity<WarehouseJobCreatedDTO> registerWarehouseJob(@RequestBody @Valid NewWarehouseJobDTO body){
        WarehouseJob createdEvent = eventService.registerJobToWarehouse(body);

        return assembler.toEventCreatedResponse(createdEvent, HttpStatus.CREATED);
    }

    // GET eventos cadastrados
    @GetMapping("/jobs")
    public ResponseEntity<List<WarehouseListEventsResponseDTO>> getRegisteredJobsByWarehouse(){
        return assembler.toWarehousesEventsResponse(eventService.getAllWarehouseJobs(), HttpStatus.OK);
    }

    // GET eventos cadastros em uma warehouse
    @GetMapping("{warehouseId}/jobs")
    @JsonView(WarehouseEventView.WarehouseEventListResponse.class)
    public ResponseEntity<List<WarehouseJobDTO>> getWarehouseJobsDetails(@PathVariable Long warehouseId){
        List<WarehouseJob> warehouseJobs = eventService.getWarehouseJobs(warehouseId);
        return assembler.toWarehouseEventResponse(warehouseJobs, HttpStatus.OK);
    }

    // Executar eventos
    @PostMapping("/jobs/execute")
    @JsonView(WarehouseEventView.WarehouseEventResponse.class)
    public ResponseEntity<List<ExecutionResponseDTO>> executeJobs(){
        List<ExecutionResponseDTO> response = eventService.executeJobs();

        return ResponseEntity.ok().body(response);
    }

    // buscar pelo evento de warehouse por id, para visualizar os produtos cadastrados e etc
    @GetMapping("/jobs/detail/{warehouseJobId}")
    @JsonView(WarehouseEventView.WarehouseEventDetailsResponse.class)
    public ResponseEntity<WarehouseJobDTO> getWarehouseJobDetail(@PathVariable Long warehouseJobId){
        WarehouseJob result = eventService.getJob(warehouseJobId);

        return assembler.toWarehouseEventResponse(result, HttpStatus.OK);

    }

    @PatchMapping( "/jobs/products")
    @JsonView(WarehouseEventView.WarehouseEventDetailsResponse.class)
    public ResponseEntity<WarehouseJobDTO> addProductIntoJob(@RequestBody UpdateJobProductsDTO updateJobProductsDTO){
        WarehouseJob result = eventService.addProductsIntoJob(updateJobProductsDTO.getWarehouseEventId(), updateJobProductsDTO.getProductList());

        return assembler.toWarehouseEventResponse(result, HttpStatus.OK);
    }

    // Remover um evento da warehouse
    @DeleteMapping("/jobs/{warehouseJobId}")
    public ResponseEntity<Void> removeEventFromWarehouse(@PathVariable Long warehouseJobId){

        eventService.removeWarehouseJob(warehouseJobId);

        return ResponseEntity.noContent().build();
    }

    // Remover produtos do evento
    @DeleteMapping("/jobs/products")
    @JsonView(WarehouseEventView.WarehouseEventDetailsResponse.class)
    public ResponseEntity<WarehouseJobDTO> removeProductFromJob(@RequestBody UpdateJobProductsDTO updateJobProductsDTO){
        WarehouseJob result = eventService.removeProductFromJob(updateJobProductsDTO.getWarehouseEventId(), updateJobProductsDTO.getProductList());

        return assembler.toWarehouseEventResponse(result, HttpStatus.OK);
    }
}
