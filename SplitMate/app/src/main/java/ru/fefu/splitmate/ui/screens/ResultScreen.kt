package ru.fefu.splitmate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.fefu.splitmate.data.model.Calculation
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ResultScreen(
    calculation: Calculation?,
    onEdit: () -> Unit,
    onNewCalculation: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (calculation == null) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Расчет не найден",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "ID расчета был удален или не существует",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onNewCalculation,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text("Создать новый расчет")
                    }
                }
            } else {

                val format = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
                    maximumFractionDigits = 2
                    minimumFractionDigits = 2
                }

                Text(
                    text = "ID: ${calculation.id.takeLast(6)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Text(
                    text = "Результат расчета",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {

                        ResultRow(
                            label = "Общая сумма:",
                            value = "${format.format(calculation.totalAmount)} ₽"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ResultRow(
                            label = "Чаевые (${calculation.tipPercentage}%):",
                            value = "${format.format(calculation.tipAmount)} ₽"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ResultRow(
                            label = "Итого с чаевыми:",
                            value = "${format.format(calculation.totalWithTip)} ₽",
                            isTotal = true
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Divider()

                        Spacer(modifier = Modifier.height(24.dp))


                        ResultRow(
                            label = "На ${calculation.peopleCount} ${peopleWord(calculation.peopleCount)}:",
                            value = "${format.format(calculation.perPerson)} ₽",
                            isHighlighted = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))


                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onEdit,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text("Изменить данные", fontSize = 16.sp)
                    }

                    OutlinedButton(
                        onClick = onNewCalculation,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Новый расчет", fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Раскошеливаемся :)",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}


private fun peopleWord(count: Int): String {
    return when {
        count % 10 == 1 && count % 100 != 11 -> "человека"
        count % 10 in 2..4 && count % 100 !in 12..14 -> "человека"
        else -> "человек"
    }
}

@Composable
fun ResultRow(
    label: String,
    value: String,
    isTotal: Boolean = false,
    isHighlighted: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (isTotal) 18.sp else 16.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlighted) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )

        Text(
            text = value,
            fontSize = if (isTotal) 20.sp else 18.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isHighlighted) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun Divider() {
    androidx.compose.material3.Divider(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        thickness = 1.dp
    )
}