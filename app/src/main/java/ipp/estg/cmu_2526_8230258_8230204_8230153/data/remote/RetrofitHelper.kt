package ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote

import ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote.api.GeoapifyDataApi
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote.api.NutritionApiService
import ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote.api.SpoonacularDataApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

    private const val GEOAPIFY_BASE_URL = "https://api.geoapify.com/"
    private const val NUTRITION_BASE_URL = "https://api.nal.usda.gov/"
    private const val BASE_URL = "https://api.spoonacular.com/"


    fun getGeoapifyInstance() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(GEOAPIFY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    fun getNutritionInstance() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(NUTRITION_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getSpoonacularInstance() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val nutritionApi = RetrofitHelper.getNutritionInstance().create(NutritionApiService::class.java)
    val geoapifyApi = RetrofitHelper.getGeoapifyInstance().create(GeoapifyDataApi::class.java)
    val spoonacularApi = RetrofitHelper.getSpoonacularInstance().create(SpoonacularDataApi::class.java)
}