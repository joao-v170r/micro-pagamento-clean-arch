package br.com.microservice.pagamento.controler;

import br.com.microservice.pagamento.dto.PagamentoDTO;
import br.com.microservice.pagamento.usecase.ReadPagamentoUserCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pagamento")
@Tag(name = "Pagamento", description = "Endpoints que controlam pagamentos")
public class ReadPagamentoController {

    final ReadPagamentoUserCase useCase;

    public ReadPagamentoController(ReadPagamentoUserCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar pagamento"
    )
    public ResponseEntity<PagamentoDTO> findById(@PathVariable String id) {
        return ResponseEntity.ok(useCase.find(id));
    }

    @GetMapping("/pedido/{id}")
    @Operation(
            summary = "Buscar pagamento"
    )
    public ResponseEntity<PagamentoDTO> findByPedidoId(@PathVariable String pedidoId) {
        return ResponseEntity.ok(useCase.findByPedidoId(pedidoId));
    }

    @GetMapping
    @Operation(
            summary = "Listar todos os clientes"
    )
    public ResponseEntity<List<PagamentoDTO>> findAll(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable page
    ) {
        return ResponseEntity.ok(useCase.findAll(page));
    }
}
