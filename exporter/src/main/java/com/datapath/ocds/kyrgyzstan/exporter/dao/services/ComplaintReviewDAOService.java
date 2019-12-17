package com.datapath.ocds.kyrgyzstan.exporter.dao.services;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.ComplaintDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplaintReviewDAOService {

    public static final String COMPLAINTS_QUERY = "select\n" +
            "  oc.id                 id,\n" +
            "  oc.date_consideration review_date\n" +
            "from json_event je\n" +
            "  inner join orders o on o.id = je.order_id\n" +
            "  inner join order_complaint oc on je.object_id = oc.id\n" +
            "where je.id = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<ComplaintDAO> getComplaints(Integer jsonEventId) {
        return jdbcTemplate.query(COMPLAINTS_QUERY, new BeanPropertyRowMapper<>(ComplaintDAO.class), jsonEventId);
    }


}
