package com.datapath.ocds.kyrgyzstan.exporter.dao.repository;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JsonEventRepository extends CrudRepository<JsonEvent, Integer> {

    @Query(value = "SELECT e.* FROM json_event e\n" +
            "JOIN orders o on e.order_id = o.id\n" +
            "WHERE o.pre_qualification IS NULL\n" +
            "ORDER BY e.date_created", nativeQuery = true)
    List<JsonEvent> findByOrderByDateCreated();


    @Query(value = "SELECT e.* FROM json_event e\n" +
            "                  JOIN orders o on e.order_id = o.id\n" +
            "WHERE o.pre_qualification IS NULL AND e.date_created > CAST (? as timestamp without time zone)\n" +
            "ORDER BY e.date_created", nativeQuery = true)
    List<JsonEvent> findNewEvents(String date);

    @Query(value = "SELECT EXISTS(SELECT id FROM json_event WHERE type = 4 AND order_id = ? AND date_created <= CAST (? as timestamp without time zone))", nativeQuery = true)
    boolean existBidsDisclosedEvent(Integer orderId, String date);

}
