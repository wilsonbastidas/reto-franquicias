package co.com.nequi.api.mapper;

import co.com.nequi.api.dto.FranchiseRequest;
import co.com.nequi.api.dto.FranchiseResponse;
import co.com.nequi.api.dto.UpdateFranchiseNameRequest;
import co.com.nequi.model.franchise.Franchise;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FranchiseMapper {
    FranchiseResponse toDTO(Franchise franchise);
    Franchise toDomain(FranchiseRequest franchiseDTO);
    Franchise toDomain(UpdateFranchiseNameRequest request);

}
