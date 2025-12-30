package com.example.budgetkeeperspring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SankeyDto {
    private Set<SankeyNode> nodes;
    private List<SankeyLink> links;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SankeyLink {
        private String source;
        private String target;
        private BigDecimal value;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SankeyNode {
        String name;
    }
}
