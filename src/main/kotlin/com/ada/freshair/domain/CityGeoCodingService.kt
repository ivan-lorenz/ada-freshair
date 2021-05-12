package com.ada.freshair.domain

import arrow.core.Either
import com.ada.freshair.domain.error.ApplicationError
import com.ada.freshair.infrastructure.City

data class GeoCoordinates(val lat: Double, val lon: Double)
data class CityGeoCoded(val name: String, val countryCode: String, val coordinates: GeoCoordinates)

interface CityGeoCodingService {
    fun getGeoCoordinates(city: City): Either<ApplicationError, CityGeoCoded>
}
