package com.example.budgetkeeperspring.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE log SET deleted = true WHERE id=? and version = ?")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @Version
    private Integer version;

    private LocalDateTime date;
    private String level;
    private String message;
    private boolean deleted = Boolean.FALSE;
}
