package com.arpangroup.contract_tesing_provider_only.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class UserController {

    @GetMapping("/getUserDetails")
    public UserResponse getUser() {
        UserResponse userResponse = new UserResponse(true, "tommma");
        return userResponse;
    }
}
