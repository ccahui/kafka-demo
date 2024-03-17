package com.learnkafka.libraryeventsproducer.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record LibraryEventUpdate(
        @NotNull
        Integer libraryEventId,
        LibraryEventType libraryEventType,
        @NotNull
        @Valid
        Book book
) {
}
