package com.datapath.ocds.kyrgyzstan.api.dao;

import com.datapath.ocds.kyrgyzstan.api.events.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends MongoRepository<Event, String> {

    Optional<Event> findFirstByOrderByDateDesc();

    List<Event> findByOcid(String ocid);


}
