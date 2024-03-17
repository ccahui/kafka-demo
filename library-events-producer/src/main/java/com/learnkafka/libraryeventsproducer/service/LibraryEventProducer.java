
package com.learnkafka.libraryeventsproducer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.learnkafka.libraryeventsproducer.dtos.LibraryEvent;
import com.learnkafka.libraryeventsproducer.dtos.LibraryEventUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
@RequiredArgsConstructor
public class LibraryEventProducer {

    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topic}")
    public String topic;

    public CompletableFuture<SendResult<Integer, String>> sendLibraryEventAsync(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.libraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        ProducerRecord<Integer, String> producerRecord = buildProducerRecord(key, value, topic);
        var completableFuture = kafkaTemplate.send(producerRecord);
        return completableFuture
                .whenComplete((sendResult, throwable) -> {
                    if (throwable != null) {
                        handleFailure(key, value, throwable);
                    } else {
                        handleSuccess(key, value, sendResult);

                    }
                });
    }

    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {
        List<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));
        return new ProducerRecord<>(topic, null, key, value, recordHeaders);
    }


    public SendResult<Integer, String> sendLibraryEventSynchronous(LibraryEventUpdate libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        Integer key = libraryEvent.libraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);
        SendResult<Integer, String> sendResult = null;
        try {
            sendResult = kafkaTemplate.sendDefault(key, value).get(1, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            log.error("ExecutionException/InterruptedException Sending the Message and the exception is {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception Sending the Message and the exception is {}", e.getMessage());
            throw e;
        }
        return sendResult;
    }

    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Error Sending the Message and the exception is {}", ex.getMessage());
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("Message Sent SuccessFully for the key : {} and the value is {} , partition is {}", key, value, result.getRecordMetadata().partition());
    }
}
