package com.youssef.connectedvehicles.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import static lombok.AccessLevel.PRIVATE;

@Document(collection = "database_sequences")
@Data
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class DatabaseSequence {

    @Id
    String id;

    long seq;

}