package br.com.mercadolivre.projetointegrador.jobs.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
public class NewJobDTO {


    @NotNull
    @NotEmpty
    private final String name;

    @NotNull
    @NotEmpty
    private final String executor;
}
