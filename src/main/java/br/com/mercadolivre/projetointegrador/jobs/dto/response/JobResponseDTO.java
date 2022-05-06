package br.com.mercadolivre.projetointegrador.jobs.dto.response;

import br.com.mercadolivre.projetointegrador.jobs.view.WarehouseEventView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@JsonView({WarehouseEventView.WarehouseEventListResponse.class, WarehouseEventView.WarehouseEventDetailsResponse.class})
public class JobResponseDTO {

    private final Long id;
    private final String name;
}
