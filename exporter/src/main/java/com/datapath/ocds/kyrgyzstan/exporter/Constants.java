package com.datapath.ocds.kyrgyzstan.exporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

import static java.time.LocalTime.MIDNIGHT;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static java.util.Objects.nonNull;

public final class Constants {

    private static final Logger log = LoggerFactory.getLogger(Constants.class);
    public static final String OCID_PREFIX = "ocds-h7i0z4-";
    public static final String DASH = "-";

    public static final DateTimeFormatter PG_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(DateTimeFormatter.ISO_LOCAL_TIME).toFormatter();

    private static final ZoneId KG_ZONE_ID = ZoneId.of("Asia/Bishkek");

    public static String convert(String value) {
        return nonNull(value)
                ? ZonedDateTime.of(LocalDateTime.parse(value, PG_DATE_TIME_FORMATTER), KG_ZONE_ID)
                .format(ISO_OFFSET_DATE_TIME)
                : null;

    }

    public static String convertDateToDateTime(String value) {
        try {
            return nonNull(value)
                    ? ZonedDateTime.of(LocalDate.parse(value), MIDNIGHT, KG_ZONE_ID).format(ISO_OFFSET_DATE_TIME)
                    : null;
        } catch (DateTimeParseException exception) {
            log.warn("Can't parse date time - {}", value);
            return null;
        }
    }

}
