package com.cwretirement.codetest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class ReconciliationService {
    private static final Logger LOG = LoggerFactory.getLogger(ReconciliationService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${contribution.reconciliation.url}")
    private String contributionReconciliationURL;

    @Value("${trade.settlement.reconciliation.url}")
    private String tradeSettlementReconciliationURL;

    public void reconcileContribution(Contribution contribution) {
        callReconciliationService(contributionReconciliationURL, contribution);
    }

    public void reconcileTradeSettlement(TradeSettlement tradeSettlement) {
        callReconciliationService(tradeSettlementReconciliationURL, tradeSettlement);
    }

    private void callReconciliationService(String urlString, Object payload) {
        try {
            URL url = URI.create(urlString).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5_000);
            connection.setReadTimeout(5_000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            String jsonString = objectMapper.writeValueAsString(payload);
            LOG.debug("Writing to reconciliation service at {}: {}", urlString, jsonString);
            byte[] representation = jsonString.getBytes(StandardCharsets.UTF_8);
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setFixedLengthStreamingMode(representation.length);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(representation, 0, representation.length);
            }
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new ReconciliationException(ReconciliationException.Type.CANNOT_RECONCILE,
                        String.format("Reconciliation service returned HTTP code %d", responseCode));
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(String.format("Configured URL %s was not well-formed", urlString), e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to serialise reconciliation payload to JSON", e);
        } catch (SocketTimeoutException e) {
            throw new ReconciliationException(ReconciliationException.Type.SERVICE_TIMEOUT);
        } catch (IOException e) {
            throw new ReconciliationException(ReconciliationException.Type.SERVICE_UNAVAILABLE, e);
        }
    }
}