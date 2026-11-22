package co.com.nequi.api;

import co.com.nequi.api.handler.BranchHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Configuration
public class BranchRouterRest {

    @Bean
    public RouterFunction<ServerResponse> branchRouterFunction(BranchHandler handler) {
        return route(POST("/api/v1/branches"), handler::createBranch)
                .andRoute(PATCH("/api/v1/branches/update-name"), handler::updateBranchName);
    }
}
