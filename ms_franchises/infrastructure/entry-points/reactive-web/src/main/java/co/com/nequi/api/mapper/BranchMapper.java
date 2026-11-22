package co.com.nequi.api.mapper;


import co.com.nequi.api.dto.BranchRequest;
import co.com.nequi.api.dto.BranchResponse;
import co.com.nequi.api.dto.UpdateBranchNameRequest;
import co.com.nequi.model.branch.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchMapper {

    @Mapping(target = "franchise.id", source = "franchiseId")
    Branch toCreateDomain(BranchRequest branchRequest);

    BranchResponse toResponse(Branch branch);
    Branch toDomain(UpdateBranchNameRequest request);
}
