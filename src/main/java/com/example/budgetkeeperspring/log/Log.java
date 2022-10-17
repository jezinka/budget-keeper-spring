package com.example.budgetkeeperspring.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Log {

    private Long id;

    private Date date;
    private String type;
    private String message;
    private Boolean isDeleted;
}
