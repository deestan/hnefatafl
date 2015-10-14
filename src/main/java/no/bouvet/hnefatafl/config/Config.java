package no.bouvet.hnefatafl.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class Config extends Configuration {
    @NotNull
    private Integer searchDepth = 2;

    @JsonProperty
    public Integer getSearchDepth() {
        return searchDepth;
    }

    @JsonProperty
    public void setSearchDepth(Integer searchDepth) {
        this.searchDepth = searchDepth;
    }
}
