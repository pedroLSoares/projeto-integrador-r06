package br.com.mercadolivre.projetointegrador.events.mapper;

import br.com.mercadolivre.projetointegrador.events.dto.response.JobResponseDTO;
import br.com.mercadolivre.projetointegrador.events.model.Job;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JobMapper {

    JobMapper INSTANCE = Mappers.getMapper(JobMapper.class);

    JobResponseDTO toDto(Job job);
}
