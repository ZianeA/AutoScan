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
        //Arrange

        //Act
        val mappedEquipment = mapper.map(createEquipmentResponse())

        //Assert
        val expectedEquipment = createEquipment(scanState = Equipment.ScanState.NotScanned)
        Assertions.assertThat(mappedEquipment).isEqualTo(expectedEquipment)
    }
}