package com.example.onmbarcode.presentation.equipment.skeleton

import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.equipment.EquipmentHolder
import com.example.onmbarcode.presentation.util.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.item_skeleton_equipment)
abstract class SkeletonEquipmentEpoxyModel : EpoxyModelWithHolder<SkeletonEquipmentHolder>()

class SkeletonEquipmentHolder : KotlinEpoxyHolder()