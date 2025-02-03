package com.example.feedm.petsFeature.domain.foodsUseCases

import com.example.feedm.core.domain.model.FoodModel
import com.example.feedm.petsFeature.data.FoodRepository
import jakarta.inject.Inject

class AddFoodUseCase @Inject constructor(
    private val repository: FoodRepository,
    private val addFoodToPetUseCase: AddFoodToPetUseCase
) {
    suspend operator fun invoke(food: FoodModel, petId: Int?) {
        val foodId = repository.addFood(food).toInt()
        if (petId != null) {
            addFoodToPetUseCase(petId, foodId)
        }
    }
}
