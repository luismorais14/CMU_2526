package ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote.api

import ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote.model.GeoapifyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoapifyDataApi {
    @GET("v2/places")
    suspend fun getPlaces(
        @Query("categories") categories : String,
        @Query("filter") filter : String,
        @Query("limit") limit : Int,
        @Query("apiKey") apiKey : String
    ) : GeoapifyResponse
}