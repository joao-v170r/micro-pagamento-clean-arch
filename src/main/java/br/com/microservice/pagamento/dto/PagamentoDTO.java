package br.com.microservice.pagamento.dto;

import java.util.HashMap;

public record PagamentoDTO(
        String id,
        String status,
        HashMap<String, String> detalhes,
        String moeda,
        String metodo
) {
}
