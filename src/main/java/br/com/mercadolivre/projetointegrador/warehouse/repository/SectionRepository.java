package br.com.mercadolivre.projetointegrador.warehouse.repository;

import br.com.mercadolivre.projetointegrador.warehouse.model.Section;
import br.com.mercadolivre.projetointegrador.warehouse.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findAllByWarehouse(Warehouse warehouse);

    Optional<Section> findByManagerId(Long id);

}
