package br.com.mercadolivre.projetointegrador.jobs.controller;

import br.com.mercadolivre.projetointegrador.jobs.dto.request.NewJobDTO;
import br.com.mercadolivre.projetointegrador.jobs.dto.response.JobResponseDTO;
import br.com.mercadolivre.projetointegrador.jobs.dto.response.WarehouseJobCreatedDTO;
import br.com.mercadolivre.projetointegrador.jobs.mapper.JobMapper;
import br.com.mercadolivre.projetointegrador.jobs.model.Job;
import br.com.mercadolivre.projetointegrador.jobs.service.JobService;
import br.com.mercadolivre.projetointegrador.warehouse.exception.ErrorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @Operation(
            summary = "LISTA OS JOBS DISPONÍVEIS NO SISTEMA",
            description = "Vincula um job existente em uma warehouse")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = WarehouseJobCreatedDTO.class))
                            })
            })
    @GetMapping
    public ResponseEntity<List<JobResponseDTO>> listAllJobs(){
        List<Job> jobs = jobService.findAll();

        return ResponseEntity.ok(JobMapper.INSTANCE.toDto(jobs));
    }

    @Operation(
            summary = "CADASTRA UM NOVO JOB NO SISTEMA",
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
                            responseCode = "409",
                            description = "Job já existente!",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = ErrorDTO.class))
                            })
            })
    @PostMapping
    public ResponseEntity<JobResponseDTO> createJob(@RequestBody @Valid NewJobDTO newJobDTO){
        Job created = jobService.createJob(JobMapper.INSTANCE.toModel(newJobDTO));

        return ResponseEntity.ok(JobMapper.INSTANCE.toDto(created));
    }
}
