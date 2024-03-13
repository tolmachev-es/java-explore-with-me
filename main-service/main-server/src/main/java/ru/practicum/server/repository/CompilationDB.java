package ru.practicum.server.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompilationDB {
    private final CompilationRepository compilationRepository;
}
