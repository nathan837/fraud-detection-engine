package com.security.fraud_detection_engine.engine.velocity;

public enum VelocityWindow {

    SIXTY_SECONDS(60, "60s"),
    ONE_HOUR(3600, "1hr"),
    TWENTY_FOUR_HOURS(86400, "24hr");

    private final long seconds;
    private final String label;

    VelocityWindow(long seconds, String label) {
        this.seconds = seconds;
        this.label = label;
    }

    public long getSeconds() {
        return seconds;
    }

    public String getLabel() {
        return label;
    }
}