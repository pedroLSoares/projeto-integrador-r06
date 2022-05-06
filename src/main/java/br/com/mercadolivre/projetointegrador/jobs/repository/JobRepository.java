package br.com.mercadolivre.projetointegrador.jobs.repository;

import br.com.mercadolivre.projetointegrador.jobs.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
}
