package co.com.nequi.r2dbc.mapper;

import co.com.nequi.model.product.Product;
import co.com.nequi.r2dbc.entities.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {

    @Mapping(target = "branchId", source = "branch.id")
    ProductEntity toEntity(Product product);

    Product toDomain(ProductEntity productEntity);
}
