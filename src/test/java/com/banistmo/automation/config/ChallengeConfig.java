package com.banistmo.automation.config;

public final class ChallengeConfig {

    private static final String DEFAULT_PERSONAS_URL = "https://www.banistmo.com/personas/";

    private ChallengeConfig() {
    }

    public static String personasUrl() {
        return System.getProperty("challenge.personas.url", DEFAULT_PERSONAS_URL);
    }
}
