package br.com.mercadolivre.projetointegrador.events.dto.response;

import br.com.mercadolivre.projetointegrador.events.view.WarehouseEventView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
@JsonView({WarehouseEventView.WarehouseEventListResponse.class, WarehouseEventView.WarehouseEventDetailsResponse.class})
public class EventResponseDTO {

    private Long id;
    private String name;
}
