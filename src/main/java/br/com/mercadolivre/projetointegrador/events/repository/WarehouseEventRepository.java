package br.com.mercadolivre.projetointegrador.events.repository;

import br.com.mercadolivre.projetointegrador.events.model.WarehouseEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseEventRepository extends JpaRepository<WarehouseEvent, Long> {

    List<WarehouseEvent> findAllByWarehouseId(Long warehouseId);
}
