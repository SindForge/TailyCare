package com.example.feedm.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.feedm.core.data.database.entities.MealEntity

@Dao
interface MealDao {

//Full List

    @Query("SELECT * FROM meal_table WHERE pet_id = :petId")
    suspend fun getMealsByPetId(petId: Int): List<MealEntity>

    @Query("DELETE FROM meal_table")
    suspend fun clearAllMeals()


//Individual elements

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMealForAPet(meal: MealEntity)

    @Query("DELETE FROM meal_table WHERE (meal_id in (:mealId))")
    suspend fun deleteMealForAPet(mealId: List<Int>)
}