package br.com.microservice.pagamento.gateway.database.mongo.mapper;

import br.com.microservice.pagamento.domain.Pagamento;
import br.com.microservice.pagamento.gateway.database.mongo.entity.PagamentoEntity;

public class PagamentoMapper {
    public static Pagamento mapToDomain(PagamentoEntity dto) {
        return Pagamento.reconstituir(
                dto.getId(),
                dto.getNome(),
                new CPF(dto.getCpf()),
                dto.getEmail(),
                dto.getDataNascimento(),
                dto.getEnderecos(),
                dto.getTelefones()
        );
    }

    public static PagamentoEntity mapToEntity(Pagamento domain) {
        return new PagamentoEntity(
                domain.getId(),
                domain.getNome(),
                domain.getCpf().numero(),
                domain.getEmail(),
                domain.getDataNascimento(),
                domain.getEnderecos(),
                domain.getTelefones()
        );
    }
}
