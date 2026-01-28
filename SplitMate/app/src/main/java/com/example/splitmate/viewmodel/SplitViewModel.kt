package com.example.splitmate.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.splitmate.data.Calc
import com.example.splitmate.data.TipOption
import com.example.splitmate.data.defaultTipOptions

class SplitViewModel : ViewModel() {

    private val _uiState = MutableLiveData(SplitUiState())
    val uiState: LiveData<SplitUiState> = _uiState

    private val _toastEvent = MutableLiveData<Event<String>>()
    val toastEvent: LiveData<Event<String>> = _toastEvent

    fun getCalculationById(id: String): Calc? =
        _uiState.value?.calculations?.find { it.id == id }

    fun getLatestCalculation(): Calc? =
        _uiState.value?.calculations?.lastOrNull()

    fun onEvent(event: SplitEvent) {
        val state = _uiState.value ?: SplitUiState()

        when (event) {
            is SplitEvent.UpdateTotal -> {
                val result = validateTotal(event.value)

                _uiState.value = state.copy(
                    totalAmount = event.value,
                    totalError = result.errorMessage,
                    showErrors = event.value.isNotEmpty(),
                    isCalculateEnabled = isInputValid(event.value, state.peopleCount)
                )
            }

            is SplitEvent.UpdatePeople -> {
                val result = validatePeople(event.value)

                _uiState.value = state.copy(
                    peopleCount = event.value,
                    peopleError = result.errorMessage,
                    showErrors = event.value.isNotEmpty(),
                    isCalculateEnabled = isInputValid(state.totalAmount, event.value)
                )
            }

            is SplitEvent.SelectTip -> {
                _uiState.value = state.copy(selectedTip = event.tipOption)
            }

            SplitEvent.Calculate -> {
                val totalRes = validateTotal(state.totalAmount)
                val peopleRes = validatePeople(state.peopleCount)

                if (!totalRes.isValid || !peopleRes.isValid) {
                    _uiState.value = state.copy(
                        totalError = totalRes.errorMessage,
                        peopleError = peopleRes.errorMessage,
                        showErrors = true
                    )
                    return
                }

                val calculation = Calc(
                    id = System.currentTimeMillis().toString(),
                    totalAmount = state.totalAmount.toDouble(),
                    peopleCount = state.peopleCount.toInt(),
                    tipPercentage = state.selectedTip.percentage
                )

                val updatedHistory = (state.calculations + calculation).takeLast(5)

                _uiState.value = state.copy(
                    currentCalculation = calculation,
                    calculations = updatedHistory,
                    totalError = null,
                    peopleError = null,
                    showErrors = false
                )

                _toastEvent.value = Event("Расчет сохранен")
            }

            SplitEvent.Reset -> {
                _uiState.value = SplitUiState(
                    selectedTip = defaultTipOptions.getOrElse(2) { defaultTipOptions.first() }
                )
                _toastEvent.value = Event("Форма очищена")
            }
        }
    }

    private fun isInputValid(total: String, people: String): Boolean =
        validateTotal(total).isValid && validatePeople(people).isValid

    private fun validateTotal(total: String): ValidationResult {
        if (total.isBlank()) return ValidationResult(false, null)

        val value = total.toDoubleOrNull()
            ?: return ValidationResult(false, "Введите корректное число")

        if (value <= 0) {
            return ValidationResult(false, "Сумма не может быть отрицательной или равной 0")
        }

        return ValidationResult(true)
    }

    private fun validatePeople(people: String): ValidationResult {
        if (people.isBlank()) return ValidationResult(false, null)

        val value = people.toIntOrNull()
            ?: return ValidationResult(false, "Введите целое число")

        if (value <= 0) {
            return ValidationResult(false, "Количество человек должно быть больше 0")
        }

        return ValidationResult(true)
    }
}

data class SplitUiState(
    val totalAmount: String = "",
    val peopleCount: String = "",
    val selectedTip: TipOption = defaultTipOptions.getOrElse(2) { defaultTipOptions.first() },
    val isCalculateEnabled: Boolean = false,
    val currentCalculation: Calc? = null,
    val calculations: List<Calc> = emptyList(),
    val totalError: String? = null,
    val peopleError: String? = null,
    val showErrors: Boolean = false
)

sealed class SplitEvent {
    data class UpdateTotal(val value: String) : SplitEvent()
    data class UpdatePeople(val value: String) : SplitEvent()
    data class SelectTip(val tipOption: TipOption) : SplitEvent()
    object Calculate : SplitEvent()
    object Reset : SplitEvent()
}

private data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)

class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        if (hasBeenHandled) return null
        hasBeenHandled = true
        return content
    }

    fun peekContent(): T = content
}