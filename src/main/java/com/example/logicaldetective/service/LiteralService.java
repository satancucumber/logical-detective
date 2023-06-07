package com.example.logicaldetective.service;

import com.example.logicaldetective.entity.LiteralDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface LiteralService {
    List<LiteralDto> findAll ();
    LiteralDto findById( Long id);
    LiteralDto save (LiteralDto book);
    void deleteById (Long id);
}
