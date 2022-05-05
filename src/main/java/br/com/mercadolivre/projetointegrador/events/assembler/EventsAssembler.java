package br.com.mercadolivre.projetointegrador.events.assembler;

import br.com.mercadolivre.projetointegrador.events.dto.response.WarehouseEventCreatedDTO;
import br.com.mercadolivre.projetointegrador.events.dto.response.WarehouseEventDTO;
import br.com.mercadolivre.projetointegrador.events.dto.response.WarehouseListEventsResponseDTO;
import br.com.mercadolivre.projetointegrador.events.mapper.EventMapper;
import br.com.mercadolivre.projetointegrador.events.mapper.WarehouseEventMapper;
import br.com.mercadolivre.projetointegrador.events.model.WarehouseEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EventsAssembler {

    public ResponseEntity<WarehouseEventCreatedDTO> toEventCreatedResponse(WarehouseEvent event, HttpStatus status){


        WarehouseEventCreatedDTO dto = WarehouseEventCreatedDTO.builder()
                .idEvent(event.getId())
                .idWarehouse(event.getWarehouse().getId())
                .build();


        return ResponseEntity.status(status).body(dto);
    }

    public ResponseEntity<WarehouseEventDTO> toWarehouseEventResponse(WarehouseEvent event, HttpStatus status){
        WarehouseEventDTO dto = WarehouseEventMapper.INSTANCE.toDto(event);
        return ResponseEntity.status(status).body(dto);
    }

    public ResponseEntity<List<WarehouseEventDTO>> toWarehouseEventResponse(List<WarehouseEvent> events, HttpStatus status){

        return ResponseEntity.status(status).body(WarehouseEventMapper.INSTANCE.toDto(events));
    }

    public ResponseEntity<List<WarehouseListEventsResponseDTO>> toWarehousesEventsResponse(List<WarehouseEvent> events, HttpStatus status){
        Map<Long, WarehouseListEventsResponseDTO> resultDto = new HashMap<>();

        events.forEach(event -> {
            Long warehouseId = event.getWarehouse().getId();
            if(resultDto.get(warehouseId) == null){
                resultDto.put(warehouseId, new WarehouseListEventsResponseDTO(warehouseId, new ArrayList<>()));
            }
            WarehouseListEventsResponseDTO dto = resultDto.get(warehouseId);


            dto.getRegisteredEvents().add(EventMapper.INSTANCE.toDto(event.getEvent()));
        });

        var test = resultDto.values();

        return ResponseEntity.status(status).body(new ArrayList<>(resultDto.values()));
    }
}
