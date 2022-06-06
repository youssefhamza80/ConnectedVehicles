package com.youssef.connectedvehicles.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.test.context.junit4.SpringRunner;

import com.youssef.connectedvehicles.entity.DatabaseSequence;

@SpringBootTest
@RunWith(SpringRunner.class)
class SequenceGeneratorServiceTest {

	MongoOperations mongoOperationMock;

	@Autowired
	SequenceGeneratorService sequenceGeneratorService;

	@PostConstruct
	void constructMock() {
		mongoOperationMock = Mockito.mock(MongoOperations.class);
		sequenceGeneratorService.setMongoOperations(mongoOperationMock);
	}

	@Test
	void testGenerateNewSequenceExistingCounter() {
		String sequenceName = "DUMMY_SEQUENCE";

		long expectedValue = 3;

		when(mongoOperationMock.findAndModify(any(Query.class), any(UpdateDefinition.class),
				any(FindAndModifyOptions.class), any())).thenReturn(new DatabaseSequence(sequenceName, expectedValue));

		long actualValue = sequenceGeneratorService.generateSequence(sequenceName);

		assertEquals(expectedValue, actualValue);
	}

	@Test
	void testGenerateNewSequenceNullCounter() {
		String sequenceName = "DUMMY_SEQUENCE";

		long expectedValue = 1;

		when(mongoOperationMock.findAndModify(any(Query.class), any(UpdateDefinition.class),
				any(FindAndModifyOptions.class), any())).thenReturn(null);

		long actualValue = sequenceGeneratorService.generateSequence(sequenceName);

		assertEquals(expectedValue, actualValue);
	}
}
