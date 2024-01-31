package at.videc.opensource.scrum.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@ConfigurationProperties("at.videc.opensource.scrum.planning-poker")
public class ApplicationProperties implements Serializable {

    private Float[] estimates;
    private Integer coffeeBreakDuration;

    private String cardWidth;

    private String cardHeight;

    public Float[] getEstimates() {
        return estimates;
    }

    public void setEstimates(Float[] estimates) {
        this.estimates = estimates;
    }

    public Integer getCoffeeBreakDuration() {
        return coffeeBreakDuration;
    }

    public void  setCoffeeBreakDuration(Integer coffeeBreakDuration) {
        this.coffeeBreakDuration = coffeeBreakDuration;
    }

    public String getCardWidth() {
        return cardWidth;
    }

    public void setCardWidth(String cardWidth) {
        this.cardWidth = cardWidth;
    }

    public String getCardHeight() {
        return cardHeight;
    }

    public void setCardHeight(String cardHeight) {
        this.cardHeight = cardHeight;
    }
}
