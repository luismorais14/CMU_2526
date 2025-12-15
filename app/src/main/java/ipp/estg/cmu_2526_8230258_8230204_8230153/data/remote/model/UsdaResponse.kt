package ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote.model

data class UsdaResponse(
    val totalHits: Int? = 0,
    val currentPage: Int? = 0,
    val totalPages: Int? = 0,
    val foods: List<UsdaFood> = emptyList(),
    val error: String? = null
)

data class UsdaFood(
    val fdcId: Int,
    val description: String,
    val dataType: String? = null,
    val publicationDate: String? = null,
    val foodNutrients: List<UsdaNutrient> = emptyList(),
    val brandOwner: String? = null,
    val servingSize: Double? = null,
    val servingSizeUnit: String? = null
)

data class UsdaNutrient(
    val nutrientId: Int,
    val nutrientName: String,
    val unitName: String,
    val value: Double,
    val derivationCode: String? = null,
    val derivationDescription: String? = null
)