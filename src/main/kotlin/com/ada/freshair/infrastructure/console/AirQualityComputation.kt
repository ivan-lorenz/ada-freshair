package com.ada.freshair.infrastructure.console

import com.ada.freshair.domain.CityAirQualityService
import com.ada.freshair.infrastructure.City

class AirQualityComputation(
    private val cityAirQualityService: CityAirQualityService
) {
    fun compute(cities: List<String>) {
        cities
            .map { City.fromParameter(it) }
            .map { Pair(it.name, cityAirQualityService.averageIndex(it)) }
            .forEach { println("${it.first} average air quality index forecast is ${it.second.index}") }
    }
}