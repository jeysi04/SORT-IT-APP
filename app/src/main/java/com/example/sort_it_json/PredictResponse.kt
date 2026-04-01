package com.example.sort_it_json

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class StageResult(
    val label: String,
    val probability: Double
) : Parcelable

@Parcelize
data class PredictResponse(
    val stage1: StageResult,
    val stage2: StageResult?,
    val stage3: StageResult?
) : Parcelable