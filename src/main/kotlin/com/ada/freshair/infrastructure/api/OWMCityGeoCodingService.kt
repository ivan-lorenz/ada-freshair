package com.ada.freshair.infrastructure.api

import arrow.core.Either
import com.ada.freshair.domain.CityGeoCoded
import com.ada.freshair.domain.CityGeoCodingService
import com.ada.freshair.domain.GeoCoordinates
import com.ada.freshair.domain.error.ApplicationError
import com.ada.freshair.infrastructure.City
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers

class OMWCity(val name: String, val country: String, val lat: Double, val lon: Double)

class OWMCityGeoCodingService(
    private val baseUrl: URL,
    private val apiKey: String) : CityGeoCodingService {

    private var objectMapper: ObjectMapper = jacksonObjectMapper()

    init {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    override fun getGeoCoordinates(city: City): Either<ApplicationError, CityGeoCoded> {
        val request = HttpRequest.newBuilder()
            .uri(URL(baseUrl, "geo/1.0/direct?q=${city.name},${city.country}&limit=1&appid=${apiKey}").toURI())
            .GET()
            .build()

        val response: HttpResponse<String> = HttpClient.newHttpClient()
            .send(request, BodyHandlers.ofString())

        return Either.fromNullable(
            objectMapper.readValue<List<OMWCity>>(response.body())
            .map {
                CityGeoCoded(
                    city.name,
                    city.country,
                    GeoCoordinates(it.lat, it.lon)
                )
            }.firstOrNull()
        ).mapLeft { ApplicationError() }

    }

}
