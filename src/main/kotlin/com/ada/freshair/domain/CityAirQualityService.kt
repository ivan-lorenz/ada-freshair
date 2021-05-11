package com.ada.freshair.domain

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.ada.freshair.infrastructure.City
import java.math.BigDecimal
import java.math.RoundingMode

data class AirQualityIndex(val cityName : String, private val doubleIndex: Double) {
    val index: BigDecimal = BigDecimal(doubleIndex)
        .setScale(2, RoundingMode.HALF_UP)
}

class CityAirQualityService(
    private val cityGeocodingService: CityGeoCodingService,
    private val airQualityForecastService: AirQualityForecastService
) {
   fun averageIndex(city: City): Option<AirQualityIndex> {
        val (_, _, coordinates) = cityGeocodingService.getGeoCoordinates(city) ?: return None
        val airQualityForecasts = airQualityForecastService.getAirQualityForecast(coordinates) ?: return None

        return Some(AirQualityIndex(city.name, airQualityForecasts
            .map { it.index }
            .average()))
    }

}
