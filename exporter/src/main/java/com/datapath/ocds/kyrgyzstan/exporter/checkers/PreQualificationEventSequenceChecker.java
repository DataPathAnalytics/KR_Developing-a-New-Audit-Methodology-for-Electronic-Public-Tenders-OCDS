package com.datapath.ocds.kyrgyzstan.exporter.checkers;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.EventSequenceDAOService;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

@Component
public class PreQualificationEventSequenceChecker {

    private EventSequenceDAOService dao;

    public PreQualificationEventSequenceChecker(EventSequenceDAOService dao) {
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
            case 11:
                return checkBids1StageDisclosed(event);
            case 12:
                return checkComplaintReceived(event);
            case 13:
                return checkComplaintReview(event);
            case 14:
                return checkComplaintRejected(event);
            case 15:
                return checkComplaintResolved(event);
            case 16:
                return check2StageInvitationPublished(event);
            case 17:
                return checkQualificationCompleted(event);
            case 18:
                return checkSupplierRefused(event);
            case 19:
                return checkSupplierConfirmed(event);

        }
        return new ArrayList<>(0);
    }

    private List<String> checkSupplierConfirmed(JsonEvent event) {
        List<String> errors = new ArrayList<>(0);
        List<Integer> prevEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());

        if (!prevEventTypes.contains(5)) {
            errors.add("supplierConfirmedWithoutEvaluation");
        }

        if (prevEventTypes.contains(3)) {
            errors.add("supplierConfirmedAfterCancellation");
        }
        return errors;
    }

    private List<String> checkSupplierRefused(JsonEvent event) {
        List<String> errors = new ArrayList<>(0);
        List<Integer> prevEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());

        if (!prevEventTypes.contains(5)) {
            errors.add("supplierRefusedWithoutEvaluation");
        }

        if (prevEventTypes.contains(3)) {
            errors.add("supplierRefusedAfterCancellation");
        }
        return errors;
    }

    private List<String> checkQualificationCompleted(JsonEvent event) {
        List<String> errors = new ArrayList<>(0);
        List<Integer> prevEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());

        if (!prevEventTypes.contains(11)) {
            errors.add("qualificationWithoutBids");
        }

        if (prevEventTypes.contains(3)) {
            errors.add("qualificationAfterCancellation");
        }
        return errors;
    }

    private List<String> check2StageInvitationPublished(JsonEvent event) {
        List<String> errors = new ArrayList<>(0);
        List<Integer> prevEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());

        if (!prevEventTypes.contains(17)) {
            errors.add("invitationWithoutQualification");
        }

        if (prevEventTypes.contains(3)) {
            errors.add("invitationAfterCancellation");
        }
        return errors;
    }

    private List<String> checkComplaintResolved(JsonEvent event) {
        List<String> errors = new ArrayList<>(0);
        List<Integer> prevEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getObjectId(), event.getDateCreated());

        if (!prevEventTypes.contains(13)) {
            errors.add("complaintResolveWithoutReview");
        }
        return errors;
    }

    private List<String> checkComplaintRejected(JsonEvent event) {
        List<String> errors = new ArrayList<>(0);
        List<Integer> prevEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getObjectId(), event.getDateCreated());

        if (!prevEventTypes.contains(12)) {
            errors.add("complaintReviewWithoutReceive");
        }
        return errors;
    }

    private List<String> checkComplaintReview(JsonEvent event) {
        List<String> errors = new ArrayList<>(0);
        List<Integer> prevEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getObjectId(), event.getDateCreated());

        if (!prevEventTypes.contains(12)) {
            errors.add("complaintReviewWithoutReceive");
        }
        return errors;
    }

    private List<String> checkComplaintReceived(JsonEvent event) {
        List<String> errors = new ArrayList<>(0);
        List<Integer> prevEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());

        if (!prevEventTypes.contains(0)) {
            errors.add("complaintWithoutPublication");
        }
        return errors;
    }

    private List<String> checkBids1StageDisclosed(JsonEvent event) {
        List<String> errors = new ArrayList<>(0);
        List<Integer> prevEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());

        if (!prevEventTypes.contains(0)) {
            errors.add("bidsFirstStageWithoutPublication");
        }
        if (prevEventTypes.contains(3)) {
            errors.add("bidsFirstStageAfterCancellation");
        }
        return errors;
    }

    private List<String> checkContractRegistered(JsonEvent event) {
        List<String> errors = new ArrayList<>(0);
        List<Integer> prevEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());

        if (!prevEventTypes.contains(5)) {
            errors.add("contractWithoutEvaluation");
        }
        if (prevEventTypes.contains(3)) {
            errors.add("contractAfterCancellation");
        }
        return errors;
    }

    private List<String> checkEnquiryAnswered(JsonEvent event) {
        List<String> errors = new ArrayList<>();
        List<Integer> prevEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getObjectId(), event.getDateCreated());

        if (!prevEventTypes.contains(7)) {
            errors.add("answerWithoutEnquiry");
        }
        return errors;
    }

    private List<String> checkEnquiryReceived(JsonEvent event) {
        List<String> errors = new ArrayList<>();
        List<Integer> prevEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());

        if (!prevEventTypes.contains(0)) {
            errors.add("enquiryWithoutPublication");
        }
        return errors;
    }

    private List<String> checkAuctionStarted(JsonEvent event) {
        List<String> errors = new ArrayList<>();
        List<Integer> prevEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());

        if (!prevEventTypes.contains(4)) {
            errors.add("auctionWithoutBids");
        }
        if (prevEventTypes.contains(3)) {
            errors.add("auctionAfterCancellation");
        }
        return errors;
    }

    private List<String> checkEvaluationCompleted(JsonEvent event) {
        List<String> errors = new ArrayList<>();
        List<Integer> prevEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());

        if (!prevEventTypes.contains(4)) {
            errors.add("evaluationWithoutBids");
        }
        if (prevEventTypes.contains(3)) {
            errors.add("evaluationAfterCancellation");
        }
        return errors;
    }

    private List<String> checkBidsDisclosed(JsonEvent event) {
        List<String> errors = new ArrayList<>();
        List<Integer> prevEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());

        if (!prevEventTypes.contains(16)) {
            errors.add("bidsWithoutPublication");
        }
        if (prevEventTypes.contains(3)) {
            errors.add("bidsAfterCancellation");
        }
        return errors;
    }

    private List<String> checkTenderCancelled(JsonEvent event) {
        List<String> errors = new ArrayList<>();
        List<Integer> prevEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());

        if (!prevEventTypes.contains(0)) {
            errors.add("cancellationWithoutPublication");
        }
        return errors;
    }

    private List<String> checkCNAutoProlonged(JsonEvent event) {
        List<String> errors = new ArrayList<>();
        List<Integer> previousEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getObjectId(), event.getDateCreated());

        if (!CollectionUtils.containsAny(previousEventTypes, List.of(0, 16))) {
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

    private List<String> checkCNUpdated(JsonEvent event) {
        List<String> errors = new ArrayList<>();
        List<Integer> previousEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getObjectId(), event.getDateCreated());

        if (!CollectionUtils.containsAny(previousEventTypes, List.of(0, 16))) {
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

    private List<String> checkCNPublication(JsonEvent event) {
        List<Integer> previousEventTypes = dao.getEventTypesBeforeDate(event.getOrderId(), event.getDateCreated());
        return previousEventTypes.isEmpty() ? emptyList() : List.of("publicationNotFirst");
    }

}
