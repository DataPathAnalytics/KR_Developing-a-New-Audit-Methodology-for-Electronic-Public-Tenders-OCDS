package com.datapath.ocds.kyrgyzstan.exporter.dao.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventSequenceDAOService {

    private static final String PRE_QUALIFICATION_QUERY = "SELECT pre_qualification FROM orders WHERE id = ?";
    private static final String ORDER_EVENTS = "SELECT type FROM json_event WHERE order_id = ? AND date_created < ? :: timestamp without time zone";
    private static final String ORDER_OBJECT_EVENTS = "SELECT type FROM json_event WHERE order_id = ? AND object_id = ? AND date_created < ? :: timestamp without time zone";
    private JdbcTemplate jdbcTemplate;

    public EventSequenceDAOService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Boolean isPreQualificationOrder(Integer orderId) {
        return jdbcTemplate.queryForObject(PRE_QUALIFICATION_QUERY, Boolean.class, orderId);
    }

    public List<Integer> getEventTypesBeforeDate(Integer orderId, String date) {
        return jdbcTemplate.queryForList(ORDER_EVENTS, Integer.class, orderId, date);
    }

    public List<Integer> getEventTypesBeforeDate(Integer orderId, Integer objectId, String date) {
        return jdbcTemplate.queryForList(ORDER_OBJECT_EVENTS, Integer.class, orderId, objectId, date);
    }

}