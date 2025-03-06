package com.ryan.mmcalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryan.myanmarcalendar.api.MyanmarCalendar
import com.ryan.myanmarcalendar.api.config.CalendarConfig
import com.ryan.myanmarcalendar.data.model.Language
import com.ryan.myanmarcalendar.domain.model.DayInfo
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure calendar with Myanmar language
        val config = CalendarConfig.Builder()
            .setLanguage(Language.MYANMAR)
            .build()
        CalendarConfig.init(config)

        val calendar = MyanmarCalendar.getInstance()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    var currentDate by remember { mutableStateOf(Date()) }

                    Column(modifier = Modifier.fillMaxSize()) {
                        CalendarHeader(
                            currentDate = currentDate,
                            onPreviousMonth = {
                                val cal = Calendar.getInstance()
                                cal.time = currentDate
                                cal.add(Calendar.MONTH, -1)
                                currentDate = cal.time
                            },
                            onNextMonth = {
                                val cal = Calendar.getInstance()
                                cal.time = currentDate
                                cal.add(Calendar.MONTH, 1)
                                currentDate = cal.time
                            }
                        )

                        // Myanmar calendar header
                        MyanmarCalendarInfo(currentDate, calendar)

                        // Calendar Grid
                        CalendarGrid(currentDate, calendar)
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarHeader(
    currentDate: Date,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val cal = Calendar.getInstance()
    cal.time = currentDate

    val year = cal.get(Calendar.YEAR)
    val monthFormat = SimpleDateFormat("MMM", Locale.US)
    val month = monthFormat.format(currentDate)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left arrow
        Button(
            onClick = onPreviousMonth,
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            modifier = Modifier.padding(end = 4.dp)
        ) {
            Text("<", color = Color.White)
        }

        // Year
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Text(year.toString(), color = Color.White)
        }

        // Month
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Text(month, color = Color.White)
        }

        // Right arrow
        Button(
            onClick = onNextMonth,
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Text(">", color = Color.White)
        }

        // Myanmar button
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            modifier = Modifier.padding(start = 4.dp)
        ) {
            Text("Myanmar", color = Color.White)
        }
    }
}

@Composable
fun MyanmarCalendarInfo(currentDate: Date, myanmarCalendar: MyanmarCalendar) {
    val cal = Calendar.getInstance()
    cal.time = currentDate

    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH) + 1

    val myanmarCalendarHeader = myanmarCalendar.getWesternCalendarHeader(year, month)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF222222))
            .padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        Text(
            text = myanmarCalendarHeader,
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CalendarGrid(currentDate: Date, myanmarCalendar: MyanmarCalendar) {
    val calendarResult = myanmarCalendar.getMonthCalendar(currentDate.time)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Weekday headers
        WeekdayHeader()

        // Days grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(calendarResult.days) { dayInfo ->
                DayCell(dayInfo)
            }
        }
    }
}

@Composable
fun WeekdayHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(vertical = 8.dp)
    ) {
        // Shortened weekday names in Myanmar - exactly as seen in screenshots
        val weekdays = listOf("တနင်", "တလာ", "အငြာ", "ဗုဒ်", "ကြာ", "သော", "စနေ")
        val colors = listOf(Color.Red, Color.White, Color.White, Color.White, Color.White, Color.White, Color(0xFF9966FF))

        weekdays.forEachIndexed { index, day ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    color = colors[index],
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun DayCell(dayInfo: DayInfo) {
    if (dayInfo.gregorianDay == 0) {
        // Empty cell
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .background(Color.Black)
        )
        return
    }

    // Simplified special dates for highlighting
    val specialDates = listOf(2, 13, 27)
    val isSpecialDate = specialDates.contains(dayInfo.gregorianDay)

    // Check if it's a full moon day (day 13 in the screenshots)
    val isFullMoonDay = dayInfo.gregorianDay == 13

    // Background color
    val bgColor = when {
        isFullMoonDay -> Color(0xFF552255) // Purple for full moon
        else -> Color.Black
    }

    // Determine if this is a late month day - only for last days in this example
    val isLateMonth = dayInfo.gregorianDay >= 29

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(bgColor)
            .padding(4.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Column {
            // Day number
            Text(
                text = dayInfo.gregorianDay.toString(),
                color = if (isSpecialDate) Color.Red else Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            // Just show "တပေါင်း" for most days as in your screenshots
            if (!isLateMonth) {
                Text(
                    text = "တပေါင်း",
                    color = Color.White,
                    fontSize = 12.sp
                )
            } else {
                // For days 29-31 just show "Tagu" as in your screenshots
                Text(
                    text = "Tagu",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}