package com.starlingbank.tech.api.roundup;

import com.starlingbank.tech.StaticDataHelper;
import com.starlingbank.tech.api.roundUp.SaveTheChangeAPI;
import com.starlingbank.tech.exception.StarlingBusinessException;
import com.starlingbank.tech.service.RoundUpAccountTransactions;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class SaveTheChangeAPITest {

    @InjectMocks
    private SaveTheChangeAPI saveTheChangeAPI;

    @Mock
    private RoundUpAccountTransactions roundUpAccountTransactions;

    private MockMvc mockMvc;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String clientAuthToken = "eyJhbGciOiJQUzI1NiJ9.eyJpc3MiOiJhcGktZGVtb";
    private static final String transactionStartDate = "20191228";

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(saveTheChangeAPI).build();
    }

    @Test
    public void whenAccountTransactionsGaveRoundupValue_returnSuccess() throws Exception {
        //given
        given(roundUpAccountTransactions.getAccountTransactionsRoundup(transactionStartDate)).willReturn(
                StaticDataHelper.getAccountsRoundupDetails());

        //when
        MockHttpServletRequestBuilder mockRequestBuilder = MockMvcRequestBuilders.get("/roundup/account/transactions/startDate/{startDate}", transactionStartDate)
                .header(HttpHeaders.AUTHORIZATION, clientAuthToken)
                .accept("application/json");

        MockHttpServletResponse result = mockMvc.perform(mockRequestBuilder).andReturn().getResponse();

        //then
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(result.getContentAsString()).contains(StaticDataHelper.accountUID);
        assertThat(result.getContentAsString()).contains(String.valueOf(StaticDataHelper.roundupMoney.getMinorAmountUnit()));
    }

    @Test
    public void whenNoAccountTransactionsExist_returnFailure() {
        //given
        given(roundUpAccountTransactions.getAccountTransactionsRoundup(transactionStartDate))
                .willThrow(new StarlingBusinessException("No accounts found"));

        //when
        MockHttpServletRequestBuilder mockRequestBuilder = MockMvcRequestBuilders.get("/roundup/account/transactions/startDate/{startDate}", transactionStartDate)
                .header(HttpHeaders.AUTHORIZATION, clientAuthToken)
                .accept("application/json");

        //then
        try {
            mockMvc.perform(mockRequestBuilder);
        }
        catch(Exception e) {
            assertThat(e.getCause()).isInstanceOf(StarlingBusinessException.class);
        }
    }

    @Test
    public void whenTransactionStartDateHasInvalidFormat_returnFailure() throws Exception {
        //given

        //when
        MockHttpServletRequestBuilder mockRequestBuilder = MockMvcRequestBuilders.get("/roundup/account/transactions/startDate/{startDate}",
                "2019-12000-000")
                .header(HttpHeaders.AUTHORIZATION, clientAuthToken)
                .accept("application/json");

        //then
        mockMvc.perform(mockRequestBuilder).andExpect(status().isBadRequest());
    }
}
