package no.bouvet.hnefatafl;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import no.bouvet.hnefatafl.config.Config;
import no.bouvet.hnefatafl.health.BrainHealthCheck;
import no.bouvet.hnefatafl.resources.Brain;

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
        Brain brain = new Brain(config.getSearchDepth());
        environment.jersey().register(brain);

        final BrainHealthCheck healthCheck = new BrainHealthCheck(brain);
        environment.healthChecks().register("template", healthCheck);
    }
}
