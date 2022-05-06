package br.com.mercadolivre.projetointegrador.unit.service;

import br.com.mercadolivre.projetointegrador.jobs.model.Job;
import br.com.mercadolivre.projetointegrador.jobs.model.WarehouseJob;
import br.com.mercadolivre.projetointegrador.jobs.service.JobExecutorsService;
import br.com.mercadolivre.projetointegrador.test_utils.WarehouseTestUtils;
import br.com.mercadolivre.projetointegrador.warehouse.dto.response.BatchResponseDTO;
import br.com.mercadolivre.projetointegrador.warehouse.enums.CategoryEnum;
import br.com.mercadolivre.projetointegrador.warehouse.model.Batch;
import br.com.mercadolivre.projetointegrador.warehouse.model.Product;
import br.com.mercadolivre.projetointegrador.warehouse.model.Warehouse;
import br.com.mercadolivre.projetointegrador.warehouse.repository.BatchRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class JobExecutorsServiceTests {

    @Mock
    private BatchRepository batchRepository;

    @InjectMocks
    private JobExecutorsService executorsService;

    @Test
    public void shouldRemoveAllBatchesWithLessThan3WeekOnDueDate(){
        Product mockedProduct = new Product(1L, "mocked", CategoryEnum.FS, new Date());
        Batch mockedBatch = WarehouseTestUtils.getBatch1();
        mockedBatch.setDueDate(LocalDate.now());


        WarehouseJob mockEvent = new WarehouseJob(
                1L,
                new Warehouse(),
                new Job(
                        null,
                        "removeBatches",
                        "removalEventExecutor"
                ),
                new ArrayList<>(List.of(mockedProduct)),
                null
        );

        Mockito.when(batchRepository.findAllBySectionWarehouseAndProductIn(Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>(List.of(mockedBatch)));


        List<BatchResponseDTO> result = executorsService.batchRemovalExecutor(mockEvent);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(mockedBatch.getId(), result.get(0).getId());
        Mockito.verify(batchRepository, Mockito.times(1)).deleteAllById(Mockito.any());
    }
}
