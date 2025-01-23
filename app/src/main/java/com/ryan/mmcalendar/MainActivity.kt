package com.ryan.mmcalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ryan.myanmarcalendar.data.repository.CalendarRepositoryImpl
import com.ryan.myanmarcalendar.domain.model.DayInfo
import com.ryan.myanmarcalendar.domain.usecase.GetCurrentMonthCalendarUseCase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = CalendarRepositoryImpl()
        val useCase = GetCurrentMonthCalendarUseCase(repository)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalendarScreen(useCase)
                }
            }
        }
    }
}

@Composable
fun CalendarScreen(useCase: GetCurrentMonthCalendarUseCase) {
    val result = useCase(System.currentTimeMillis())

    Column(modifier = Modifier.padding(16.dp)) {
        // Header
        Text(
            text = "${result.gregorianMonth.month}/${result.gregorianMonth.year}",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "${result.myanmarMonth.monthName} ${result.myanmarMonth.year}",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Calendar Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Weekday headers
            items(listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")) { day ->
                Text(
                    text = day,
                    modifier = Modifier.padding(4.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }

            // Calendar days
            items(result.days) { dayInfo ->
                DayCell(dayInfo)
            }
        }
    }
}

@Composable
fun DayCell(dayInfo: DayInfo) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dayInfo.gregorianDay.toString(),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = dayInfo.myanmarDay.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}