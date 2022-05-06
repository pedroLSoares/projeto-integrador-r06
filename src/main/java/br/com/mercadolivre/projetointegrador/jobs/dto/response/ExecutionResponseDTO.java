package br.com.mercadolivre.projetointegrador.jobs.dto.response;

import br.com.mercadolivre.projetointegrador.jobs.view.WarehouseEventView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonView(WarehouseEventView.WarehouseEventResponse.class)
public class ExecutionResponseDTO {

    private Long warehouseId;
    private List<JobsExecutedDTO> jobsExecuted;
}
