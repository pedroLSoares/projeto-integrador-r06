package br.com.mercadolivre.projetointegrador.jobs.service;

import br.com.mercadolivre.projetointegrador.jobs.exceptions.ExecutorNotImplemented;
import br.com.mercadolivre.projetointegrador.jobs.model.Job;
import br.com.mercadolivre.projetointegrador.jobs.model.WarehouseJob;
import br.com.mercadolivre.projetointegrador.jobs.repository.JobRepository;
import br.com.mercadolivre.projetointegrador.warehouse.exception.db.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public Job find(Long id){
        return jobRepository.findById(id).orElseThrow(() -> new NotFoundException("Event " + id + " not found"));
    }

    public List<Job> findAll(){
        return jobRepository.findAll();
    }

    public Job createJob(Job job){
        try {
            JobExecutorsService.class.getMethod(job.getExecutor(), WarehouseJob.class);
            return jobRepository.save(job);
        } catch (NoSuchMethodException e) {
            throw new ExecutorNotImplemented("Método executor não implementado");
        }
    }
}
