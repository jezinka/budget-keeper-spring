package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.LogDTO;
import com.example.budgetkeeperspring.entity.Log;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.mapper.LogMapper;
import com.example.budgetkeeperspring.repository.LogRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class LogService {

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
        return logMapper.mapToDto(log);
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
}
