package br.com.mercadolivre.projetointegrador.jobs.controller;

import br.com.mercadolivre.projetointegrador.jobs.dto.request.UpdateJobProductsDTO;
import br.com.mercadolivre.projetointegrador.jobs.dto.request.NewWarehouseJobDTO;
import br.com.mercadolivre.projetointegrador.jobs.dto.response.*;
import br.com.mercadolivre.projetointegrador.jobs.model.WarehouseJob;
import br.com.mercadolivre.projetointegrador.jobs.service.WarehouseJobService;
import br.com.mercadolivre.projetointegrador.jobs.assembler.EventsAssembler;
import br.com.mercadolivre.projetointegrador.jobs.view.WarehouseEventView;
import br.com.mercadolivre.projetointegrador.warehouse.exception.ErrorDTO;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/warehouse")
@RequiredArgsConstructor
@Tag(name = "[Warehouse] - Warehouse Jobs")
public class WarehouseJobsController {

    private final WarehouseJobService eventService;
    private final EventsAssembler assembler;

    @Operation(
            summary = "ASSOCIA UM JOB EM UMA WAREHOUSE",
            description = "Vincula um job existente em uma warehouse")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Job criado com sucesso",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = WarehouseJobCreatedDTO.class))
                            }),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Warehouse ou Evento inexistente!",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = ErrorDTO.class))
                            })
            })
    @PostMapping("/jobs")
    public ResponseEntity<WarehouseJobCreatedDTO> registerWarehouseJob(@RequestBody @Valid NewWarehouseJobDTO body){
        WarehouseJob createdEvent = eventService.registerJobToWarehouse(body);

        return assembler.toEventCreatedResponse(createdEvent, HttpStatus.CREATED);
    }

    @Operation(
            summary = "RETORNA JOBS VINCULADOS",
            description = "Retorna uma lista com todos os armazens que possuem jobs vinculados")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista retornada com sucesso",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = WarehouseListEventsResponseDTO.class)))
                            })
            })
    @GetMapping("/jobs")
    public ResponseEntity<List<WarehouseListEventsResponseDTO>> getRegisteredJobsByWarehouse(){
        return assembler.toWarehousesEventsResponse(eventService.getAllWarehouseJobs(), HttpStatus.OK);
    }

    @Operation(
            summary = "RETORNA JOBS VINCULADOS EM UM ARMAZÉM",
            description = "Retorna uma lista com todos os eventos vinculados em um único armazém, caso não possua, retorna uma lista vazia")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista retornada com sucesso",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = WarehouseJobDTO.class)))
                            })
            })
    @GetMapping("{warehouseId}/jobs")
    @JsonView(WarehouseEventView.WarehouseEventListResponse.class)
    public ResponseEntity<List<WarehouseJobDTO>> getWarehouseJobsDetails(@PathVariable Long warehouseId){
        List<WarehouseJob> warehouseJobs = eventService.getWarehouseJobs(warehouseId);
        return assembler.toWarehouseEventResponse(warehouseJobs, HttpStatus.OK);
    }

    @Operation(
            summary = "EXECUTA JOBS",
            description = "Executa todos os jobs cadastrados em todos os armazéns")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Jobs executados com sucesso",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = ExecutionResponseDTO.class)))
                            })
            })
    @PostMapping("/jobs/execute")
    @JsonView(WarehouseEventView.WarehouseEventResponse.class)
    public ResponseEntity<List<ExecutionResponseDTO>> executeJobs(){
        List<ExecutionResponseDTO> response = eventService.executeJobs();

        return ResponseEntity.ok().body(response);
    }


    @Operation(
            summary = "EXIBE TODAS AS INFORMAÇÕES DE UM JOB VINCULADO AO ARMAZÉM",
            description = "Executa todos os jobs cadastrados em todos os armazéns")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = WarehouseJobDTO.class))
                            })
            })
    @GetMapping("/jobs/detail/{warehouseJobId}")
    @JsonView(WarehouseEventView.WarehouseEventDetailsResponse.class)
    public ResponseEntity<WarehouseJobDTO> getWarehouseJobDetail(@PathVariable Long warehouseJobId){
        WarehouseJob result = eventService.getJob(warehouseJobId);

        return assembler.toWarehouseEventResponse(result, HttpStatus.OK);

    }

    @Operation(
            summary = "INSERE PRODUTOS PARA SEREM CONSIDERADOS PELOS JOBS")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = WarehouseJobDTO.class))
                            })
            })
    @PatchMapping( "/jobs/products")
    @JsonView(WarehouseEventView.WarehouseEventDetailsResponse.class)
    public ResponseEntity<WarehouseJobDTO> addProductIntoJob(@RequestBody UpdateJobProductsDTO updateJobProductsDTO){
        WarehouseJob result = eventService.addProductsIntoJob(updateJobProductsDTO.getWarehouseEventId(), updateJobProductsDTO.getProductList());

        return assembler.toWarehouseEventResponse(result, HttpStatus.OK);
    }

    @Operation(
            summary = "REMOVE UM JOB VINCULADO")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            content = {
                                    @Content(
                                            mediaType = "application/json")
                            })
            })
    @DeleteMapping("/jobs/{warehouseJobId}")
    public ResponseEntity<Void> removeEventFromWarehouse(@PathVariable Long warehouseJobId){

        eventService.removeWarehouseJob(warehouseJobId);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "REMOVE PRODUTOS PARA NÃO SEREM CONSIDERADOS PELOS JOBS")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = WarehouseJobDTO.class))
                            })
            })
    @DeleteMapping("/jobs/products")
    @JsonView(WarehouseEventView.WarehouseEventDetailsResponse.class)
    public ResponseEntity<WarehouseJobDTO> removeProductFromJob(@RequestBody UpdateJobProductsDTO updateJobProductsDTO){
        WarehouseJob result = eventService.removeProductFromJob(updateJobProductsDTO.getWarehouseEventId(), updateJobProductsDTO.getProductList());

        return assembler.toWarehouseEventResponse(result, HttpStatus.OK);
    }
}
