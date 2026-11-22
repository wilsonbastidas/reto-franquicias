package co.com.nequi.api;

import co.com.nequi.api.handler.FranchiseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class FranchiseRouterRest {

    @Bean
    public RouterFunction<ServerResponse> franchiseRouterFunction(FranchiseHandler handler) {
        return route(POST("/api/v1/franchise"), handler::createFranchise);
    }
}
