package br.com.mercadolivre.projetointegrador.events.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class UpdateEventProductsDTO {

    private Long warehouseEventId;
    private List<Long> productList;
}
