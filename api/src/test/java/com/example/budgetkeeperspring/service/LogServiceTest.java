package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.LogDTO;
import com.example.budgetkeeperspring.entity.Log;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.mapper.LogMapper;
import com.example.budgetkeeperspring.repository.LogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {

    @Mock
    private LogRepository logRepository;

    @Mock
    private LogMapper logMapper;

    @InjectMocks
    private LogService logService;

    private Log log;
    private LogDTO logDTO;

    @BeforeEach
    void setUp() {
        log = new Log();
        logDTO = new LogDTO();
    }

    @Test
    void getAll_returnsAllLogsSortedByDateDesc() {
        when(logRepository.findAll(Sort.by(Sort.Direction.DESC, "date"))).thenReturn(List.of(log));
        when(logMapper.mapToDto(log)).thenReturn(logDTO);

        List<LogDTO> result = logService.getAll();

        assertEquals(1, result.size());
        assertEquals(logDTO, result.get(0));
    }

    @Test
    void getErrorOrLastActive_returnsErrorLogIfExists() {
        when(logRepository.findFirstByDeletedIsFalseAndLevelOrderByDateDesc("ERROR")).thenReturn(log);
        when(logMapper.mapToDto(log)).thenReturn(logDTO);

        LogDTO result = logService.getErrorOrLastActive();

        assertEquals(logDTO, result);
    }

    @Test
    void getErrorOrLastActive_returnsLastActiveLogIfNoErrorLog() {
        when(logRepository.findFirstByDeletedIsFalseAndLevelOrderByDateDesc("ERROR")).thenReturn(null);
        when(logRepository.findFirstByDeletedIsFalseAndLevelOrderByDateDesc("INFO")).thenReturn(log);
        when(logMapper.mapToDto(log)).thenReturn(logDTO);

        LogDTO result = logService.getErrorOrLastActive();

        assertEquals(logDTO, result);
    }

    @Test
    void getAllActive_returnsAllActiveLogsSortedByDateDesc() {
        when(logRepository.findByDeletedIsFalse(Sort.by(Sort.Direction.DESC, "date"))).thenReturn(List.of(log));
        when(logMapper.mapToDto(log)).thenReturn(logDTO);

        List<LogDTO> result = logService.getAllActive();

        assertEquals(1, result.size());
        assertEquals(logDTO, result.get(0));
    }

    @Test
    void deleteLog_deletesLogById() {
        doNothing().when(logRepository).deleteById(1L);

        logService.deleteLog(1L);

        verify(logRepository, times(1)).deleteById(1L);
    }

    @Test
    void getLogById_returnsLogDTOIfExists() {
        when(logRepository.findById(1L)).thenReturn(Optional.of(log));
        when(logMapper.mapToDto(log)).thenReturn(logDTO);

        LogDTO result = logService.getLogById(1L);

        assertEquals(logDTO, result);
    }

    @Test
    void getLogById_throwsNotFoundExceptionIfLogDoesNotExist() {
        when(logRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> logService.getLogById(1L));
    }
}