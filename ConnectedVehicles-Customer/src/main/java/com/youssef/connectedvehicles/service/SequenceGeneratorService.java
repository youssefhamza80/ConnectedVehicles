package com.youssef.connectedvehicles.service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.youssef.connectedvehicles.entity.DatabaseSequence;

@Service
@AllArgsConstructor
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SequenceGeneratorService {

	MongoOperations mongoOperations;

	public long generateSequence(String seqName) {
		DatabaseSequence counter = mongoOperations.findAndModify(
				query(where("_id").is(seqName)),
				new Update().inc("seq", 1), 
				options().returnNew(true).upsert(true), 
				DatabaseSequence.class);
		return !Objects.isNull(counter) ? counter.getSeq() : 1;
	}
}