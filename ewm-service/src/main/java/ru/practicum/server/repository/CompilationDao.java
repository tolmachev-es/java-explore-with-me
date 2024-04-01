package ru.practicum.server.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.dto.compilationDtos.NewCompilationDto;
import ru.practicum.server.dto.compilationDtos.UpdateCompilationRequest;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.mappers.EventMapper;
import ru.practicum.server.repository.entities.CompilationEntity;
import ru.practicum.server.repository.entities.EventEntity;

import java.util.*;

@Component
@RequiredArgsConstructor
public class CompilationDao {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    private static Specification<CompilationEntity> hasPinned(Boolean pinned) {
        if (pinned == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("pinned"), pinned);
    }

    @Transactional
    public CompilationEntity getCompilationById(Long id) {
        Optional<CompilationEntity> compilationEntity = compilationRepository.findById(id);
        if (compilationEntity.isPresent()) {
            return compilationEntity.get();
        } else {
            throw new NotFoundException("Not found");
        }
    }

    @Transactional
    public List<CompilationEntity> getPageableCompilations(Boolean pinned, Pageable pageable) {
        Specification<CompilationEntity> specification = Specification.where(hasPinned(pinned));
        return compilationRepository.findAll(specification, pageable).toList();
    }

    @Transactional
    public CompilationEntity createCompilation(NewCompilationDto newCompilationDto) {
        CompilationEntity compilationEntity = EventMapper.EVENT_MAPPER
                .toCompilationEntityFromNewCompilationDto(newCompilationDto);
        compilationEntity.setEventEntities(getAllEvents(newCompilationDto.getEvents()));
        compilationRepository.save(compilationEntity);
        return compilationEntity;
    }

    @Transactional
    public void removeById(Long id) {
        compilationRepository.delete(getCompilationById(id));
    }

    @Transactional
    public CompilationEntity updateCompilation(UpdateCompilationRequest request, Long compilationId) {
        CompilationEntity compilationEntity = setDifferentField(
                request, compilationRepository.getReferenceById(compilationId));
        compilationRepository.save(compilationEntity);
        return compilationEntity;
    }

    private Set<EventEntity> getAllEvents(List<Long> events) {
        List<EventEntity> eventEntities = new ArrayList<>();
        if (events == null) {
            return new HashSet<>(eventEntities);
        } else {
            Specification<EventEntity> eventEntitySpecification = Specification
                    .where((root, query, criteriaBuilder) -> root.get("id").in(events));
            eventEntities = eventRepository.findAll(eventEntitySpecification);
            if (eventEntities.size() < events.size()) {
                throw new NotFoundException("NOT FOUND");
            } else {
                return new HashSet<>(eventEntities);
            }
        }

    }

    private CompilationEntity setDifferentField(UpdateCompilationRequest request, CompilationEntity compilation) {
        if (request.getPinned() != null) {
            compilation.setPinned(request.getPinned());
        }
        if (request.getTitle() != null) {
            compilation.setTitle(request.getTitle());
        }
        if (request.getEvents() != null) {
            compilation.setEventEntities(getAllEvents(request.getEvents()));
        }
        return compilation;
    }
}
