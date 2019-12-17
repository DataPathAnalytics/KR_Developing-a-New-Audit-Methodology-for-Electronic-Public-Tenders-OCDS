package com.datapath.ocds.kyrgyzstan.exporter;

import com.datapath.ocds.kyrgyzstan.exporter.checkers.EventSequenceChecker;
import com.datapath.ocds.kyrgyzstan.exporter.checkers.NoPreQualificationEventSequenceChecker;
import com.datapath.ocds.kyrgyzstan.exporter.checkers.PreQualificationEventSequenceChecker;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import com.datapath.ocds.kyrgyzstan.exporter.dao.services.EventSequenceDAOService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class EventSequenceCheckerTest {

    @Mock
    private EventSequenceDAOService dao;
    @Mock
    private PreQualificationEventSequenceChecker preQualificationChecker;
    @Mock
    private NoPreQualificationEventSequenceChecker noPreQualificationChecker;
    @InjectMocks
    private EventSequenceChecker checker;

    private List<String> preQualificationErrors;
    private List<String> noPreQualificationErrors;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(dao.isPreQualificationOrder(1)).thenReturn(false);
        when(dao.isPreQualificationOrder(2)).thenReturn(true);

        preQualificationErrors = Collections.singletonList("pre");
        noPreQualificationErrors = Collections.singletonList("noPre");

        when(preQualificationChecker.getErrors(any())).thenReturn(preQualificationErrors);
        when(noPreQualificationChecker.getErrors(any())).thenReturn(noPreQualificationErrors);
    }

    @Test
    public void testGetErrorsFromNoPreQualificationOrder() {
        JsonEvent event = new JsonEvent();
        event.setOrderId(1);

        List<String> errors = checker.getErrors(event);
        assertEquals(errors, noPreQualificationErrors);
    }

    @Test
    public void testGetErrorsFromPreQualificationOrder() {
        JsonEvent event = new JsonEvent();
        event.setOrderId(2);
        List<String> errors = checker.getErrors(event);
        assertEquals(errors, preQualificationErrors);
    }
}