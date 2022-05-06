package br.com.mercadolivre.projetointegrador.jobs.exceptions.handlers;

import br.com.mercadolivre.projetointegrador.jobs.exceptions.JobExecutorException;
import br.com.mercadolivre.projetointegrador.warehouse.exception.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class JobExceptionsHandler {

    @ExceptionHandler(JobExecutorException.class)
    public ResponseEntity<ErrorDTO> jobExecutorExceptionHandler(JobExecutorException e) {
        ErrorDTO error = new ErrorDTO();
        error.setError("Ocorreu um erro");
        error.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
