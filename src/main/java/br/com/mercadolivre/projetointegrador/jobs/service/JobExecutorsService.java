package br.com.mercadolivre.projetointegrador.jobs.service;

import br.com.mercadolivre.projetointegrador.jobs.model.WarehouseJob;
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
public class JobExecutorsService {

    private final BatchRepository batchRepository;

    public List<BatchResponseDTO> batchRemovalExecutor(WarehouseJob warehouseJob){
        Warehouse warehouse = warehouseJob.getWarehouse();

        List<Batch> batchList = batchRepository.findAllBySectionWarehouseAndProductIn(warehouse, warehouseJob.getProducts());

        List<Batch> toRemove = batchList.stream().filter(batch -> batch.getDueDate().isBefore(LocalDate.now().plusWeeks(3))).collect(Collectors.toList());

        batchRepository.deleteAllById(toRemove.stream().map(Batch::getId).collect(Collectors.toList()));


        return BatchMapper.INSTANCE.toResponseDTOList(toRemove);
    }
}
