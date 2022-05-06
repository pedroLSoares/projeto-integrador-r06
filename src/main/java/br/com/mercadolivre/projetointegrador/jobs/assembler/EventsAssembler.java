package br.com.mercadolivre.projetointegrador.jobs.assembler;

import br.com.mercadolivre.projetointegrador.jobs.controller.WarehouseJobsController;
import br.com.mercadolivre.projetointegrador.jobs.dto.response.WarehouseJobCreatedDTO;
import br.com.mercadolivre.projetointegrador.jobs.dto.response.WarehouseJobDTO;
import br.com.mercadolivre.projetointegrador.jobs.dto.response.WarehouseListEventsResponseDTO;
import br.com.mercadolivre.projetointegrador.jobs.mapper.JobMapper;
import br.com.mercadolivre.projetointegrador.jobs.mapper.WarehouseJobMapper;
import br.com.mercadolivre.projetointegrador.jobs.model.WarehouseJob;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class EventsAssembler {

    public ResponseEntity<WarehouseJobCreatedDTO> toEventCreatedResponse(WarehouseJob event, HttpStatus status){


        WarehouseJobCreatedDTO dto = WarehouseJobCreatedDTO.builder()
                .idJob(event.getId())
                .idWarehouse(event.getWarehouse().getId())
                .build();

        dto.setExecutionUri(linkTo(methodOn(WarehouseJobsController.class).executeJobs()).toString());


        return ResponseEntity.status(status).body(dto);
    }

    public ResponseEntity<WarehouseJobDTO> toWarehouseEventResponse(WarehouseJob event, HttpStatus status){
        WarehouseJobDTO dto = WarehouseJobMapper.INSTANCE.toDto(event);
        return ResponseEntity.status(status).body(dto);
    }

    public ResponseEntity<List<WarehouseJobDTO>> toWarehouseEventResponse(List<WarehouseJob> events, HttpStatus status){

        return ResponseEntity.status(status).body(WarehouseJobMapper.INSTANCE.toDto(events));
    }

    public ResponseEntity<List<WarehouseListEventsResponseDTO>> toWarehousesEventsResponse(List<WarehouseJob> events, HttpStatus status){
        Map<Long, WarehouseListEventsResponseDTO> resultDto = new HashMap<>();

        events.forEach(event -> {
            Long warehouseId = event.getWarehouse().getId();
            if(resultDto.get(warehouseId) == null){
                resultDto.put(warehouseId, new WarehouseListEventsResponseDTO(warehouseId, new ArrayList<>()));
            }
            WarehouseListEventsResponseDTO dto = resultDto.get(warehouseId);


            dto.getRegisteredJobs().add(JobMapper.INSTANCE.toDto(event.getJob()));
        });

        var test = resultDto.values();

        return ResponseEntity.status(status).body(new ArrayList<>(resultDto.values()));
    }
}
