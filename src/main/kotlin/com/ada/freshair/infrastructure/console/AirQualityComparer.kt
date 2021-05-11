package com.ada.freshair.infrastructure.console

import arrow.core.filterOption
import com.ada.freshair.domain.AirQualityIndex
import com.ada.freshair.domain.CityAirQualityService
import com.ada.freshair.infrastructure.City

class AirQualityComparer(
    private val cityAirQualityService: CityAirQualityService) {

    fun compare(cities: List<String>) {
        val airQualityIndex = cities
            .map { City.fromParameter(it) }
            .map { cityAirQualityService.averageIndex(it) }
            .filterOption()
            .maxWithOrNull(Comparator.comparing(AirQualityIndex::index))

        if (airQualityIndex != null)
            println("${airQualityIndex.cityName} has the cleaner air quality index.")
    }
}
