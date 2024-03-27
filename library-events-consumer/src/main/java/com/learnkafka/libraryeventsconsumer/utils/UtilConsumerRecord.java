package com.learnkafka.libraryeventsconsumer.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.libraryeventsconsumer.models.LibraryEvent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UtilConsumerRecord {
    private final ObjectMapper objectMapper;
    public LibraryEvent parseLibraryEventRecord(String json) {
        try {
            return objectMapper.readValue(json, LibraryEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
