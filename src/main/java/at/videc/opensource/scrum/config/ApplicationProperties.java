package at.videc.opensource.scrum.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("at.videc.opensource.scrum.planning-poker")
public class ApplicationProperties {

    private Float[] estimates;

    public Float[] getEstimates() {
        return estimates;
    }

    public void setEstimates(Float[] estimates) {
        this.estimates = estimates;
    }
}
