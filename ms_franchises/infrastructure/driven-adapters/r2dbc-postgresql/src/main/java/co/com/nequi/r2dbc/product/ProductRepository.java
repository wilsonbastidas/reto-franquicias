package co.com.nequi.r2dbc.product;

import co.com.nequi.r2dbc.entities.ProductEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductRepository extends ReactiveCrudRepository<ProductEntity, UUID> {
    Mono<ProductEntity> findByName(String name);

    @Query("""
            SELECT DISTINCT ON (p.branch_id) p.*
            FROM products p
            INNER JOIN branches b ON p.branch_id = b.id
            WHERE b.franchise_id = :franchiseId
            ORDER BY p.branch_id, p.stock DESC
            """)
    Flux<ProductEntity> findMaxStockByFranchiseId(UUID franchiseId);
}
