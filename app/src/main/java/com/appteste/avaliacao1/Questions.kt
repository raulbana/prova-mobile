package com.appteste.avaliacao1

data class Questions(
    val question: String,
    val num1: Int,
    val num2: Int,
    val operator: String,
    var isAnswered: Boolean,
    var correctAnswer: Int,
    var userAnswer: Int?,
    var isCorrect: Boolean?
)
