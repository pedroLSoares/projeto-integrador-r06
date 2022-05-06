package br.com.mercadolivre.projetointegrador.jobs.mapper;

import br.com.mercadolivre.projetointegrador.jobs.dto.response.JobResponseDTO;
import br.com.mercadolivre.projetointegrador.jobs.model.Job;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JobMapper {

    JobMapper INSTANCE = Mappers.getMapper(JobMapper.class);

    JobResponseDTO toDto(Job job);
}
