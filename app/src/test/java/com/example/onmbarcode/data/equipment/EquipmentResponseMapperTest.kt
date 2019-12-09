package com.example.onmbarcode.data.equipment

import com.example.onmbarcode.presentation.equipment.Equipment
import com.example.onmbarcode.util.createEquipment
import com.example.onmbarcode.util.createEquipmentResponse
import io.mockk.clearAllMocks
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
internal class EquipmentResponseMapperTest {
    private val mapper = EquipmentResponseMapper()

    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `map HashMap to Equipment`() {
        //Act
        val mappedEquipment = mapper.map(createEquipmentResponse())

        //Assert
        val expectedEquipment = createEquipment(scanState = Equipment.ScanState.NotScanned)
        Assertions.assertThat(mappedEquipment).isEqualTo(expectedEquipment)
    }

    @Test
    fun `map Equipment to HashMap`() {
        //Act
        val mappedEquipmentResponse = mapper.mapReverse(createEquipment())

        //Assert
        val response = createEquipmentResponse()
        val expectedEquipmentResponse = hashMapOf<Any, Any>(
            "observation" to response["observation"] as String,
            "date_de_scan" to response["date_de_scan"] as String,
            "aff" to (response["aff"] as Array<*>)[0] as Int
        )
        Assertions.assertThat(mappedEquipmentResponse).isEqualTo(expectedEquipmentResponse)
    }
}