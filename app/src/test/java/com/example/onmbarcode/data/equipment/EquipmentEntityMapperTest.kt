package com.example.onmbarcode.data.equipment

import com.example.onmbarcode.util.createEquipment
import com.example.onmbarcode.util.createEquipmentEntity
import io.mockk.clearAllMocks
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
internal class EquipmentEntityMapperTest {
    private val mapper = EquipmentEntityMapper()

    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `map EquipmentEntity to Equipment`() {
        //Act
        val mappedEquipment = mapper.map(createEquipmentEntity())

        //Assert
        assertThat(mappedEquipment).isEqualTo(createEquipment())
    }

    @Test
    fun `map Equipment to EquipmentEntity`() {
        //Act
        val mappedEquipmentEntity = mapper.mapReverse(createEquipment())

        //Assert
        assertThat(mappedEquipmentEntity).isEqualTo(createEquipmentEntity())
    }
}