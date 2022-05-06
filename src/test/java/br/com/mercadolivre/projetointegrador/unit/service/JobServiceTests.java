package br.com.mercadolivre.projetointegrador.unit.service;

import br.com.mercadolivre.projetointegrador.jobs.exceptions.JobExecutorException;
import br.com.mercadolivre.projetointegrador.jobs.model.Job;
import br.com.mercadolivre.projetointegrador.jobs.repository.JobRepository;
import br.com.mercadolivre.projetointegrador.jobs.service.JobService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JobServiceTests {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobService jobService;

    @Test
    public void shouldCreateJob(){
        Job mocked = new Job(null, "mock", "batchRemovalExecutor");
        Mockito.when(jobRepository.save(mocked)).thenReturn(mocked);

        Job result = jobService.createJob(mocked);

        Mockito.verify(jobRepository, Mockito.times(1)).save(mocked);
        Assertions.assertEquals(result, mocked);
    }

    @Test
    public void shouldNotCreateJobWhenReceiveInvalidExecutor(){
        Job mocked = new Job(null, "mock", "nonExistentExecutor");

        Assertions.assertThrows(JobExecutorException.class, () -> jobService.createJob(mocked));
    }
}
