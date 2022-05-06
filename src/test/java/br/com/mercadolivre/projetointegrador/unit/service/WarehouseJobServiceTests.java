package br.com.mercadolivre.projetointegrador.unit.service;

import br.com.mercadolivre.projetointegrador.events.dto.request.NewWarehouseJobDTO;
import br.com.mercadolivre.projetointegrador.events.dto.response.ExecutionResponseDTO;
import br.com.mercadolivre.projetointegrador.events.model.Job;
import br.com.mercadolivre.projetointegrador.events.model.WarehouseJob;
import br.com.mercadolivre.projetointegrador.events.repository.WarehouseJobRepository;
import br.com.mercadolivre.projetointegrador.events.service.JobExecutorsService;
import br.com.mercadolivre.projetointegrador.events.service.JobService;
import br.com.mercadolivre.projetointegrador.events.service.WarehouseJobService;
import br.com.mercadolivre.projetointegrador.warehouse.dto.response.BatchResponseDTO;
import br.com.mercadolivre.projetointegrador.warehouse.enums.CategoryEnum;
import br.com.mercadolivre.projetointegrador.warehouse.exception.db.NotFoundException;
import br.com.mercadolivre.projetointegrador.warehouse.model.Product;
import br.com.mercadolivre.projetointegrador.warehouse.model.Warehouse;
import br.com.mercadolivre.projetointegrador.warehouse.repository.ProductRepository;
import br.com.mercadolivre.projetointegrador.warehouse.service.WarehouseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class WarehouseJobServiceTests {

    @Mock
    private WarehouseService warehouseService;

    @Mock
    private JobService jobService;

    @Mock
    private WarehouseJobRepository warehouseJobRepository;

    @Mock
    private JobExecutorsService executorsService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private WarehouseJobService warehouseJobService;

    @Test
    public void shouldRegisterNewEventInWarehouse(){
        NewWarehouseJobDTO dto = new NewWarehouseJobDTO(1L, 1L, new ArrayList<>());

        Mockito.when(warehouseService.findWarehouse(Mockito.anyLong())).thenReturn(new Warehouse());
        Mockito.when(jobService.find(Mockito.anyLong())).thenReturn(new Job());
        Mockito.when(productRepository.findAllById(Mockito.any())).thenReturn(Collections.emptyList());
        Mockito.when(warehouseJobRepository.save(Mockito.any())).thenReturn(new WarehouseJob());

        WarehouseJob warehouseJob = warehouseJobService.registerJobToWarehouse(dto);

        Assertions.assertNotNull(warehouseJob);
        Mockito.verify(warehouseJobRepository, Mockito.times(1)).save(Mockito.any(WarehouseJob.class));

    }

    @Test
    public void shouldAddNewProductIntoWarehouseEvent(){
        WarehouseJob expected = new WarehouseJob(1L, new Warehouse(), new Job(), new ArrayList<>(), null);
        Mockito.when(warehouseJobRepository.findById(Mockito.any())).thenReturn(Optional.of(expected));
        Mockito.when(productRepository.findAllById(Mockito.any())).thenReturn(List.of(new Product()));
        Mockito.when(warehouseJobRepository.save(Mockito.any())).thenReturn(expected);

        WarehouseJob warehouseJob = warehouseJobService.addProductsIntoJob(1L, List.of(1L));

        Assertions.assertNotNull(warehouseJob);
        Assertions.assertTrue(warehouseJob.getProducts().size() > 0);
        Mockito.verify(warehouseJobRepository, Mockito.times(1)).save(Mockito.any(WarehouseJob.class));
    }

    @Test
    public void shouldNotAddNewProductIntoWarehouseEventWhenNotFoundEvent(){
        Mockito.when(warehouseJobRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> warehouseJobService.addProductsIntoJob(1L, List.of(1L)));
    }

    @Test
    public void shouldNotAddNewProductIntoWarehouseEventWhenNotFoundProduct(){
        WarehouseJob expected = new WarehouseJob(1L, new Warehouse(), new Job(), new ArrayList<>(), null);
        Mockito.when(warehouseJobRepository.findById(Mockito.any())).thenReturn(Optional.of(expected));
        Mockito.when(productRepository.findAllById(Mockito.any())).thenReturn(Collections.emptyList());

        Assertions.assertThrows(NotFoundException.class, () -> warehouseJobService.addProductsIntoJob(1L, List.of(1L)));

    }

    @Test
    public void shouldRemoveProductFromWarehouseEvent(){
        Product mockedProduct = new Product(1L, "mocked", CategoryEnum.FS, new Date());

        WarehouseJob expected = new WarehouseJob(1L, new Warehouse(), new Job(), new ArrayList<>(List.of(mockedProduct)), null);

        Mockito.when(warehouseJobRepository.findById(Mockito.any())).thenReturn(Optional.of(expected));
        Mockito.when(warehouseJobRepository.save(Mockito.any())).thenReturn(expected);

        WarehouseJob warehouseJob = warehouseJobService.removeProductFromJob(1L, List.of(1L));

        Assertions.assertEquals(warehouseJob.getProducts().size(), 0);
        Mockito.verify(warehouseJobRepository, Mockito.times(1)).save(expected);
    }

    @Test
    public void shouldNotRemoveProductFromWarehouseEventWhenNotFindEvent(){
        Mockito.when(warehouseJobRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> warehouseJobService.removeProductFromJob(1L, List.of(1L)));

    }

    @Test
    public void shouldCallAllRegisteredEvents(){

        Job mockedJob = new Job(
                null,
                "removeBatches",
                "batchRemovalExecutor",
                3
        );
        Product mockedProduct = new Product(1L, "mocked", CategoryEnum.FS, new Date());
        WarehouseJob expected = new WarehouseJob(1L, new Warehouse(), mockedJob, new ArrayList<>(List.of(mockedProduct)), null);

        Mockito.when(warehouseJobRepository.findAll()).thenReturn(List.of(expected));
        Mockito.when(executorsService.batchRemovalExecutor(Mockito.any())).thenReturn(List.of(new BatchResponseDTO()));
        Mockito.when(warehouseJobRepository.save(Mockito.any())).thenReturn(expected);

        List<ExecutionResponseDTO> result = warehouseJobService.executeJobs();

        Assertions.assertTrue(result.get(0).getJobsExecuted().get(0).getResults().size() > 0);
        Mockito.verify(warehouseJobRepository, Mockito.times(1)).save(expected);
        Mockito.verify(executorsService, Mockito.times(1)).batchRemovalExecutor(Mockito.any());
    }

}
