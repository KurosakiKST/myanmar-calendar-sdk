package com.ryan.mmcalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryan.myanmarcalendar.api.MyanmarCalendar
import com.ryan.myanmarcalendar.data.model.Astro
import com.ryan.myanmarcalendar.data.model.Language
import com.ryan.myanmarcalendar.domain.model.CalendarResult
import com.ryan.myanmarcalendar.domain.model.DayInfo
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val calendar = MyanmarCalendar.getInstance()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var selectedDate by remember { mutableStateOf(Date()) }

                    Column(modifier = Modifier.fillMaxSize()) {
                        MonthNavigator(
                            selectedDate = selectedDate,
                            onDateChange = { selectedDate = it }
                        )
                        CalendarScreen(
                            calendar = calendar,
                            selectedDate = selectedDate
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonthNavigator(
    selectedDate: Date,
    onDateChange: (Date) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = {
            val calendar = java.util.Calendar.getInstance()
            calendar.time = selectedDate
            calendar.add(java.util.Calendar.MONTH, -1)
            onDateChange(calendar.time)
        }) {
            Icon(Icons.Default.ArrowBack, "Previous Month")
        }

        IconButton(onClick = {
            val calendar = java.util.Calendar.getInstance()
            calendar.time = selectedDate
            calendar.add(java.util.Calendar.MONTH, 1)
            onDateChange(calendar.time)
        }) {
            Icon(Icons.Default.ArrowForward, "Next Month")
        }
    }
}

@Composable
fun CalendarScreen(calendar: MyanmarCalendar, selectedDate: Date) {
    val result = calendar.getMonthCalendar(selectedDate.time)
    val currentDate = calendar.convertToMyanmarDate(selectedDate)
    val astro = Astro.of(currentDate)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CalendarHeader(result, astro)
        Spacer(modifier = Modifier.height(16.dp))
        CalendarGrid(result.days, calendar)
    }
}

@Composable
private fun CalendarHeader(result: CalendarResult, astro: Astro) {
    val language = Language.MYANMAR
    val calendar = MyanmarCalendar.getInstance()

    Column(
        modifier = Modifier
            .padding(8.dp)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        HeaderSection("Western Date", "${result.gregorianMonth.day}/${result.gregorianMonth.month}/${result.gregorianMonth.year}")
        HeaderSection(
            calendar.getMyanmarText("Myanmar Year"),
            result.myanmarMonth.year.toString()
        )
        HeaderSection(
            calendar.getMyanmarText("Myanmar Month"),
            calendar.getMyanmarText(result.myanmarMonth.monthName)
        )
        HeaderSection("Leap Month", result.myanmarMonth.isLeapMonth.toString())

        Spacer(modifier = Modifier.height(8.dp))
//        AstroSection(astro, language)
    }
}

@Composable
private fun HeaderSection(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun AstroSection(astro: Astro, language: Language) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        astro.getYatyaza(language).let { if(it.isNotEmpty()) AstroRow("Yatyaza", it) }
        astro.getPyathada(language).let { if(it.isNotEmpty()) AstroRow("Pyathada", it) }
        AstroRow("Nagahle", astro.getNagahle(language))
        AstroRow("Mahabote", astro.getMahabote(language))
        AstroRow("Nakhat", astro.getNakhat(language))
        AstroRow("Year Name", astro.getYearName(language))
    }
}

@Composable
private fun AstroRow(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun CalendarGrid(days: List<DayInfo>, calendar: MyanmarCalendar) {
    Column {
        WeekdayHeader()
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.height(400.dp)
        ) {
            items(days) { dayInfo ->
                DayCell(dayInfo, calendar)
            }
        }
    }
}

@Composable
private fun WeekdayHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun DayCell(dayInfo: DayInfo, calendar: MyanmarCalendar) {
    if (dayInfo.gregorianDay == 0) {
        Spacer(modifier = Modifier.padding(4.dp))
        return
    }

    Column(
        modifier = Modifier
            .padding(2.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(4.dp)
            )
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            dayInfo.gregorianDay.toString(),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            calendar.getMyanmarText(dayInfo.myanmarDay.toString()),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            calendar.getMyanmarText(dayInfo.myanmarMonth),
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )

        MoonPhaseIndicator(dayInfo.moonPhase)
    }
}

@Composable
private fun MoonPhaseIndicator(moonPhase: Int) {
    when (moonPhase) {
        1 -> Text("🌕", fontSize = 12.sp)
        3 -> Text("🌑", fontSize = 12.sp)
        else -> Spacer(modifier = Modifier.height(12.dp))
    }
}