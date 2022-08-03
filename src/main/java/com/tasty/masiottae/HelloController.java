package com.tasty.masiottae;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HelloController {

    @GetMapping("/")
    public String hello() {
        log.error("log test");
        return "Hello World!";
    }
}
