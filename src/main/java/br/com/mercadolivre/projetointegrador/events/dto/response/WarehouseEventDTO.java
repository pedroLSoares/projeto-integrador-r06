package br.com.mercadolivre.projetointegrador.events.dto.response;

import br.com.mercadolivre.projetointegrador.events.model.Event;
import br.com.mercadolivre.projetointegrador.events.view.WarehouseEventView;
import br.com.mercadolivre.projetointegrador.warehouse.dto.response.ProductDTO;
import br.com.mercadolivre.projetointegrador.warehouse.dto.response.WarehouseResponseDTO;
import br.com.mercadolivre.projetointegrador.warehouse.model.Warehouse;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@JsonView({WarehouseEventView.WarehouseEventDetailsResponse.class})
public class WarehouseEventDTO {

    @JsonView({WarehouseEventView.WarehouseEventListResponse.class})
    private Long id;

    private WarehouseResponseDTO warehouse;

    @JsonView({WarehouseEventView.WarehouseEventListResponse.class})
    private EventResponseDTO event;

    @JsonView({WarehouseEventView.WarehouseEventDetailsResponse.class})
    private List<ProductDTO> products;

    @JsonView({WarehouseEventView.WarehouseEventListResponse.class})
    private LocalDateTime lastExecution;
}
