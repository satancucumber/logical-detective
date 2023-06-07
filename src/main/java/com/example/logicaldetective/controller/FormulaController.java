package com.example.logicaldetective.controller;

import com.example.logicaldetective.entity.FormulaDto;
import com.example.logicaldetective.service.FormulaService;
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
public class FormulaController {

    FormulaService formulaService;

    @Autowired
    public FormulaController(FormulaService formulaService) {
        this.formulaService = formulaService;
    }
    @GetMapping("/formulas")
    public List<FormulaDto> allFormulas() {
        return formulaService.findAll();
    }

    @GetMapping("/formula/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FormulaDto> getFormula(@PathVariable Long id) {
        return ResponseEntity.ok().body(formulaService.findById(id));
    }

    @PostMapping("/formula")
    public ResponseEntity<FormulaDto> createFormula( @RequestBody FormulaDto formula) throws URISyntaxException {
        FormulaDto result = formulaService.save(formula);
        return ResponseEntity.created(new URI("/api/v1/formulas/" + result.getId()))
                .body(result);
    }

    @PutMapping("/formula/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FormulaDto> updateFormula( @PathVariable Long id, @RequestBody FormulaDto formula) {
        return ResponseEntity.ok().body(formulaService.save(formula));
    }

    @DeleteMapping("/formula/{id}")
    public ResponseEntity<?> deleteFormula(@PathVariable Long id) {
        formulaService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
