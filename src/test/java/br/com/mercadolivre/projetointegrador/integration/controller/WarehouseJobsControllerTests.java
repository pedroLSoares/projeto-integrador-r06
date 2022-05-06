package br.com.mercadolivre.projetointegrador.integration.controller;

import br.com.mercadolivre.projetointegrador.events.dto.request.UpdateJobProductsDTO;
import br.com.mercadolivre.projetointegrador.events.dto.request.NewWarehouseJobDTO;
import br.com.mercadolivre.projetointegrador.events.model.Job;
import br.com.mercadolivre.projetointegrador.events.model.WarehouseJob;
import br.com.mercadolivre.projetointegrador.events.repository.WarehouseJobRepository;
import br.com.mercadolivre.projetointegrador.test_utils.IntegrationTestUtils;
import br.com.mercadolivre.projetointegrador.test_utils.WithMockManagerUser;
import br.com.mercadolivre.projetointegrador.warehouse.model.Batch;
import br.com.mercadolivre.projetointegrador.warehouse.model.Product;
import br.com.mercadolivre.projetointegrador.warehouse.model.Section;
import br.com.mercadolivre.projetointegrador.warehouse.model.Warehouse;
import br.com.mercadolivre.projetointegrador.warehouse.repository.BatchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(profiles = "test")
@WithMockManagerUser
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class WarehouseJobsControllerTests {

    private final String CONTROLLER_URL = "/api/v1/warehouse";

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired private MockMvc mockMvc;
    @Autowired private IntegrationTestUtils integrationTestUtils;
    @Autowired private BatchRepository batchRepository;
    @Autowired private WarehouseJobRepository warehouseJobRepository;

    @Test
    public void shouldRegisterNewEventIntoWarehouse() throws Exception {
        Job job = integrationTestUtils.createJob();
        Warehouse warehouse = integrationTestUtils.createWarehouse();
        NewWarehouseJobDTO payloadDTO = new NewWarehouseJobDTO(job.getId(), warehouse.getId(), new ArrayList<>());

        mockMvc
                .perform(MockMvcRequestBuilders.post(CONTROLLER_URL + "/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payloadDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

    }

    @Test
    public void shouldNotRegisterAnyDataWhenNotFindEvent() throws Exception {

        Warehouse warehouse = integrationTestUtils.createWarehouse();
        NewWarehouseJobDTO payloadDTO = new NewWarehouseJobDTO(99999L, warehouse.getId(), new ArrayList<>());

        mockMvc
                .perform(MockMvcRequestBuilders.post(CONTROLLER_URL + "/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payloadDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Não encontrado"));

    }

    @Test
    public void shouldFindAllRegisteredEventsByWarehouse() throws Exception {
        integrationTestUtils.createWarehouseEvent();

        mockMvc
                .perform(MockMvcRequestBuilders.get(CONTROLLER_URL + "/jobs"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].registeredJobs").isNotEmpty());

    }

    @Test
    public void shouldListAllEventsFromWarehouse() throws Exception {
        WarehouseJob warehouseJob = integrationTestUtils.createWarehouseEvent();

        mockMvc
                .perform(MockMvcRequestBuilders.get(CONTROLLER_URL + "/" + warehouseJob.getWarehouse().getId() +"/jobs"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty());
    }


    @Test
    public void shouldRemoveAllBatchesThatHasDueDateLassThan3Weeks() throws Exception {
        warehouseJobRepository.deleteAll();
        WarehouseJob warehouseJob = integrationTestUtils.createWarehouseEvent();
        Section section = integrationTestUtils.createSection(warehouseJob.getWarehouse());

        Batch batch = integrationTestUtils.createBatch(section, LocalDate.now(), warehouseJob.getProducts().get(0));

        mockMvc
                .perform(MockMvcRequestBuilders.post(CONTROLLER_URL + "/jobs/execute"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty());

        Optional<Batch> removed = batchRepository.findById(batch.getId());

        Assertions.assertTrue(removed.isEmpty());

    }

    @Test
    public void shouldReturnWarehouseEventDetails() throws Exception {
        WarehouseJob warehouseJob = integrationTestUtils.createWarehouseEvent();

        mockMvc
                .perform(MockMvcRequestBuilders.get(CONTROLLER_URL + "/jobs/detail/" + warehouseJob.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.job.name").value(warehouseJob.getJob().getName()));
    }

    @Test
    public void shouldReturnNotFoundWhenEventNotExists() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(CONTROLLER_URL + "/jobs/detail/" + 999))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Não encontrado"));
    }

    @Test
    public void shouldAddProductIntoWarehouseEvent() throws Exception {
        WarehouseJob warehouseJob = integrationTestUtils.createWarehouseEvent();
        Product product = integrationTestUtils.createProduct();

        UpdateJobProductsDTO body = new UpdateJobProductsDTO(warehouseJob.getId(), List.of(product.getId()));

        mockMvc
                .perform(MockMvcRequestBuilders.patch(CONTROLLER_URL + "/jobs/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        WarehouseJob event = warehouseJobRepository.findById(warehouseJob.getId()).orElse(new WarehouseJob());

        Assertions.assertTrue(event.getProducts().contains(product));
    }

    @Test
    public void shouldNotAddProductIntoWarehouseEventWhenReceiveInvalidIds() throws Exception {
        WarehouseJob warehouseJob = integrationTestUtils.createWarehouseEvent();

        UpdateJobProductsDTO body = new UpdateJobProductsDTO(warehouseJob.getId(), List.of(99999L));

        mockMvc
                .perform(MockMvcRequestBuilders.patch(CONTROLLER_URL + "/jobs/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Não encontrado"));
    }

    @Test
    public void shouldRemoveWarehouseEvent() throws Exception {
        WarehouseJob warehouseJob = integrationTestUtils.createWarehouseEvent();

        mockMvc.perform(MockMvcRequestBuilders.delete(CONTROLLER_URL + "/jobs/{id}", warehouseJob.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Optional<WarehouseJob> event = warehouseJobRepository.findById(warehouseJob.getId());

        Assertions.assertTrue(event.isEmpty());
    }

    @Test
    public void shouldRemoveWarehouseEventProducts() throws Exception {
        WarehouseJob warehouseJob = integrationTestUtils.createWarehouseEvent();
        Product toRemove = warehouseJob.getProducts().get(0);

        UpdateJobProductsDTO body = new UpdateJobProductsDTO(warehouseJob.getId(), List.of(toRemove.getId()));

        mockMvc
                .perform(MockMvcRequestBuilders.delete(CONTROLLER_URL + "/jobs/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        WarehouseJob event = warehouseJobRepository.findById(warehouseJob.getId()).orElse(new WarehouseJob());

        Assertions.assertFalse(event.getProducts().contains(toRemove));
    }
}
