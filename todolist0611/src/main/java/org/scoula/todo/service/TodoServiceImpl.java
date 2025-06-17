package org.scoula.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.annotations.Select;
import org.scoula.todo.domain.TodoDTO;
import org.scoula.todo.mapper.TodoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service    // 서비스 계층임을 명시 > Bean 등록
@RequiredArgsConstructor    // final 필드 초기화 하는 생성자 + @Autowired가 적용된 생성자
public class TodoServiceImpl implements TodoService {

    private final TodoMapper todoMapper;

    @Override
    public List<TodoDTO> selectAll() {

        // todo 테이블 모든 내용 조회 SQL 호출 -> 결과 반환
        List<TodoDTO> todos = todoMapper.selectAll();

        log.info("조회된 todo 개수 : {}", todos.size());
        return todos;
    }
}
