package com.learnkafka.libraryeventsproducer.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.libraryeventsproducer.dtos.LibraryEvent;
import com.learnkafka.libraryeventsproducer.service.LibraryEventProducer;
import com.learnkafka.libraryeventsproducer.utils.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class LibraryEventsControllerUnitTest {

    @MockBean
    private LibraryEventProducer libraryEventProduce;
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;
    @Test
    void postLibraryEventCreatedSuccess() throws Exception {
        when(libraryEventProduce.sendLibraryEventAsync(isA(LibraryEvent.class))).thenReturn(null);
        var requestJson = objectMapper.writeValueAsString(TestUtil.libraryEventRecord());
        mockMvc.perform(post("/v1/libraryevent").content(requestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
    @Test
    void postLibraryEventBadRequestError() throws Exception {
        when(libraryEventProduce.sendLibraryEventAsync(isA(LibraryEvent.class))).thenReturn(null);
        var requestJson = objectMapper.writeValueAsString(TestUtil.libraryEventRecordWithInvalidBook());
        mockMvc.perform(post("/v1/libraryevent").content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}