package br.com.microservice.pagamento.controler;

import br.com.microservice.pagamento.domain.Pagamento;
import br.com.microservice.pagamento.gateway.CrudPagamentoGateway;
import br.com.microservice.pagamento.usecase.ReadPagamentoUserCase;
import br.com.microservice.pagamento.usecase.mapper.PagamentoMapper;
import br.com.microservice.pagamento.utils.ClienteMockData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(ReadPagamentoController.class)
@AutoConfigureMockMvc
@Import(ReadPagamentoUserCase.class)
public class ReadPagamentoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CrudPagamentoGateway gateway;

    @Autowired
    ObjectMapper mapper;

    @Test
    void readFindClienteSucess() throws Exception {
        Pagamento mock = ClienteMockData.validCliente();

        when(gateway.findById(any()))
                .thenReturn(Optional.of(mock));

        String resultExpectedJson = mapper.writeValueAsString(PagamentoMapper.mapToDTO(mock));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/cliente/{id}", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(resultExpectedJson));
    }

    @Test
    void readFindAllSucess() throws Exception {
        Pagamento mock = ClienteMockData.validCliente();

        when(gateway.findAll(any()))
                .thenReturn(List.of(mock));

        String resultExpectedJson = mapper.writeValueAsString(List.of(PagamentoMapper.mapToDTO(mock)));

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/cliente")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(resultExpectedJson));
    }

    @Test
    void readFindClienteWithClienteNotFoundException() throws Exception {

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/cliente/{id}", UUID.randomUUID().toString())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Cliente não encontrado"));
    }

    @Test
    void readFindAllEmptyClientes() throws Exception {

        String resultExpectedJson = mapper.writeValueAsString(List.of());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/cliente")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(resultExpectedJson));
    }
}
