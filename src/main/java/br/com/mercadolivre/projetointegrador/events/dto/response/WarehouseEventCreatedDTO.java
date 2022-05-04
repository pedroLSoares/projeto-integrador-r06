package br.com.mercadolivre.projetointegrador.events.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class WarehouseEventCreatedDTO {

    private Long idEvent;
    private Long idWarehouse;
    private String executionUri;
}
