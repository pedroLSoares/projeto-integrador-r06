package br.com.mercadolivre.projetointegrador.events.mapper;

import br.com.mercadolivre.projetointegrador.events.dto.response.EventResponseDTO;
import br.com.mercadolivre.projetointegrador.events.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EventMapper {

    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    EventResponseDTO toDto(Event event);
}
