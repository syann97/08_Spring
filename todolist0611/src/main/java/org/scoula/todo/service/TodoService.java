package org.scoula.todo.service;

import org.scoula.todo.domain.TodoDTO;

import java.util.List;

public interface TodoService {

    List<TodoDTO> selectAll();
}
