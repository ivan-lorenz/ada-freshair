package com.ada.freshair.infrastructure.api

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.ada.freshair.domain.AirQualityForecast
import com.ada.freshair.domain.AirQualityForecastService
import com.ada.freshair.domain.GeoCoordinates
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class OMWAirQuality(val aqi: Int)
class OMWAirQualityForecast(val main: OMWAirQuality)
class OMWAirQualityForecasts(val list: List<OMWAirQualityForecast>)

class OWMAirQualityForecastService(
    private val baseUrl: URL,
    private val apiKey: String) : AirQualityForecastService {

    private var objectMapper: ObjectMapper = jacksonObjectMapper()

    init {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    override fun getAirQualityForecast(coordinates: GeoCoordinates): Option<List<AirQualityForecast>> {
        val request = HttpRequest.newBuilder()
            .uri(URL(
                baseUrl,
                "data/2.5/air_pollution/forecast?lat=${coordinates.lat}&lon=${coordinates.lon}&appid=$apiKey"
                ).toURI())
            .GET()
            .build()

        val response: HttpResponse<String> = HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString())

        val forecasts = objectMapper.readValue<OMWAirQualityForecasts>(response.body())
            .list
            .map { AirQualityForecast(it.main.aqi) }

        return if (forecasts.isEmpty()) None else Some(forecasts)
    }

}
