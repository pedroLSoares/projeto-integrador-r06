package br.com.mercadolivre.projetointegrador.jobs.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@Setter
public class NewJobDTO {


    @NotNull
    @NotEmpty
    private final String name;

    @NotNull
    @NotEmpty
    private final String executor;
}
