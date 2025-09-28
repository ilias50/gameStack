
package com.gamestack.games.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RawgResponse {
    @JsonProperty("results")
    private List<RawgGameResult> results;

    public List<RawgGameResult> getResults() {
        return results;
    }

    public void setResults(List<RawgGameResult> results) {
        this.results = results;
    }
}
