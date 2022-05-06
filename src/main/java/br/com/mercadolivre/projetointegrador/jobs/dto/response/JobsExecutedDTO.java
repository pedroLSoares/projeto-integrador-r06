package br.com.mercadolivre.projetointegrador.jobs.dto.response;

import br.com.mercadolivre.projetointegrador.jobs.view.WarehouseEventView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@JsonView(WarehouseEventView.WarehouseEventResponse.class)
public class JobsExecutedDTO {

    private String job;
    private List<Object> results;
}
