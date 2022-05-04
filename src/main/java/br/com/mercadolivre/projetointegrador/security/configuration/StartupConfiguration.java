package br.com.mercadolivre.projetointegrador.security.configuration;

import br.com.mercadolivre.projetointegrador.events.model.Event;
import br.com.mercadolivre.projetointegrador.events.repository.EventRepository;
import br.com.mercadolivre.projetointegrador.security.model.UserRole;
import br.com.mercadolivre.projetointegrador.security.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;

@Configuration
public class StartupConfiguration implements ApplicationListener<ContextRefreshedEvent> {

  boolean alreadySetup = false;

  @Autowired private RolesRepository rolesRepository;
  @Autowired private EventRepository eventRepository;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    if (alreadySetup) {
      return;
    }
    List<UserRole> roles = rolesRepository.findAll();

    if (roles.isEmpty()) {
      rolesRepository.saveAll(
          List.of(new UserRole(null, "CUSTOMER"), new UserRole(null, "MANAGER")));
    }

    eventRepository.save(new Event(
            null,
            "removeBatches",
            "removalEventExecutor",
            3
    ));

    alreadySetup = true;
  }
}
