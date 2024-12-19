package kostenko.practice7.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import kostenko.practice7.model.Fridge;

@Repository
public interface FridgeRepository extends R2dbcRepository<Fridge, Long> {
}
