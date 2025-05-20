package br.com.microservice.pagamento.usecase.mapper;

import br.com.microservice.pagamento.domain.Pagamento;
import br.com.microservice.pagamento.dto.PagamentoDTO;

public class PagamentoMapper {
    public static Pagamento mapToDomain(PagamentoDTO dto) {
        return Pagamento.reconstituir(
                dto.codGateway(),
                dto.dtAtualizacao(),
                dto.dtCriacao(),
                dto.gatewayPagamento(),
                dto.metodoPagamento(),
                dto.moedaPagamento(),
                dto.valorTotal(),
                dto.pedidoId(),
                dto.id(),
                dto.detalhes(),
                dto.status()
        );
    }

    public static PagamentoDTO mapToDTO(Pagamento pagamento) {
        return new PagamentoDTO(
                pagamento.getId(),
                pagamento.getPedidoId(),
                pagamento.getValorTotal(),
                pagamento.getMoedaPagamento(),
                pagamento.getMetodoPagamento(),
                pagamento.getGatewayPagamento(),
                pagamento.getDetalhes(),
                pagamento.getStatus(),
                pagamento.getDtCriacao(),
                pagamento.getDtAtualizacao(),
                pagamento.getCodGateway()
        );
    }
}
