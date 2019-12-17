package com.datapath.ocds.kyrgyzstan.exporter.dao.services;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.*;
import com.datapath.ocds.kyrgyzstan.exporter.dao.repository.CompanyRepository;
import com.datapath.ocds.kyrgyzstan.exporter.dao.repository.OrderRepository;
import com.datapath.ocds.kyrgyzstan.exporter.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CNPublicationDAOService {

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
            "\n" +
            "  c.inn               id,\n" +
            "  c.title_en          name_en,\n" +
            "  c.title_ru          name_ru,\n" +
            "  c.title_ky          name_kg,\n" +
            "  a.code           as ate_code,\n" +
            "  a.country        as country_name,\n" +
            "  a.region         as region,\n" +
            "  a.subregion      as subregion,\n" +
            "  a.district       as district,\n" +
            "  a.subdistrict    as subdistrict,\n" +
            "  a.subsubdistrict as subsubdistrict,\n" +
            "  a.locality       as locality,\n" +
            "  c.fact_address   as street_address\n" +
            "from json_event je\n" +
            "  join orders o on o.id = je.order_id\n" +
            "  join company c on c.id = o.company_id\n" +
            "  left join ate_parsed a on a.id = c.ate_id\n" +
            "where je.id = ?";

    private static final String ALLOWED_TENDERER_QUERY =
            "with ate_parsed as (\n" +
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
                    "  c.inn,\n" +
                    "  c.title_en,\n" +
                    "  c.title_ru,\n" +
                    "  c.title_ky,\n" +
                    "  a.code           as ate_code,\n" +
                    "  a.country        as country_name,\n" +
                    "  a.region         as region,\n" +
                    "  a.subregion      as subregion,\n" +
                    "  a.district       as district,\n" +
                    "  a.subdistrict    as subdistrict,\n" +
                    "  a.subsubdistrict as subsubdistrict,\n" +
                    "  a.locality       as locality,\n" +
                    "  c.fact_address   as street_address\n" +
                    "from json_event je\n" +
                    "  inner join orders_single_source_allowed_companies ssa on ssa.order_id = je.order_id\n" +
                    "  inner join company c on c.id = ssa.company_id\n" +
                    "  inner join ate_parsed a on c.ate_id = a.id\n" +
                    "where je.id = ?";

    private static final String RELATED_PROCESSES =
            "select case when previous_signed_order_number is not null\n" +
                    "  then (select id from orders where number=o.previous_signed_order_number limit 1 ) else null end as identifier,\n" +
                    "       'prior' as relationship\n" +
                    "from orders o WHERE length(o.previous_signed_order_number)>5\n" +
                    "                    AND o.id = ?\n" +
                    "union\n" +
                    "select ns.previous_not_signed_order_id identifier,\n" +
                    "       'unsuccessfulProcess' as relationship\n" +
                    "from orders o\n" +
                    "  join orders_previous_not_signed_orders ns on ns.order_id=o.id\n" +
                    "WHERE o.id = ?";

    private static final String QUALIFICATION_REQUIREMENTS =
            "select qr.id,q.title\n" +
                    "from qualifier_requirement qr\n" +
                    "join qualifier q on q.id=qr.qualifier_id\n" +
                    "where qr.order_id = ?";

    private static final String ITEMS =
            "select\n" +
                    "  p.id,\n" +
                    "  l.id             related_lot,\n" +
                    "  oz.original_code classification_id,\n" +
                    "  p.amount         quantity,\n" +
                    "  m.id unit_id,\n" +
                    "  m.full_name unit_name,\n" +
                    "  p.prce_for_unit unit_value_amount\n" +
                    "from lot l\n" +
                    "  join products p on p.lot_id = l.id\n" +
                    "  join okgz oz on oz.code = p.okgz_id\n" +
                    "  join measurement_unit m on p.measurement_unit_id = m.id\n" +
                    "where l.order_id = ?";

    private static final String DOCUMENTS =
            "select att.id,null::integer related_lot,null::integer related_item\n" +
                    "  from orders o\n" +
                    "  join attachment att on (att.id = o.main_order_attachment or att.id = o.reason_attachment_id)\n" +
                    "where o.id = ?\n" +
                    "union\n" +
                    "select att.id,l.id related_lot,null related_item\n" +
                    "from lot l\n" +
                    "  join attachment att on (att.id = l.pre_qualification_attachment or att.id = l.time_table_id)\n" +
                    "where l.order_id = ?\n" +
                    "union\n" +
                    "select att.id id,null related_lot,p.id related_item\n" +
                    "from  lot l\n" +
                    "  join products p on p.lot_id=l.id\n" +
                    "   join product_attachment pa on pa.product_id=p.id\n" +
                    "   join attachment att on pa.attachment_id = att.id\n" +
                    "where l.order_id = ?";

    private static final String LOTS = "SELECT id,sum_contest AS amount, number FROM lot WHERE order_id=?";

    private static final String CONTRACT_POINTS_QUERY = "select\n" +
            "  p.full_name    as name,\n" +
            "  p.mobile_phone as phone,\n" +
            "  p.email        as email,\n" +
            "  p.position     as role\n" +
            "from company c\n" +
            "  join person p on p.company_id = c.id\n" +
            "where c.id = ?";

    private static final String TENDER_QUERY = "select\n" +
            "  o.id                              id,\n" +
            "  o.status                          current_stage,\n" +
            "  o.date_published                  date_published,\n" +
            "  o.number,\n" +
            "  'electronicSubmission' as         submission_method,\n" +
            "  case\n" +
            "  when o.procurement_method in (0, 7, 8, 9)\n" +
            "    then 'open'\n" +
            "  when o.procurement_method = 6 and o.only_previously_signed_companies = true\n" +
            "    then 'selective'\n" +
            "  when o.procurement_method = 6 and o.only_previously_signed_companies = false\n" +
            "    then 'open'\n" +
            "  when o.procurement_method = 6 and o.only_previously_signed_companies is null\n" +
            "    then 'open'\n" +
            "  end                    as         procurement_method,\n" +
            "  case\n" +
            "  when o.pre_qualification = true then 'preQualification'::text\n" +
            "  when o.procurement_method = 0 then 'egov'::text\n" +
            "  when o.procurement_method = 6 then 'singleSource'::text\n" +
            "  when o.procurement_method = 7 then 'oneStage'::text\n" +
            "  when o.procurement_method = 8 then 'simplicated'::text\n" +
            "  when o.procurement_method = 9 then 'downgrade'::text\n" +
            "  end                    as         procurement_method_details,\n" +
            "  case\n" +
            "  when o.single_source_reason = 0\n" +
            "    then 'additionalProcurement10'\n" +
            "  when o.single_source_reason = 1\n" +
            "    then 'additionalProcurement25'\n" +
            "  when o.single_source_reason = 2\n" +
            "    then 'annualProcurement'\n" +
            "  when o.single_source_reason = 3\n" +
            "    then 'forPenalSystem'\n" +
            "  when o.single_source_reason = 4\n" +
            "    then 'intellectualRights'\n" +
            "  when o.single_source_reason = 5\n" +
            "    then 'twiceUnsuccessful'\n" +
            "  when o.single_source_reason = 6\n" +
            "    then 'urgentNeed'\n" +
            "  when o.single_source_reason = 7\n" +
            "    then 'earlyElections'\n" +
            "  when o.single_source_reason = 8\n" +
            "    then 'foreignInstitutionsKR'\n" +
            "  when o.single_source_reason = 9\n" +
            "    then 'art'\n" +
            "  when o.single_source_reason = 10\n" +
            "    then 'selfReliance'\n" +
            "  end                    as         procurement_method_rationale,\n" +
            "  o.total_sum            as         value_amount,\n" +
            "  'KGS'                  as         value_currency,\n" +
            "  o.date_published       as         start_date,\n" +
            "  o.date_contest         as         end_date,\n" +
            "  cc.id                             condition_of_contract_id,\n" +
            "  cc.day_rate_delivery              late_delivery_rate,\n" +
            "  cc.day_rate_payment               late_payment_rate,\n" +
            "  cc.forfeit_day_rate               late_guarantee_rate,\n" +
            "  cc.guarantee_provision            guarantee_percent,\n" +
            "  cc.max_deductible_amount_delivery max_deductible_amount_delivery,\n" +
            "  cc.max_deductible_amount_payment  max_deductible_amount_payment,\n" +
            "  cc.max_deductible_amount          max_deductible_amount_guarantee,\n" +
            "  cc.allow_guarantee                has_guarantee,\n" +
            "  cc.allow_insurance                has_insurance,\n" +
            "  cc.allow_related_service          has_related_services,\n" +
            "  cc.allow_spares                   has_spares,\n" +
            "  cc.allow_technical_control        has_technical_control,\n" +
            "  cc.allow_advance                  has_prepayment,\n" +
            "  cc.allow_after_acceptance         has_acceptance_payment,\n" +
            "  cc.allow_after_shipment           has_shipment_payment,\n" +
            "  cc.advance                        prepayment_percent,\n" +
            "  cc.after_acceptance               acceptance_payment_percent,\n" +
            "  cc.after_shipment                 shipment_payment_percent,\n" +
            "  cc.insurance                      insurance_type,\n" +
            "  cc.court_type                     has_arbitral_tribunal\n" +
            "from json_event je\n" +
            "  inner join orders o on o.id = je.order_id\n" +
            "  inner join company c on c.id = o.company_id\n" +
            "  left join conditions_of_contract cc on cc.id = o.contract_id\n" +
            "where je.id = ?";

    private static final String CONDITION_OF_CONTRACT_QUERY = "select\n" +
            "  cc.id,\n" +
            "  cc.day_rate_delivery              late_delivery_rate,\n" +
            "  cc.day_rate_payment               late_payment_rate,\n" +
            "  cc.forfeit_day_rate               late_guarantee_rate,\n" +
            "  cc.guarantee_provision            guarantee_percent,\n" +
            "  cc.max_deductible_amount_delivery max_deductible_amount_delivery,\n" +
            "  cc.max_deductible_amount_payment  max_deductible_amount_payment,\n" +
            "  cc.max_deductible_amount          max_deductible_amount_guarantee,\n" +
            "  cc.allow_guarantee                has_guarantee,\n" +
            "  cc.allow_insurance                has_insurance,\n" +
            "  cc.allow_related_service          has_related_services,\n" +
            "  cc.allow_spares                   has_spares,\n" +
            "  cc.allow_technical_control        has_technical_control,\n" +
            "  cc.allow_advance                  has_prepayment,\n" +
            "  cc.allow_after_acceptance         has_acceptance_payment,\n" +
            "  cc.allow_after_shipment           has_shipment_payment,\n" +
            "  cc.advance                        prepayment_percent,\n" +
            "  cc.after_acceptance               acceptance_payment_percent,\n" +
            "  cc.after_shipment                 shipment_payment_percent,\n" +
            "  cc.insurance                      insurance_type,\n" +
            "  cc.court_type                     has_arbitral_tribunal\n" +
            "from conditions_of_contract cc\n" +
            "where cc.order_id = ?";

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CompanyRepository companyRepository;

    private JdbcTemplate jdbcTemplate;

    public CNPublicationDAOService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Order getOrder(Integer orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    public Company getCompany(Integer companyId) {
        return companyRepository.findById(companyId).orElseThrow(() -> new EntityNotFoundException("Company not found"));
    }

    public List<PartyDAO> getAllowedTenderers(Integer jsonEventId) {
        return jdbcTemplate.query(ALLOWED_TENDERER_QUERY, new BeanPropertyRowMapper<>(PartyDAO.class), jsonEventId);
    }

    public List<RelatedProcessDAO> getRelatedProcesses(Integer orderId) {
        return jdbcTemplate.query(RELATED_PROCESSES, new BeanPropertyRowMapper<>(RelatedProcessDAO.class), orderId, orderId);
    }

    public List<QualificationRequirementDAO> getQualificationRequirements(Integer orderId) {
        return jdbcTemplate.query(QUALIFICATION_REQUIREMENTS, new BeanPropertyRowMapper<>(QualificationRequirementDAO.class), orderId);
    }

    public List<LotDAO> getLots(Integer orderId) {
        return jdbcTemplate.query(LOTS, new BeanPropertyRowMapper<>(LotDAO.class), orderId);
    }

    public List<TenderItemDAO> getItems(Integer orderId) {
        return jdbcTemplate.query(ITEMS, new BeanPropertyRowMapper<>(TenderItemDAO.class), orderId);
    }

    public ConditionOfContractDAO getConditionsOfContract(Integer orderId) {
        return jdbcTemplate.queryForObject(CONDITION_OF_CONTRACT_QUERY, new BeanPropertyRowMapper<>(ConditionOfContractDAO.class), orderId);
    }

    public List<TenderDocumentDAO> getDocuments(Integer orderId) {
        return jdbcTemplate.query(DOCUMENTS, new BeanPropertyRowMapper<>(TenderDocumentDAO.class), orderId, orderId, orderId);
    }

    public PartyDAO getBuyer(Integer jsonEventId) {
        return jdbcTemplate.queryForObject(PARTIES_QUERY, new BeanPropertyRowMapper<>(PartyDAO.class), jsonEventId);
    }

    public List<ContactPointDAO> getContactPoints(Integer companyId) {
        return jdbcTemplate.query(CONTRACT_POINTS_QUERY, new BeanPropertyRowMapper<>(ContactPointDAO.class), companyId);
    }

    public TenderDAO getTender(Integer jsonEventId) {
        return jdbcTemplate.queryForObject(TENDER_QUERY, new BeanPropertyRowMapper<>(TenderDAO.class), jsonEventId);
    }

}