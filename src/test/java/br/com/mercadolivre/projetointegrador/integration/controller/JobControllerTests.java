package br.com.mercadolivre.projetointegrador.integration.controller;

import br.com.mercadolivre.projetointegrador.jobs.dto.request.NewJobDTO;
import br.com.mercadolivre.projetointegrador.jobs.model.Job;
import br.com.mercadolivre.projetointegrador.jobs.repository.JobRepository;
import br.com.mercadolivre.projetointegrador.test_utils.IntegrationTestUtils;
import br.com.mercadolivre.projetointegrador.test_utils.WithMockManagerUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(profiles = "test")
@WithMockManagerUser
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class JobControllerTests {

    private final String CONTROLLER_URL = "/api/v1/jobs";

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IntegrationTestUtils integrationTestUtils;

    @Autowired
    private JobRepository jobRepository;

    @Test
    public void shouldReturnConflictWhenExecutorJobAlreadyExists() throws Exception{
        Job job = integrationTestUtils.createJob();
        NewJobDTO newJobDTO = new NewJobDTO("removeBatches", job.getExecutor());

        mockMvc
                .perform(MockMvcRequestBuilders.post(CONTROLLER_URL )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newJobDTO)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void shouldCreateNewJob() throws Exception{
        jobRepository.deleteAll();
        NewJobDTO newJobDTO = new NewJobDTO("removeBatches", "batchRemovalExecutor");

        mockMvc
                .perform(MockMvcRequestBuilders.post(CONTROLLER_URL )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newJobDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void shouldReturnErrorWhenReceiveInvalidExecutor() throws Exception{
        NewJobDTO newJobDTO = new NewJobDTO("removeBatches", "nonExistentExecutor");

        mockMvc
                .perform(MockMvcRequestBuilders.post(CONTROLLER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newJobDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").isNotEmpty());
    }

    @Test
    public void shouldListAllJobs() throws Exception{
        integrationTestUtils.createJob();

        mockMvc
                .perform(MockMvcRequestBuilders.get(CONTROLLER_URL )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty());
    }
}
