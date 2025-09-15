package com.cwretirement.codetest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.stream.Stream;

@SpringBootApplication
public class CodeTestApplication implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(CodeTestApplication.class);

    @Autowired
    ReconciliationService reconciliationService;

    @Value("${transaction.file.path}")
    private Path path;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CodeTestApplication.class);
        app.run(args);
    }

    @Override
    public void run(String... args) {
        try (Stream<String> lines = Files.lines(path)) {
            lines.skip(1).forEach(line -> {
                LOG.debug("Read line: {}", line);
                String[] fields = line.split("\\|", -1);
                Transaction transaction;
                try {
                    transaction = new Transaction(fields);
                } catch (ParseException | NumberFormatException e) {
                    LOG.error("Failed to parse transaction: {}", line, e);
                    return;
                }
                if (transaction == null) {
                    LOG.warn("Skipping null transaction for line: {}", line);
                    return;
                }
                LOG.debug("Parsed transaction: {}", transaction);
                final Transaction.Type type = transaction.getType();
                if (type == null) {
                    LOG.warn("Unknown transaction type '{}' for line: {}", fields[6], line);
                    return;
                }
                if (type.isContribution()) {
                    Contribution contribution = transaction.toContribution();
                    reconciliationService.reconcileContribution(contribution);
                } else if (type.isTradeSettlement()) {
                    // Trade settlement reconciliation
                    TradeSettlement settlement = transaction.toTradeSettlement();
                    reconciliationService.reconcileTradeSettlement(settlement);
                } else {
                    LOG.warn("Unhandled transaction type '{}' for line: {}", type, line);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Unable to read transaction file: " + path, e);
        }
    }
}
