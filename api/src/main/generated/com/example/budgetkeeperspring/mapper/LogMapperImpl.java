package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.LogDTO;
import com.example.budgetkeeperspring.entity.Log;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-07T14:04:53+0000",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.4.jar, environment: Java 17.0.17 (Eclipse Adoptium)"
)
@Component
public class LogMapperImpl implements LogMapper {

    private final DateTimeFormatter dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168 = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" );

    @Override
    public Log mapToEntity(LogDTO logDTO) {
        if ( logDTO == null ) {
            return null;
        }

        Log.LogBuilder log = Log.builder();

        if ( logDTO.getDate() != null ) {
            log.date( LocalDateTime.parse( logDTO.getDate(), dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168 ) );
        }
        log.id( logDTO.getId() );
        log.level( logDTO.getLevel() );
        log.message( logDTO.getMessage() );
        if ( logDTO.getDeleted() != null ) {
            log.deleted( logDTO.getDeleted() );
        }

        return log.build();
    }

    @Override
    public LogDTO mapToDto(Log log) {
        if ( log == null ) {
            return null;
        }

        LogDTO.LogDTOBuilder logDTO = LogDTO.builder();

        if ( log.getDate() != null ) {
            logDTO.date( dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168.format( log.getDate() ) );
        }
        logDTO.id( log.getId() );
        logDTO.level( log.getLevel() );
        logDTO.message( log.getMessage() );
        logDTO.deleted( log.isDeleted() );

        return logDTO.build();
    }
}
