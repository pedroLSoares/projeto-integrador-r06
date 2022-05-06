package br.com.mercadolivre.projetointegrador.jobs.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class WarehouseJobCreatedDTO {

    private Long idJob;
    private Long idWarehouse;
    private String executionUri;
}
