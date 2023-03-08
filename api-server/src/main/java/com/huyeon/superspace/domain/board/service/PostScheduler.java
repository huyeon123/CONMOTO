package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.service.PopularBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostScheduler {
    private final PopularBoardService popularBoardService;

    @Async
    @Scheduled(cron = "0 0 * * * *") //매 시각마다 배치 프로세스 시작
    public void batchProcess() {
        log.info("Start Batch Process");
        StopWatch stopWatch = new StopWatch("Batch Process");
        stopWatch.start("Update Popular Posts");

        popularBoardService.updatePopularPosts();

        log.info("End Batch Process");
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
    }
}
