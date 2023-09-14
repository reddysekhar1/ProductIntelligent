package com.intelliservice.agentanalytics.app;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import io.jaegertracing.Configuration;

@SpringBootApplication
@ComponentScan({"com.intelliservice.agentanalytics.*"})
public class Application extends SpringBootServletInitializer {
	private static final Logger log = LoggerFactory.getLogger(Application.class);
    @Autowired
    Environment environment;
    @Value("${spring.application.name}")
    private String applicationName;

    public static void main(String[] args) {
        SpringApplication.run(com.intelliservice.agentanalytics.app.Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            String port = environment.getProperty("local.server.port");
            log.info("Server started - http://localhost:{}/swagger-ui.html",port);
        };
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    @Bean
    public io.opentracing.Tracer initTracer() {
        Configuration.SamplerConfiguration samplerConfig = new Configuration.SamplerConfiguration()
                .withType("const")
                .withParam(1);
        Configuration.ReporterConfiguration reporterConfig = Configuration.ReporterConfiguration.fromEnv()
                .withLogSpans(true);

        return Configuration.fromEnv(this.applicationName)
                .withSampler(samplerConfig)
                .withReporter(reporterConfig).getTracer();
    }
    
   
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
    	return builder.setConnectTimeout(Duration.ofMillis(600000))
        .setReadTimeout(Duration.ofMillis(600000)).build();
         
    }

}
