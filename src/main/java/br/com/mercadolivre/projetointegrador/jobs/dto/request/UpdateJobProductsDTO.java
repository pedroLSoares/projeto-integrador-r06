package br.com.mercadolivre.projetointegrador.jobs.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
public class UpdateJobProductsDTO {

    private final Long warehouseEventId;
    private final List<Long> productList;
}
