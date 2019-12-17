package com.datapath.ocds.kyrgyzstan.exporter.managers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventManagerFactoryImpl implements EventManagerFactory {

    @Autowired
    private CNPublicationEventManager cnPublicationEventManager;
    @Autowired
    private CNUpdatedEventManager cnUpdatedEventManager;
    @Autowired
    private CNAutoProlongedManager cnAutoProlongedManager;
    @Autowired
    private TenderCancelledManager tenderCancelledManager;
    @Autowired
    private BidsDisclosedEventManager bidsDisclosedEventManager;
    @Autowired
    private EvaluationCompletedEventManager evaluationCompletedEventManager;
    @Autowired
    private ContractRegisteredEventManager contractRegisteredEventManager;
    @Autowired
    private SupplierRefusedEventManager supplierRefusedEventManager;
    @Autowired
    private SupplierConfirmedEventManager supplierConfirmedEventManager;
    @Autowired
    private AuctionStartedEventManager auctionStartedEventManager;
    @Autowired
    private EnquiryAnsweredEventManager enquiryAnsweredEventManager;
    @Autowired
    private EnquiryReceivedEventManager enquiryReceivedEventManager;
    @Autowired
    private ComplaintReviewEventManager complaintReviewEventManager;
    @Autowired
    private ComplaintRejectedEventManager complaintRejectedEventManager;
    @Autowired
    private ComplaintResolvedEventManager complaintResolvedEventManager;
    @Autowired
    private TwoStageInvitationPublishedEventManager twoStageInvitationPublishedEventManager;
    @Autowired
    private ComplaintReceivedEventManager complaintReceivedEventManager;
    @Autowired
    private ReEvaluationEventManager reEvaluationEventManager;
    @Autowired
    private QualificationCompletedEventManager qualificationCompletedEventManager;
    @Autowired
    private Bids1StageOpenedEventManager bids1StageOpenedEventManager;

    @Override
    public EventManager getManager(int eventType) {
        switch (eventType) {
            case 0:
                return cnPublicationEventManager;
            case 1:
                return cnUpdatedEventManager;
            case 2:
                return cnAutoProlongedManager;
            case 3:
                return tenderCancelledManager;
            case 4:
                return bidsDisclosedEventManager;
            case 5:
                return evaluationCompletedEventManager;
            case 6:
                return auctionStartedEventManager;
            case 7:
                return enquiryReceivedEventManager;
            case 8:
                return enquiryAnsweredEventManager;
            case 9:
                return contractRegisteredEventManager;
            case 10:
                return reEvaluationEventManager;
            case 11:
                return bids1StageOpenedEventManager;
            case 12:
                return complaintReceivedEventManager;
            case 13:
                return complaintReviewEventManager;
            case 14:
                return complaintRejectedEventManager;
            case 15:
                return complaintResolvedEventManager;
            case 16:
                return twoStageInvitationPublishedEventManager;
            case 17:
                return qualificationCompletedEventManager;
            case 18:
                return supplierRefusedEventManager;
            case 19:
                return supplierConfirmedEventManager;
            default:
                return new NotYetRealizedEventManager();
        }
    }
}