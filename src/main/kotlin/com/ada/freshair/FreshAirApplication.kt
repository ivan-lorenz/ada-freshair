package com.ada.freshair

import com.ada.freshair.domain.AirQualityForecastService
import com.ada.freshair.domain.CityAirQualityService
import com.ada.freshair.domain.CityGeoCodingService
import com.ada.freshair.infrastructure.api.OWMAirQualityForecastService
import com.ada.freshair.infrastructure.api.OWMCityGeoCodingService
import com.ada.freshair.infrastructure.console.AirQualityComparer
import com.ada.freshair.infrastructure.console.AirQualityComputation
import java.net.URL

fun main(args: Array<String>) {

    val baseUrl = URL("http://api.openweathermap.org")
    val apiKey = System.getenv("OWM_APIKEY")

    val cityGeoCodingService: CityGeoCodingService = OWMCityGeoCodingService(baseUrl, apiKey)
    val airQualityForecastService: AirQualityForecastService = OWMAirQualityForecastService(baseUrl, apiKey)
    val cityAirQualityService = CityAirQualityService(cityGeoCodingService, airQualityForecastService)
    val airQualityComputation = AirQualityComputation(cityAirQualityService)
    val airQualityComparer = AirQualityComparer(cityAirQualityService)

    val arguments = args.fold(Pair(emptyMap<String, List<String>>(), "")) { (map, lastKey), elem ->
        if (elem.startsWith("-"))  Pair(map + (elem to emptyList()), elem)
        else Pair(map + (lastKey to map.getOrDefault(lastKey, emptyList()) + elem), lastKey)
    }.first

    for (argument in arguments.keys) {
        val cities = arguments[argument] ?: emptyList()
        when (argument) {
            "--city" -> airQualityComputation.compute(cities)
            "--compare" -> airQualityComparer.compare(cities)
        }
    }
}

