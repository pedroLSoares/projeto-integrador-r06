package br.com.mercadolivre.projetointegrador.events.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class WarehouseListEventsResponseDTO {

    private Long warehouseId;
    private List<JobResponseDTO> registeredJobs;
}
