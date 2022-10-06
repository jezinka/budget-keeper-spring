package com.example.budgetkeeperspring.liability;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiabilityLookout {
    Long id;
    Date date;
    Float outcome;
}
