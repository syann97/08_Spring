package org.scoula.todo.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.todo.service.TodoService;
import org.springframework.web.bind.annotation.PostMapping;


@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;

    @PostMapping("/insert")
    public String insertTodo() {
        return "redirect:/"; // 메인 페이지 리다이렉트
    }

    @PostMapping("/update")
    public String updateTodo() {
        return "redirect:/"; // 메인 페이지 리다이렉트
    }

    @PostMapping("/delete")
    public String deleteTodo() {
        return "redirect:/"; // 메인 페이지 리다이렉트
    }

}
