package no.bouvet.hnefatafl;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import no.bouvet.hnefatafl.config.Config;
import no.bouvet.hnefatafl.health.BrainHealthCheck;
import no.bouvet.hnefatafl.resources.Brain;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

public class App extends Application<Config> {
    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public String getName() {
        return "hnefatafl";
    }

    @Override
    public void initialize(Bootstrap<Config> bootstrap) {
        System.out.println("App.initialize");
    }

    @Override
    public void run(Config config, Environment environment) {
        // Enable CORS headers
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        // Add URL mapping for CORS
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        Brain brain = new Brain(config.getSearchDepth());
        environment.jersey().register(brain);

        final BrainHealthCheck healthCheck = new BrainHealthCheck(brain);
        environment.healthChecks().register("template", healthCheck);
    }
}
