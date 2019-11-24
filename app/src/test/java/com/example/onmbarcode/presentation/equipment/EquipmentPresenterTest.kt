package com.example.onmbarcode.presentation.equipment

import io.mockk.clearAllMocks
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
internal class EquipmentPresenterTest {
    private val equipmentRepository: EquipmentRepository = mockk()
    private val view: EquipmentView = mockk()
    private lateinit var presenter: EquipmentPresenter

    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
        presenter = EquipmentPresenter(view)
    }
}