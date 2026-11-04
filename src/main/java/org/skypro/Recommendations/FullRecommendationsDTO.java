package org.skypro.Recommendations;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FullRecommendationsDTO {

    String userId;
    List<RecommendationsDTO> recommendations;

    public FullRecommendationsDTO(String userId, List<RecommendationsDTO> recommendations) {
        this.userId = userId;
        this.recommendations = recommendations;
    }

    @JsonProperty("user_id")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("recommendations")
    public List<RecommendationsDTO> getRecommendations() {
        return recommendations;
    }
}
