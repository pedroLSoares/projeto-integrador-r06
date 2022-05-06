package br.com.mercadolivre.projetointegrador.warehouse.dto.response;

import br.com.mercadolivre.projetointegrador.jobs.view.WarehouseEventView;
import br.com.mercadolivre.projetointegrador.warehouse.model.Location;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class WarehouseResponseDTO {

  @JsonView({WarehouseEventView.WarehouseEventListResponse.class, WarehouseEventView.WarehouseEventDetailsResponse.class})
  private Long id;

  @JsonView({WarehouseEventView.WarehouseEventListResponse.class, WarehouseEventView.WarehouseEventDetailsResponse.class})
  private String name;

  private Location location;

  private List<Map<String, String>> links;
}
