package com.datapath.ocds.kyrgyzstan.exporter.dao.repository;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Integer> {
}
