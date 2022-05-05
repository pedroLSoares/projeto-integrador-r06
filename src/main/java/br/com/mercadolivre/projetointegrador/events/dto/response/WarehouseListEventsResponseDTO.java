package br.com.mercadolivre.projetointegrador.events.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WarehouseListEventsResponseDTO {

    private Long warehouseId;
    private List<EventResponseDTO> registeredEvents;
}
