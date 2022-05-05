package br.com.mercadolivre.projetointegrador.events.service;

import br.com.mercadolivre.projetointegrador.events.dto.request.NewWarehouseEventDTO;
import br.com.mercadolivre.projetointegrador.events.dto.response.EventsExecutedDTO;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseEventService {

    private final WarehouseService warehouseService;
    private final EventService eventService;
    private final WarehouseEventRepository warehouseEventRepository;
    private final BatchRepository batchRepository;
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

    public WarehouseEvent addProductIntoEvent(Long warehouseEventId, Long productId){
        WarehouseEvent warehouseEvent = warehouseEventRepository
                .findById(warehouseEventId).orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Produto não encontrado"));

        warehouseEvent.getProducts().add(product);

        return warehouseEventRepository.save(warehouseEvent);
    }

    public WarehouseEvent removeProductFromEvent(Long warehouseEventId, Long productId){
        WarehouseEvent warehouseEvent = warehouseEventRepository
                .findById(warehouseEventId).orElseThrow(() -> new NotFoundException("Evento não encontrado"));

        warehouseEvent.getProducts().removeIf(p -> p.getId().equals(productId));

        return warehouseEventRepository.save(warehouseEvent);
    }

    public List<EventsExecutedDTO> executeEvents(){
        List<WarehouseEvent> eventList = warehouseEventRepository.findAll();

        return eventList.stream().map(event -> {
            try {
                EventsExecutedDTO eventsExecutedDTO = new EventsExecutedDTO();
                eventsExecutedDTO.setEvent(event.getEvent().getName());
                Method mtd = this.getClass().getMethod(event.getEvent().getExecutor(), WarehouseEvent.class);
                eventsExecutedDTO.setResults(mtd.invoke(this, event));
                event.setLastExecution(LocalDateTime.now());

                warehouseEventRepository.save(event);

                return eventsExecutedDTO;

            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }

        }).collect(Collectors.toList());

    }

    public List<BatchResponseDTO> removalEventExecutor(WarehouseEvent warehouseEvent){
        Warehouse warehouse = warehouseEvent.getWarehouse();

        List<Batch> batchList = batchRepository.findAllBySectionWarehouseAndProductIn(warehouse, warehouseEvent.getProducts());

        List<Batch> toRemove = batchList.stream().filter(batch -> batch.getDueDate().isBefore(LocalDate.now().plusWeeks(3))).collect(Collectors.toList());

        batchRepository.deleteAllById(toRemove.stream().map(Batch::getId).collect(Collectors.toList()));


        return BatchMapper.INSTANCE.toResponseDTOList(toRemove);
    }
}
