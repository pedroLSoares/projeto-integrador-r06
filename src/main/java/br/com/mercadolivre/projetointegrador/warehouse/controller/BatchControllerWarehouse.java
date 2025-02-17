package br.com.mercadolivre.projetointegrador.warehouse.controller;

import br.com.mercadolivre.projetointegrador.warehouse.docs.config.SecuredWarehouseRestController;
import br.com.mercadolivre.projetointegrador.warehouse.exception.ErrorDTO;
import br.com.mercadolivre.projetointegrador.warehouse.assembler.BatchAssembler;
import br.com.mercadolivre.projetointegrador.warehouse.dto.response.BatchResponseDTO;
import br.com.mercadolivre.projetointegrador.warehouse.exception.db.NotFoundException;
import br.com.mercadolivre.projetointegrador.warehouse.model.Batch;
import br.com.mercadolivre.projetointegrador.warehouse.service.BatchService;
import br.com.mercadolivre.projetointegrador.warehouse.view.BatchView;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "[Warehouse] - Batch")
@RequestMapping("/api/v1/warehouse/batches")
public class BatchControllerWarehouse implements SecuredWarehouseRestController {

  private final BatchService batchService;
  private final BatchAssembler assembler;

  @Operation(
      summary = "RETORNA UM LOTE",
      description = "Retorna um lote com o id correspondente ao passado na url")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lote retornado com sucesso",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = BatchResponseDTO.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "Lote não encontrado",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorDTO.class))
            })
      })
  @GetMapping("/{id}")
  public ResponseEntity<BatchResponseDTO> findBatchById(@PathVariable Long id)
      throws NotFoundException {
    return assembler.toResponse(batchService.findById(id), HttpStatus.OK);
  }

  @GetMapping("/ad/{sellerId}")
  @JsonView(BatchView.BatchAd.class)
  public ResponseEntity<List<BatchResponseDTO>> listBatchesToAd(@PathVariable Long sellerId) {
    List<Batch> batchList = batchService.listBySellerId(sellerId);

    return assembler.toBatchResponse(batchList, HttpStatus.OK);
  }
}
