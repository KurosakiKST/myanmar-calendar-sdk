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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryan.myanmarcalendar.api.MyanmarCalendar
import com.ryan.myanmarcalendar.data.model.Astro
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
                    CalendarScreen(calendar)
                }
            }
        }
    }
}

@Composable
fun CalendarScreen(calendar: MyanmarCalendar) {
    val result = calendar.getCurrentMonthCalendar()
    val currentDate = calendar.convertToMyanmarDate(Date())
    val astro = Astro.of(currentDate)

    Column(modifier = Modifier.padding(16.dp)) {
        // Western Calendar Info
        Text("Western Date: ${result.gregorianMonth.month}/${result.gregorianMonth.year}")

        // Myanmar Calendar Info
        Text("Myanmar Year: ${result.myanmarMonth.year}")
        Text("Myanmar Month: ${result.myanmarMonth.monthName}")
        Text("Is Leap Month: ${result.myanmarMonth.isLeapMonth}")

        // Astro Info
        Text("Yatyaza: ${astro.getYatyaza()}")
        Text("Pyathada: ${astro.getPyathada()}")
        Text("Nagahle Direction: ${astro.getNagahle()}")
        Text("Mahabote: ${astro.getMahabote()}")
        Text("Nakhat: ${astro.getNakhat()}")
        Text("Year Name: ${astro.getYearName()}")

        if (astro.isSabbath()) Text("Sabbath Day")
        if (astro.isThamanyo()) Text("Thamanyo")
        if (astro.isAmyeittasote()) Text("Amyeittasote")

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")) { day ->
                Text(
                    text = day,
                    modifier = Modifier.padding(4.dp)
                )
            }

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
        Text(dayInfo.gregorianDay.toString())
        Text(dayInfo.myanmarDay.toString())
        Text(dayInfo.myanmarMonth, fontSize = 10.sp)
    }
}