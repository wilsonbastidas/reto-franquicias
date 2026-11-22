package co.com.nequi.api;

import co.com.nequi.api.dto.ProductRequest;
import co.com.nequi.api.dto.StockProductRequest;
import co.com.nequi.api.dto.UpdateProductNameRequest;
import co.com.nequi.api.handler.ProductHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ProductRouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/products",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = ProductHandler.class,
                    beanMethod = "createProduct",
                    operation = @Operation(
                            operationId = "createProduct",
                            summary = "Create product",
                            description = "Creates a new product in a branch",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = ProductRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Product created successfully"),
                                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                                    @ApiResponse(responseCode = "404", description = "Branch not found"),
                                    @ApiResponse(responseCode = "409", description = "Conflict"),
                                    @ApiResponse(responseCode = "500", description = "Internal server error")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/products/{id}",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.DELETE,
                    beanClass = ProductHandler.class,
                    beanMethod = "deleteProductById",
                    operation = @Operation(
                            operationId = "deleteProductById",
                            summary = "Delete product",
                            description = "Deletes a product by its ID",
                            parameters = {
                                    @Parameter(name = "id", description = "Product ID", required = true, in = ParameterIn.PATH)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
                                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                                    @ApiResponse(responseCode = "404", description = "Product not found"),
                                    @ApiResponse(responseCode = "500", description = "Internal server error")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/products/update-stock",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.PATCH,
                    beanClass = ProductHandler.class,
                    beanMethod = "updateStockProduct",
                    operation = @Operation(
                            operationId = "updateStockProduct",
                            summary = "Update product stock",
                            description = "Updates the stock of an existing product",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = StockProductRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Product stock updated successfully"),
                                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                                    @ApiResponse(responseCode = "404", description = "Product not found"),
                                    @ApiResponse(responseCode = "500", description = "Internal server error")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/franchises/{franchiseId}/products/max-stock",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = ProductHandler.class,
                    beanMethod = "getMaxStockProductsByFranchise",
                    operation = @Operation(
                            operationId = "getMaxStockProductsByFranchise",
                            summary = "Get max stock products by franchise",
                            description = "Returns the product with the highest stock for each branch within a franchise",
                            parameters = {
                                    @Parameter(name = "franchiseId", description = "Franchise ID", required = true, in = ParameterIn.PATH)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
                                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found"),
                                    @ApiResponse(responseCode = "500", description = "Internal server error")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/products/update-name",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.PATCH,
                    beanClass = ProductHandler.class,
                    beanMethod = "updateProductName",
                    operation = @Operation(
                            operationId = "updateProductName",
                            summary = "Update product name",
                            description = "Updates the name of an existing product",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateProductNameRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Product name updated successfully"),
                                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                                    @ApiResponse(responseCode = "404", description = "Product not found"),
                                    @ApiResponse(responseCode = "409", description = "Conflict"),
                                    @ApiResponse(responseCode = "500", description = "Internal server error")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> productRouterFunction(ProductHandler handler) {
        return route(POST("/api/v1/products"), handler::createProduct)
                .andRoute(DELETE("/api/v1/products/{id}"), handler::deleteProductById)
                .andRoute(PATCH("/api/v1/products/update-stock"), handler::updateStockProduct)
                .andRoute(GET("/api/v1/franchises/{franchiseId}/products/max-stock"),
                        handler::getMaxStockProductsByFranchise)
                .andRoute(PATCH("/api/v1/products/update-name"), handler::updateProductName);
    }
}