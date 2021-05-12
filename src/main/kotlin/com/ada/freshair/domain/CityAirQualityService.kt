package com.ada.freshair.domain

import arrow.core.Either
import com.ada.freshair.domain.error.ApplicationError
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
    fun averageIndex(city: City): Either<ApplicationError, AirQualityIndex> =
        cityGeocodingService.getGeoCoordinates(city)
            .flatMap { airQualityForecastService.getAirQualityForecast(it.coordinates) }
            .map {
                AirQualityIndex(city.name, it
                    .map { forecast -> forecast.index }
                    .average())
            }.toEither { ApplicationError() }
}
