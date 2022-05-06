package br.com.mercadolivre.projetointegrador.events.repository;

import br.com.mercadolivre.projetointegrador.events.model.WarehouseJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseJobRepository extends JpaRepository<WarehouseJob, Long> {

    List<WarehouseJob> findAllByWarehouseId(Long warehouseId);
}
