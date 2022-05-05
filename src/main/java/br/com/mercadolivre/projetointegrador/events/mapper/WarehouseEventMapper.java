package br.com.mercadolivre.projetointegrador.events.mapper;

import br.com.mercadolivre.projetointegrador.events.dto.response.WarehouseEventDTO;
import br.com.mercadolivre.projetointegrador.events.model.WarehouseEvent;
import br.com.mercadolivre.projetointegrador.warehouse.mapper.BatchMapper;
import br.com.mercadolivre.projetointegrador.warehouse.mapper.ProductMapper;
import br.com.mercadolivre.projetointegrador.warehouse.mapper.WarehouseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {WarehouseMapper.class, EventMapper.class, ProductMapper.class})
public interface WarehouseEventMapper {
    WarehouseEventMapper INSTANCE = Mappers.getMapper(WarehouseEventMapper.class);

    WarehouseEventDTO toDto(WarehouseEvent model);

    List<WarehouseEventDTO> toDto(List<WarehouseEvent> list);
}
