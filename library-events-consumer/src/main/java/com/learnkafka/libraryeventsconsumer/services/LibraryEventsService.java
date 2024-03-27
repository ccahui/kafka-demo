package com.learnkafka.libraryeventsconsumer.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.learnkafka.libraryeventsconsumer.models.LibraryEvent;
import com.learnkafka.libraryeventsconsumer.repositories.LibraryEventsRepository;
import com.learnkafka.libraryeventsconsumer.utils.UtilConsumerRecord;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class LibraryEventsService {

    private final LibraryEventsRepository libraryEventsRepository;
    private final UtilConsumerRecord util;
    public void processLibraryEvent(ConsumerRecord<Integer,String> consumerRecord) {
        LibraryEvent libraryEvent = util.parseLibraryEventRecord(consumerRecord.value());
        log.info("libraryEvent : {} ", libraryEvent);

        if(libraryEvent.getLibraryEventId()!=null && ( libraryEvent.getLibraryEventId()==999 )){
            throw new RecoverableDataAccessException("Temporary Network Issue");
        }

        switch (libraryEvent.getLibraryEventType()) {
            case NEW -> save(libraryEvent);
            case UPDATE -> {
                //validate the libraryevent
                validate(libraryEvent);
                save(libraryEvent);
            }
            default -> log.info("Invalid Library Event Type");
        }

    }

    private void validate(LibraryEvent libraryEvent) {
        if(libraryEvent.getLibraryEventId()==null){
            throw new IllegalArgumentException("Library Event Id is missing");
        }

        Optional<LibraryEvent> libraryEventOptional = libraryEventsRepository.findById(libraryEvent.getLibraryEventId());
        if(!libraryEventOptional.isPresent()){
            throw new IllegalArgumentException("Not a valid library Event");
        }
        log.info("Validation is successful for the library Event : {} ", libraryEventOptional.get());
    }

    private void save(LibraryEvent libraryEvent) {
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        libraryEventsRepository.save(libraryEvent);
        log.info("Successfully Persisted the libary Event {} ", libraryEvent);
    }
}
