package com.ada.freshair.infrastructure.console

import arrow.core.left
import arrow.core.right
import assertk.assertThat
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

class AirQualityComparerTest {

    private lateinit var cityAirQualityService: CityAirQualityService
    private lateinit var airQualityComparer: AirQualityComparer

    private val standardOut = System.out
    private val outputStreamCaptor: ByteArrayOutputStream = ByteArrayOutputStream()

    @BeforeEach
    fun setUp() {
        System.setOut(PrintStream(outputStreamCaptor))
        cityAirQualityService = mock()
        airQualityComparer = AirQualityComparer(cityAirQualityService)
    }

    @AfterEach
    fun teardown() {
        System.setOut(standardOut)
    }

    @Test
    fun `should compare air quality index and get best city`() {
        val country = "ES"
        val barcelona = "Barcelona"
        val barcelonaIndex = 1.50
        val madrid = "Madrid"
        val madridIndex = 1.49
        val cities = listOf("$barcelona,$country", "$madrid,$country")
        whenever(cityAirQualityService.averageIndex(City(barcelona, country)))
            .thenReturn(AirQualityIndex(barcelona, barcelonaIndex).right())
        whenever(cityAirQualityService.averageIndex(City(madrid, country)))
            .thenReturn(AirQualityIndex(madrid, madridIndex).right())

        airQualityComparer.compare(cities)

        assertThat(outputStreamCaptor.toString().trim())
            .isEqualTo("$barcelona has the cleaner air quality index.")
    }

    @Test
    fun `should fail if cannot compute air quality for a city`() {
        val country = "ES"
        val barcelona = "Barcelona"
        val barcelonaIndex = 1.50
        val madrid = "Madrid"
        val cities = listOf("$barcelona,$country", "$madrid,$country")
        whenever(cityAirQualityService.averageIndex(City(barcelona, country)))
            .thenReturn(AirQualityIndex(barcelona, barcelonaIndex).right())
        whenever(cityAirQualityService.averageIndex(City(madrid, country)))
            .thenReturn(ApplicationError().left())

        airQualityComparer.compare(cities)

        assertThat(outputStreamCaptor.toString().trim())
            .isEqualTo("Cannot compare air quality due to application error.")
    }
}