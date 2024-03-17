package com.learnkafka.libraryeventsproducer.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.libraryeventsproducer.dtos.LibraryEvent;
import com.learnkafka.libraryeventsproducer.dtos.LibraryEventType;
import com.learnkafka.libraryeventsproducer.dtos.LibraryEventUpdate;
import com.learnkafka.libraryeventsproducer.service.LibraryEventProducer;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
public class LibraryEventsController {

    @Autowired
    LibraryEventProducer libraryEventProducer;

    @PostMapping("/v1/libraryevent")
    public ResponseEntity<?> postLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException {

        if (LibraryEventType.NEW != libraryEvent.libraryEventType()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only NEW event type is supported");
        }
        //invoke kafka producer
        libraryEventProducer.sendLibraryEventAsync(libraryEvent);
        log.info("after produce call");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    //PUT
    @PutMapping("/v1/libraryevent")
    public ResponseEntity<?> putLibraryEvent(@RequestBody @Valid LibraryEventUpdate libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        if (!LibraryEventType.UPDATE.equals(libraryEvent.libraryEventType()))  {
            log.info("Inside the if block");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only UPDATE event type is supported");
        }
        libraryEventProducer.sendLibraryEventSynchronous(libraryEvent);
        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }

}