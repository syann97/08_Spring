package org.scoula.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.todo.domain.TodoDTO;
import org.scoula.todo.service.TodoService;
import org.scoula.todo.service.TodoServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
public class HomeController {

    // TodoService 를 상속 받아 구현한 TodoServiceImpl Bean 의존성 주입(DI)
    private final TodoService todoService;

    // 메인 페이지
    @GetMapping("/")
    public String home(Model model) {

        // Spring Model 객체 : Controller : View 데이터 전달 객체
        //                    (Request Scope)

        List<TodoDTO> todos = todoService.selectAll();

        model.addAttribute("todos", todos);


        log.info("================> HomController /");
        return "index"; // View의 이름
    }
}