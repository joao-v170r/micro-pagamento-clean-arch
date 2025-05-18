package br.com.microservice.pagamento.gateway.dto;

import br.com.microservice.pagamento.domain.value_objects.MetodoPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;

public record OutputExternalPagamentoDTO(
        String id,
        String status,
        BigDecimal valor,
        String moeda,
        HashMap<String, String> detalhes,
        MetodoPagamento metodo,
        LocalDateTime dataProcessamento
) {

}
