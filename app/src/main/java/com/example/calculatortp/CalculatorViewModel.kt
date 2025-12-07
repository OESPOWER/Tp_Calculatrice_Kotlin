package com.example.calculatortp

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel() {

    var display = mutableStateOf("0")
        private set

    private var leftOperand: Double? = null
    private var pendingOp: Char? = null
    private var inputBuffer: String = ""

    private fun updateDisplay() {
        val opPart = pendingOp?.let { " ${translateOp(it)} " } ?: ""
        val leftPart = leftOperand?.let { formatNumber(it) } ?: ""
        val newDisplay = (leftPart + opPart + inputBuffer).trim()
        display.value = if (newDisplay.isEmpty()) "0" else newDisplay
    }

    private fun translateOp(op: Char): String {
        return when (op) {
            '*' -> "×"
            '/' -> "÷"
            '-' -> "−"
            else -> op.toString()
        }
    }

    private fun formatNumber(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {

            val rounded = String.format("%.8f", value).trimEnd('0').trimEnd('.')
            rounded
        }
    }

    fun onDigit(d: Char) {
        if (d !in '0'..'9') return


        if (inputBuffer.isEmpty() && leftOperand != null && pendingOp == null) {
            leftOperand = null
        }

        inputBuffer += d
        updateDisplay()
    }

    fun onOperation(op: Char) {
        if (op !in listOf('+', '-', '*', '/', '%')) return


        if (pendingOp != null && inputBuffer.isNotEmpty()) {
            val result = compute(pendingOp!!, leftOperand, inputBuffer)
            if (result == null) {
                display.value = "Erreur"
                clearAll()
                return
            }
            leftOperand = result
            inputBuffer = ""
            pendingOp = op
            updateDisplay()
            return
        }


        if (leftOperand == null && inputBuffer.isNotEmpty()) {
            leftOperand = safeParse(inputBuffer)
            inputBuffer = ""
            pendingOp = op
            updateDisplay()
            return
        }


        if (leftOperand != null && inputBuffer.isEmpty()) {
            pendingOp = op
            updateDisplay()
        }
    }

    fun onEquals() {
        if (pendingOp == null || inputBuffer.isEmpty()) return

        val result = compute(pendingOp!!, leftOperand, inputBuffer)
        if (result == null) {
            display.value = "Erreur"
            clearAll()
            return
        }

        leftOperand = result
        pendingOp = null
        inputBuffer = ""
        updateDisplay()
    }

    fun onNegate() {

        if (inputBuffer.isNotEmpty()) {
            inputBuffer = if (inputBuffer.startsWith("-")) {
                inputBuffer.substring(1)
            } else {
                "-$inputBuffer"
            }
            updateDisplay()
        }

        else if (leftOperand != null && pendingOp == null) {
            leftOperand = -leftOperand!!
            updateDisplay()
        }
    }

    fun onBackspace() {

        if (inputBuffer.isNotEmpty()) {

            if (inputBuffer.length == 2 && inputBuffer.startsWith("-")) {
                inputBuffer = ""
            } else {
                inputBuffer = inputBuffer.dropLast(1)
            }
            updateDisplay()
        }

        else if (pendingOp != null) {
            pendingOp = null
            updateDisplay()
        }
        // Effacer le dernier chiffre de leftOperand si pas d'opération
        else if (leftOperand != null) {
            val str = formatNumber(leftOperand!!)

            if (str.length == 1 || (str.length == 2 && str.startsWith("-"))) {
                leftOperand = null
                display.value = "0"
            } else {
                val newStr = str.dropLast(1)
                leftOperand = if (newStr == "-" || newStr.isEmpty()) null else safeParse(newStr)
                updateDisplay()
            }
        }
    }

    fun onReset() {
        clearAll()
        display.value = "0"
    }

    private fun clearAll() {
        leftOperand = null
        pendingOp = null
        inputBuffer = ""
    }

    private fun safeParse(s: String): Double {
        return try {
            s.toDouble()
        } catch (e: Exception) {
            0.0
        }
    }

    private fun compute(op: Char, left: Double?, rightStr: String): Double? {
        val r = safeParse(rightStr)
        val l = left ?: 0.0
        return try {
            when (op) {
                '+' -> l + r
                '-' -> l - r
                '*' -> l * r
                '/' -> if (r == 0.0) null else l / r
                '%' -> if (r == 0.0) null else l % r
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}