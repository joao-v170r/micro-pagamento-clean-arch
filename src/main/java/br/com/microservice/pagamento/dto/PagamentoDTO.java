package br.com.microservice.pagamento.dto;

import br.com.microservice.pagamento.domain.value_objects.MetodoPagamento;
import br.com.microservice.pagamento.domain.value_objects.MoedaPagamento;
import br.com.microservice.pagamento.domain.value_objects.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;

public record PagamentoDTO(
        String id,
        String pedidoId,
        BigDecimal valorTotal,
        MoedaPagamento moedaPagamento,
        MetodoPagamento metodoPagamento,
        String gatewayPagamento,
        HashMap<String, String> detalhes,
        StatusPagamento status,
        LocalDateTime dtCriacao,
        LocalDateTime dtAtualizacao,
        String codGateway
) {
}
