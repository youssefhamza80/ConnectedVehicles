package com.youssef.connectedvehicles.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import static lombok.AccessLevel.PRIVATE;

@Document(collection = "database_sequences")
@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = PRIVATE)
public class DatabaseSequence {

    @Id
	@NonNull
    String id;

    @NonNull
	Integer seq;
}