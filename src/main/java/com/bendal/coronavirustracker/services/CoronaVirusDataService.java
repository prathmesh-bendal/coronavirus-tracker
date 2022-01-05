package com.bendal.coronavirustracker.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bendal.coronavirustracker.models.Location;

@Service
public class CoronaVirusDataService {
	private static String virusDataURL="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	private List<Location> locations=new ArrayList<>();
	
	
	
	public List<Location> getLocations() {
		return locations;
	}



	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchVirusData() throws IOException, InterruptedException {
		List<Location> newLocations=new ArrayList<>();
		HttpClient client= HttpClient.newHttpClient();
		HttpRequest request= HttpRequest.newBuilder().uri(URI.create(virusDataURL)).build();
		
		HttpResponse<String> httpResponse= client.send(request, HttpResponse.BodyHandlers.ofString());
		
		//System.out.println(httpResponse.body());
		StringReader csvBodyReader = new StringReader(httpResponse.body());
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(csvBodyReader);
		
		for (CSVRecord record : records) {
			Location location=new Location();
            location.setState(record.get("Province/State"));
            location.setCountry(record.get("Country/Region"));
            int latestCases = Integer.parseInt(record.get(record.size() - 1));
            int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
            location.setLatestTotalCases(latestCases);
            location.setDiffFromPrevDay(latestCases - prevDayCases);
            
            newLocations.add(location);
            //System.out.println(location);
        }
		this.locations=newLocations;
	}

}
