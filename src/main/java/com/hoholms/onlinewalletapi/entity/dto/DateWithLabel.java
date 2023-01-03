package com.hoholms.onlinewalletapi.entity.dto;

import lombok.*;
import org.springframework.security.core.Transient;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

@Transient
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DateWithLabel {
    private LocalDate date;
    private String label;

    public DateWithLabel(LocalDate date) {
        this.date = date;
        this.label = StringUtils.capitalize(date.getMonth().toString().toLowerCase()).substring(0, 3)
                + " " + date.getYear();
    }

    public DateWithLabel(String label) {
        this.label = label;

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("MMM yyyy")
                .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                .toFormatter(Locale.US);

        this.date = LocalDate.parse(label, formatter);
    }
}
