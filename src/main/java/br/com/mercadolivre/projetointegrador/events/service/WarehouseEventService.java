package br.com.mercadolivre.projetointegrador.events.service;

import br.com.mercadolivre.projetointegrador.events.dto.request.NewWarehouseEventDTO;
import br.com.mercadolivre.projetointegrador.events.model.Event;
import br.com.mercadolivre.projetointegrador.events.model.WarehouseEvent;
import br.com.mercadolivre.projetointegrador.events.repository.WarehouseEventRepository;
import br.com.mercadolivre.projetointegrador.warehouse.model.Batch;
import br.com.mercadolivre.projetointegrador.warehouse.model.Section;
import br.com.mercadolivre.projetointegrador.warehouse.model.Warehouse;
import br.com.mercadolivre.projetointegrador.warehouse.repository.BatchRepository;
import br.com.mercadolivre.projetointegrador.warehouse.repository.SectionRepository;
import br.com.mercadolivre.projetointegrador.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseEventService {

    private final WarehouseService warehouseService;
    private final EventService eventService;
    private final WarehouseEventRepository warehouseEventRepository;
    private final BatchRepository batchRepository;

    public WarehouseEvent registerEventToWarehouse(NewWarehouseEventDTO newEvent){
        Warehouse warehouse = warehouseService.findWarehouse(newEvent.idWarehouse);
        Event event = eventService.find(newEvent.idEvent);

        WarehouseEvent warehouseEvent = new WarehouseEvent(null,warehouse, event, null, null);

        return warehouseEventRepository.save(warehouseEvent);

    }

    public List<WarehouseEvent> getAllWarehouseEvents(){
        return warehouseEventRepository.findAll();
    }

    public List<WarehouseEvent> getWarehouseEvents(Long warehouseId){
        return warehouseEventRepository.findAllByWarehouseId(warehouseId);
    }

    public void executeEvents(){
        List<WarehouseEvent> eventList = warehouseEventRepository.findAll();

        List<Object> integerList = eventList.stream().map(event -> {
            try {
                Method mtd = this.getClass().getMethod(event.getEvent().getExecutor(), WarehouseEvent.class);
                return mtd.invoke(this, event);

            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return 0;
            }

        }).collect(Collectors.toList());


    }

    private Integer removalEventExecutor(WarehouseEvent warehouseEvent){
        Warehouse warehouse = warehouseEvent.getWarehouse();

        List<Batch> batchList = batchRepository.findAllBySectionWarehouseAndProductIn(warehouse, warehouseEvent.getProducts());

        List<Batch> toRemove = batchList.stream().filter(batch -> batch.getDueDate().isBefore(LocalDate.now().plusWeeks(3))).collect(Collectors.toList());

        batchRepository.deleteAllById(toRemove.stream().map(Batch::getId).collect(Collectors.toList()));


        return toRemove.size();
    }
}
