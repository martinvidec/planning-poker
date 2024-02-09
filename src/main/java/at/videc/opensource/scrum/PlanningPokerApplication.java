package at.videc.opensource.scrum;

import at.videc.opensource.scrum.config.ApplicationProperties;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.spring.annotation.EnableVaadin;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
//@Theme(value = "planning-poker-ui", variant = Lumo.DARK)
@Theme(themeClass = Material.class, variant = Material.DARK)
@EnableConfigurationProperties(ApplicationProperties.class)
@Push
public class PlanningPokerApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(PlanningPokerApplication.class, args);
    }

}
