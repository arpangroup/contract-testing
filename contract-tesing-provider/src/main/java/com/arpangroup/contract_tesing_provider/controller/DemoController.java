package com.arpangroup.contract_tesing_provider.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/")
public class DemoController {
    List<DemoDto> pacts = new ArrayList<>();

    @GetMapping("/")
    public String sayHello() {
        return "Hello World from provider!";
    }

    @GetMapping(value = "/pact")
    @ResponseBody
    public ResponseEntity<DemoDto> getPactData() {
//        return new DemoDto(true, "tom");
        return ResponseEntity.ok(new DemoDto(true, "tom"));
    }

    @PostMapping("/pact")
    @ResponseStatus(HttpStatus.CREATED)
    public void createPact(DemoDto pact) {
        pacts.add(pact);
    }
}
