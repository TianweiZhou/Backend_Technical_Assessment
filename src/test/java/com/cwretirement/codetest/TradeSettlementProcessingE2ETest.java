package com.cwretirement.codetest;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@SpringBootTest
class TradeSettlementProcessingE2ETest {

    @Autowired
    CodeTestApplication codeTestApplication;

    @MockitoBean
    private ReconciliationService reconciliationService;

    @Test
    void runProcessesTradeSettlements() throws ParseException {
        // Capture all trade settlement objects
        ArgumentCaptor<TradeSettlement> captor = ArgumentCaptor.forClass(TradeSettlement.class);
        verify(reconciliationService, times(3)).reconcileTradeSettlement(captor.capture());
        final List<TradeSettlement> settlementList = captor.getAllValues();
        final Date jan24 = Transaction.DATE_FORMAT.parse("24-Jan-20");

        TradeSettlement settlement1 = new TradeSettlement(
                TradeSettlement.Type.PURCHASE,
                jan24,
                jan24,
                jan24,
                jan24,
                new BigDecimal("0.7460"),
                new BigDecimal("118.820375"),
                "VANGUARD RETIREMENT 2025 POOLED FUND",
                new BigDecimal("-88.64"),
                "VCJ2",
                4435534L
        );
        TradeSettlement settlement2 = new TradeSettlement(
                TradeSettlement.Type.SALE,
                jan24,
                jan24,
                jan24,
                jan24,
                new BigDecimal("-0.0910"),
                new BigDecimal("125.384615"),
                "VANGUARD RETIREMENT 2035 POOLED FUND",
                new BigDecimal("11.41"),
                "VCJ5",
                4435537L
        );
        TradeSettlement settlement3 = new TradeSettlement(
                TradeSettlement.Type.PURCHASE,
                jan24,
                jan24,
                jan24,
                jan24,
                new BigDecimal("0.7910"),
                new BigDecimal("129.228824"),
                "VANGUARD RETIREMENT 2050 POOLED FUND",
                new BigDecimal("-102.22"),
                "VCJ8",
                4435540L
        );
        assertThat(settlementList).hasSize(3).containsExactly(settlement1, settlement2, settlement3);
    }
}