package co.com.nequi.api.mapper;

import co.com.nequi.api.dto.FranchiseRecord;
import co.com.nequi.api.dto.FranchiseResponseDTO;
import co.com.nequi.model.franchise.Franchise;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FranchiseMapper {
    FranchiseResponseDTO toDTO(Franchise franchise);
    Franchise toDomain(FranchiseRecord franchiseDTO);

}
