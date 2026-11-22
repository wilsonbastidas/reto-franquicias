package co.com.nequi.api.mapper;


import co.com.nequi.api.dto.ProductRequest;
import co.com.nequi.api.dto.ProductResponse;
import co.com.nequi.model.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "branch.id", source = "branchId")
    Product toDomain(ProductRequest productRequest);

    ProductResponse toResponse(Product product);
}
