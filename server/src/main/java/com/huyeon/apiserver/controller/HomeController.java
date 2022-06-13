package com.huyeon.apiserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huyeon.apiserver.model.Board;
import com.huyeon.apiserver.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HomeController {

    private final BoardService boardService;

    @GetMapping("/rest")
    public String get() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        List<Board> boardList = boardService.getAll();

        String json = mapper.writeValueAsString(boardList);

        log.info("get : {}", json);

        return json;
    }

    @GetMapping("/rest/{id}")
    public String getOne(@PathVariable Long id) throws JsonProcessingException {
        Board board = boardService.getByRandom(id);

        String json = new ObjectMapper().writeValueAsString(board);

        log.info("get : {}", json);

        return json;
    }

    @PostMapping("/rest")
    public String post(@RequestBody String jsonMessage) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Board[] boardArr = mapper.readValue(jsonMessage, Board[].class);

        log.info("Post on Board : {}", jsonMessage);

        boardService.saveAll(boardArr);

        List<Board> boardList = boardService.getAll();

        String json = mapper.writeValueAsString(boardList);

        log.info("Post Complete : {}", json);

        return json;
    }

    @PutMapping("/rest")
    public String update(@RequestBody String jsonMessage) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Board[] boardArr = mapper.readValue(jsonMessage, Board[].class);

        log.info("Put on Board : {}", jsonMessage);

        boardService.saveAll(boardArr);

        List<Board> boardList = boardService.getAll();

        String json = mapper.writeValueAsString(boardList);

        log.info("Put Complete : {}", json);

        return json;
    }

    @DeleteMapping("/rest/all")
    public void deleteAll(){
        boardService.deleteAll();
        log.info("Board Table Clear");
    }

}
