package br.com.microservice.pagamento.gateway.dto;

import br.com.microservice.pagamento.domain.value_objects.MetodoPagamento;

import java.math.BigDecimal;

public record InputExternalPagamentoDTO(
        BigDecimal valor,
        String moeda,
        MetodoPagamento metodo
) {
}
