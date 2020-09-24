package com.meteoalgerie.autoscan.data.equipment

import com.meteoalgerie.autoscan.util.createEquipmentEntity
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
internal class EquipmentRepositoryTest {
    /*private val equipmentDao: EquipmentDao = mockk()
    private val equipmentService: EquipmentService = mockk()
    private val entityMapper: Mapper<EquipmentEntity, Equipment> = mockk()
    private val responseMapper: Mapper<HashMap<*, *>, Equipment> = mockk()
    private val repository =
        EquipmentRepository(equipmentDao, equipmentService, entityMapper, responseMapper)

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
                entityMapper.mapReverse(scannedAndSyncedEquipment)
            } returns scannedAndSyncedEquipmentEntity
            every { responseMapper.mapReverse(scannedAndSyncedEquipment) } returns hashMapOf<Any, Any>()
            every { equipmentService.update(any(), any()) } returns Completable.complete()
            every { equipmentDao.update(any()) } returns Completable.complete()

            //Act
            val equipment = createEquipment(scanState = ScanState.NotScanned)
            repository.updateEquipment(equipment)
                .test()

            //Assert
            verify {
                equipmentService.update(
                    equipment.id,
                    hashMapOf<Any, Any>()
                )
            }
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
                entityMapper.mapReverse(createEquipment(scanState = ScanState.ScannedButNotSynced))
            } returns scannedButNotSyncedEquipment
            every {
                entityMapper.mapReverse(createEquipment(scanState = ScanState.ScannedAndSynced))
            } returns scannedAndSyncedEquipment
            every { responseMapper.mapReverse(any()) } returns hashMapOf<Any, Any>()

            every { equipmentService.update(any(), any()) } returns Completable.error(Throwable())
            every { equipmentDao.update(any()) } returns Completable.complete()

            //Act
            repository.updateEquipment(createEquipment(scanState = ScanState.NotScanned))
                .test()

            //Assert
            verify { equipmentDao.update(scannedButNotSyncedEquipment) }
            verify(exactly = 0) { equipmentDao.update(scannedAndSyncedEquipment) }
        }
    }*/
}