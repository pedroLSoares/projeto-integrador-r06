package br.com.mercadolivre.projetointegrador.jobs.dto.response;

import br.com.mercadolivre.projetointegrador.jobs.view.WarehouseEventView;
import br.com.mercadolivre.projetointegrador.warehouse.dto.response.ProductDTO;
import br.com.mercadolivre.projetointegrador.warehouse.dto.response.WarehouseResponseDTO;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@JsonView({WarehouseEventView.WarehouseEventDetailsResponse.class})
public class WarehouseJobDTO {

    @JsonView({WarehouseEventView.WarehouseEventListResponse.class})
    private final Long id;

    private final WarehouseResponseDTO warehouse;

    @JsonView({WarehouseEventView.WarehouseEventListResponse.class, WarehouseEventView.WarehouseEventDetailsResponse.class})
    private final JobResponseDTO job;

    @JsonView({WarehouseEventView.WarehouseEventDetailsResponse.class})
    private final List<ProductDTO> products;

    @JsonView({WarehouseEventView.WarehouseEventListResponse.class})
    private final LocalDateTime lastExecution;
}
