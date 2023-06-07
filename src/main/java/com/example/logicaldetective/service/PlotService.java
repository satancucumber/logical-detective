package com.example.logicaldetective.service;

import com.example.logicaldetective.entity.LiteralDto;
import com.example.logicaldetective.entity.PlotDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

public interface PlotService {
    List<PlotDto> findAll ();
    PlotDto findById(Long id);
    PlotDto save (PlotDto plot);

    void deleteById (Long id);
}
