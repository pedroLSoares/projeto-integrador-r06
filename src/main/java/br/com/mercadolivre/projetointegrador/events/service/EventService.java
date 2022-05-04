package br.com.mercadolivre.projetointegrador.events.service;

import br.com.mercadolivre.projetointegrador.events.model.Event;
import br.com.mercadolivre.projetointegrador.events.repository.EventRepository;
import br.com.mercadolivre.projetointegrador.warehouse.exception.db.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event find(Long id){
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event " + id + " not found"));
    }
}
