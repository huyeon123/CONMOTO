package com.huyeon.apiserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huyeon.apiserver.model.Board;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
public class HomeController {

    ArrayList<Board> list = new ArrayList<>(List.of(
            new Board("제목1","내용1"),
            new Board("제목2", "내용2")
    ));

    @GetMapping("/rest")
    public String get() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(list);

        log.info("get : {}", json);

        return json;
    }

    @PostMapping("/rest")
    public String post(@RequestBody String jsonMessage) throws JsonProcessingException {
        //jsonMessage 파싱 및 Board 데이터로 매핑
        ObjectMapper mapper = new ObjectMapper();
        Board[] boardArr = mapper.readValue(jsonMessage, Board[].class);
        //list 추가
        Collections.addAll(list, boardArr);
        //list를 String으로 매핑
        String json = mapper.writeValueAsString(list);
        //로그 출력
        log.info("post : {}", json);
        //return string
        return json;
    }

    @DeleteMapping("/rest/all")
    public void deleteAll(){
        log.info("list를 비웁니다.");
        list.clear();
    }

}
