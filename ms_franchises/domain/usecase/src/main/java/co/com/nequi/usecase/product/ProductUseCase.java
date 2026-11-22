package co.com.nequi.usecase.product;

import co.com.nequi.model.exceptions.ConflictException;
import co.com.nequi.model.exceptions.NotFoundException;
import co.com.nequi.model.gateways.BranchRepositoryGateway;
import co.com.nequi.model.gateways.FranchiseRepositoryGateway;
import co.com.nequi.model.gateways.ProductRepositoryGateway;
import co.com.nequi.model.product.Product;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class ProductUseCase {

    private final ProductRepositoryGateway productRepository;
    private final BranchRepositoryGateway branchRepository;
    private final FranchiseRepositoryGateway franchiseRepository;

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

    public Mono<Product> updateStockProduct(Product product) {
        return productRepository.findById(product.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Product not found")))
                .flatMap(existingProduct -> {
                    existingProduct.setStock(product.getStock());
                    existingProduct.setUpdatedAt(LocalDateTime.now());
                    return productRepository.update(existingProduct);
                });
    }

    public Flux<Product> getMaxStockProductsByFranchise(UUID franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Franchise not found")))
                .flatMapMany(franchise -> productRepository.findMaxStockByFranchiseId(franchiseId));
    }

    public Mono<Product> updateProductName(UUID id, String name) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Product not found")))
                .flatMap(existing -> productRepository.findByName(name)
                        .flatMap(found -> {
                            if (!found.getId().equals(id)) {
                                return Mono.error(new ConflictException("Product with name " + name + " already exists"));
                            }
                            return Mono.just(existing);
                        })
                        .defaultIfEmpty(existing)
                        .flatMap(product -> {
                            product.setName(name);
                            product.setUpdatedAt(LocalDateTime.now());
                            return productRepository.update(product);
                        })
                );
    }
}
