package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.OrderStatus;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.*;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.CNPublicationDAOService;
import com.datapath.ocds.kyrgyzstan.exporter.events.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.datapath.ocds.kyrgyzstan.exporter.Constants.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;

@Component
@Slf4j
public class CnPublicationEventReceiver extends EventReceiverAbs {

    private static final String TENDER = "tender";
    private static final int SINGLE_SOURCE = 6;

    private CNPublicationDAOService dao;

    public CnPublicationEventReceiver(CNPublicationDAOService dao) {
        this.dao = dao;
    }

    public CNPublicationEvent receive(JsonEvent jsonEvent) {
        Order order = dao.getOrder(jsonEvent.getOrderId());
        Company company = dao.getCompany(order.getCompanyId());

        CNPublicationEvent event = new CNPublicationEvent();
        event.setDate(convert(jsonEvent.getDateCreated()));
        event.setSingleTag(TENDER);
        event.setInitiationType(TENDER);
        event.setOcid(OCID_PREFIX + jsonEvent.getOrderId());
        event.setId(event.getOcid() + DASH + jsonEvent.getId());
        event.setEventType(jsonEvent.getType());

        List<Party> parties = getParties(company, jsonEvent.getId());

        Tender tender = getTender(jsonEvent, order);
        event.setTender(tender);

        if (order.getProcurementMethod().equals(SINGLE_SOURCE)) {
            event.setRelatedProcesses(getRelatedProcesses(order.getId()));

            List<Party> allowedTenderersParties = getAllowedTenderers(order.getId());
            parties.addAll(allowedTenderersParties);
        }
        event.setParties(parties);
        event.setEventErrors(getSequenceErrors(jsonEvent));

        return event;
    }

    private List<RelatedProcess> getRelatedProcesses(Integer orderId) {
        List<RelatedProcess> result = new ArrayList<>();
        List<RelatedProcessDAO> daoProcess = dao.getRelatedProcesses(orderId);
        for (RelatedProcessDAO processDAO : daoProcess) {
            RelatedProcess process = new RelatedProcess();
            process.setIdentifier(processDAO.getIdentifier());
            process.setRelationship(singletonList(processDAO.getRelationship()));
            result.add(process);
        }
        return result.isEmpty() ? null : result;
    }

    private Tender getTender(JsonEvent jsonEvent, Order order) {
        TenderDAO tenderDAO = dao.getTender(jsonEvent.getId());

        Tender tender = new Tender();
        tender.setId(tenderDAO.getId());
        tender.setStatus("active");
        tender.setCurrentStage(OrderStatus.getStatus(tenderDAO.getCurrentStage()).getName());
        tender.setDatePublished(convert(tenderDAO.getDatePublished()));
        tender.setDate(convert(tenderDAO.getDatePublished()));
        tender.setProcurementMethod(tenderDAO.getProcurementMethod());
        tender.setProcurementMethodDetails(tenderDAO.getProcurementMethodDetails());
        tender.setProcurementMethodRationale(tenderDAO.getProcurementMethodRationale());
        tender.setSubmissionMethod(List.of(tenderDAO.getSubmissionMethod()));

        Value value = new Value();
        value.setAmount(tenderDAO.getValueAmount());
        value.setCurrency(tenderDAO.getValueCurrency());
        tender.setValue(value);

        Guarantee guarantee = new Guarantee();
        guarantee.setAmount(order.getWarrantyProvision());
        guarantee.setMonetary(order.getAllowMonetaryValue());
        tender.setGuarantee(guarantee);

        Period tenderPeriod = new Period();
        tenderPeriod.setStartDate(convert(tenderDAO.getStartDate()));
        tenderPeriod.setEndDate(convert(tenderDAO.getEndDate()));
        tender.setTenderPeriod(tenderPeriod);
        tender.setNumber(tenderDAO.getNumber());

        if (nonNull(order.getDateContest()) && nonNull(order.getDatePublished())) {
            Period enquiryPeriod = new Period();
            LocalDateTime datePublished = LocalDateTime.parse(order.getDatePublished(), DATE_TIME_FORMATTER);
            LocalDateTime dateContest = LocalDateTime.parse(order.getDateContest(), DATE_TIME_FORMATTER);

            if (dateContest.minusDays(5).isAfter(datePublished)) {
                enquiryPeriod.setStartDate(convert(order.getDatePublished()));
            }

            if (dateContest.minusDays(5).isAfter(datePublished)) {
                enquiryPeriod.setEndDate(convert(dateContest.minusDays(5).format(DATE_TIME_FORMATTER)));
            }

            tender.setEnquiryPeriod(enquiryPeriod);
        }

        tender.setQualificationRequirements(getQualificationRequirements(order.getId()));
        tender.setLots(getLots(order.getId()));
        tender.setItems(getItems(order.getId()));
        tender.setConditionOfContract(getConditionOfContract(order.getId()));
        tender.setDocuments(getDocuments(jsonEvent));
        return tender;
    }

    private List<TenderDocument> getDocuments(JsonEvent jsonEvent) {
        List<TenderDocument> result = new ArrayList<>();
        List<TenderDocumentDAO> daoDocuments = dao.getDocuments(jsonEvent.getOrderId());
        for (TenderDocumentDAO daoDocument : daoDocuments) {
            TenderDocument document = new TenderDocument();
            document.setId(daoDocument.getId());
            document.setRelatedItem(daoDocument.getRelatedItem());
            if (daoDocument.getRelatedLot() != null) {
                document.setRelatedLots(List.of(daoDocument.getRelatedLot()));
            }
            document.setDatePublished(convert(jsonEvent.getDateCreated()));
            result.add(document);
        }
        return result.isEmpty() ? null : result;
    }

    private ConditionOfContract getConditionOfContract(Integer orderId) {
        try {

            ConditionOfContractDAO daoCondition = dao.getConditionsOfContract(orderId);

            ConditionOfContract condition = new ConditionOfContract();
            condition.setId(daoCondition.getId());

            condition.setLateDeliveryRate(daoCondition.getLateDeliveryRate());
            condition.setLatePaymentRate(daoCondition.getLatePaymentRate());
            condition.setLateGuaranteeRate(daoCondition.getLateGuaranteeRate());

            condition.setGuaranteePercent(daoCondition.getGuaranteePercent());
            condition.setMaxDeductibleAmountDelivery(daoCondition.getMaxDeductibleAmountDelivery());
            condition.setMaxDeductibleAmountPayment(daoCondition.getMaxDeductibleAmountPayment());
            try {
                if (daoCondition.getMaxDeductibleAmountGuarantee() != null) {
                    condition.setMaxDeductibleAmountGuarantee(Double.parseDouble(daoCondition.getMaxDeductibleAmountGuarantee()));
                }
            } catch (NumberFormatException ex) {
                log.warn("Condition of contract has invalid MaxDeductibleAmountGuarantee value ");
            }

            condition.setHasGuarantee(daoCondition.getHasGuarantee());
            condition.setHasInsurance(daoCondition.getHasInsurance());
            condition.setHasRelatedServices(daoCondition.getHasRelatedServices());
            condition.setHasSpares(daoCondition.getHasSpares());
            condition.setHasTechnicalControl(daoCondition.getHasTechnicalControl());
            condition.setHasPrepayment(daoCondition.getHasPrepayment());
            condition.setHasAcceptancePayment(daoCondition.getHasAcceptancePayment());
            condition.setHasShipmentPayment(daoCondition.getHasShipmentPayment());
            condition.setPrepaymentPercent(daoCondition.getPrepaymentPercent());
            condition.setAcceptancePaymentPercent(daoCondition.getAcceptancePaymentPercent());
            condition.setShipmentPaymentPercent(daoCondition.getShipmentPaymentPercent());
            condition.setInsuranceType(daoCondition.getInsuranceType());
            condition.setHasArbitralTribunal(daoCondition.getHasArbitralTribunal());

            return condition;
        } catch (EmptyResultDataAccessException ex) {
            //In some cases that information absent in database. Need to discuss with Kyrgyzstan
            return null;
        }
    }

    private List<TenderItem> getItems(Integer orderId) {
        List<TenderItem> result = new ArrayList<>();

        List<TenderItemDAO> daoItems = dao.getItems(orderId);
        for (TenderItemDAO itemDAO : daoItems) {
            TenderItem item = new TenderItem();
            item.setId(itemDAO.getId());
            item.setRelatedLot(itemDAO.getRelatedLot());
            item.setQuantity(itemDAO.getQuantity());

            Classification classification = new Classification();
            classification.setId(itemDAO.getClassificationId());
            classification.setScheme("CPV");
            item.setClassification(classification);

            Unit unit = new Unit();
            unit.setId(itemDAO.getUnitId());
            unit.setName(itemDAO.getUnitName());
            Value value = new Value();
            value.setAmount(itemDAO.getUnitValueAmount());
            value.setCurrency("KGS");
            unit.setValue(value);
            item.setUnit(unit);

            result.add(item);
        }
        return result;
    }

    private List<Lot> getLots(Integer orderId) {
        List<Lot> result = new ArrayList<>();
        List<LotDAO> lotsDAO = dao.getLots(orderId);
        for (LotDAO lotDAO : lotsDAO) {
            Lot lot = new Lot();
            lot.setId(lotDAO.getId());
            lot.setStatus("active");
            lot.setNumber(lotDAO.getNumber());

            Value value = new Value();
            value.setAmount(lotDAO.getAmount());
            value.setCurrency("KGS");
            lot.setValue(value);
            result.add(lot);
        }

        return result;
    }

    private List<QualificationRequirement> getQualificationRequirements(Integer orderId) {
        List<QualificationRequirement> result = new ArrayList<>();
        List<QualificationRequirementDAO> requirementsDAO = dao.getQualificationRequirements(orderId);
        for (QualificationRequirementDAO requirementDAO : requirementsDAO) {
            QualificationRequirement requirement = new QualificationRequirement();
            requirement.setId(requirementDAO.getId());
            requirement.setType(requirementDAO.getTitle());
            result.add(requirement);
        }
        return result;
    }

    private List<Party> getParties(Company company, Integer jsonEventId) {
        ArrayList<Party> parties = new ArrayList<>();
        parties.add(getBuyer(jsonEventId, company.getId()));
        parties.add(getDepartmentParty());
        return parties;
    }

    private Party getDepartmentParty() {
        Identifier departmentIdentifier = new Identifier();
        departmentIdentifier.setId("01707201410029");
        departmentIdentifier.setScheme("KG-INN");

        Party department = new Party();
        department.setId("KG-INN-01707201410029");
        department.setRoles(singletonList("reviewBody"));
        department.setIdentifier(departmentIdentifier);
        return department;
    }

    private Party getBuyer(Integer jsonEventId, Integer companyId) {
        PartyDAO daoBuyer = dao.getBuyer(jsonEventId);

        Party buyer = new Party();
        buyer.setId("KG-INN-" + daoBuyer.getId());
        buyer.setRoles(asList("buyer", "procuringEntity"));

        Identifier identifier = new Identifier();
        identifier.setId(daoBuyer.getId());
        identifier.setScheme("KG-INN");
        identifier.setLegalName(daoBuyer.getNameEn());
        identifier.setLegalName_ru(daoBuyer.getNameRu());
        identifier.setLegalName_kg(daoBuyer.getNameKg());
        buyer.setIdentifier(identifier);

        Address address = new Address();
        address.setAteCode(daoBuyer.getAteCode());
        address.setCountryName(daoBuyer.getCountryName());
        address.setRegion(daoBuyer.getRegion());
        address.setSubRegion(daoBuyer.getSubRegion());
        address.setDistrict(daoBuyer.getDistrict());
        address.setSubDistrict(daoBuyer.getSubDistrict());
        address.setSubSubDistrict(daoBuyer.getSubSubDistrict());
        address.setLocality(daoBuyer.getLocality());
        address.setStreetAddress(daoBuyer.getStreetAddress());

        buyer.setAddress(address);

        buyer.setAdditionalContactPoints(getContactPoints(companyId));

        return buyer;
    }

    private List<Party> getAllowedTenderers(Integer orderId) {
        List<Party> result = new ArrayList<>();
        List<PartyDAO> tenderers = dao.getAllowedTenderers(orderId);
        for (PartyDAO tenderer : tenderers) {
            Party party = new Party();
            party.setId("KG-INN" + DASH + tenderer.getId());
            party.setRoles(singletonList("allowedTenderer"));

            Identifier identifier = new Identifier();
            identifier.setId(tenderer.getId());
            identifier.setScheme("KG-INN");
            identifier.setLegalName(tenderer.getNameEn());
            identifier.setLegalName_ru(tenderer.getNameRu());
            identifier.setLegalName_kg(tenderer.getNameKg());
            party.setIdentifier(identifier);

            Address address = new Address();
            address.setAteCode(tenderer.getAteCode());
            address.setCountryName(tenderer.getCountryName());
            address.setRegion(tenderer.getRegion());
            address.setSubRegion(tenderer.getSubRegion());
            address.setDistrict(tenderer.getDistrict());
            address.setSubDistrict(tenderer.getSubDistrict());
            address.setSubSubDistrict(tenderer.getSubSubDistrict());
            address.setLocality(tenderer.getLocality());
            address.setStreetAddress(tenderer.getStreetAddress());
            party.setAddress(address);
        }
        return result;
    }

    private List<ContactPoint> getContactPoints(Integer companyId) {
        ArrayList<ContactPoint> contactPoints = new ArrayList<>();

        dao.getContactPoints(companyId).forEach(daoPoint -> {
            ContactPoint point = new ContactPoint();
            point.setName(daoPoint.getName());
            point.setPhone(daoPoint.getPhone());
            point.setEmail(daoPoint.getEmail());
            point.setRole(daoPoint.getRole());
            contactPoints.add(point);
        });

        return contactPoints;
    }

}
