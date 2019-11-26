package com.example.onmbarcode.presentation.equipment

import com.example.onmbarcode.presentation.equipment.Equipment.*
import com.example.onmbarcode.presentation.util.Clock
import com.example.onmbarcode.util.SyncSchedulerProvider
import com.example.onmbarcode.util.createDesk
import com.example.onmbarcode.util.createEquipment
import io.mockk.*
import io.mockk.junit5.MockKExtension
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
internal class EquipmentPresenterTest {
    private val equipmentRepository: EquipmentRepository = mockk()
    private val view: EquipmentView = mockk()
    private val clock: Clock = mockk()
    private lateinit var presenter: EquipmentPresenter

    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
        presenter = EquipmentPresenter(view, equipmentRepository, SyncSchedulerProvider(), clock)
    }

    @Nested
    inner class Start {
        @Test
        fun `pass list of equipments to view`() {
            //Arrange
            val desk = createDesk()
            val equipments = listOf(createEquipment())
            every { equipmentRepository.getEquipments(desk.barcode.toString()) } returns Single.just(
                equipments
            )
            every { view.displayEquipments(any()) } just runs

            //Act
            presenter.start(desk)

            //Assert
            verify { view.displayEquipments(equipments) }
        }
    }

    @Nested
    inner class OnBarcodeChange {
        @Test
        fun `return when barcode is less than 5 digits long`() {
            //Act
            presenter.onBarcodeChange("123")

            //Assert
            verify { view wasNot called }
        }

        @Test
        fun `clear input area when barcode is 5 digits long`() {
            //Arrange
            every { view.clearBarcodeInputArea() } just runs
            every { equipmentRepository.findEquipment(any()) } returns Single.just(createEquipment())

            //Act
            presenter.onBarcodeChange("12345")

            //Assert
            verify { view.clearBarcodeInputArea() }
        }

        @Test
        fun `verify if barcode is exists`() {
            //Arrange
            every { view.clearBarcodeInputArea() } just runs
            every { equipmentRepository.findEquipment(any()) } returns Single.just(createEquipment())

            //Act
            val barcode = 12345
            presenter.onBarcodeChange(barcode.toString())

            //Assert
            verify { equipmentRepository.findEquipment(barcode) }
        }

        @Test
        fun `Rearrange equipments and update UI if barcode exists`() {
            //Arrange
            val equipmentToBeScanned = createEquipment(scanState = ScanState.PendingScan)
            val randomEquipment = createEquipment(barcode = 99999)
            every { view.clearBarcodeInputArea() } just runs
            every { equipmentRepository.findEquipment(any()) } returns Single.just(
                equipmentToBeScanned
            )
            every { view.getEquipments() } returns listOf(randomEquipment, equipmentToBeScanned)
            every { view.scrollToTopAndDisplayEquipments(any()) } just runs

            //Act
            presenter.onBarcodeChange("12345")

            //Assert
            val expectedEquipments = listOf(equipmentToBeScanned, randomEquipment)
            verify { view.scrollToTopAndDisplayEquipments(expectedEquipments) }
        }

        @Test
        fun `update repository if barcode exists`() {
            //Arrange
            val equipmentToBeScanned =
                createEquipment(scanState = ScanState.NotScanned, scanDate = 101)
            val scanDate: Long = System.currentTimeMillis()
            every { view.clearBarcodeInputArea() } just runs
            every { equipmentRepository.findEquipment(any()) } returns Single.just(
                equipmentToBeScanned
            )
            every { clock.currentTimeMillis } returns scanDate
            every { view.getEquipments() } returns listOf(
                createEquipment(barcode = 99999),
                equipmentToBeScanned
            )
            every { equipmentRepository.updateEquipment(any()) } returns Completable.complete()
            every { view.scrollToTopAndDisplayEquipments(any()) } just runs
            every { view.displayEquipmentsDelayed(any(), any()) } just runs

            //Act
            presenter.onBarcodeChange("12345")

            //Assert
            val expectedEquipment = createEquipment(
                scanState = ScanState.ScannedAndSynced,
                scanDate = scanDate
            )
            verify { equipmentRepository.updateEquipment(expectedEquipment) }
        }

        //TODO UI should be updated according to result of updating the repository
        @Test
        fun `update UI when finished updating repository`() {
            //Arrange
            val equipmentToBeScanned =
                createEquipment(barcode = 99999, scanState = ScanState.NotScanned, scanDate = 505)
            val randomEquipment = createEquipment(scanDate = 909)
            val equipments = listOf(randomEquipment, equipmentToBeScanned)
            val scanDate: Long = System.currentTimeMillis()
            every { equipmentRepository.findEquipment(any()) } returns Single.just(
                equipmentToBeScanned
            )
            every { clock.currentTimeMillis } returns scanDate
            every { view.clearBarcodeInputArea() } just runs
            every { view.scrollToTopAndDisplayEquipments(any()) } just runs
            every { view.getEquipments() } returns equipments
            every { equipmentRepository.updateEquipment(any()) } returns Completable.complete()
            every { view.displayEquipmentsDelayed(any(), any()) } just runs


            //Act
            presenter.onBarcodeChange(equipmentToBeScanned.barcode.toString())

            //Assert
            val expectedEquipments = listOf(
                randomEquipment,
                equipmentToBeScanned.copy(
                    scanState = ScanState.ScannedAndSynced,
                    scanDate = scanDate
                )
            )
            verify {
                view.displayEquipmentsDelayed(
                    expectedEquipments,
                    equipmentToBeScanned.barcode
                )
            }
        }
    }
}