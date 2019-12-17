package com.datapath.ocds.kyrgyzstan.exporter.checkers;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.EventSequenceDAOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

@Component
public class NoPreQualificationEventSequenceChecker {

    private EventSequenceDAOService dao;

    @Autowired
    public void setDao(EventSequenceDAOService dao) {
        this.dao = dao;
    }

    public List<String> getErrors(JsonEvent event) {
        switch (event.getType()) {
            case 0:
                return checkCNPublication(event);
            case 1:
                return checkCNUpdated(event);
            case 2:
                return checkCNAutoProlonged(event);
            case 3:
                return checkTenderCancelled(event);
            case 4:
                return checkBidsDisclosed(event);
            case 5:
                return checkEvaluationCompleted(event);
            case 6:
                return checkAuctionStarted(event);
            case 7:
                return checkEnquiryReceived(event);
            case 8:
                return checkEnquiryAnswered(event);
            case 9:
                return checkContractRegistered(event);
            case 12:
                return checkComplaintReceived(event);
            case 13:
                return checkComplaintReview(event);
            case 14:
                return checkComplaintRejected(event);
            case 15:
                return checkComplaintResolved(event);
            case 18:
                return checkSupplierRefused(event);
            case 19:
                return checkSupplierConfirmed(event);

        }
        return new ArrayList<>(0);
    }

    private List<String> checkSupplierConfirmed(JsonEvent event) {
        List<String> errors = new ArrayList<>(0);
        List<Integer> previousEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());
        if (!previousEventTypes.contains(5)) {
            errors.add("supplierConfirmedWithoutEvaluation");
        }
        if (previousEventTypes.contains(3)) {
            errors.add("supplierConfirmedAfterCancellation");
        }
        return errors;
    }

    private List<String> checkSupplierRefused(JsonEvent event) {
        List<String> errors = new ArrayList<>(0);
        List<Integer> previousEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());
        if (!previousEventTypes.contains(5)) {
            errors.add("supplierRefusedWithoutEvaluation");
        }
        if (previousEventTypes.contains(3)) {
            errors.add("supplierRefusedAfterCancellation");
        }
        return errors;
    }

    private List<String> checkComplaintResolved(JsonEvent event) {
        List<String> errors = new ArrayList<>(0);
        List<Integer> previousEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getObjectId(), event.getDateCreated());
        if (!previousEventTypes.contains(13)) {
            errors.add("complaintResolveWithoutReview");
        }
        return errors;
    }

    private List<String> checkComplaintRejected(JsonEvent event) {
        List<String> errors = new ArrayList<>(0);
        List<Integer> previousEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getObjectId(), event.getDateCreated());
        if (!previousEventTypes.contains(12)) {
            errors.add("complaintRejectWithoutReceive");
        }
        return errors;
    }

    private List<String> checkComplaintReview(JsonEvent event) {
        List<String> errors = new ArrayList<>(0);
        List<Integer> previousEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getObjectId(), event.getDateCreated());
        if (!previousEventTypes.contains(12)) {
            errors.add("complaintReviewWithoutReceive");
        }
        return errors;
    }

    private List<String> checkComplaintReceived(JsonEvent event) {
        List<String> errors = new ArrayList<>(0);
        List<Integer> previousEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());
        if (!previousEventTypes.contains(0)) {
            errors.add("complaintWithoutPublication");
        }
        return errors;
    }

    private List<String> checkContractRegistered(JsonEvent event) {
        List<String> errors = new ArrayList<>(0);
        List<Integer> previousEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());
        if (!previousEventTypes.contains(5)) {
            errors.add("contractWithoutEvaluation");
        }
        if (previousEventTypes.contains(3)) {
            errors.add("contractAfterCancellation");
        }
        return errors;
    }

    private List<String> checkEnquiryAnswered(JsonEvent event) {
        List<String> errors = new ArrayList<>(0);
        List<Integer> previousEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getObjectId(), event.getDateCreated());
        if (!previousEventTypes.contains(7)) {
            errors.add("answerWithoutEnquiry");
        }
        return errors;
    }

    private List<String> checkEnquiryReceived(JsonEvent event) {
        List<String> errors = new ArrayList<>(0);
        List<Integer> previousEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());
        if (!previousEventTypes.contains(0)) {
            errors.add("enquiryWithoutPublication");
        }
        return errors;
    }

    private List<String> checkAuctionStarted(JsonEvent event) {
        List<String> errors = new ArrayList<>();
        List<Integer> previousEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());

        if (!previousEventTypes.contains(4)) {
            errors.add("auctionWithoutBids");
        }

        if (previousEventTypes.contains(3)) {
            errors.add("auctionAfterCancellation");
        }

        return errors;
    }

    private List<String> checkCNPublication(JsonEvent event) {
        List<Integer> previousEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());
        if (previousEventTypes.isEmpty()) {
            return new ArrayList<>();
        } else {
            return singletonList("publicationNotFirst");
        }
    }

    private List<String> checkCNUpdated(JsonEvent event) {
        List<String> errors = new ArrayList<>();
        List<Integer> previousEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());
        if (!previousEventTypes.contains(0)) {
            errors.add("updateWithoutPublication");
        }
        if (previousEventTypes.contains(3)) {
            errors.add("updateAfterCancellation");
        }
        if (previousEventTypes.contains(4)) {
            errors.add("updateAfterBids");
        }
        return errors;
    }

    private List<String> checkCNAutoProlonged(JsonEvent event) {
        List<String> errors = new ArrayList<>();
        List<Integer> previousEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());
        if (!previousEventTypes.contains(0)) {
            errors.add("prolongationWithoutPublication");
        }
        if (previousEventTypes.contains(3)) {
            errors.add("prolongationAfterCancellation");
        }
        if (previousEventTypes.contains(4)) {
            errors.add("prolongationAfterBids");
        }
        return errors;
    }

    private List<String> checkTenderCancelled(JsonEvent event) {
        List<String> errors = new ArrayList<>(0);
        List<Integer> previousEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());
        if (!previousEventTypes.contains(0)) {
            errors.add("cancellationWithoutPublication");
        }
        return errors;
    }

    private List<String> checkBidsDisclosed(JsonEvent event) {
        List<String> errors = new ArrayList<>(0);
        List<Integer> previousEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());
        if (!previousEventTypes.contains(0)) {
            errors.add("bidsWithoutPublication");
        }
        if (previousEventTypes.contains(3)) {
            errors.add("bidsAfterCancellation");
        }
        return errors;
    }

    private List<String> checkEvaluationCompleted(JsonEvent event) {
        ArrayList<String> errors = new ArrayList<>();

        List<Integer> previousEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());

        if (!previousEventTypes.contains(4)) {
            errors.add("evaluationWithoutBids");
        }

        if (previousEventTypes.contains(3)) {
            errors.add("evaluationAfterCancellation");
        }

        return errors;
    }
}