package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.document.AutoIncrementSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
@RequiredArgsConstructor
public class AutoIncrementService {
    private final MongoOperations mongoOperations;

    public Long getNextSequence(String sequenceName) {
        AutoIncrementSequence counter = mongoOperations.findAndModify(
                query(where("_id").is(sequenceName)),
                new Update().inc("seq", 1),
                options().returnNew(true).upsert(true),
                AutoIncrementSequence.class);

        return Objects.isNull(counter) ? 1 : counter.getSeq();
    }
}
