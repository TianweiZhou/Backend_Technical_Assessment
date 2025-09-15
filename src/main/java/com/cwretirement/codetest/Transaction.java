package com.cwretirement.codetest;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Transaction {

    public enum Type {
        CONTRIBUTION,
        PURCHASE_CASH_SETTLEMENT,
        SALE_CASH_SETTLEMENT;

        private static final Map<String, Type> TRANSACTION_DESCRIPTION_TO_TRANSACTION_TYPE = new HashMap<String, Type>() {{
            put("Contribution", CONTRIBUTION);
            put("Purchase Cash Settlement", PURCHASE_CASH_SETTLEMENT);
            put("Sale Cash Settlement", SALE_CASH_SETTLEMENT);
        }};

        public static Type fromString(String string) {
            return TRANSACTION_DESCRIPTION_TO_TRANSACTION_TYPE.get(string);
        }

        public boolean isContribution() {
            return this == CONTRIBUTION;
        }

        public boolean isTradeSettlement() {
            return this == PURCHASE_CASH_SETTLEMENT || this == SALE_CASH_SETTLEMENT;
        }
    }

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yy");

    private long acct;
    private String acctNm;
    private Date entered;
    private Date posted;
    private Date dateSettled;
    private Date dateTraded;
    private String transactionDescription;
    private BigDecimal quantity;
    private BigDecimal rate;
    private String descr;
    private BigDecimal amount;
    private String symbolCusip;
    private String explanation;
    private long job;

    private Type type;

    public Transaction(String[] fields) throws ParseException {
        acct = Long.parseLong(fields[0]);
        acctNm = fields[1];
        entered = DATE_FORMAT.parse(fields[2]);
        posted = DATE_FORMAT.parse(fields[3]);
        dateSettled = DATE_FORMAT.parse(fields[4]);
        dateTraded = DATE_FORMAT.parse(fields[5]);
        transactionDescription = fields[6];
        quantity = new BigDecimal(fields[7]);
        rate = new BigDecimal(fields[8]);
        descr = fields[9];
        amount = new BigDecimal(fields[10]);
        symbolCusip = fields[11];
        explanation = fields[12];
        job = Long.parseLong(fields[13]);

        type = Type.fromString(transactionDescription);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "acct=" + acct +
                ", acctNm='" + acctNm + '\'' +
                ", entered=" + entered +
                ", posted=" + posted +
                ", dateSettled=" + dateSettled +
                ", dateTraded=" + dateTraded +
                ", transactionDescription='" + transactionDescription + '\'' +
                ", quantity=" + quantity +
                ", rate=" + rate +
                ", descr='" + descr + '\'' +
                ", amount=" + amount +
                ", symbolCusip='" + symbolCusip + '\'' +
                ", explanation='" + explanation + '\'' +
                ", job=" + job +
                '}';
    }

    public Type getType() {
        return type;
    }

    public TradeSettlement toTradeSettlement() {
        if (type == null || !type.isTradeSettlement()) {
            throw new IllegalStateException("Cannot convert non-trade-settlement transaction to TradeSettlement");
        }
        TradeSettlement.Type settlementType = TradeSettlement.Type.fromString(transactionDescription);
        return new TradeSettlement(
                settlementType,
                entered,
                posted,
                dateSettled,
                dateTraded,
                quantity,
                rate,
                descr,
                amount,
                symbolCusip,
                job
        );
    }

    public Contribution toContribution() {
        assert Type.CONTRIBUTION.equals(type);
        Contribution result = new Contribution(
                Long.parseLong(explanation),
                descr,
                entered,
                posted,
                amount,
                job
        );
        return result;
    }
}
