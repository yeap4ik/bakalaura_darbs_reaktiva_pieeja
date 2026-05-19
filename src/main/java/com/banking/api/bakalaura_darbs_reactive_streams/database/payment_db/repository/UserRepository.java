package com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.repository;

import com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {
}
