package ipp.estg.cmu_2526_8230258_8230204_8230153.data.model

import com.google.gson.annotations.SerializedName

data class RecipeResponse(
    @SerializedName("results") val results: List<RecipeDto>
)

data class RecipeDto(
    val id: Int,
    val title: String,
    val image: String?,
    val nutrition: NutritionDto?
)

data class NutritionDto(
    val nutrients: List<NutrientDto>
)

data class NutrientDto(
    val name: String,
    val amount: Double,
    val unit: String
)