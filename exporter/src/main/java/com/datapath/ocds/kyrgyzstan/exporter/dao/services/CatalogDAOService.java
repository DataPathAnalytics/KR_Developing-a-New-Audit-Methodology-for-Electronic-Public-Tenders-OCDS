package com.datapath.ocds.kyrgyzstan.exporter.dao.services;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.AteDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.CurrencyRateDAO;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.LegalFormDAO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogDAOService {

    private static final String ATE_QUERY = "with ate_parsed as (\n" +
            "  with recursive ate_rec(id, code, tree) as (\n" +
            "    select id, code, name_ru::text as tree from ate where parent is null\n" +
            "    union\n" +
            "    select a.id, a.code, concat(ate_rec.tree, '|', a.name_ru) as tree\n" +
            "    from ate a join ate_rec on a.parent = ate_rec.id\n" +
            "  )\n" +
            "  select id, code,\n" +
            "    nullif(split_part(tree, '|', 1), '') as country,\n" +
            "    nullif(split_part(tree, '|', 2), '') as region,\n" +
            "    nullif(split_part(tree, '|', 3), '') as subregion,\n" +
            "    nullif(split_part(tree, '|', 4), '') as district,\n" +
            "    nullif(split_part(tree, '|', 5), '') as subdistrict,\n" +
            "    nullif(split_part(tree, '|', 6), '') as subsubdistrict,\n" +
            "    nullif(split_part(tree, '|', 7), '') as locality\n" +
            "  from ate_rec)\n" +
            "select * from ate_parsed\n";

    private static final String RATES_QUERY = "SELECT\n" +
            "  r.date,\n" +
            "  r.rate_per_som AS rate,\n" +
            "  c.code,\n" +
            "  c.name_ru AS name\n" +
            "FROM currency_rate r\n" +
            "  JOIN currency c ON r.currency_ncode = c.ncode";

    private static final String LEGAL_FORMS_QUERY = "SELECT c.inn, cn.iso_code country_code, l.title_ru\n" +
            "FROM company c \n" +
            "JOIN country cn ON c.country_id = cn.id\n" +
            "JOIN legal_form l ON c.legal_form_id = l.id";

    private JdbcTemplate jdbcTemplate;

    public CatalogDAOService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AteDAO> getAtes() {
        return jdbcTemplate.query(ATE_QUERY, new BeanPropertyRowMapper<>(AteDAO.class));
    }

    public List<CurrencyRateDAO> getRates() {
        return jdbcTemplate.query(RATES_QUERY, new BeanPropertyRowMapper<>(CurrencyRateDAO.class));
    }

    public List<LegalFormDAO> getLegalForms() {
        return jdbcTemplate.query(LEGAL_FORMS_QUERY, new BeanPropertyRowMapper<>(LegalFormDAO.class));
    }

}
