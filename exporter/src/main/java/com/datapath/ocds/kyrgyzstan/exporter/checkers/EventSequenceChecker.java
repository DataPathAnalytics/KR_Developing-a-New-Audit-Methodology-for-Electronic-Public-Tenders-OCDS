package com.datapath.ocds.kyrgyzstan.exporter.checkers;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.EventSequenceDAOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
public class EventSequenceChecker {

    private EventSequenceDAOService dao;
    private PreQualificationEventSequenceChecker preQualificationChecker;
    private NoPreQualificationEventSequenceChecker noPreQualificationChecker;

    @Autowired
    public void setDao(EventSequenceDAOService dao) {
        this.dao = dao;
    }

    @Autowired
    public void setPreQualificationChecker(PreQualificationEventSequenceChecker preQualificationChecker) {
        this.preQualificationChecker = preQualificationChecker;
    }

    @Autowired
    public void setNoPreQualificationChecker(NoPreQualificationEventSequenceChecker noPreQualificationChecker) {
        this.noPreQualificationChecker = noPreQualificationChecker;
    }

    public List<String> getErrors(JsonEvent event) {
        Boolean isPreQualificationOrder = dao.isPreQualificationOrder(event.getOrderId());
        if (nonNull(isPreQualificationOrder) && isPreQualificationOrder) {
            List<String> errors = preQualificationChecker.getErrors(event);
            return isEmpty(errors) ? null : errors;
        } else {
            List<String> errors = noPreQualificationChecker.getErrors(event);
            return errors.isEmpty() ? null : errors;
        }
    }

}
