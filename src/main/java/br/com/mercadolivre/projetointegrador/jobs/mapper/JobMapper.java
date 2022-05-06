package br.com.mercadolivre.projetointegrador.jobs.mapper;

import br.com.mercadolivre.projetointegrador.jobs.dto.request.NewJobDTO;
import br.com.mercadolivre.projetointegrador.jobs.dto.response.JobResponseDTO;
import br.com.mercadolivre.projetointegrador.jobs.model.Job;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface JobMapper {

    JobMapper INSTANCE = Mappers.getMapper(JobMapper.class);

    JobResponseDTO toDto(Job job);

    List<JobResponseDTO> toDto(List<Job> jobs);

    Job toModel(NewJobDTO dto);
}
