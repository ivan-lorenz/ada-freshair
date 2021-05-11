package com.ada.freshair.infrastructure.console

import arrow.core.filterOption
import com.ada.freshair.domain.CityAirQualityService
import com.ada.freshair.infrastructure.City

class AirQualityComputation(
    private val cityAirQualityService: CityAirQualityService
) {
    fun compute(cities: List<String>) {
        cities
            .map { City.fromParameter(it) }
            .map { cityAirQualityService.averageIndex(it) }
            .filterOption()
            .forEach { println("${it.cityName} average air quality index forecast is ${it.index}") }
    }
}