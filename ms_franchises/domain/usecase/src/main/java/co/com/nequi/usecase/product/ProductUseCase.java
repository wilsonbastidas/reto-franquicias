package co.com.nequi.usecase.product;

import co.com.nequi.model.exceptions.NotFoundException;
import co.com.nequi.model.gateways.BranchRepositoryGateway;
import co.com.nequi.model.gateways.ProductRepositoryGateway;
import co.com.nequi.model.product.Product;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductUseCase {

    private final ProductRepositoryGateway productRepository;
    private final BranchRepositoryGateway branchRepository;

    public Mono<Product> createProduct(Product product) {
        return branchRepository.findById(product.getBranch().getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Branch not found")))
                .flatMap(branch -> {
                    product.setBranch(branch);
                    return productRepository.save(product);
                });
    }
}
