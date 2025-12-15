package ipp.estg.cmu_2526_8230258_8230204_8230153.data.remote.model

data class GeoapifyResponse(
    val type: String,
    val features: List<Feature>
)

data class Feature(
    val type: String,
    val properties: PlaceProperties,
    val geometry: Geometry
)

data class PlaceProperties(
    val name: String?,
    val country: String?,
    val city: String?,
    val street: String?,
    val lat: Double,
    val lon: Double,
    val formatted: String?,
    val categories: List<String>?,
    val contact: Contact?
)

data class Contact(
    val phone: String?
)

data class Geometry(
    val type: String,
    val coordinates: List<Double>
)
