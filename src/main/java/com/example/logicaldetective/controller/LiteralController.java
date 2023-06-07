package com.example.logicaldetective.controller;

import com.example.logicaldetective.entity.LiteralDto;
import com.example.logicaldetective.service.LiteralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/v1")
public class LiteralController {
    LiteralService literalService;
    @Autowired
    public LiteralController(LiteralService literalService) {
        this.literalService = literalService;
    }
    @GetMapping("/literals")
    public List<LiteralDto> allLiterals() {
        return literalService.findAll();
    }

    @GetMapping("/literal/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<LiteralDto> getLiteral(@PathVariable Long id) {
        return ResponseEntity.ok().body(literalService.findById(id));
    }

    @PostMapping("/literal")
    public ResponseEntity<LiteralDto> createLiteral( @RequestBody LiteralDto literal) throws URISyntaxException {
        LiteralDto result = literalService.save(literal);
        return ResponseEntity.created(new URI("/api/v1/literals/" + result.getId()))
                .body(result);
    }

    @PutMapping("/literal/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<LiteralDto> updateLiteral( @PathVariable Long id, @RequestBody LiteralDto literal) {
        return ResponseEntity.ok().body(literalService.save(literal));
    }

    @DeleteMapping("/literal/{id}")
    public ResponseEntity<?> deleteLiteral(@PathVariable Long id) {
        literalService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
