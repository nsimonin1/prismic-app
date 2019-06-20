package org.simon.pascal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.prismic.Cache;
import io.prismic.Logger;

@SpringBootApplication
public class PrismicPocApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrismicPocApplication.class, args);
	}
	
	// PRISMIC BEANS

    @Bean
    public static Cache prismicCache() {
        return new Cache.BuiltInCache(200);
    }

    @Bean
    public static Logger prismicLogger() {
        return new Logger.PrintlnLogger();
    }

}
