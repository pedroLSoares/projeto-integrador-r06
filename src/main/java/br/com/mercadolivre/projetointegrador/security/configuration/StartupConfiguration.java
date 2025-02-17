package br.com.mercadolivre.projetointegrador.security.configuration;

import br.com.mercadolivre.projetointegrador.jobs.model.Job;
import br.com.mercadolivre.projetointegrador.jobs.repository.JobRepository;
import br.com.mercadolivre.projetointegrador.security.model.UserRole;
import br.com.mercadolivre.projetointegrador.security.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;
import java.util.Optional;

@Configuration
public class StartupConfiguration implements ApplicationListener<ContextRefreshedEvent> {

  boolean alreadySetup = false;

  @Autowired private RolesRepository rolesRepository;
  @Autowired private JobRepository jobRepository;

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

    Optional<Job> optionalJob = jobRepository.findByExecutor("batchRemovalExecutor");

    if(optionalJob.isEmpty()){
      jobRepository.save(new Job(
              null,
              "removeBatches",
              "batchRemovalExecutor"
      ));
    }

    alreadySetup = true;
  }
}
