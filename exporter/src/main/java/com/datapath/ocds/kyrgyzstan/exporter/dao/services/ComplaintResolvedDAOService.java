package com.datapath.ocds.kyrgyzstan.exporter.dao.services;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.ComplaintDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.DocumentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplaintResolvedDAOService {

    private static final String COMPLAINTS_QUERY = "SELECT\n" +
            "  oc.id,\n" +
            "  oc.date_answer response_date,\n" +
            "  oc.response\n" +
            "FROM json_event je\n" +
            "  JOIN orders o ON o.id = je.order_id\n" +
            "  JOIN order_complaint oc ON je.object_id = oc.id\n" +
            "WHERE je.id = ?";

    private static final String DOCUMENTS_QUERY = "select\n" +
            "  oca.attachment_id id\n" +
            "from json_event je\n" +
            "  join orders o on o.id = je.order_id\n" +
            "  join order_complaint oc on je.object_id = oc.id\n" +
            "  join order_complaint_answer_attachment oca on oca.order_complaint_id = oc.id\n" +
            "where je.id = ? AND oc.id = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<ComplaintDAO> getComplaints(Integer jsonEventId) {
        return jdbcTemplate.query(COMPLAINTS_QUERY, new BeanPropertyRowMapper<>(ComplaintDAO.class), jsonEventId);
    }

    public List<DocumentDAO> getDocuments(Integer jsonEventId, Integer complaintId) {
        return jdbcTemplate.query(DOCUMENTS_QUERY, new BeanPropertyRowMapper<>(DocumentDAO.class), jsonEventId, complaintId);
    }

}