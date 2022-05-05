package br.com.mercadolivre.projetointegrador.unit.service;

import br.com.mercadolivre.projetointegrador.events.dto.request.NewWarehouseEventDTO;
import br.com.mercadolivre.projetointegrador.events.dto.response.EventsExecutedDTO;
import br.com.mercadolivre.projetointegrador.events.model.Event;
import br.com.mercadolivre.projetointegrador.events.model.WarehouseEvent;
import br.com.mercadolivre.projetointegrador.events.repository.WarehouseEventRepository;
import br.com.mercadolivre.projetointegrador.events.service.EventExecutorsService;
import br.com.mercadolivre.projetointegrador.events.service.EventService;
import br.com.mercadolivre.projetointegrador.events.service.WarehouseEventService;
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
public class WarehouseEventServiceTests {

    @Mock
    private WarehouseService warehouseService;

    @Mock
    private EventService eventService;

    @Mock
    private WarehouseEventRepository warehouseEventRepository;

    @Mock
    private EventExecutorsService executorsService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private WarehouseEventService warehouseEventService;

    @Test
    public void shouldRegisterNewEventInWarehouse(){
        NewWarehouseEventDTO dto = new NewWarehouseEventDTO(1L, 1L, new ArrayList<>());

        Mockito.when(warehouseService.findWarehouse(Mockito.anyLong())).thenReturn(new Warehouse());
        Mockito.when(eventService.find(Mockito.anyLong())).thenReturn(new Event());
        Mockito.when(productRepository.findAllById(Mockito.any())).thenReturn(Collections.emptyList());
        Mockito.when(warehouseEventRepository.save(Mockito.any())).thenReturn(new WarehouseEvent());

        WarehouseEvent warehouseEvent = warehouseEventService.registerEventToWarehouse(dto);

        Assertions.assertNotNull(warehouseEvent);
        Mockito.verify(warehouseEventRepository, Mockito.times(1)).save(Mockito.any(WarehouseEvent.class));

    }

    @Test
    public void shouldAddNewProductIntoWarehouseEvent(){
        WarehouseEvent expected = new WarehouseEvent(1L, new Warehouse(), new Event(), new ArrayList<>(), null);
        Mockito.when(warehouseEventRepository.findById(Mockito.any())).thenReturn(Optional.of(expected));
        Mockito.when(productRepository.findAllById(Mockito.any())).thenReturn(List.of(new Product()));
        Mockito.when(warehouseEventRepository.save(Mockito.any())).thenReturn(expected);

        WarehouseEvent warehouseEvent = warehouseEventService.addProductsIntoEvent(1L, List.of(1L));

        Assertions.assertNotNull(warehouseEvent);
        Assertions.assertTrue(warehouseEvent.getProducts().size() > 0);
        Mockito.verify(warehouseEventRepository, Mockito.times(1)).save(Mockito.any(WarehouseEvent.class));
    }

    @Test
    public void shouldNotAddNewProductIntoWarehouseEventWhenNotFoundEvent(){
        Mockito.when(warehouseEventRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> warehouseEventService.addProductsIntoEvent(1L, List.of(1L)));
    }

    @Test
    public void shouldNotAddNewProductIntoWarehouseEventWhenNotFoundProduct(){
        WarehouseEvent expected = new WarehouseEvent(1L, new Warehouse(), new Event(), new ArrayList<>(), null);
        Mockito.when(warehouseEventRepository.findById(Mockito.any())).thenReturn(Optional.of(expected));
        Mockito.when(productRepository.findAllById(Mockito.any())).thenReturn(Collections.emptyList());

        Assertions.assertThrows(NotFoundException.class, () -> warehouseEventService.addProductsIntoEvent(1L, List.of(1L)));

    }

    @Test
    public void shouldRemoveProductFromWarehouseEvent(){
        Product mockedProduct = new Product(1L, "mocked", CategoryEnum.FS, new Date());

        WarehouseEvent expected = new WarehouseEvent(1L, new Warehouse(), new Event(), new ArrayList<>(List.of(mockedProduct)), null);

        Mockito.when(warehouseEventRepository.findById(Mockito.any())).thenReturn(Optional.of(expected));
        Mockito.when(warehouseEventRepository.save(Mockito.any())).thenReturn(expected);

        WarehouseEvent warehouseEvent = warehouseEventService.removeProductFromEvent(1L, List.of(1L));

        Assertions.assertEquals(warehouseEvent.getProducts().size(), 0);
        Mockito.verify(warehouseEventRepository, Mockito.times(1)).save(expected);
    }

    @Test
    public void shouldNotRemoveProductFromWarehouseEventWhenNotFindEvent(){
        Mockito.when(warehouseEventRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> warehouseEventService.removeProductFromEvent(1L, List.of(1L)));

    }

    @Test
    public void shouldCallAllRegisteredEvents(){

        Event mockedEvent = new Event(
                null,
                "removeBatches",
                "removalEventExecutor",
                3
        );
        Product mockedProduct = new Product(1L, "mocked", CategoryEnum.FS, new Date());
        WarehouseEvent expected = new WarehouseEvent(1L, new Warehouse(), mockedEvent, new ArrayList<>(List.of(mockedProduct)), null);

        Mockito.when(warehouseEventRepository.findAll()).thenReturn(List.of(expected));
        Mockito.when(executorsService.removalEventExecutor(Mockito.any())).thenReturn(List.of(new BatchResponseDTO()));
        Mockito.when(warehouseEventRepository.save(Mockito.any())).thenReturn(expected);

        List<EventsExecutedDTO> result = warehouseEventService.executeEvents();

        Assertions.assertTrue(result.get(0).getResults().size() > 0);
        Mockito.verify(warehouseEventRepository, Mockito.times(1)).save(expected);
        Mockito.verify(executorsService, Mockito.times(1)).removalEventExecutor(Mockito.any());
    }

}
