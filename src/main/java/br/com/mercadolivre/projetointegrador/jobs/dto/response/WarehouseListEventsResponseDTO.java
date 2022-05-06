package br.com.mercadolivre.projetointegrador.jobs.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
public class WarehouseListEventsResponseDTO {

    private final Long warehouseId;
    private final List<JobResponseDTO> registeredJobs;
}
