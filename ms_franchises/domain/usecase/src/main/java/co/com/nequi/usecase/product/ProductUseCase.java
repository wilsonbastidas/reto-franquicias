package co.com.nequi.usecase.product;

import co.com.nequi.model.exceptions.ConflictException;
import co.com.nequi.model.exceptions.NotFoundException;
import co.com.nequi.model.gateways.BranchRepositoryGateway;
import co.com.nequi.model.gateways.ProductRepositoryGateway;
import co.com.nequi.model.product.Product;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class ProductUseCase {

    private final ProductRepositoryGateway productRepository;
    private final BranchRepositoryGateway branchRepository;

    public Mono<Product> createProduct(Product product) {
        return branchRepository.findById(product.getBranch().getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Branch not found")))
                .flatMap(branch ->
                        productRepository.findByName(product.getName())
                                .hasElement()
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(new ConflictException(
                                                "Product with name " + product.getName() + " already exists"));
                                    }
                                    product.setBranch(branch);
                                    return productRepository.save(product);
                                })
                );
    }

    public Mono<Void> deleteProduct(UUID productId) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("Product not found")))
                .flatMap(product -> productRepository.deleteById(productId));
    }
}
