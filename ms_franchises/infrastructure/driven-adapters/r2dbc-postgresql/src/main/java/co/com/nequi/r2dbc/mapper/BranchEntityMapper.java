package co.com.nequi.r2dbc.mapper;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.r2dbc.entities.BranchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchEntityMapper {

    @Mapping(target = "franchiseId", source = "franchise.id")
    BranchEntity toEntity(Branch branch);

    @Mapping(target = "franchise.id", source = "franchiseId")
    @Mapping(target = "franchise.name", ignore = true)
    @Mapping(target = "franchise.createdAt", ignore = true)
    @Mapping(target = "franchise.updatedAt", ignore = true)
    Branch toDomain(BranchEntity branchEntity);
}
