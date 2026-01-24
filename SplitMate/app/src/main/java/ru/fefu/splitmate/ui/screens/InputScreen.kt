package ru.fefu.splitmate.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.fefu.splitmate.data.model.TipOption
import ru.fefu.splitmate.data.model.defaultTipOptions
import ru.fefu.splitmate.ui.viewmodel.SplitEvent
import ru.fefu.splitmate.ui.viewmodel.SplitUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    uiState: SplitUiState,
    onEvent: (SplitEvent) -> Unit,
    onCalculate: () -> Unit,
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Введите данные",
                fontSize = 28.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))


            OutlinedTextField(
                value = uiState.totalAmount,
                onValueChange = { onEvent(SplitEvent.UpdateTotal(it)) },
                label = { Text("Общая сумма") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                prefix = { Text("₽ ") },
                isError = uiState.totalAmountDouble <= 0 && uiState.totalAmount.isNotEmpty()
            )

            if (uiState.totalAmountDouble <= 0 && uiState.totalAmount.isNotEmpty()) {
                Text(
                    text = "Сумма должна быть больше 0",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))


            OutlinedTextField(
                value = uiState.peopleCount,
                onValueChange = { onEvent(SplitEvent.UpdatePeople(it)) },
                label = { Text("Количество человек") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = uiState.peopleCountInt <= 0 && uiState.peopleCount.isNotEmpty()
            )

            if (uiState.peopleCountInt <= 0 && uiState.peopleCount.isNotEmpty()) {
                Text(
                    text = "Количество людей должно быть больше 0",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))


            Text(
                text = "Чаевые",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            TipOptionsRow(
                options = defaultTipOptions,
                selected = uiState.selectedTip,
                onSelect = { onEvent(SplitEvent.SelectTip(it)) }
            )

            Spacer(modifier = Modifier.weight(1f))


            if (!uiState.isCalculateEnabled && (uiState.totalAmount.isNotEmpty() || uiState.peopleCount != "1")) {
                Text(
                    text = "Заполните все поля корректно для расчета",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Назад")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = onCalculate,
                    modifier = Modifier.weight(1f),
                    enabled = uiState.isCalculateEnabled
                ) {
                    Text("Рассчитать")
                }
            }
        }
    }
}

@Composable
fun TipOptionsRow(
    options: List<TipOption>,
    selected: TipOption,
    onSelect: (TipOption) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options) { tip ->
            TipOptionItem(
                tip = tip,
                isSelected = tip.percentage == selected.percentage,
                onClick = { onSelect(tip) }
            )
        }
    }
}

@Composable
fun TipOptionItem(
    tip: TipOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Text(
            text = tip.label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            fontSize = 16.sp
        )
    }
}