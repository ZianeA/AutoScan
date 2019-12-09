package com.example.onmbarcode.data.equipment

import com.example.onmbarcode.data.mapper.Mapper
import com.example.onmbarcode.presentation.equipment.Equipment
import com.example.onmbarcode.presentation.equipment.Equipment.*
import com.example.onmbarcode.util.createEquipment
import com.example.onmbarcode.util.createEquipmentEntity
import io.mockk.*
import io.mockk.junit5.MockKExtension
import io.reactivex.Completable
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
internal class EquipmentRepositoryTest {
    private val equipmentDao: EquipmentDao = mockk()
    private val equipmentService: EquipmentService = mockk()
    private val mapper: Mapper<EquipmentEntity, Equipment> = mockk()
    private val repository = EquipmentRepository(equipmentDao, equipmentService, mapper)

    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
    }

    @Nested
    inner class UpdateEquipment() {
        @Test
        fun `update both remote and local sources`() {
            //Arrange
            val scannedAndSyncedEquipment = createEquipment(scanState = ScanState.ScannedAndSynced)
            val scannedAndSyncedEquipmentEntity =
                createEquipmentEntity(scanState = ScanState.ScannedAndSynced)
            every {
                mapper.mapReverse(createEquipment(scanState = ScanState.ScannedAndSynced))
            } returns scannedAndSyncedEquipmentEntity
            every { equipmentService.update(any()) } returns Completable.complete()
            every { equipmentDao.update(any()) } returns Completable.complete()

            //Act
            repository.updateEquipment(createEquipment(scanState = ScanState.NotScanned))
                .test()

            //Assert
            verify { equipmentService.update(scannedAndSyncedEquipment) }
            verify { equipmentDao.update(scannedAndSyncedEquipmentEntity) }
        }

        @Test
        fun `update local source when remote fails`() {
            //Arrange
            val scannedButNotSyncedEquipment =
                createEquipmentEntity(scanState = ScanState.ScannedButNotSynced)
            val scannedAndSyncedEquipment =
                createEquipmentEntity(scanState = ScanState.ScannedAndSynced)

            every {
                mapper.mapReverse(createEquipment(scanState = ScanState.ScannedButNotSynced))
            } returns scannedButNotSyncedEquipment
            every {
                mapper.mapReverse(createEquipment(scanState = ScanState.ScannedAndSynced))
            } returns scannedAndSyncedEquipment

            every { equipmentService.update(any()) } returns Completable.error(Throwable())
            every { equipmentDao.update(any()) } returns Completable.complete()

            //Act
            repository.updateEquipment(createEquipment(scanState = ScanState.NotScanned))
                .test()

            //Assert
            verify { equipmentDao.update(scannedButNotSyncedEquipment) }
            verify(exactly = 0) { equipmentDao.update(scannedAndSyncedEquipment) }
        }
    }
}