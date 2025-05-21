package br.com.microservice.pagamento.controller;

import br.com.microservice.pagamento.dto.PagamentoDTO;
import br.com.microservice.pagamento.usecase.ProcessamentoPagamentoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/processa-pagamento")
@Tag(name = "Pagamento", description = "Endpoints que controlam pagamentos")
public class ProcessaPagamentoController {

    final ProcessamentoPagamentoUseCase useCase;

    public ProcessaPagamentoController(ProcessamentoPagamentoUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/{id}")
    @Operation(
            summary = "Processa pagamento e verifica atualização no gateway externo"
    )
    public ResponseEntity<PagamentoDTO> processaPagamento(@PathVariable String id) {
        return ResponseEntity.ok(useCase.processaPagamento(id));
    }
}
