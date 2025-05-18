package br.com.microservice.pagamento.dto.usecase;

import br.com.microservice.pagamento.domain.value_objects.MetodoPagamento;

import java.math.BigDecimal;

public record CreatePagamentoDTO(
        String clienteId,
        String pedidoId,
        MetodoPagamento metodoPagamento,
        BigDecimal valorTotal
) {
}
