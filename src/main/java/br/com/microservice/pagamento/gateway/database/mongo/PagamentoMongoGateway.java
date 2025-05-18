package br.com.microservice.pagamento.gateway.database.mongo;

import br.com.microservice.pagamento.domain.Pagamento;
import br.com.microservice.pagamento.gateway.CrudPagamentoGateway;
import br.com.microservice.pagamento.gateway.database.mongo.entity.PagamentoEntity;
import br.com.microservice.pagamento.gateway.database.mongo.mapper.PagamentoMapper;
import br.com.microservice.pagamento.gateway.database.mongo.repository.PagamentoRepository;
import br.com.microservice.pagamento.gateway.exception.mongo.PagamentoMongoError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PagamentoMongoGateway implements CrudPagamentoGateway {

    private final PagamentoRepository repository;

    @Override
    public Optional<Pagamento> findByPedidoId(String pedidoId) {

        if (pedidoId == null || pedidoId.isBlank()) {
            throw new PagamentoMongoError.PagamentoInvalidArgumentException("pedidoId não pode ser vazio ou nulo.");
        }

        try {
            Optional<PagamentoEntity> entity = repository.findByPedidoId(pedidoId);
            return entity.map(PagamentoMapper::mapToDomain);
        } catch (Exception e) {
            log.error("Falha ao buscar pagamento pelo id do pedido: {}", pedidoId, e);
            throw new PagamentoMongoError.PagamentoPersistenceException("erro ao acessar o banco de dados.", e);
        }
    }

    @Override
    public Optional<Pagamento> findById(String id) {

        if (id == null || id.isBlank()) {
            throw new PagamentoMongoError.PagamentoInvalidArgumentException("id do pagamento inválido.");
        }

        try {
            Optional<PagamentoEntity> entity = repository.findById(id);
            return entity.map(PagamentoMapper::mapToDomain);
        } catch (Exception e) {
            log.error("Falha ao buscar pagamento por ID: {}", id, e);
            throw new PagamentoMongoError.PagamentoPersistenceException("erro ao acessar o banco de dados.", e);
        }
    }

    @Override
    public Boolean existId(String id) {
        try {
            return repository.existsById(id);
        } catch (Exception e) {
            log.error("Falha ao verificar existência do pagamento com ID: {}", id, e);
            throw new PagamentoMongoError.PagamentoPersistenceException("erro ao acessar o banco de dados.", e);
        }
    }

    @Override
    public List<Pagamento> findAll() {
        try {
            return repository.findAll()
                    .stream()
                    .map(PagamentoMapper::mapToDomain)
                    .toList();
        } catch (Exception e) {
            log.error("Falha ao listar pagamentos.", e);
            throw new PagamentoMongoError.PagamentoPersistenceException("erro ao acessar o banco de dados.", e);
        }
    }

    @Override
    public Pagamento save(Pagamento pagamento) {

        if (pagamento == null) {
            throw new PagamentoMongoError.PagamentoInvalidArgumentException("Pagamento não pode ser nulo.");
        }
        try {
            PagamentoEntity entity = PagamentoMapper.mapToEntity(pagamento);
            PagamentoEntity savedEntity = repository.save(entity);
            return PagamentoMapper.mapToDomain(savedEntity);
        } catch (DataIntegrityViolationException e) {
            log.error("Conflito ao salvar pagamento: {}", pagamento.getId(), e);
            throw new PagamentoMongoError.PagamentoConflictException("Pagamento ja esta salvo.");
        } catch (Exception e) {
            log.error("Falha ao salvar pagamento.", e);
            throw new PagamentoMongoError.PagamentoPersistenceException("erro ao persistir pagamento.", e);
        }
    }

    @Override
    public void deleteById(String id) {

        if (!repository.existsById(id)) {
            throw new PagamentoMongoError.PagamentoNotFoundException("pagamento não encontrado para exclusão.");
        }

        try {
            repository.deleteById(id);
        } catch (Exception e) {
            log.error("Falha ao excluir pagamento com ID: {}", id, e);
            throw new PagamentoMongoError.PagamentoPersistenceException("erro ao excluir pagamento.", e);
        }
    }

    @Override
    public List<Pagamento> findAll(Pageable page) {
        return repository.findAll(page).map(PagamentoMapper::mapToDomain).stream().toList();
    }
}