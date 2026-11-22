package co.com.nequi.r2dbc.mapper;

import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.r2dbc.entities.FranchiseEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FranchiseEntityMapper {

    Franchise toDomain(FranchiseEntity franchiseEntity);

    FranchiseEntity toEntity(Franchise franchise);
}
