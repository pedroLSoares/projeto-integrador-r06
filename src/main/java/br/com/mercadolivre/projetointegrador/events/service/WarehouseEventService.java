package br.com.mercadolivre.projetointegrador.events.service;

import br.com.mercadolivre.projetointegrador.events.dto.request.NewWarehouseEventDTO;
import br.com.mercadolivre.projetointegrador.events.dto.response.EventsExecutedDTO;
import br.com.mercadolivre.projetointegrador.events.dto.response.ExecutionResponseDTO;
import br.com.mercadolivre.projetointegrador.events.model.Event;
import br.com.mercadolivre.projetointegrador.events.model.WarehouseEvent;
import br.com.mercadolivre.projetointegrador.events.repository.WarehouseEventRepository;
import br.com.mercadolivre.projetointegrador.warehouse.dto.response.BatchResponseDTO;
import br.com.mercadolivre.projetointegrador.warehouse.exception.db.NotFoundException;
import br.com.mercadolivre.projetointegrador.warehouse.mapper.BatchMapper;
import br.com.mercadolivre.projetointegrador.warehouse.model.Batch;
import br.com.mercadolivre.projetointegrador.warehouse.model.Product;
import br.com.mercadolivre.projetointegrador.warehouse.model.Warehouse;
import br.com.mercadolivre.projetointegrador.warehouse.repository.BatchRepository;
import br.com.mercadolivre.projetointegrador.warehouse.repository.ProductRepository;
import br.com.mercadolivre.projetointegrador.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseEventService {

    private final WarehouseService warehouseService;
    private final EventService eventService;
    private final WarehouseEventRepository warehouseEventRepository;
    private final EventExecutorsService executorsService;
    private final ProductRepository productRepository;

    public WarehouseEvent getEvent(final Long warehouseEventId){
        return warehouseEventRepository.findById(warehouseEventId).orElseThrow(() -> new NotFoundException("Evento não encontrado"));
    }

    public WarehouseEvent registerEventToWarehouse(NewWarehouseEventDTO newEvent){
        Warehouse warehouse = warehouseService.findWarehouse(newEvent.idWarehouse);
        Event event = eventService.find(newEvent.idEvent);
        List<Product> products = productRepository.findAllById(newEvent.getProductList());

        WarehouseEvent warehouseEvent = new WarehouseEvent(null,warehouse, event, products, null);

        return warehouseEventRepository.save(warehouseEvent);

    }

    public List<WarehouseEvent> getAllWarehouseEvents(){
        return warehouseEventRepository.findAll();
    }

    public List<WarehouseEvent> getWarehouseEvents(Long warehouseId){
        return warehouseEventRepository.findAllByWarehouseId(warehouseId);
    }

    public void removeWarehouseEvent(Long warehouseEventId){
         warehouseEventRepository.deleteById(warehouseEventId);
    }

    public WarehouseEvent addProductsIntoEvent(Long warehouseEventId, List<Long> productsIds){
        WarehouseEvent warehouseEvent = warehouseEventRepository
                .findById(warehouseEventId).orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        List<Product> products = productRepository.findAllById(productsIds);

        if(products.isEmpty()){
            throw new NotFoundException("Produtos não encontrados");
        }

        warehouseEvent.getProducts().addAll(products);

        return warehouseEventRepository.save(warehouseEvent);
    }

    public WarehouseEvent removeProductFromEvent(Long warehouseEventId, List<Long> productIds){
        WarehouseEvent warehouseEvent = warehouseEventRepository
                .findById(warehouseEventId).orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        warehouseEvent.getProducts().removeIf(p -> productIds.contains(p.getId()));

        return warehouseEventRepository.save(warehouseEvent);
    }

    public List<ExecutionResponseDTO> executeEvents(){
        List<WarehouseEvent> eventList = warehouseEventRepository.findAll();
        Map<Long, ExecutionResponseDTO> response = new HashMap<>();

        eventList.forEach(event -> {
            try {
                Long warehouseId = event.getWarehouse().getId();
                ExecutionResponseDTO dto = response.computeIfAbsent(warehouseId, (k) -> new ExecutionResponseDTO(warehouseId, new ArrayList<>()));

                EventsExecutedDTO eventsExecutedDTO = new EventsExecutedDTO();
                eventsExecutedDTO.setEvent(event.getEvent().getName());
                Method mtd = executorsService.getClass().getMethod(event.getEvent().getExecutor(), WarehouseEvent.class);
                eventsExecutedDTO.setResults((List<Object>) mtd.invoke(executorsService, event));
                event.setLastExecution(LocalDateTime.now());

                warehouseEventRepository.save(event);

                dto.getEventsExecuted().add(eventsExecutedDTO);

                response.put(warehouseId, dto);





            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();

            }

        });

        return new ArrayList<>(response.values());
    }
}
