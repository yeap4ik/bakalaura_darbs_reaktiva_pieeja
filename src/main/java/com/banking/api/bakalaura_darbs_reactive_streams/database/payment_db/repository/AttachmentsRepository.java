package com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.repository;

import com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.dao.CustomAttachmentsDao;
import com.banking.api.bakalaura_darbs_reactive_streams.database.payment_db.entity.Attachments;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AttachmentsRepository extends ReactiveCrudRepository<Attachments, Long>, CustomAttachmentsDao {
}
