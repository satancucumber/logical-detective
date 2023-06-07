package com.example.logicaldetective.map;

import com.example.logicaldetective.entity.Plot;
import com.example.logicaldetective.entity.PlotDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlotMapper {
    Plot dtoToModel(PlotDto plotDto);

    PlotDto modelToDto(Plot plot);

    List<PlotDto> toListDto(List<Plot> plots);
}
