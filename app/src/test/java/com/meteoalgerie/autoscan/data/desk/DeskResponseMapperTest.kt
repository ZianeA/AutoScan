package com.meteoalgerie.autoscan.data.desk

import com.meteoalgerie.autoscan.util.*
import io.mockk.clearAllMocks
import io.mockk.junit5.MockKExtension
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