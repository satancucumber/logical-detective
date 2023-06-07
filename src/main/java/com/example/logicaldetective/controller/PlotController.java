package com.example.logicaldetective.controller;

import com.example.logicaldetective.entity.PlotDto;
import com.example.logicaldetective.service.PlotService;
import lombok.RequiredArgsConstructor;
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
public class PlotController {
    PlotService plotService;
    @Autowired
    public PlotController(PlotService plotService) {
        this.plotService = plotService;
    }

    @GetMapping("/plots")
    public List<PlotDto> allPlots() {
        return plotService.findAll();
    }

    @GetMapping("/plot/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PlotDto> getPlot(@PathVariable Long id) {
        return ResponseEntity.ok().body(plotService.findById(id));
    }

    @PostMapping("/plot")
    public ResponseEntity<PlotDto> createPlot( @RequestBody PlotDto plot) throws URISyntaxException {
        PlotDto result = plotService.save(plot);
        return ResponseEntity.created(new URI("/api/v1/plots/" + result.getId()))
                .body(result);
    }

    @PutMapping("/plot/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PlotDto> updatePlot( @PathVariable Long id, @RequestBody PlotDto plot) {
        return ResponseEntity.ok().body(plotService.save(plot));
    }

    @DeleteMapping("/plot/{id}")
    public ResponseEntity<?> deletePlot(@PathVariable Long id) {
        plotService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
