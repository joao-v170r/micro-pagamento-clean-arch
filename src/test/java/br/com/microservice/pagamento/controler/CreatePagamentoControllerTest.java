package br.com.microservice.pagamento.controler;

import br.com.microservice.pagamento.domain.Pagamento;
import br.com.microservice.pagamento.dto.rest_controller.InputCreatePagamentoDTO;
import br.com.microservice.pagamento.gateway.CrudPagamentoGateway;
import br.com.microservice.pagamento.usecase.CreatePagamentoUseCase;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CreatePagamentoController.class)
@AutoConfigureMockMvc
@Import(CreatePagamentoUseCase.class)
class CreatePagamentoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CrudPagamentoGateway gateway;

    @Autowired
    ObjectMapper mapper;

    @Test
    void createSucess() throws Exception {
        InputCreatePagamentoDTO input = ClienteMockData.validInput();
        Pagamento pagamentoMock = ClienteMockData.validCliente();

        when(gateway.save(any()))
                .thenReturn(pagamentoMock);

        String resultExpectedJson = mapper.writeValueAsString(PagamentoMapper.mapToDTO(pagamentoMock));

        mockMvc.perform(
                    post("/create-cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input))
                ).andExpect(status().isCreated())
                    .andExpect(MockMvcResultMatchers.content().json(resultExpectedJson));
    }

    @Test
    void createWithClienteAlreadyExistsException() throws Exception {
        InputCreatePagamentoDTO input = ClienteMockData.validInput();
        Pagamento pagamentoMock = ClienteMockData.validCliente();

        when(gateway.findByPedidoId(any()))
                .thenReturn(Optional.of(pagamentoMock));

        mockMvc.perform(
                        post("/create-cliente")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(input))
                ).andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("CPF já foi utilizado")));
    }
}