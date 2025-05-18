package br.com.microservice.pagamento.dto.rest_controller;

import br.com.microservice.pagamento.domain.value_objects.MetodoPagamento;

import java.math.BigDecimal;

public record InputCreatePagamentoDTO(
        String clienteId,
        String pedidoId,
        MetodoPagamento metodoPagamento,
        BigDecimal valorTotal
) {
}

