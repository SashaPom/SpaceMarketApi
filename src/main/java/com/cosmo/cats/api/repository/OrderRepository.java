package com.cosmo.cats.api.repository;

import com.cosmo.cats.api.repository.entity.OrderEntity;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends NaturalIdRepository<OrderEntity, UUID>{

}
