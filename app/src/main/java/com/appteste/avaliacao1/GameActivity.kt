package com.appteste.avaliacao1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GameActivity : AppCompatActivity() {

    interface Expression {
        val num1: Int
        val num2: Int
        val operator: String
        val expression: String
    }

    private lateinit var responseText: TextView
    private lateinit var questionText: TextView
    private lateinit var answerInput: EditText
    private lateinit var submitButton: Button
    private lateinit var nextButton: Button
    private lateinit var mainContainer: ConstraintLayout

    private val questions = mutableListOf<Questions>()
    private var score = 0
    private var currentIndex = 0
    private lateinit var currentQuestion: Questions

    private fun generateExpression(): Expression {
        val num1 = (0..99).random()
        val num2 = (0..99).random()
        val operator = if ((0..1).random() == 0) "+" else "-"

        if (num2 > num1 && operator == "-") {
            return generateExpression()
        }

        return object : Expression {
            override val num1 = num1
            override val num2 = num2
            override val operator = operator
            override val expression = "$num1 $operator $num2"
        }
    }

    private fun loadQuestions() {
        for (i in 1..5) {
            val expression = generateExpression()
            val correctAnswer = calculateAnswer(expression.num1, expression.num2, expression.operator)
            questions.add(
                Questions(
                    expression.expression,
                    expression.num1,
                    expression.num2,
                    expression.operator,
                    false,
                    correctAnswer,
                    null,
                    null
                )
            )
        }

        currentQuestion = questions[currentIndex]
        questionText.text = currentQuestion.question
    }

    private fun calculateAnswer(num1: Int, num2: Int, operator: String): Int {
        return when (operator) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            else -> throw IllegalArgumentException("Operador inválido: $operator")
        }
    }

    fun onAnswer(answer: Int, question: Questions) {
        question.userAnswer = answer
        question.isAnswered = true

        println("Resposta fornecida: $answer")
        println("Resposta correta: ${question.correctAnswer}")
        if (answer == question.correctAnswer) {
            question.isCorrect = true
            responseText.text = "Resposta correta!"
            score += 20
            mainContainer.setBackgroundColor(Color.parseColor("#00FF00"))
        } else {
            question.isCorrect = false
            responseText.text = "Resposta incorreta! A resposta correta é ${question.correctAnswer}."
            mainContainer.setBackgroundColor(Color.parseColor("#FF0000"))
        }
        submitButton.isEnabled = false
        nextButton.visibility = View.VISIBLE
    }

    fun nextQuestion(view: View) {
        submitButton.isEnabled = true
        nextButton.visibility = View.INVISIBLE
        mainContainer.setBackgroundColor(Color.parseColor("#FFFFFF"))
        responseText.text = ""

        if (currentIndex + 1 < questions.size) {
            currentIndex++
            currentQuestion = questions[currentIndex]
            questionText.text = currentQuestion.question
        } else {
            finishGame()
        }
    }

    fun onSubmit(view: View) {
        try {
            val answer = answerInput.text.toString().toInt()
            onAnswer(answer, currentQuestion)
            answerInput.text.clear()
        } catch (err: Exception) {
            responseText.text = "Resposta inválida!"
            answerInput.text.clear()
            Toast.makeText(this, "Resposta inválida!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun finishGame() {
        Toast.makeText(this, "Jogo finalizado! Pontuação: $score", Toast.LENGTH_LONG).show()
        val intent = Intent(this, FinalScreenActivity::class.java)

        intent.putExtra("score", score)
        startActivity(intent);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        responseText = findViewById(R.id.responseText)
        questionText = findViewById(R.id.questionText)
        answerInput = findViewById(R.id.answerInput)
        submitButton = findViewById(R.id.submitButton)
        nextButton = findViewById(R.id.nextButton)
        mainContainer = findViewById(R.id.main)

        ViewCompat.setOnApplyWindowInsetsListener(mainContainer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadQuestions()
    }
}