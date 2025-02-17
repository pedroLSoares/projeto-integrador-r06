package br.com.mercadolivre.projetointegrador.jobs.mapper;

import br.com.mercadolivre.projetointegrador.jobs.dto.response.WarehouseJobDTO;
import br.com.mercadolivre.projetointegrador.jobs.model.WarehouseJob;
import br.com.mercadolivre.projetointegrador.warehouse.mapper.ProductMapper;
import br.com.mercadolivre.projetointegrador.warehouse.mapper.WarehouseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {WarehouseMapper.class, JobMapper.class, ProductMapper.class})
public interface WarehouseJobMapper {
    WarehouseJobMapper INSTANCE = Mappers.getMapper(WarehouseJobMapper.class);

    WarehouseJobDTO toDto(WarehouseJob model);

    List<WarehouseJobDTO> toDto(List<WarehouseJob> list);
}
