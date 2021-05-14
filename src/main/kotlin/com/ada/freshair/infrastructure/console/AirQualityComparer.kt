package com.ada.freshair.infrastructure.console

import arrow.core.getOrElse
import arrow.core.sequenceEither
import com.ada.freshair.domain.AirQualityIndex
import com.ada.freshair.domain.CityAirQualityService
import com.ada.freshair.infrastructure.City

class AirQualityComparer(
    private val cityAirQualityService: CityAirQualityService) {

    fun compare(cities: List<String>) {
        val airQualityIndex = cities
            .map { City.fromParameter(it) }
            .map { cityAirQualityService.averageIndex(it) }
            .sequenceEither()
            .getOrElse { listOf() }
            .maxWithOrNull(Comparator.comparing(AirQualityIndex::index))

        if (airQualityIndex != null)
            println("${airQualityIndex.cityName} has the cleaner air quality index.")
        else
            println("Cannot compare air quality due to application error.")
    }
}
