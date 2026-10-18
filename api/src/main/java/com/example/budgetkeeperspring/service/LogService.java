package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.LogDTO;
import com.example.budgetkeeperspring.entity.Log;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.mapper.LogMapper;
import com.example.budgetkeeperspring.repository.LogRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class LogService {

    public static final String INFO = "INFO";
    public static final String WARN = "WARN";
    public static final String ERROR = "ERROR";
    // manual actions: kept out of getErrorOrLastActive, which reports the mail pipeline state
    public static final String USER = "USER";

    private final LogRepository logRepository;
    private final LogMapper logMapper;

    public LogService(LogRepository logRepository, LogMapper logMapper) {
        this.logRepository = logRepository;
        this.logMapper = logMapper;
    }

    public List<LogDTO> getAll() {
        return logRepository.findAll(Sort.by(Sort.Direction.DESC, "date"))
                .stream()
                .map(logMapper::mapToDto)
                .collect(toList());
    }

    public LogDTO getErrorOrLastActive() {
        Log log = logRepository.findFirstByDeletedIsFalseAndLevelOrderByDateDesc("ERROR");
        if (log == null) {
            log = logRepository.findFirstByDeletedIsFalseAndLevelOrderByDateDesc("INFO");
        }
        return logMapper.mapToDto(Objects.requireNonNullElseGet(log, Log::new));
    }

    public List<LogDTO> getAllActive() {
        return logRepository.findByDeletedIsFalse(Sort.by(Sort.Direction.DESC, "date"))
                .stream()
                .map(logMapper::mapToDto)
                .collect(toList());
    }

    public void deleteLog(Long id) {
        logRepository.deleteById(id);
    }

    public LogDTO getLogById(Long id) {
        return logRepository.findById(id)
                .map(logMapper::mapToDto)
                .orElseThrow(NotFoundException::new);
    }

    /**
     * One short entry in an expense's history (expenseId null = event not tied to an expense yet).
     * Best effort: never throws, so a failed history write can't fail (and redeliver) the action it describes.
     */
    public void expense(Long expenseId, String level, String message) {
        try {
            logRepository.save(Log.builder()
                    .date(LocalDateTime.now())
                    .level(level)
                    .expenseId(expenseId)
                    .message(StringUtils.abbreviate(message, 255))
                    .build());
        } catch (RuntimeException e) {
            log.warn("Could not write expense log [{}] {}: {}", expenseId, message, e.toString());
        }
    }
}
