package com.arpangroup.contract_tesing_consumer.controller;

import com.arpangroup.contract_tesing_consumer.dto.DemoDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DemoController {
    List<DemoDto> pacts = new ArrayList<>();

    @GetMapping("/")
    public String sayHello() {
        return "Hello World from consumer!";
    }


}
