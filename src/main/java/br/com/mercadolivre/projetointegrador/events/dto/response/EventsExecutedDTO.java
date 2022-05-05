package br.com.mercadolivre.projetointegrador.events.dto.response;

import br.com.mercadolivre.projetointegrador.events.view.WarehouseEventView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@JsonView(WarehouseEventView.WarehouseEventResponse.class)
public class EventsExecutedDTO {

    private String event;
    private Object results;
}
