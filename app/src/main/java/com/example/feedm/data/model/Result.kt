package com.example.feedm.data.model

data class Result(
    val title: String,
    val url: String,
    val id: String,
    val score: Double?,
    val publishedDate: String?,
    val author: String?,
    val text: String?,
    val highlights: List<String>?,
    val highlightScores: List<Double>?,
    val summary: String?
)