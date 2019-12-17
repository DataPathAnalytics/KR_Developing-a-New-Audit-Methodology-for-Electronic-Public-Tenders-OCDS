package com.datapath.ocds.kyrgyzstan.exporter.checkers;

import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.EventSequenceDAOService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class PreQualificationEventSequenceCheckerTest {

    @Mock
    private EventSequenceDAOService dao;
    @InjectMocks
    private PreQualificationEventSequenceChecker checker;
    private JsonEvent event;

    @Before
    public void setUp() {
        event = new JsonEvent();
        event.setOrderId(1);
        event.setDateCreated("2018-12-12");
        event.setObjectId(2);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCNPublicationErrors() {
        event.setType(0);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(3));

        List<String> errors = checker.getErrors(event);
        assertEquals(1, errors.size());
        assertEquals("publicationNotFirst", errors.get(0));
    }

    @Test
    public void testValidCNPublication() {
        event.setType(0);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of());

        List<String> errors = checker.getErrors(event);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testCNPUpdatedErrors() {
        event.setType(1);
        when(dao.getEventTypesBeforeDate(anyInt(), anyInt(), anyString())).thenReturn(List.of(3, 4));

        List<String> errors = checker.getErrors(event);
        assertEquals(3, errors.size());
        assertEquals("updateWithoutPublication", errors.get(0));
        assertEquals("updateAfterCancellation", errors.get(1));
        assertEquals("updateAfterBids", errors.get(2));
    }

    @Test
    public void testValidCNUpdated() {
        event.setType(1);
        when(dao.getEventTypesBeforeDate(anyInt(), anyInt(), anyString())).thenReturn(List.of(0));

        List<String> errors = checker.getErrors(event);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testValidCNUpdatedWith16() {
        event.setType(1);
        when(dao.getEventTypesBeforeDate(anyInt(), anyInt(), anyString())).thenReturn(List.of(16));

        List<String> errors = checker.getErrors(event);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testCNAutoProlongedErrors() {
        event.setType(2);
        when(dao.getEventTypesBeforeDate(anyInt(), anyInt(), anyString())).thenReturn(List.of(3, 4));

        List<String> errors = checker.getErrors(event);
        assertEquals(3, errors.size());
        assertEquals("prolongationWithoutPublication", errors.get(0));
        assertEquals("prolongationAfterCancellation", errors.get(1));
        assertEquals("prolongationAfterBids", errors.get(2));
    }

    @Test
    public void testValidCNAutoProlonged() {
        event.setType(2);
        when(dao.getEventTypesBeforeDate(anyInt(), anyInt(), anyString())).thenReturn(List.of(0));

        List<String> errors = checker.getErrors(event);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testValidCNAutoProlongedWith16Event() {
        event.setType(2);
        when(dao.getEventTypesBeforeDate(anyInt(), anyInt(), anyString())).thenReturn(List.of(16));

        List<String> errors = checker.getErrors(event);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testTenderCancelledErrors() {
        event.setType(3);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(3, 4));

        List<String> errors = checker.getErrors(event);
        assertEquals(1, errors.size());
        assertEquals("cancellationWithoutPublication", errors.get(0));
    }

    @Test
    public void testValidTenderCancelled() {
        event.setType(3);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(0));

        List<String> errors = checker.getErrors(event);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testBidsDisclosedErrors() {
        event.setType(4);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(3));

        List<String> errors = checker.getErrors(event);
        assertEquals(2, errors.size());
        assertEquals("bidsWithoutPublication", errors.get(0));
        assertEquals("bidsAfterCancellation", errors.get(1));
    }

    @Test
    public void testValidBidsDisclosed() {
        event.setType(4);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(16));

        List<String> errors = checker.getErrors(event);
        assertTrue(errors.isEmpty());
    }


    @Test
    public void testEvaluationCompletedErrors() {
        event.setType(5);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(3));

        List<String> errors = checker.getErrors(event);
        assertEquals(2, errors.size());
        assertEquals("evaluationWithoutBids", errors.get(0));
        assertEquals("evaluationAfterCancellation", errors.get(1));
    }

    @Test
    public void testValidEvaluationCompleted() {
        event.setType(5);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(4));

        List<String> errors = checker.getErrors(event);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testAuctionStartedErrors() {
        event.setType(6);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(3));

        List<String> errors = checker.getErrors(event);
        assertEquals(2, errors.size());
        assertEquals("auctionWithoutBids", errors.get(0));
        assertEquals("auctionAfterCancellation", errors.get(1));
    }

    @Test
    public void testValidAuctionStarted() {
        event.setType(6);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(4));

        List<String> errors = checker.getErrors(event);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testEnquiryReceivedErrors() {
        event.setType(7);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of());

        List<String> errors = checker.getErrors(event);
        assertEquals(1, errors.size());
        assertEquals("enquiryWithoutPublication", errors.get(0));
    }

    @Test
    public void testValidEnquiryReceived() {
        event.setType(7);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(0));

        List<String> errors = checker.getErrors(event);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testEnquiryAnsweredErrors() {
        event.setType(8);
        when(dao.getEventTypesBeforeDate(anyInt(), anyInt(), anyString())).thenReturn(List.of());

        List<String> errors = checker.getErrors(event);
        assertEquals(1, errors.size());
        assertEquals("answerWithoutEnquiry", errors.get(0));
    }

    @Test
    public void testValidEnquiryAnswered() {
        event.setType(8);
        when(dao.getEventTypesBeforeDate(anyInt(), anyInt(), anyString())).thenReturn(List.of(7));

        List<String> errors = checker.getErrors(event);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testContractRegisteredErrors() {
        event.setType(9);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(3));

        List<String> errors = checker.getErrors(event);
        assertEquals(2, errors.size());
        assertEquals("contractWithoutEvaluation", errors.get(0));
        assertEquals("contractAfterCancellation", errors.get(1));
    }

    @Test
    public void testValidContractRegistered() {
        event.setType(9);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(5));

        List<String> errors = checker.getErrors(event);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testBids1StageDisclosedErrors() {
        event.setType(11);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(3));

        List<String> errors = checker.getErrors(event);
        assertEquals(2, errors.size());
        assertEquals("bidsFirstStageWithoutPublication", errors.get(0));
        assertEquals("bidsFirstStageAfterCancellation", errors.get(1));
    }

    @Test
    public void testValidBids1StageDisclosed() {
        event.setType(11);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(0));

        List<String> errors = checker.getErrors(event);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testComplaintReceivedErrors() {
        event.setType(12);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of());

        List<String> errors = checker.getErrors(event);
        assertEquals(1, errors.size());
        assertEquals("complaintWithoutPublication", errors.get(0));
    }

    @Test
    public void testValidComplaintReceived() {
        event.setType(12);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(0));

        List<String> errors = checker.getErrors(event);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testComplaintReviewErrors() {
        event.setType(13);
        when(dao.getEventTypesBeforeDate(anyInt(), anyInt(), anyString())).thenReturn(List.of());

        List<String> errors = checker.getErrors(event);
        assertEquals(1, errors.size());
        assertEquals("complaintReviewWithoutReceive", errors.get(0));
    }

    @Test
    public void testValidComplaintReview() {
        event.setType(13);
        when(dao.getEventTypesBeforeDate(anyInt(), anyInt(), anyString())).thenReturn(List.of(12));

        List<String> errors = checker.getErrors(event);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testComplaintRejectedErrors() {
        event.setType(14);
        when(dao.getEventTypesBeforeDate(anyInt(), anyInt(), anyString())).thenReturn(List.of());

        List<String> errors = checker.getErrors(event);
        assertEquals(1, errors.size());
        assertEquals("complaintReviewWithoutReceive", errors.get(0));
    }

    @Test
    public void testValidComplaintRejected() {
        event.setType(14);
        when(dao.getEventTypesBeforeDate(anyInt(), anyInt(), anyString())).thenReturn(List.of(12));

        List<String> errors = checker.getErrors(event);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testComplaintResolvedErrors() {
        event.setType(15);
        when(dao.getEventTypesBeforeDate(anyInt(), anyInt(), anyString())).thenReturn(List.of());

        List<String> errors = checker.getErrors(event);
        assertEquals(1, errors.size());
        assertEquals("complaintResolveWithoutReview", errors.get(0));
    }

    @Test
    public void testValidComplaintResolved() {
        event.setType(15);
        when(dao.getEventTypesBeforeDate(anyInt(), anyInt(), anyString())).thenReturn(List.of(13));

        List<String> errors = checker.getErrors(event);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void test2StageInvitationPublishedErrors() {
        event.setType(16);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(3));

        List<String> errors = checker.getErrors(event);
        assertEquals(2, errors.size());
        assertEquals("invitationWithoutQualification", errors.get(0));
        assertEquals("invitationAfterCancellation", errors.get(1));
    }

    @Test
    public void testValid2StageInvitationPublished() {
        event.setType(16);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(17));

        List<String> errors = checker.getErrors(event);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testQualificationCompletedErrors() {
        event.setType(17);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(3));

        List<String> errors = checker.getErrors(event);
        assertEquals(2, errors.size());
        assertEquals("qualificationWithoutBids", errors.get(0));
        assertEquals("qualificationAfterCancellation", errors.get(1));
    }

    @Test
    public void testValidQualificationCompleted() {
        event.setType(17);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(11));

        List<String> errors = checker.getErrors(event);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testSupplierRefusedErrors() {
        event.setType(18);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(3));

        List<String> errors = checker.getErrors(event);
        assertEquals(2, errors.size());
        assertEquals("supplierRefusedWithoutEvaluation", errors.get(0));
        assertEquals("supplierRefusedAfterCancellation", errors.get(1));
    }

    @Test
    public void testValidSupplierRefused() {
        event.setType(18);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(5));

        List<String> errors = checker.getErrors(event);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testSupplierConfirmedErrors() {
        event.setType(19);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(3));

        List<String> errors = checker.getErrors(event);
        assertEquals(2, errors.size());
        assertEquals("supplierConfirmedWithoutEvaluation", errors.get(0));
        assertEquals("supplierConfirmedAfterCancellation", errors.get(1));
    }

    @Test
    public void testValidSupplierConfirmed() {
        event.setType(19);
        when(dao.getEventTypesBeforeDate(anyInt(), anyString())).thenReturn(List.of(5));

        List<String> errors = checker.getErrors(event);
        assertTrue(errors.isEmpty());
    }


}