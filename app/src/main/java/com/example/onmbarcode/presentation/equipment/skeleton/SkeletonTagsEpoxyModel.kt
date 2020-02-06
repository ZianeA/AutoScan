package com.example.onmbarcode.presentation.equipment.skeleton

import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.onmbarcode.R
import com.example.onmbarcode.presentation.util.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.view_skeleton_tags)
abstract class SkeletonTagsEpoxyModel : EpoxyModelWithHolder<SkeletonTagsHolder>()

class SkeletonTagsHolder : KotlinEpoxyHolder()