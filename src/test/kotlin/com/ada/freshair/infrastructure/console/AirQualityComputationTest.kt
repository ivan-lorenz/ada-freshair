package com.ada.freshair.infrastructure.console

import arrow.core.left
import arrow.core.right
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.ada.freshair.domain.AirQualityIndex
import com.ada.freshair.domain.CityAirQualityService
import com.ada.freshair.domain.error.ApplicationError
import com.ada.freshair.infrastructure.City
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.math.BigDecimal
import java.math.RoundingMode


class AirQualityComputationTest {

    private lateinit var cityAirQualityService: CityAirQualityService
    private lateinit var airQualityComputation: AirQualityComputation

    private val standardOut = System.out
    private val outputStreamCaptor: ByteArrayOutputStream = ByteArrayOutputStream()

    @BeforeEach
    fun setUp() {
        System.setOut(PrintStream(outputStreamCaptor))
        cityAirQualityService = mock()
        airQualityComputation = AirQualityComputation(cityAirQualityService)
    }

    @AfterEach
    fun teardown() {
        System.setOut(standardOut)
    }

    @Test
    fun `should compute average air quality index`() {
        val city = "Barcelona"
        val country = "ES"
        val cities = listOf("$city,$country")
        val index = 1.50
        val expectedIndex = BigDecimal(index).setScale(2, RoundingMode.HALF_UP)
        whenever(cityAirQualityService.averageIndex(City(city, country)))
            .thenReturn(AirQualityIndex(city, index).right())

        airQualityComputation.compute(cities)

        assertThat(outputStreamCaptor.toString().trim())
            .isEqualTo("$city average air quality index forecast is $expectedIndex")
    }

    @Test
    fun `should omit output when no data`() {
        val city = "Barcelona"
        val country = "ES"
        val cities = listOf("$city,$country")
        whenever(cityAirQualityService.averageIndex(City(city, country))).thenReturn(ApplicationError().left())

        airQualityComputation.compute(cities)

        assertThat(outputStreamCaptor.toString().trim()).isEmpty()
    }

    @Test
    fun `should omit output for cities with errors`() {
        val barcelona = "Barcelon"
        val madrid = "Madrid"
        val country = "ES"
        val cities = listOf("$barcelona,$country", "$madrid,$country")
        val index = 1.50
        val expectedIndex = BigDecimal(index).setScale(2, RoundingMode.HALF_UP)
        whenever(cityAirQualityService.averageIndex(City(barcelona, country)))
            .thenReturn(ApplicationError().left())
        whenever(cityAirQualityService.averageIndex(City(madrid, country)))
            .thenReturn(AirQualityIndex(madrid, index).right())

        airQualityComputation.compute(cities)

        assertThat(outputStreamCaptor.toString().trim())
            .isEqualTo("$madrid average air quality index forecast is $expectedIndex")
    }
}