package com.example.splitmate.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.splitmate.data.Calc
import com.example.splitmate.data.TipOption
import com.example.splitmate.data.defaultTipOptions

class SplitViewModel : ViewModel() {

    var uiState by mutableStateOf(SplitUiState())
        private set

    var toastMessage by mutableStateOf<String?>(null)
        private set

    fun consumeToast() {
        toastMessage = null
    }

    fun onEvent(event: SplitEvent) {
        when (event) {
            is SplitEvent.UpdateTotal -> {
                val parsed = event.value
                val total = parsed ?: 0.0
                val totalRes = if (parsed == null) {
                    ValidationResult(false, "Введите корректное число")
                } else {
                    validateTotal(total)
                }

                val peopleRes = validatePeople(uiState.peopleCount)

                uiState = uiState.copy(
                    totalAmount = total,
                    totalError = totalRes.errorMessage,
                    showErrors = true,
                    isCalculateEnabled = totalRes.isValid && peopleRes.isValid
                )
            }

            is SplitEvent.UpdatePeople -> {
                val parsed = event.value
                val people = parsed ?: 0
                val peopleRes = if (parsed == null) {
                    ValidationResult(false, "Введите целое число")
                } else {
                    validatePeople(people)
                }

                val totalRes = validateTotal(uiState.totalAmount)

                uiState = uiState.copy(
                    peopleCount = people,
                    peopleError = peopleRes.errorMessage,
                    showErrors = true,
                    isCalculateEnabled = totalRes.isValid && peopleRes.isValid
                )
            }

            is SplitEvent.SelectTip -> {
                uiState = uiState.copy(selectedTip = event.tipOption)
            }

            SplitEvent.Calculate -> {
                val totalRes = validateTotal(uiState.totalAmount)
                val peopleRes = validatePeople(uiState.peopleCount)

                if (!totalRes.isValid || !peopleRes.isValid) {
                    uiState = uiState.copy(
                        totalError = totalRes.errorMessage,
                        peopleError = peopleRes.errorMessage,
                        showErrors = true,
                        isCalculateEnabled = false
                    )
                    return
                }

                val calculation = Calc(
                    id = System.currentTimeMillis().toString(),
                    totalAmount = uiState.totalAmount,
                    peopleCount = uiState.peopleCount,
                    tipPercentage = uiState.selectedTip.percentage
                )

                val updatedHistory = (uiState.calculations + calculation).takeLast(5)

                uiState = uiState.copy(
                    currentCalculation = calculation,
                    calculations = updatedHistory,
                    totalError = null,
                    peopleError = null,
                    showErrors = false,
                    isCalculateEnabled = false
                )

                toastMessage = "Расчет сохранен"
            }

            SplitEvent.Reset -> {
                uiState = SplitUiState(
                    selectedTip = defaultTipOptions.getOrElse(2) { defaultTipOptions.first() }
                )
                toastMessage = "Форма очищена"
            }
        }
    }

    private fun validateTotal(total: Double): ValidationResult {
        return if (total <= 0.0) {
            ValidationResult(false, "Сумма должна быть больше 0")
        } else {
            ValidationResult(true)
        }
    }

    private fun validatePeople(people: Int): ValidationResult {
        return if (people <= 0) {
            ValidationResult(false, "Количество человек должно быть больше 0")
        } else {
            ValidationResult(true)
        }
    }
}

data class SplitUiState(
    val totalAmount: Double = 0.0,
    val peopleCount: Int = 0,
    val selectedTip: TipOption = defaultTipOptions.getOrElse(2) { defaultTipOptions.first() },
    val isCalculateEnabled: Boolean = false,
    val currentCalculation: Calc? = null,
    val calculations: List<Calc> = emptyList(),
    val totalError: String? = null,
    val peopleError: String? = null,
    val showErrors: Boolean = false
)

sealed class SplitEvent {
    data class UpdateTotal(val value: Double?) : SplitEvent()
    data class UpdatePeople(val value: Int?) : SplitEvent()
    data class SelectTip(val tipOption: TipOption) : SplitEvent()
    object Calculate : SplitEvent()
    object Reset : SplitEvent()
}

private data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)