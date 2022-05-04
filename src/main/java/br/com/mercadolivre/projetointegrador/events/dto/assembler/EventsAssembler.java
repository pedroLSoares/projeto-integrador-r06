package br.com.mercadolivre.projetointegrador.events.dto.assembler;

import br.com.mercadolivre.projetointegrador.events.dto.response.WarehouseEventCreatedDTO;
import br.com.mercadolivre.projetointegrador.events.model.WarehouseEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class EventsAssembler {

    public ResponseEntity<WarehouseEventCreatedDTO> toEventCreatedResponse(WarehouseEvent event, HttpStatus status){


        WarehouseEventCreatedDTO dto = WarehouseEventCreatedDTO.builder()
                .idEvent(event.getId())
                .idWarehouse(event.getWarehouse().getId())
                .build();


        return ResponseEntity.ok(dto);
    }
}
