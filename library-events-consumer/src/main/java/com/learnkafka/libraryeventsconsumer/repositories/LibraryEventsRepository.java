package com.learnkafka.libraryeventsconsumer.repositories;


import com.learnkafka.libraryeventsconsumer.models.LibraryEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryEventsRepository extends JpaRepository<LibraryEvent,Integer> {
}
