package com.cwretirement.codetest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TradeSettlement {

    private Type type;
    private Date dateEntered;
    private Date datePosted;
    private Date dateSettled;
    private Date dateTraded;
    private BigDecimal units;
    private BigDecimal unitPrice;
    private String fundName;
    private BigDecimal amount;
    private String symbolCusip;
    private long custodianReference;

    public TradeSettlement(Type type,
                           Date dateEntered,
                           Date datePosted,
                           Date dateSettled,
                           Date dateTraded,
                           BigDecimal units,
                           BigDecimal unitPrice,
                           String fundName,
                           BigDecimal amount,
                           String symbolCusip,
                           long custodianReference) {
        this.type = type;
        this.dateEntered = dateEntered;
        this.datePosted = datePosted;
        this.dateSettled = dateSettled;
        this.dateTraded = dateTraded;
        this.units = units;
        this.unitPrice = unitPrice;
        this.fundName = fundName;
        this.amount = amount;
        this.symbolCusip = symbolCusip;
        this.custodianReference = custodianReference;
    }

    @Override
    public String toString() {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TradeSettlement that = (TradeSettlement) o;
        return custodianReference == that.custodianReference &&
                type == that.type &&
                java.util.Objects.equals(dateEntered, that.dateEntered) &&
                java.util.Objects.equals(datePosted, that.datePosted) &&
                java.util.Objects.equals(dateSettled, that.dateSettled) &&
                java.util.Objects.equals(dateTraded, that.dateTraded) &&
                java.util.Objects.equals(units, that.units) &&
                java.util.Objects.equals(unitPrice, that.unitPrice) &&
                java.util.Objects.equals(fundName, that.fundName) &&
                java.util.Objects.equals(amount, that.amount) &&
                java.util.Objects.equals(symbolCusip, that.symbolCusip);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(type, dateEntered, datePosted, dateSettled, dateTraded, units, unitPrice, fundName, amount, symbolCusip, custodianReference);
    }

    public enum Type {
        PURCHASE,
        SALE;

        private static final Map<String, Type> TRANSACTION_DESCRIPTION_TO_SETTLEMENT_TYPE = new HashMap<String, Type>() {{
            put("Purchase Cash Settlement", PURCHASE);
            put("Sale Cash Settlement", SALE);
        }};

        public static Type fromString(String string) {
            return TRANSACTION_DESCRIPTION_TO_SETTLEMENT_TYPE.get(string);
        }
    }



}
