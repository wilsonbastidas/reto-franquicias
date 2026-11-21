package co.com.nequi.r2dbc;

import co.com.nequi.r2dbc.entities.FranchiseEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

// TODO: This file is just an example, you should delete or modify it
public interface FranchiseRepository extends ReactiveCrudRepository<FranchiseEntity, UUID> {

}
