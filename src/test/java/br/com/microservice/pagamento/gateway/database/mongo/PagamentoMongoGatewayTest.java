package br.com.microservice.pagamento.gateway.database.mongo;

import br.com.microservice.pagamento.domain.Pagamento;
import br.com.microservice.pagamento.gateway.database.mongo.entity.PagamentoEntity;
import br.com.microservice.pagamento.gateway.database.mongo.mapper.PagamentoMapper;
import br.com.microservice.pagamento.gateway.database.mongo.repository.PagamentoRepository;
import br.com.microservice.pagamento.gateway.exception.mongo.PagamentoMongoError;
import br.com.microservice.pagamento.utils.ClienteMockData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.AssertionErrors;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@ExtendWith(MockitoExtension.class)
class PagamentoMongoGatewayTest {

    @Mock
    PagamentoRepository repository;

    @InjectMocks
    PagamentoMongoGateway gateway;

    @Test
    void findByPedidoId() {
        Pagamento mock = ClienteMockData.validCliente();

        Mockito.when(repository.findByCpf(Mockito.any(String.class)))
                .thenReturn(Optional.of(PagamentoMapper.mapToEntity(mock)));

        var opCliente = gateway.findByPedidoId(mock.getCpf().numero());

        assertTrue("Verifica função findByCpf do gateway mongo", opCliente.isPresent());
        AssertionErrors.assertEquals(
                "Verifica resultado retornado",
                mock.getId(),
                opCliente.get().getId()
        );
        verify(repository).findByCpf(mock.getCpf().numero());
    }

    @Test
    void findByPedidoIdNull() {
        Assertions.assertThrows(
                PagamentoMongoError.PagamentoInvalidArgumentException.class,
                () -> gateway.findByPedidoId(null)
        );
    }

    @Test
    void findByPedidoIdWithRuntimeException() {
        Mockito.when(repository.findByCpf(any())).thenThrow(new RuntimeException());

        Assertions.assertThrows(
                PagamentoMongoError.PagamentoPersistenceException.class,
                () -> gateway.findByPedidoId(ClienteMockData.validInput().cpf())
        );
    }

    @Test
    void findById() {
        Pagamento mock = ClienteMockData.validCliente();

        Mockito.when(repository.findById(Mockito.any(String.class)))
                .thenReturn(Optional.of(PagamentoMapper.mapToEntity(mock)));

        var opCliente = gateway.findById(mock.getId());

        assertTrue("Verifica função findByCpf do gateway mongo", opCliente.isPresent());
        AssertionErrors.assertEquals(
                "Verifica resultado retornado",
                mock.getId(),
                opCliente.get().getId()
        );
        verify(repository).findById(mock.getId());
    }

    @Test
    void findByIdNull() {
        Assertions.assertThrows(
                PagamentoMongoError.PagamentoInvalidArgumentException.class,
                () -> gateway.findById(null)
        );
    }

    @Test
    void findByIdWithRuntimeException() {
        Mockito.when(repository.findById(any())).thenThrow(new RuntimeException());
        Assertions.assertThrows(
                PagamentoMongoError.PagamentoPersistenceException.class,
                () -> gateway.findById(UUID.randomUUID().toString())
        );
    }

    @Test
    void existId() {
        String id = UUID.randomUUID().toString();
        Mockito.when(repository.existsById(id)).thenReturn(true);
        assertTrue("Verifica se existe id", gateway.existId(id));
    }

    @Test
    void existIdWithRuntimeException() {
        String id = UUID.randomUUID().toString();
        Mockito.when(repository.existsById(id)).thenThrow(new RuntimeException());

        Assertions.assertThrows(
                PagamentoMongoError.PagamentoPersistenceException.class,
                () -> gateway.existId(id)
        );
    }

    @Test
    void findAll() {
        Pageable pageable = Pageable.ofSize(10);
        List<PagamentoEntity> mockResults = List.of(
                ClienteMockData.validClienteEntity(),
                ClienteMockData.validClienteEntity()
        );
        Mockito.when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(
                mockResults,
                PageRequest.of(0, 10),
                mockResults.size()
        ));

        var result = gateway.findAll(pageable);

        AssertionErrors.assertEquals(
                "Verificando retorno esperado",
                result.size(),
                mockResults.size()
        );

        result.forEach(e -> {
            int index = result.indexOf(e);
            AssertionErrors.assertEquals(
                    "Index: " +index+". Verificando id: " + e.getId() + " dos clientes do resultado",
                    e.getId(),
                    mockResults.get(index).getId()
            );
        });
    }

    @Test
    void save() {
        Pagamento mock = ClienteMockData.validCliente();

        Mockito.when(repository.save(any(PagamentoEntity.class)))
                .thenAnswer(i -> i.getArgument(0));

        Pagamento pagamento = gateway.save(mock);

        AssertionErrors.assertEquals(
                "Verificando id do resultado com o esperado",
                pagamento.getId(),
                mock.getId()
        );
    }

    @Test
    void saveClienteNull() {
        Assertions.assertThrows(
                PagamentoMongoError.PagamentoInvalidArgumentException.class,
                () -> gateway.save(null)
        );
    }

    @Test
    void saveWithDataIntegrityViolationException() {
        Mockito.when(repository.save(any()))
                        .thenThrow(new DataIntegrityViolationException("Error Test"));

        Assertions.assertThrows(
                PagamentoMongoError.PagamentoConflictException.class,
                () -> gateway.save(ClienteMockData.validCliente())
        );
    }

    @Test
    void saveWithException() {
        Mockito.when(repository.save(any()))
                .thenThrow(new RuntimeException());

        Assertions.assertThrows(
                PagamentoMongoError.PagamentoPersistenceException.class,
                () -> gateway.save(ClienteMockData.validCliente())
        );
    }

    @Test
    void deleteById() {
        String id = UUID.randomUUID().toString();

        Mockito.when(repository.existsById(id))
                .thenReturn(true);

        gateway.deleteById(id);
        Mockito.verify(repository).deleteById(any(String.class));

    }

    @Test
    void deleteByIdNull() {
        Assertions.assertThrows(
                PagamentoMongoError.PagamentoNotFoundException.class,
                () -> gateway.deleteById(null)
        );
    }

    @Test
    void deleteByIdWithException() {
        Mockito.when(repository.existsById(any(String.class)))
                        .thenReturn(true);
        Mockito.doThrow(new RuntimeException()).when(repository).deleteById(any());
        Assertions.assertThrows(
                PagamentoMongoError.PagamentoPersistenceException.class,
                () -> gateway.deleteById(UUID.randomUUID().toString())
        );
    }
}