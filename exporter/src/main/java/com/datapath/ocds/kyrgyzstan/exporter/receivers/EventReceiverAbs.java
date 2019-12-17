package com.datapath.ocds.kyrgyzstan.exporter.receivers;

import com.datapath.ocds.kyrgyzstan.exporter.checkers.EventSequenceChecker;
import com.datapath.ocds.kyrgyzstan.exporter.dao.entity.JsonEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

public abstract class EventReceiverAbs {

    static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(DateTimeFormatter.ISO_LOCAL_TIME).toFormatter();

    private EventSequenceChecker eventChecker;

    @Autowired
    public void setEventChecker(EventSequenceChecker eventChecker) {
        this.eventChecker = eventChecker;
    }

    List<String> getSequenceErrors(JsonEvent event) {
        return eventChecker.getErrors(event);
    }
}
