package br.com.mercadolivre.projetointegrador.events.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class NewWarehouseEventDTO {

    public Long idEvent;
    public Long idWarehouse;
    private List<Long> productList;

}
