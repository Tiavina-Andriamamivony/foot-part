package org.prog3.foot.controllers;

import org.prog3.foot.models.Tansfert;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BaseController {

    @GetMapping
    public ResponseEntity<String> Greating(){
        return ResponseEntity.ok("Welcome to the official API of the FIFA!");
    }
  
}
