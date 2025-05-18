package br.com.microservice.pagamento.controler;

import br.com.microservice.pagamento.domain.Pagamento;
import br.com.microservice.pagamento.gateway.CrudPagamentoGateway;
import br.com.microservice.pagamento.usecase.ProcessamentoPagamentoUseCase;
import br.com.microservice.pagamento.usecase.mapper.PagamentoMapper;
import br.com.microservice.pagamento.utils.ClienteMockData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@WebMvcTest(UpdateClienteController.class)
@AutoConfigureMockMvc
@Import(ProcessamentoPagamentoUseCase.class)
public class UpdatePagamentoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CrudPagamentoGateway gateway;

    @Autowired
    ObjectMapper mapper;

    @Test
    void updateClienteSucesss() throws Exception {
        InputUpdateClienteDTO input = ClienteMockData.validInputUpdateClienteDTO();
        Pagamento mock = ClienteMockData.validCliente();

        Mockito.when(gateway.findById(mock.getId())).thenReturn(Optional.of(mock));
        Mockito.when(gateway.save(Mockito.any(Pagamento.class))).thenAnswer(invocationOnMock -> {
            return invocationOnMock.getArgument(0);
        });

        mock.setNome(input.nome());
        mock.setEmail(input.email());
        mock.setDataNascimento(LocalDate.parse(input.dataNascimento()));
        mock.setEnderecos(new HashSet<>(input.enderecos()));
        mock.setTelefone(new HashSet<>(input.telefones()));

        mockMvc.perform(
                MockMvcRequestBuilders.put("/update-cliente/{id}", mock.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(PagamentoMapper.mapToDTO(mock))));
    }

    @Test
    void updateClienteWithClienteNotFoundException() throws Exception {
        InputUpdateClienteDTO input = ClienteMockData.validInputUpdateClienteDTO();
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/update-cliente/{id}", UUID.randomUUID().toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(input)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message")
                        .value("UpdateClienteUseCase: id do cliente não encontrado"));
    }
}
