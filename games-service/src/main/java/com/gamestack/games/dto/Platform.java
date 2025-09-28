package com.gamestack.games.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Platform {
    @JsonProperty("platform")
    private PlatformDetail platform;

    public PlatformDetail getPlatform() {
        return platform;
    }

    public void setPlatform(PlatformDetail platform) {
        this.platform = platform;
    }

    public static class PlatformDetail {
        @JsonProperty("name")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
