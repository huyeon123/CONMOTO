package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.entity.Groups;
import com.huyeon.apiserver.model.entity.Noty;
import com.huyeon.apiserver.model.entity.NotyType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class EmitterRepositoryTest {
    @Autowired
    EmitterRepository emitterRepository;

    private final Long DEFAULT_TIMEOUT = 60L * 1000L * 60L;

    @Test
    @DisplayName("새로운 Emitter 추가")
    public void save() throws Exception {
        //given
        String userId = "user@test.com";
        String emitterId =  userId + "_" + System.currentTimeMillis();
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        //when, then
        Assertions.assertDoesNotThrow(() -> emitterRepository.save(emitterId, sseEmitter));
    }

    @Test
    @DisplayName("수신한 이벤트 캐시에 저장")
    public void saveEventCache() throws Exception {
        //given
        String userId = "user@test.com";
        String eventCacheId =  userId + "_" + System.currentTimeMillis();
        Noty noty = Noty.builder()
                .senderName("groupName or userName")
                .receivers(List.of("sub@test.com"))
                .message("그룹에 초대합니다!")
                .redirectUrl("url")
                .type(NotyType.GROUP_INVITE)
                .build();

        //when, then
        Assertions.assertDoesNotThrow(() -> emitterRepository.saveEventCache(eventCacheId, noty));
    }

    @Test
    @DisplayName("특정 회원이 접속한 모든 Emitter를 찾음")
    public void findAllEmitterStartWithByMemberId() throws Exception {
        //given
        String userId = "user@test.com";
        String emitterId1 = userId + "_" + System.currentTimeMillis();
        emitterRepository.save(emitterId1, new SseEmitter(DEFAULT_TIMEOUT));

        Thread.sleep(100);
        String emitterId2 = userId + "_" + System.currentTimeMillis();
        emitterRepository.save(emitterId2, new SseEmitter(DEFAULT_TIMEOUT));

        Thread.sleep(100);
        String emitterId3 = userId + "_" + System.currentTimeMillis();
        emitterRepository.save(emitterId3, new SseEmitter(DEFAULT_TIMEOUT));


        //when
        Map<String, SseEmitter> ActualResult = emitterRepository.findAllEmitterStartWithByMemberId(userId);

        //then
        Assertions.assertEquals(3, ActualResult.size());
    }

    @Test
    @DisplayName("특정 회원에게 수신된 이벤트를 캐시에서 모두 찾음")
    public void findAllEventCacheStartWithByMemberId() throws Exception {
        //given
        String userId = "user@test.com";
        String eventCacheId1 =  userId + "_" + System.currentTimeMillis();
        Noty notification1 = Noty.builder()
                .senderName("groupName or userName")
                .receivers(List.of("sub@test.com"))
                .message("그룹에 초대합니다!")
                .redirectUrl("url")
                .type(NotyType.GROUP_INVITE)
                .build();
        emitterRepository.saveEventCache(eventCacheId1, notification1);

        Thread.sleep(100);
        String eventCacheId2 =  userId + "_" + System.currentTimeMillis();
        Noty notification2 = Noty.builder()
                .senderName("groupName or userName")
                .receiveGroup(Groups.builder().name("testGroup").build())
                .message("그룹에 새로운 멤버가 들어왔습니다!")
                .type(NotyType.GROUP_NEW_MEMBER)
                .build();
        emitterRepository.saveEventCache(eventCacheId2, notification2);

        Thread.sleep(100);
        String eventCacheId3 =  userId + "_" + System.currentTimeMillis();
        Noty notification3 = Noty.builder()
                .senderName("groupName or userName")
                .receivers(List.of("sub@test.com"))
                .message("어서오세요!")
                .redirectUrl("url")
                .type(NotyType.DIRECT_MESSAGE)
                .build();
        emitterRepository.saveEventCache(eventCacheId3, notification3);

        //when
        Map<String, Object> ActualResult = emitterRepository.findAllEventCacheStartWithByMemberId(userId);

        //then
        Assertions.assertEquals(3, ActualResult.size());
    }

    @Test
    @DisplayName("회원의 특정 Emitter 제거")
    public void deleteById() throws Exception {
        //given
        String userId = "user@test.com";
        String emitterId =  userId + "_" + System.currentTimeMillis();
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        //when
        emitterRepository.save(emitterId, sseEmitter);
        emitterRepository.deleteById(emitterId);

        //then
        Assertions.assertEquals(0, emitterRepository.findAllEmitterStartWithByMemberId(emitterId).size());
    }

    @Test
    @DisplayName("저장된 모든 Emitter 제거")
    public void deleteAllEmitterStartWithId() throws Exception {
        //given
        String userId = "user@test.com";
        String emitterId1 = userId + "_" + System.currentTimeMillis();
        emitterRepository.save(emitterId1, new SseEmitter(DEFAULT_TIMEOUT));

        Thread.sleep(100);
        String emitterId2 = userId + "_" + System.currentTimeMillis();
        emitterRepository.save(emitterId2, new SseEmitter(DEFAULT_TIMEOUT));

        //when
        emitterRepository.deleteAllEmitterStartWithId(userId);

        //then
        Assertions.assertEquals(0, emitterRepository.findAllEmitterStartWithByMemberId(userId).size());
    }

    @Test
    @DisplayName("수신한 모든 이벤트 캐시에서 삭제")
    public void deleteAllEventCacheStartWithId() throws Exception {
        //given
        String userId = "user@test.com";
        String eventCacheId1 =  userId + "_" + System.currentTimeMillis();
        Noty notification1 = new Noty();
        emitterRepository.saveEventCache(eventCacheId1, notification1);

        Thread.sleep(100);
        String eventCacheId2 =  userId + "_" + System.currentTimeMillis();
        Noty notification2 = new Noty();
        emitterRepository.saveEventCache(eventCacheId2, notification2);

        //when
        emitterRepository.deleteAllEventCacheStartWithId(userId);

        //then
        Assertions.assertEquals(0, emitterRepository.findAllEventCacheStartWithByMemberId(userId).size());
    }
}
