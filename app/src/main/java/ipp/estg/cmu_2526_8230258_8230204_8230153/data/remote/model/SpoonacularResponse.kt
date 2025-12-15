package ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote.model

import com.google.gson.annotations.SerializedName

data class RecipeResponse(
    @SerializedName("results") val results: List<Recipe>
)

data class Recipe(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("image") val image: String?,
    @SerializedName("nutrition") val nutrition: Nutrition?
)

data class Nutrition(
    @SerializedName("nutrients") val nutrients: List<Nutrient>
)

data class Nutrient(
    @SerializedName("name") val name: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("unit") val unit: String
)