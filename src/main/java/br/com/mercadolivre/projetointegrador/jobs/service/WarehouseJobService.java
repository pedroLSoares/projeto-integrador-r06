package br.com.mercadolivre.projetointegrador.jobs.service;

import br.com.mercadolivre.projetointegrador.jobs.dto.request.NewWarehouseJobDTO;
import br.com.mercadolivre.projetointegrador.jobs.dto.response.JobsExecutedDTO;
import br.com.mercadolivre.projetointegrador.jobs.dto.response.ExecutionResponseDTO;
import br.com.mercadolivre.projetointegrador.jobs.model.Job;
import br.com.mercadolivre.projetointegrador.jobs.model.WarehouseJob;
import br.com.mercadolivre.projetointegrador.jobs.repository.WarehouseJobRepository;
import br.com.mercadolivre.projetointegrador.warehouse.exception.db.NotFoundException;
import br.com.mercadolivre.projetointegrador.warehouse.model.Product;
import br.com.mercadolivre.projetointegrador.warehouse.model.Warehouse;
import br.com.mercadolivre.projetointegrador.warehouse.repository.ProductRepository;
import br.com.mercadolivre.projetointegrador.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WarehouseJobService {

    private final WarehouseService warehouseService;
    private final JobService jobService;
    private final WarehouseJobRepository warehouseJobRepository;
    private final JobExecutorsService executorsService;
    private final ProductRepository productRepository;

    public WarehouseJob getJob(final Long warehouseEventId){
        return warehouseJobRepository.findById(warehouseEventId).orElseThrow(() -> new NotFoundException("Evento não encontrado"));
    }

    public WarehouseJob registerJobToWarehouse(NewWarehouseJobDTO newJob){
        Warehouse warehouse = warehouseService.findWarehouse(newJob.idWarehouse);
        Job job = jobService.find(newJob.idEvent);
        List<Product> products = productRepository.findAllById(newJob.getProductList());

        WarehouseJob warehouseJob = new WarehouseJob(null,warehouse, job, products, null);

        return warehouseJobRepository.save(warehouseJob);

    }

    public List<WarehouseJob> getAllWarehouseJobs(){
        return warehouseJobRepository.findAll();
    }

    public List<WarehouseJob> getWarehouseJobs(Long warehouseId){
        return warehouseJobRepository.findAllByWarehouseId(warehouseId);
    }

    public void removeWarehouseJob(Long warehouseJobId){
         warehouseJobRepository.deleteById(warehouseJobId);
    }

    public WarehouseJob addProductsIntoJob(Long warehouseEventId, List<Long> productsIds){
        WarehouseJob warehouseJob = warehouseJobRepository
                .findById(warehouseEventId).orElseThrow(() -> new NotFoundException("Job não encontrado"));

        List<Product> products = productRepository.findAllById(productsIds);

        if(products.isEmpty()){
            throw new NotFoundException("Produtos não encontrados");
        }

        warehouseJob.getProducts().addAll(products);

        return warehouseJobRepository.save(warehouseJob);
    }

    public WarehouseJob removeProductFromJob(Long warehouseEventId, List<Long> productIds){
        WarehouseJob warehouseJob = warehouseJobRepository
                .findById(warehouseEventId).orElseThrow(() -> new NotFoundException("Job não encontrado"));

        warehouseJob.getProducts().removeIf(p -> productIds.contains(p.getId()));

        return warehouseJobRepository.save(warehouseJob);
    }

    public List<ExecutionResponseDTO> executeJobs(){
        List<WarehouseJob> eventList = warehouseJobRepository.findAll();
        Map<Long, ExecutionResponseDTO> response = new HashMap<>();

        eventList.forEach(event -> {
            Long warehouseId = event.getWarehouse().getId();
            JobsExecutedDTO jobsExecutedDTO = new JobsExecutedDTO();
            ExecutionResponseDTO dto = response.computeIfAbsent(warehouseId, (k) -> new ExecutionResponseDTO(warehouseId, new ArrayList<>()));
            try {
                jobsExecutedDTO.setJob(event.getJob().getName());
                Method mtd = executorsService.getClass().getMethod(event.getJob().getExecutor(), WarehouseJob.class);
                jobsExecutedDTO.setResults((List<Object>) mtd.invoke(executorsService, event));
                event.setLastExecution(LocalDateTime.now());

                warehouseJobRepository.save(event);
                dto.getJobsExecuted().add(jobsExecutedDTO);

                response.put(warehouseId, dto);

            } catch (NoSuchMethodException e) {
                jobsExecutedDTO.setResults(List.of("Método executor " + event.getJob().getExecutor() + " não implementado"));

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                jobsExecutedDTO.setResults(List.of("Houve um problema ao chamar o executor " + event.getJob().getExecutor()));
            }

        });

        return new ArrayList<>(response.values());
    }
}
