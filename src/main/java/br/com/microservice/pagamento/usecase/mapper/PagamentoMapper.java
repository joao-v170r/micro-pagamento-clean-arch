package br.com.microservice.pagamento.usecase.mapper;

import br.com.microservice.pagamento.domain.Pagamento;
import br.com.microservice.pagamento.dto.PagamentoDTO;

public class PagamentoMapper {
    public static Pagamento mapToDomain(PagamentoDTO dto) {
        return Pagamento.reconstituir(
                dto.id(),
                dto.nome(),
                new CPF(dto.cpf()),
                dto.email(),
                dto.dataNascimento(),
                dto.enderecos(),
                dto.telefones()
        );
    }

    public static PagamentoDTO mapToDTO(Pagamento pagamento) {
        return new PagamentoDTO(
                pagamento.getId(),
                pagamento.getNome(),
                pagamento.getCpf().numero(),
                pagamento.getEmail(),
                pagamento.getDataNascimento(),
                pagamento.getEnderecos(),
                pagamento.getTelefones()
        );
    }
}
