package br.com.mercadolivre.projetointegrador.events.repository;

import br.com.mercadolivre.projetointegrador.events.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}
