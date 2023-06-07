package com.example.logicaldetective.service.impl;


import com.example.logicaldetective.entity.Plot;
import com.example.logicaldetective.entity.PlotDto;
import com.example.logicaldetective.map.PlotMapper;
import com.example.logicaldetective.repo.PlotRepo;
import com.example.logicaldetective.service.PlotService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class PlotServiceImpl implements PlotService {
    @Autowired
    private final PlotRepo plotRepository;
    @Autowired
    private final PlotMapper plotMapper;

    public PlotServiceImpl(PlotRepo plotRepository, PlotMapper plotMapper) {
        this.plotRepository = plotRepository;
        this.plotMapper = plotMapper;
    }

    @Override
    public List<PlotDto> findAll() {
        return plotMapper.toListDto(plotRepository.findAll());
    }

    @Override
    public PlotDto findById(Long id) {
        return Optional.of(getById(id)).map(plotMapper::modelToDto).get();
    }

    @Override
    @Transactional
    public PlotDto save(PlotDto plot) {
        return plotMapper.modelToDto(plotRepository.save(
                plotMapper.dtoToModel(plot)));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        var plot = getById(id);
        plotRepository.delete(plot);
    }

    private Plot getById(Long id) {
        return plotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Plot with id: " + id + " not found"));
    }
}
