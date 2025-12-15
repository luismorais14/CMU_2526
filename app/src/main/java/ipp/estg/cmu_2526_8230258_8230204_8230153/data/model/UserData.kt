package ipp.estg.cmu_2526_8230258_8230204_8230153.data.model

data class UserData(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val age: Int = 0,
    val weight: Double = 0.0,
    val initialWeight: Double = 0.0,
    val height: Double = 0.0,
    val fitnessGoal: String = "",
    val activityLevel: String = "",
    val points: Int = 0,
    val country: String = "PT",
    val lastCaloriesUpdate: String ="",
    val activePlanId: Int? = null
)
