package com.datapath.ocds.kyrgyzstan.exporter.dao.services;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.ComplaintDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.ContactPointDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.DocumentDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.PartyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplaintReceivedDAOService {

    private static final String COMPLAINTS_QUERY =
            "SELECT oc.id           AS id,\n" +
                    "       oc.date_created AS submission_date,\n" +
                    "       oc.type         AS type,\n" +
                    "       oc.subject      AS title,\n" +
                    "       oc.content      AS description,\n" +
                    "       c.inn           AS author,\n" +
                    "       cn.iso_code     AS country_code\n" +
                    "FROM json_event je\n" +
                    "       JOIN orders o ON o.id = je.order_id\n" +
                    "       JOIN order_complaint oc ON oc.order_id = o.id AND je.object_id = oc.id\n" +
                    "       JOIN company c ON c.id = oc.applicant_company_id\n" +
                    "       JOIN country cn on cn.id = c.country_id\n" +
                    "WHERE je.id = ?";

    private static final String PARTIES_QUERY = "with ate_parsed as (\n" +
            "  with recursive ate_rec(id, code, tree) as (\n" +
            "    select\n" +
            "      id,\n" +
            "      code,\n" +
            "      name_ru :: text as tree\n" +
            "    from ate\n" +
            "    where parent is null\n" +
            "    union\n" +
            "    select\n" +
            "      a.id,\n" +
            "      a.code,\n" +
            "      concat(ate_rec.tree, '|', a.name_ru) as tree\n" +
            "    from ate a\n" +
            "      join ate_rec on a.parent = ate_rec.id\n" +
            "  )\n" +
            "  select\n" +
            "    id,\n" +
            "    code,\n" +
            "    nullif(split_part(tree, '|', 1), '') as country,\n" +
            "    nullif(split_part(tree, '|', 2), '') as region,\n" +
            "    nullif(split_part(tree, '|', 3), '') as subregion,\n" +
            "    nullif(split_part(tree, '|', 4), '') as district,\n" +
            "    nullif(split_part(tree, '|', 5), '') as subdistrict,\n" +
            "    nullif(split_part(tree, '|', 6), '') as subsubdistrict,\n" +
            "    nullif(split_part(tree, '|', 7), '') as locality\n" +
            "  from ate_rec)\n" +
            "select\n" +
            "  c.inn            AS id,\n" +
            "  cn.iso_code      AS country_code,\n" +
            "  c.title_en       AS name_en,\n" +
            "  c.title_ru       AS name_ru,\n" +
            "  c.title_ky       AS name_kg,\n" +
            "  a.code           AS ate_code,\n" +
            "  cn.title_ru      AS country_name,\n" +
            "  a.region         AS region,\n" +
            "  a.subregion      AS subregion,\n" +
            "  a.district       AS district,\n" +
            "  a.subdistrict    AS subdistrict,\n" +
            "  a.subsubdistrict AS subsubdistrict,\n" +
            "  a.locality       AS locality,\n" +
            "  c.fact_address   AS street_address\n" +
            "from json_event je\n" +
            "  join orders o on o.id = je.order_id\n" +
            "  join order_complaint oc on oc.order_id = o.id and je.object_id = oc.id\n" +
            "  join company c on c.id = oc.applicant_company_id\n" +
            "  join country cn on c.country_id = cn.id\n" +
            "  left join ate_parsed a on c.ate_id = a.id\n" +
            "where je.id = ?";

    private static final String DOCUMENTS_QUERY = "select attachment_id id\n" +
            "from  order_complaint_attachment\n" +
            "where order_complaint_id  = ?";

    private static final String CONTACT_POINTS_QUERY = "SELECT\n" +
            "  p.full_name    AS name,\n" +
            "  p.mobile_phone AS phone,\n" +
            "  p.email        AS email,\n" +
            "  p.position   AS role\n" +
            "FROM json_event je\n" +
            "  JOIN orders o ON je.order_id = o.id\n" +
            "  JOIN company c ON o.company_id = c.id\n" +
            "  JOIN person p ON p.company_id = c.id\n" +
            "WHERE je.id = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<ComplaintDAO> getComplaints(Integer jsonEventId) {
        return jdbcTemplate.query(COMPLAINTS_QUERY, new BeanPropertyRowMapper<>(ComplaintDAO.class), jsonEventId);
    }

    public List<PartyDAO> getParties(Integer jsonEventId) {
        return jdbcTemplate.query(PARTIES_QUERY, new BeanPropertyRowMapper<>(PartyDAO.class), jsonEventId);
    }

    public List<DocumentDAO> getDocuments(Integer complaintId) {
        return jdbcTemplate.query(DOCUMENTS_QUERY, new BeanPropertyRowMapper<>(DocumentDAO.class), complaintId);
    }

    public List<ContactPointDAO> getContactPoints(Integer jsonEventId) {
        return jdbcTemplate.query(CONTACT_POINTS_QUERY, new BeanPropertyRowMapper<>(ContactPointDAO.class), jsonEventId);
    }

}