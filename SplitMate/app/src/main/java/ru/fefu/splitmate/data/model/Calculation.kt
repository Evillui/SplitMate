package ru.fefu.splitmate.data.model

data class Calculation(
    val id: String = "",
    val totalAmount: Double = 0.0,
    val peopleCount: Int = 1,
    val tipPercentage: Int = 10,
    val timestamp: Long = System.currentTimeMillis()
) {
    val tipAmount: Double get() = totalAmount * tipPercentage / 100
    val totalWithTip: Double get() = totalAmount + tipAmount
    val perPerson: Double get() = totalWithTip / peopleCount
}