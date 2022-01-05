package com.bendal.coronavirustracker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bendal.coronavirustracker.models.Location;
import com.bendal.coronavirustracker.services.CoronaVirusDataService;

@Controller
public class HomeController {
	
	@Autowired
	CoronaVirusDataService coronaVirusDataService;
	
	@GetMapping
	public String home(Model model) {
		List<Location> allLocations = coronaVirusDataService.getLocations();
        int totalReportedCases = allLocations.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCases = allLocations.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
		model.addAttribute("Locations",allLocations);
		model.addAttribute("totalReportedCases", totalReportedCases);
		model.addAttribute("totalNewCases", totalNewCases);
		return "home.html";
		
	}

}
