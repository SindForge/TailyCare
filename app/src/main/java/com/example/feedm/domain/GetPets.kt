package com.example.feedm.domain

import android.content.Context
import com.example.feedm.data.model.PetModel
import com.example.feedm.data.model.PetsRepository
import javax.inject.Inject

class GetPets @Inject constructor(private val repository: PetsRepository){
    operator fun invoke(): ArrayList<PetModel>?= repository.getAllPets()
}