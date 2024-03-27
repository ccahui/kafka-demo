package com.learnkafka.libraryeventsconsumer.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class LibraryEventsConsumer {
    private final LibraryEventsService service;

    @KafkaListener(topics = "${spring.kafka.listener.topics}")
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord, Acknowledgment acknowledgment) {
        log.info("ConsumerRecord a: {} ", consumerRecord);
        // Consumiendo el mensaje y eliminandolo para no consumirlo repetidas veces
        service.processLibraryEvent(consumerRecord);
        acknowledgment.acknowledge();
    }
}
