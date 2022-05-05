package br.com.mercadolivre.projetointegrador.events.service;

import br.com.mercadolivre.projetointegrador.events.model.WarehouseEvent;
import br.com.mercadolivre.projetointegrador.warehouse.dto.response.BatchResponseDTO;
import br.com.mercadolivre.projetointegrador.warehouse.mapper.BatchMapper;
import br.com.mercadolivre.projetointegrador.warehouse.model.Batch;
import br.com.mercadolivre.projetointegrador.warehouse.model.Warehouse;
import br.com.mercadolivre.projetointegrador.warehouse.repository.BatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventExecutorsService {

    private final BatchRepository batchRepository;

    public List<BatchResponseDTO> removalEventExecutor(WarehouseEvent warehouseEvent){
        Warehouse warehouse = warehouseEvent.getWarehouse();

        List<Batch> batchList = batchRepository.findAllBySectionWarehouseAndProductIn(warehouse, warehouseEvent.getProducts());

        List<Batch> toRemove = batchList.stream().filter(batch -> batch.getDueDate().isBefore(LocalDate.now().plusWeeks(3))).collect(Collectors.toList());

        batchRepository.deleteAllById(toRemove.stream().map(Batch::getId).collect(Collectors.toList()));


        return BatchMapper.INSTANCE.toResponseDTOList(toRemove);
    }
}
