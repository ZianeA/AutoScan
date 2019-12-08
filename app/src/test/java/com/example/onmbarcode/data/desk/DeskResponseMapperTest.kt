package com.example.onmbarcode.data.desk

import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.presentation.equipment.Equipment
import com.example.onmbarcode.util.*
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
internal class DeskResponseMapperTest {
    private val deskMapper = DeskResponseMapper()

    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `map HashMap to Desk`() {
        //Arrange
//        every { equipmentMapper.map(createEquipmentResponse()) } returns equipment

        //Act
        val mappedDesk = deskMapper.map(createDeskResponse())

        //Assert
        Assertions.assertThat(mappedDesk).isEqualTo(createDeskEntity())
    }
}