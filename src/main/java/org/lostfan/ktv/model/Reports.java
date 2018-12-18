package org.lostfan.ktv.model;


public enum Reports {
    TURNOVER_SHEET("turnoverSheet"),
    DAILY_REGISTER("dailyRegister"),
    CONSOLIDATED_REGISTER_ON_PAYMENT("consolidatedRegisterOnPayment"),
    REPORT_TO_BANK("reportToBank"),
    DEBTOR_REPORT("debtorReport"),
    WRITE_OFF_REPORT("writeOfReport"),
    CLAIM_REPORT("claimReport"),
    CHANNELS_REPORT("channelsReport"),
    SERVICE_SHEET("serviceSheet"),
    CONNECTED_DISCONNECTED_SUBSCRIBERS_SHEET("connectedDisconnectedSubscribersSheet");

    private final String code;

    public static Reports of(String code) {
        for (Reports reports : Reports.values()) {
            if (reports.code.equals(code)) {
                return reports;
            }
        }
        return null;
    }

    Reports(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
