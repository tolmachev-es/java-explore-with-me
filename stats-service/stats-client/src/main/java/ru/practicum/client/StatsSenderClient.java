package ru.practicum.client;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.client.models.HitDto;
import ru.practicum.client.models.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class StatsSenderClient implements StatsClient {
    private final RestTemplate restTemplate;
    private final String serverUrl;

    @Autowired
    public StatsSenderClient(@Value("${stats-server.address}") String serverUrl, RestTemplateBuilder builder) {
        this.serverUrl = serverUrl;
        this.restTemplate = builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    @Override
    public void addNewHit(String app, HttpServletRequest request) {
        HitDto hitDto = HitDto.builder()
                .app(app)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
        restTemplate.postForEntity("/hit", hitDto, HitDto.class);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverUrl + "/stats");
        builder.queryParam("start", start.format(formatter));
        builder.queryParam("end", end.format(formatter));
        if (uris != null) {
            builder.queryParam("uris", String.join(",", uris));
        }
        if (unique != null) {
            builder.queryParam("unique", unique);
        }
        URI uri = builder.build(false).toUri();
        ViewStatsDto[] stats = restTemplate.getForObject(uri, ViewStatsDto[].class);
        if (stats != null) {
            return Arrays.asList(stats);
        } else {
            return Collections.emptyList();
        }
    }
}
