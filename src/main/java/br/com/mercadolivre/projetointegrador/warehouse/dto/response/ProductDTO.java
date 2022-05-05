package br.com.mercadolivre.projetointegrador.warehouse.dto.response;

import br.com.mercadolivre.projetointegrador.events.view.WarehouseEventView;
import br.com.mercadolivre.projetointegrador.warehouse.enums.CategoryEnum;
import br.com.mercadolivre.projetointegrador.warehouse.view.ProductView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@JsonView({WarehouseEventView.WarehouseEventResponse.class})
public class ProductDTO {

  @JsonView({ProductView.Detail.class, WarehouseEventView.WarehouseEventDetailsResponse.class})
  private Long id;

  @JsonView({ProductView.Detail.class, ProductView.List.class, WarehouseEventView.WarehouseEventDetailsResponse.class})
  private String name;

  @JsonView({ProductView.Detail.class, ProductView.List.class, WarehouseEventView.WarehouseEventDetailsResponse.class})
  private CategoryEnum category;

  @JsonView(ProductView.Detail.class)
  private Date created_at;

  @JsonView({ProductView.Detail.class, ProductView.List.class})
  private List<Map<String, String>> links;
}
