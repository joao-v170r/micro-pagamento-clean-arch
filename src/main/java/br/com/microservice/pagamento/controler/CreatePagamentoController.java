package br.com.microservice.pagamento.controler;

import br.com.microservice.pagamento.dto.PagamentoDTO;
import br.com.microservice.pagamento.dto.rest_controller.InputCreatePagamentoDTO;
import br.com.microservice.pagamento.dto.usecase.CreatePagamentoDTO;
import br.com.microservice.pagamento.usecase.CreatePagamentoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/solicita-pagamento")
@Tag(name = "Pagamento", description = "Endpoints que controlam pagamentos")
public class CreatePagamentoController {

    final CreatePagamentoUseCase useCase;

    public CreatePagamentoController(CreatePagamentoUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    @Operation(
            summary = "Solicitação de pagamento"
    )
    public ResponseEntity<PagamentoDTO> create(@Valid @RequestBody InputCreatePagamentoDTO input) {
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(useCase.create(
                new CreatePagamentoDTO(
                    input.clienteId(),
                    input.pedidoId(),
                    input.metodoPagamento(),
                    input.valorTotal()
        ));
    }
}
