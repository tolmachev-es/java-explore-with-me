package ru.practicum.server.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.server.dto.NewCompilationDto;
import ru.practicum.server.dto.UpdateCompilationRequest;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.mappers.EventMapper;
import ru.practicum.server.repository.entities.CompilationEntity;
import ru.practicum.server.repository.entities.EventEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CompilationDB {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    private static Specification<CompilationEntity> hasPinned(Boolean pinned) {
        if (pinned == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("pinned"), pinned);
    }

    public CompilationEntity getCompilationById(Long id) {
        Optional<CompilationEntity> compilationEntity = compilationRepository.findById(id);
        if (compilationEntity.isPresent()) {
            return compilationEntity.get();
        } else {
            throw new NotFoundException("Not found");
        }
    }

    public List<CompilationEntity> getPageableCompilations(Boolean pinned, Pageable pageable) {
        Specification<CompilationEntity> specification = Specification.where(hasPinned(pinned));
        return compilationRepository.findAll(specification, pageable).toList();
    }

    public CompilationEntity createCompilation(NewCompilationDto newCompilationDto) {
        CompilationEntity compilationEntity = EventMapper.EVENT_MAPPER
                .toCompilationEntityFromNewCompilationDto(newCompilationDto);
        compilationEntity.setEventEntities(getAllEvents(newCompilationDto.getEvents()));
        compilationRepository.save(compilationEntity);
        return compilationEntity;
    }

    public void removeById(Long id) {
        compilationRepository.delete(getCompilationById(id));
    }

    public CompilationEntity updateCompilation(UpdateCompilationRequest request, Long compilationId) {
        CompilationEntity compilationEntity = setDifferentField(
                request, compilationRepository.getReferenceById(compilationId));
        compilationRepository.save(compilationEntity);
        return compilationEntity;
    }

    private List<EventEntity> getAllEvents(List<Long> events) {
        List<EventEntity> eventEntities = new ArrayList<>();
        if (events == null) {
            return eventEntities;
        } else {
            Specification<EventEntity> eventEntitySpecification = Specification
                    .where((root, query, criteriaBuilder) -> root.get("id").in(events));
            eventEntities = eventRepository.findAll(eventEntitySpecification);
            if (eventEntities.size() < events.size()) {
                throw new NotFoundException("NOT FOUND");
            } else {
                return eventEntities;
            }
        }

    }

    private CompilationEntity setDifferentField(UpdateCompilationRequest request, CompilationEntity compilation) {
        if (request.getPinned() != null) {
            compilation.setPinned(request.getPinned());
        }
        if (!request.getTitle().isBlank()) {
            compilation.setTitle(request.getTitle());
        }
        if (!request.getEvents().isEmpty()) {
            compilation.setEventEntities(getAllEvents(request.getEvents()));
        }
        return compilation;
    }
}
