package br.com.mercadolivre.projetointegrador.jobs.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NewWarehouseJobDTO {

    public Long idEvent;
    public Long idWarehouse;
    private final List<Long> productList;

}
