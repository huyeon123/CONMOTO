package com.huyeon.superspace.domain.board.repository;

import com.huyeon.superspace.domain.board.document.PostMonitor;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PopularBoardRepository extends MongoRepository<PostMonitor, Long> {
    @Aggregation(pipeline = {
            "{$match:  {groupUrl:  ?0}}",
            "{$sort: {viewDiff:  -1}}",
            "{$limit:  30}"
    })
    List<PostMonitor> findPopularPostsInViews(String groupUrl);
}
