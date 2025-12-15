package ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote.api

import ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote.model.UsdaResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NutritionApiService {
    @GET("fdc/v1/foods/search")
    suspend fun getNutrition(
        @Header("X-Api-Key") apiKey: String,
        @Query("query") query: String,
        @Query("pageSize") pageSize: Int = 10
    ): UsdaResponse
}