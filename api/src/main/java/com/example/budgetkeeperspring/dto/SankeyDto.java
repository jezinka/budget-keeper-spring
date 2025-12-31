package com.example.budgetkeeperspring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class SankeyDto {
    private Set<SankeyNode> nodes;
    private List<SankeyLink> links;

    public SankeyDto() {
        this.nodes = new HashSet<>();
        this.links = new ArrayList<>();
    }

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
