package ru.fefu.splitmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.fefu.splitmate.data.model.Calculation
import ru.fefu.splitmate.data.model.TipOption
import ru.fefu.splitmate.data.model.defaultTipOptions

class SplitViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SplitUiState())
    val uiState: StateFlow<SplitUiState> = _uiState.asStateFlow()

    private val _calculations = mutableListOf<Calculation>()
    val calculations: List<Calculation> get() = _calculations

    fun getCalculationById(id: String): Calculation? {
        return calculations.find { it.id == id }
    }

    fun getLatestCalculation(): Calculation? {
        return calculations.lastOrNull()
    }

    fun onEvent(event: SplitEvent) {
        when (event) {
            is SplitEvent.UpdateTotal -> {
                val newTotal = event.value.toDoubleOrNull() ?: 0.0
                _uiState.value = _uiState.value.copy(
                    totalAmount = event.value,
                    totalAmountDouble = newTotal,
                    isCalculateEnabled = isInputValid(newTotal, _uiState.value.peopleCountInt)
                )
            }

            is SplitEvent.UpdatePeople -> {
                val newPeople = event.value.toIntOrNull() ?: 1
                val validPeople = if (newPeople < 1) 1 else newPeople
                _uiState.value = _uiState.value.copy(
                    peopleCount = validPeople.toString(),
                    peopleCountInt = validPeople,
                    isCalculateEnabled = isInputValid(_uiState.value.totalAmountDouble, validPeople)
                )
            }

            is SplitEvent.SelectTip -> {
                _uiState.value = _uiState.value.copy(
                    selectedTip = event.tipOption
                )
            }

            SplitEvent.Calculate -> {
                viewModelScope.launch {
                    val calculation = Calculation(
                        id = System.currentTimeMillis().toString(),
                        totalAmount = _uiState.value.totalAmountDouble,
                        peopleCount = _uiState.value.peopleCountInt,
                        tipPercentage = _uiState.value.selectedTip.percentage
                    )

                    _calculations.add(calculation)

                    if (_calculations.size > 5) {
                        _calculations.removeAt(0)
                    }

                    _uiState.value = _uiState.value.copy(
                        currentCalculation = calculation
                    )
                }
            }

            SplitEvent.Reset -> {
                _uiState.value = SplitUiState(
                    peopleCount = "1",
                    peopleCountInt = 1,
                    selectedTip = defaultTipOptions[2]
                )
            }
        }
    }

    private fun isInputValid(total: Double, people: Int): Boolean {
        return total > 0 && people > 0
    }
}

data class SplitUiState(
    val totalAmount: String = "",
    val totalAmountDouble: Double = 0.0,
    val peopleCount: String = "1",
    val peopleCountInt: Int = 1,
    val selectedTip: TipOption = defaultTipOptions[2],
    val isCalculateEnabled: Boolean = false,
    val currentCalculation: Calculation? = null
)

sealed class SplitEvent {
    data class UpdateTotal(val value: String) : SplitEvent()
    data class UpdatePeople(val value: String) : SplitEvent()
    data class SelectTip(val tipOption: TipOption) : SplitEvent()
    object Calculate : SplitEvent()
    object Reset : SplitEvent()
}