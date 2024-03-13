package com.example.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.DecimalFormat

class CalculatorViewModel : ViewModel() {

    private val calculationHistory = mutableListOf<String>()

    private var _displayText = MutableLiveData<String>()

    val displayText: LiveData<String>
        get() = _displayText

    private var _inputExpression = MutableLiveData<String>()

    val inputExpression: LiveData<String>
        get() = _inputExpression

    init {
        _inputExpression.value = ""
    }

    fun clearInput() {
        _inputExpression.value = ""
        _displayText.value = ""
    }

    fun appendInput(digit: String) {
        checkDisplayText()
        _inputExpression.value += digit


    }

    // to check if there is already calculated expression and result
    private fun checkDisplayText() {
        if (_displayText.value != "") {
            clearInput()
        }
    }

    fun evaluateExpression() {
        try {
            val result = evaluate(_inputExpression.value.toString())
            val formattedResult = formatResult(result)
            _displayText.value = formattedResult

        } catch (e: Exception) {
            _displayText.value = "Invalid"
        }
    }


    private fun evaluate(expression: String): Double {
        val postfix = infixToPostFix(expression)
        return evaluatePostfix(postfix)
    }


    private fun evaluatePostfix(postfix: String): Double {
        val stack = mutableListOf<Double>()
        val expressionList = postfix.split(" ")

        for (value in expressionList) {
            if (value.isNotEmpty()) {
                if (value.all { it.isDigit() || it == '.' }) {
                    // Token is a number
                    stack.add(value.toDouble())
                } else {
                    // Token is an operator
                    val num2 = stack.removeLast()
                    val num1 = stack.removeLast()
                    when (value) {
                        "+" -> stack.add(num1 + num2)
                        "-" -> stack.add(num1 - num2)
                        "*" -> stack.add(num1 * num2)
                        "/" -> stack.add(num1 / num2)
                    }
                }
            }
        }

        return if (stack.size == 1) stack.single() else throw IllegalArgumentException("Invalid postfix expression")
    }


    //convert infix to postfix
    private fun infixToPostFix(expression: String): String {
        val precedence = mapOf('*' to 2, '/' to 2, '+' to 1, '-' to 1)
        val postfix = StringBuilder()
        val stack = mutableListOf<Char>()

        var i = 0
        while (i < expression.length) {
            val c = expression[i]
            if (c.isDigit() || c == '.') {

                val number = StringBuilder()
                number.append(c)
                var j = i + 1
                while (j < expression.length && (expression[j].isDigit() || expression[j] == '.')) {
                    number.append(expression[j])
                    j++
                }
                postfix.append(number).append(" ")
                i = j - 1 // stop at last char
            } else if (c == '+' || c == '-') {
                // Handle unary plus and minus
                if (i == 0 || expression[i - 1] in "+-*/") {
                    // Unary operator
                    postfix.append("0 ")
                    stack.add(c)
                } else {
                    // Binary operator
                    while (stack.isNotEmpty() && (precedence[stack.last()] ?: 0) >= (precedence[c]
                            ?: 0)
                    ) {
                        postfix.append(stack.removeLast()).append(" ")
                    }
                    stack.add(c)
                }
            } else if (c in precedence.keys) {
                // Handle other binary operators
                while (stack.isNotEmpty() && (precedence[stack.last()] ?: 0) >= (precedence[c]
                        ?: 0)
                ) {
                    postfix.append(stack.removeLast()).append(" ")
                }
                stack.add(c)
            }
            i++
        }

        while (stack.isNotEmpty()) {
            postfix.append(stack.removeLast()).append(" ")
        }

        return postfix.toString().trim()
    }

    private fun formatResult(result: Double): String {
        val maxDecimalPlaces = 8
        val df = DecimalFormat("#.########")

        if (result % 1 == 0.0) {
            return result.toLong().toString()
        }

        df.maximumFractionDigits = maxDecimalPlaces
        return df.format(result)
    }


    fun setCalculationHistory(history: List<String>){
        calculationHistory.clear()
        calculationHistory.addAll(history)
    }

    fun getCalculationHistory():List<String>{
        return calculationHistory.toList()
    }
}