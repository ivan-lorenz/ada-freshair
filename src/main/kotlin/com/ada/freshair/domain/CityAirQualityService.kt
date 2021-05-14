package com.ada.freshair.domain

import com.ada.freshair.infrastructure.City
import java.math.BigDecimal
import java.math.RoundingMode

class AirQualityIndex(val cityName : String, doubleIndex: Double) {
    val index: BigDecimal = BigDecimal(doubleIndex)
        .setScale(2, RoundingMode.HALF_UP)
}

class CityAirQualityService(
    private val cityGeocodingService: CityGeoCodingService,
    private val airQualityForecastService: AirQualityForecastService
) {
    fun averageIndex(city: City): AirQualityIndex? {
        val (_, _, coordinates) = cityGeocodingService.getGeoCoordinates(city) ?: return null
        val airQualityForecasts = airQualityForecastService.getAirQualityForecast(coordinates)

        return AirQualityIndex(city.name, airQualityForecasts
            .map { it.index }
            .average())
    }

}
