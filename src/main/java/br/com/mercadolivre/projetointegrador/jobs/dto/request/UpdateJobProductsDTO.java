package br.com.mercadolivre.projetointegrador.jobs.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class UpdateJobProductsDTO {

    private Long warehouseEventId;
    private List<Long> productList;
}
