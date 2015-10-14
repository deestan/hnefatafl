package no.bouvet.hnefatafl.config;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class Config extends Configuration {
    @NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "Stranger";

	@NotNull
	private Integer searchDepth = 2;

    @JsonProperty
    public String getTemplate() {
        return template;
    }

    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    @JsonProperty
    public Integer getSearchDepth() {
        return searchDepth;
    }

    @JsonProperty
    public void setSearchDepth(Integer  searchDepth) {
        this.searchDepth = searchDepth;
    }
}
