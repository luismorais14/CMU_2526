package ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote.api

import ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote.model.RecipeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SpoonacularDataApi {

    @GET("recipes/complexSearch")
    suspend fun getRecipesByDiet(
        @Query("apiKey") apiKey: String,
        @Query("diet") diet: String?,
        @Query("minProtein") minProtein: Int?,
        @Query("maxPrice") maxPrice: Int?,
        @Query("number") number: Int = 3,
        @Query("addRecipeNutrition") addNutrition: Boolean = true
    ): RecipeResponse
}