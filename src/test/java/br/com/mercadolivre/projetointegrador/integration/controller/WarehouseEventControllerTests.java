package br.com.mercadolivre.projetointegrador.integration.controller;

import br.com.mercadolivre.projetointegrador.events.dto.request.UpdateEventProductsDTO;
import br.com.mercadolivre.projetointegrador.events.dto.request.NewWarehouseEventDTO;
import br.com.mercadolivre.projetointegrador.events.model.Event;
import br.com.mercadolivre.projetointegrador.events.model.WarehouseEvent;
import br.com.mercadolivre.projetointegrador.events.repository.WarehouseEventRepository;
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
public class WarehouseEventControllerTests {

    private final String CONTROLLER_URL = "/api/v1/warehouse";

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired private MockMvc mockMvc;
    @Autowired private IntegrationTestUtils integrationTestUtils;
    @Autowired private BatchRepository batchRepository;
    @Autowired private WarehouseEventRepository warehouseEventRepository;

    @Test
    public void shouldRegisterNewEventIntoWarehouse() throws Exception {
        Event event = integrationTestUtils.createEvent();
        Warehouse warehouse = integrationTestUtils.createWarehouse();
        NewWarehouseEventDTO payloadDTO = new NewWarehouseEventDTO(event.getId(), warehouse.getId(), new ArrayList<>());

        mockMvc
                .perform(MockMvcRequestBuilders.post(CONTROLLER_URL + "/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payloadDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

    }

    @Test
    public void shouldNotRegisterAnyDataWhenNotFindEvent() throws Exception {

        Warehouse warehouse = integrationTestUtils.createWarehouse();
        NewWarehouseEventDTO payloadDTO = new NewWarehouseEventDTO(99999L, warehouse.getId(), new ArrayList<>());

        mockMvc
                .perform(MockMvcRequestBuilders.post(CONTROLLER_URL + "/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payloadDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Não encontrado"));

    }

    @Test
    public void shouldFindAllRegisteredEventsByWarehouse() throws Exception {
        integrationTestUtils.createWarehouseEvent();

        mockMvc
                .perform(MockMvcRequestBuilders.get(CONTROLLER_URL + "/events"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].registeredEvents").isNotEmpty());

    }

    @Test
    public void shouldListAllEventsFromWarehouse() throws Exception {
        WarehouseEvent warehouseEvent = integrationTestUtils.createWarehouseEvent();

        mockMvc
                .perform(MockMvcRequestBuilders.get(CONTROLLER_URL + "/" + warehouseEvent.getWarehouse().getId() +"/events"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty());
    }


    @Test
    public void shouldRemoveAllBatchesThatHasDueDateLassThan3Weeks() throws Exception {
        warehouseEventRepository.deleteAll();
        WarehouseEvent warehouseEvent = integrationTestUtils.createWarehouseEvent();
        Section section = integrationTestUtils.createSection(warehouseEvent.getWarehouse());

        Batch batch = integrationTestUtils.createBatch(section, LocalDate.now(), warehouseEvent.getProducts().get(0));

        mockMvc
                .perform(MockMvcRequestBuilders.post(CONTROLLER_URL + "/events/execute"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty());

        Optional<Batch> removed = batchRepository.findById(batch.getId());

        Assertions.assertTrue(removed.isEmpty());

    }

    @Test
    public void shouldReturnWarehouseEventDetails() throws Exception {
        WarehouseEvent warehouseEvent = integrationTestUtils.createWarehouseEvent();

        mockMvc
                .perform(MockMvcRequestBuilders.get(CONTROLLER_URL + "/events/detail/" + warehouseEvent.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.event.name").value(warehouseEvent.getEvent().getName()));
    }

    @Test
    public void shouldReturnNotFoundWhenEventNotExists() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get(CONTROLLER_URL + "/events/detail/" + 999))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Não encontrado"));
    }

    @Test
    public void shouldAddProductIntoWarehouseEvent() throws Exception {
        WarehouseEvent warehouseEvent = integrationTestUtils.createWarehouseEvent();
        Product product = integrationTestUtils.createProduct();

        UpdateEventProductsDTO body = new UpdateEventProductsDTO(warehouseEvent.getId(), List.of(product.getId()));

        mockMvc
                .perform(MockMvcRequestBuilders.patch(CONTROLLER_URL + "/events/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        WarehouseEvent event = warehouseEventRepository.findById(warehouseEvent.getId()).orElse(new WarehouseEvent());

        Assertions.assertTrue(event.getProducts().contains(product));
    }

    @Test
    public void shouldNotAddProductIntoWarehouseEventWhenReceiveInvalidIds() throws Exception {
        WarehouseEvent warehouseEvent = integrationTestUtils.createWarehouseEvent();

        UpdateEventProductsDTO body = new UpdateEventProductsDTO(warehouseEvent.getId(), List.of(99999L));

        mockMvc
                .perform(MockMvcRequestBuilders.patch(CONTROLLER_URL + "/events/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Não encontrado"));
    }

    @Test
    public void shouldRemoveWarehouseEvent() throws Exception {
        WarehouseEvent warehouseEvent = integrationTestUtils.createWarehouseEvent();

        mockMvc.perform(MockMvcRequestBuilders.delete(CONTROLLER_URL + "/events/{id}", warehouseEvent.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Optional<WarehouseEvent> event = warehouseEventRepository.findById(warehouseEvent.getId());

        Assertions.assertTrue(event.isEmpty());
    }

    @Test
    public void shouldRemoveWarehouseEventProducts() throws Exception {
        WarehouseEvent warehouseEvent = integrationTestUtils.createWarehouseEvent();
        Product toRemove = warehouseEvent.getProducts().get(0);

        UpdateEventProductsDTO body = new UpdateEventProductsDTO(warehouseEvent.getId(), List.of(toRemove.getId()));

        mockMvc
                .perform(MockMvcRequestBuilders.delete(CONTROLLER_URL + "/events/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        WarehouseEvent event = warehouseEventRepository.findById(warehouseEvent.getId()).orElse(new WarehouseEvent());

        Assertions.assertFalse(event.getProducts().contains(toRemove));
    }
}
