package br.com.microservice.pagamento.gateway.database.mongo.mapper;

import br.com.microservice.pagamento.domain.Pagamento;
import br.com.microservice.pagamento.gateway.database.mongo.entity.PagamentoEntity;

public class PagamentoMapper {
    public static Pagamento mapToDomain(PagamentoEntity dto) {
        return Pagamento.reconstituir(
                dto.getCodGateway(),
                dto.getDtAtualizacao(),
                dto.getDtCriacao(),
                dto.getGatewayPagamento(),
                dto.getMetodoPagamento(),
                dto.getMoedaPagamento(),
                dto.getValorTotal(),
                dto.getPedidoId(),
                dto.getId(),
                dto.getDetalhes(),
                dto.getStatus()
        );
    }

    public static PagamentoEntity mapToEntity(Pagamento domain) {
        return new PagamentoEntity(
                domain.getId(),
                domain.getPedidoId(),
                domain.getValorTotal(),
                domain.getMoedaPagamento(),
                domain.getMetodoPagamento(),
                domain.getGatewayPagamento(),
                domain.getDetalhes(),
                domain.getStatus(),
                domain.getDtCriacao(),
                domain.getDtAtualizacao(),
                domain.getCodGateway()
        );
    }
}
