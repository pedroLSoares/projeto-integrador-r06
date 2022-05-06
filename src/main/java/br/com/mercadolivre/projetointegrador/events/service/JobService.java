package br.com.mercadolivre.projetointegrador.events.service;

import br.com.mercadolivre.projetointegrador.events.model.Job;
import br.com.mercadolivre.projetointegrador.events.repository.JobRepository;
import br.com.mercadolivre.projetointegrador.warehouse.exception.db.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public Job find(Long id){
        return jobRepository.findById(id).orElseThrow(() -> new NotFoundException("Event " + id + " not found"));
    }
}
