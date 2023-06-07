package com.example.logicaldetective.service;

import com.example.logicaldetective.entity.FormulaDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface FormulaService {
    List<FormulaDto> findAll ();
    FormulaDto findById( Long id);
    FormulaDto save (FormulaDto book);
    void deleteById (Long id);
}
