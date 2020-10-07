package com.meteoalgerie.autoscan.equipment.skeleton

import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.meteoalgerie.autoscan.R
import com.meteoalgerie.autoscan.common.util.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.item_skeleton_equipment)
abstract class SkeletonEquipmentEpoxyModel : EpoxyModelWithHolder<SkeletonEquipmentHolder>()

class SkeletonEquipmentHolder : KotlinEpoxyHolder()