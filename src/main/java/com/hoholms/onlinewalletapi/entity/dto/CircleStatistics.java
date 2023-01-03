package com.hoholms.onlinewalletapi.entity.dto;

import com.hoholms.onlinewalletapi.entity.TransactionsCategory;
import lombok.*;
import org.springframework.security.core.Transient;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transient
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleStatistics {
    private List<String> categories;
    private List<BigDecimal> values;

    public CircleStatistics(Map<TransactionsCategory, BigDecimal> categoriesSumMap) {
        categories = categoriesSumMap.entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry<TransactionsCategory, BigDecimal>::getValue).reversed())
                .map(entry -> entry.getKey().getCategory())
                .collect(Collectors.toList());

        values = categoriesSumMap.entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry<TransactionsCategory, BigDecimal>::getValue).reversed())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}
