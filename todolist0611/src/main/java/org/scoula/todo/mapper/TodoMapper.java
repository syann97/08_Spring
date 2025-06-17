package org.scoula.todo.mapper;

import org.apache.ibatis.annotations.Select;
import org.scoula.todo.domain.TodoDTO;

import java.util.List;

public interface TodoMapper {

    @Select("SELECT * FROM todo ORDER BY id DESC")
    List<TodoDTO> selectAll();
}
