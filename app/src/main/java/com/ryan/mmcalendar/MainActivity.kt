package com.ryan.mmcalendar

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryan.mmcalendar.util.DiagnosticUtil
import com.ryan.myanmarcalendar.api.MyanmarCalendar
import com.ryan.myanmarcalendar.api.config.CalendarConfig
import com.ryan.myanmarcalendar.core.calculator.HolidayCalculator
import com.ryan.myanmarcalendar.data.model.Astro
import com.ryan.myanmarcalendar.data.model.Language
import com.ryan.myanmarcalendar.data.model.MyanmarDate
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MyanmarCalendar"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure calendar with Myanmar language
        val config = CalendarConfig.Builder()
            .setLanguage(Language.MYANMAR)
            .build()
        CalendarConfig.init(config)

        val calendar = MyanmarCalendar.getInstance()

        // Run diagnostics on current date
        runDiagnostics(calendar)

        setContent {
            MyanmarCalendarTheme {
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

                                // Run diagnostics when month changes
                                runDiagnostics(calendar, cal.time)
                            },
                            onNextMonth = {
                                val cal = Calendar.getInstance()
                                cal.time = currentDate
                                cal.add(Calendar.MONTH, 1)
                                currentDate = cal.time

                                // Run diagnostics when month changes
                                runDiagnostics(calendar, cal.time)
                            },
                            onSwitchCalendar = {
                                // Toggle between Myanmar and Gregorian (not implemented)
                            }
                        )

                        // Myanmar calendar header
                        MyanmarCalendarInfo(currentDate, calendar)

                        // Calendar grid
                        MyanmarCalendarGrid(currentDate, calendar)

                        // Bottom navigation
                        CalendarBottomNav()
                    }
                }
            }
        }
    }

    /**
     * Run diagnostics on current date and specific test dates
     */
    private fun runDiagnostics(calendar: MyanmarCalendar, date: Date = Date()) {
        // 1. Log current date details
        DiagnosticUtil.logMyanmarDate(date, calendar)

        // 2. Log calendar month
        val cal = Calendar.getInstance()
        cal.time = date
        DiagnosticUtil.logCalendarMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), calendar)

        // 3. Check specific dates that might be holidays
        val specificDates = getTestDates(cal.get(Calendar.YEAR))
        for (testDate in specificDates) {
            DiagnosticUtil.logHolidays(testDate, calendar)
        }

        // 4. Log all days with astrological significance
        logAstrologicalDaysInMonth(calendar, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))

        // 5. Log specific holidays in the month
        logHolidaysInMonth(calendar, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
    }

    /**
     * Get test dates to check for holidays
     */
    private fun getTestDates(year: Int): List<Date> {
        val testDates = ArrayList<Date>()
        val cal = Calendar.getInstance()

        // Check Peasants' Day (March 2)
        cal.set(year, Calendar.MARCH, 2)
        testDates.add(cal.time)

        // Check Buddhist Full Moon Holidays
        cal.set(year, Calendar.MARCH, 13) // Approximate date for Tabaung Full Moon
        testDates.add(cal.time)

        // Check Resistance Day (March 27)
        cal.set(year, Calendar.MARCH, 27)
        testDates.add(cal.time)

        return testDates
    }

    /**
     * Log all days with astrological significance in a month
     */
    private fun logAstrologicalDaysInMonth(calendar: MyanmarCalendar, year: Int, month: Int) {
        val cal = Calendar.getInstance()
        cal.set(year, month, 1)
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (day in 1..daysInMonth) {
            cal.set(year, month, day)
            val myanmarDate = calendar.convertToMyanmarDate(cal.time)
            val astro = calendar.getAstrological(myanmarDate)

            val hasAstrological = astro.hasAstrologicalSignificance()
            Log.d(TAG, "Day $day: Moon Phase=${myanmarDate.moonPhase}, Myanmar Day=${myanmarDate.day}, Astro=$hasAstrological")
        }
    }

    /**
     * Log holidays in a month
     */
    private fun logHolidaysInMonth(calendar: MyanmarCalendar, year: Int, month: Int) {
        val cal = Calendar.getInstance()
        cal.set(year, month, 1)
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (day in 1..daysInMonth) {
            cal.set(year, month, day)
            val myanmarDate = calendar.convertToMyanmarDate(cal.time)
            val holidays = HolidayCalculator.getHoliday(myanmarDate)

            if (holidays.isNotEmpty()) {
                Log.d(TAG, "Found ${holidays.size} holidays for day $day: ${holidays.joinToString()}")
            }
        }
    }
}

@Composable
fun MyanmarCalendarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(),
        content = content
    )
}

@Composable
fun CalendarHeader(
    currentDate: Date,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSwitchCalendar: () -> Unit
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left arrow
        Button(
            onClick = onPreviousMonth,
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.size(width = 40.dp, height = 40.dp)
        ) {
            Text("<", color = Color.White)
        }

        // Year
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            modifier = Modifier.height(40.dp)
        ) {
            Text(year.toString(), color = Color.White)
        }

        // Month
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            modifier = Modifier.height(40.dp)
        ) {
            Text(month, color = Color.White)
        }

        // Right arrow
        Button(
            onClick = onNextMonth,
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.size(width = 40.dp, height = 40.dp)
        ) {
            Text(">", color = Color.White)
        }

        // Myanmar button
        Button(
            onClick = onSwitchCalendar,
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            modifier = Modifier
                .height(40.dp)
                .border(1.dp, Color.Blue, MaterialTheme.shapes.small)
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
    val myanmarDate = myanmarCalendar.convertToMyanmarDate(currentDate)
    val astro = myanmarCalendar.getAstrological(currentDate)

    // Format moon phase text
    val moonPhaseText = when (myanmarDate.moonPhase) {
        0 -> "လဆန်း" // Waxing
        1 -> "လပြည့်" // Full moon
        2 -> "လဆုတ်" // Waning
        3 -> "လကွယ်" // New moon
        else -> ""
    }

    Column(
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

        // Add current Myanmar date details
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Today: ${myanmarDate.getMonthName()} $moonPhaseText ${myanmarDate.day} ${myanmarDate.getWeekDay()}နေ့",
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )

        // Show astrological info if any
        if (astro.hasAstrologicalSignificance()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = astro.getAstrologicalSummary(),
                color = Color.Yellow,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
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
        // Weekday abbreviations
        val weekdays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val colors = listOf(Color.Red, Color.White, Color.White, Color.White, Color.White, Color.White, Color(0xFF9966FF))

        weekdays.forEachIndexed { index, day ->
            Text(
                text = day,
                color = colors[index],
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun MyanmarCalendarGrid(currentDate: Date, myanmarCalendar: MyanmarCalendar) {
    val displayCalendar = Calendar.getInstance()
    displayCalendar.time = currentDate
    val displayYear = displayCalendar.get(Calendar.YEAR)
    val displayMonth = displayCalendar.get(Calendar.MONTH)

    // Get the first day of the month and total days in month
    val firstDayCalendar = Calendar.getInstance()
    firstDayCalendar.set(displayYear, displayMonth, 1)
    val firstDayOfWeek = firstDayCalendar.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday

    val daysInMonth = displayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Create a list of all days needed to display (including leading/trailing empty days)
    val daysList = buildDaysList(firstDayOfWeek, daysInMonth, displayYear, displayMonth, myanmarCalendar)

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
            items(daysList) { dayData ->
                if (dayData.day > 0) {
                    MyanmarDayCell(dayData)
                } else {
                    // Empty cell
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(Color.Black)
                    )
                }
            }
        }
    }
}

private fun buildDaysList(
    firstDayOfWeek: Int,
    daysInMonth: Int,
    year: Int,
    month: Int,
    myanmarCalendar: MyanmarCalendar
): List<DayData> {
    val daysList = mutableListOf<DayData>()

    // Add empty cells for days before the start of the month
    repeat(firstDayOfWeek) {
        daysList.add(DayData(day = 0))
    }

    // Get today's date for highlighting
    val today = Calendar.getInstance()
    val currentYear = today.get(Calendar.YEAR)
    val currentMonth = today.get(Calendar.MONTH)
    val currentDay = today.get(Calendar.DAY_OF_MONTH)

    // Add all days of the month
    for (day in 1..daysInMonth) {
        val cal = Calendar.getInstance()
        cal.set(year, month, day)

        // Check if this is today
        val isToday = year == currentYear && month == currentMonth && day == currentDay

        // Get Myanmar date info
        val myanmarDate = myanmarCalendar.convertToMyanmarDate(cal.time)
        val astro = myanmarCalendar.getAstrological(cal.time)

        // Get holiday info
        val holidays = try {
            HolidayCalculator.getHoliday(myanmarDate)
        } catch (e: Exception) {
            Log.e("MyanmarCalendar", "Error getting holidays", e)
            emptyList()
        }

        // Determine if this is a weekend
        val weekDay = cal.get(Calendar.DAY_OF_WEEK)
        val isSunday = weekDay == Calendar.SUNDAY
        val isSaturday = weekDay == Calendar.SATURDAY

        daysList.add(
            DayData(
                day = day,
                weekDay = weekDay - 1,
                myanmarDate = myanmarDate,
                astro = astro,
                holidays = holidays,
                isSunday = isSunday,
                isSaturday = isSaturday,
                isToday = isToday
            )
        )
    }

    // Fill the rest of the grid with empty cells if needed
    val remainingCells = (7 - (daysList.size % 7)) % 7
    repeat(remainingCells) {
        daysList.add(DayData(day = 0))
    }

    return daysList
}

@Composable
fun MyanmarDayCell(dayData: DayData) {
    // Extract data
    val day = dayData.day
    val myanmarDate = dayData.myanmarDate ?: return
    val astro = dayData.astro ?: return
    val holidays = dayData.holidays ?: emptyList()
    val isToday = dayData.isToday

    // Check if it's a full moon day
    val isFullMoonDay = myanmarDate.moonPhase == 1
    val isNewMoonDay = myanmarDate.moonPhase == 3

    // Get astrological information
    val yatyaza = astro.isYatyaza()
    val pyathada = astro.isPyathada()
    val pyathadaValue = astro.getPyathadaValue()
    val sabbath = astro.isSabbath()
    val sabbathEve = astro.isSabbathEve()
    val thamanyo = astro.isThamanyo()
    val thamaphyu = astro.isThamaphyu()
    val nagapor = astro.isNagapor()

    // Background color
    val bgColor = when {
        isToday -> Color(0xFF3A5566) // Blue-green for today
        isFullMoonDay -> Color(0xFF552255) // Purple for full moon
        isNewMoonDay -> Color(0xFF333355) // Dark blue for new moon
        else -> Color.Black
    }

    // Day number color
    val dayColor = when {
        dayData.isSunday -> Color.Red
        dayData.isSaturday -> Color(0xFF9966FF) // Purple for Saturday
        pyathada -> Color.Red // Red for Pyathada days
        else -> Color.White
    }

    // Moon phase text and color
    val moonPhaseText = when (myanmarDate.moonPhase) {
        0 -> "လဆန်း" // Waxing
        1 -> "လပြည့်" // Full moon
        2 -> "လဆုတ်" // Waning
        3 -> "လကွယ်" // New moon
        else -> ""
    }

    val moonPhaseColor = when (myanmarDate.moonPhase) {
        1 -> Color(0xFFFFAAFF) // Brighter color for full moon
        3 -> Color(0xFFAAAAFF) // Blueish for new moon
        else -> Color.White
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(bgColor)
            .let {
                if (isToday) {
                    it.border(1.dp, Color(0xFF55AAFF), shape = RectangleShape)
                } else {
                    it
                }
            }
            .padding(4.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            // Day number with moon phase indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = day.toString(),
                    color = dayColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                // Moon phase indicator
                if (isFullMoonDay) {
                    Text(
                        text = "○",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                } else if (isNewMoonDay) {
                    Text(
                        text = "●",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }

            // Myanmar month and day
            Text(
                text = myanmarDate.getMonthName(),
                color = Color.White,
                fontSize = 10.sp
            )

            // Moon phase and lunar day
            Text(
                text = "$moonPhaseText ${myanmarDate.day}",
                color = moonPhaseColor,
                fontSize = 10.sp
            )

            // Sabbath or Sabbath Eve
            if (sabbath) {
                Text(
                    text = "ဥပုသ်",
                    color = if (isFullMoonDay) Color.White else Color.Green,
                    fontSize = 10.sp
                )
            } else if (sabbathEve) {
                Text(
                    text = "အဖိတ်",
                    color = Color.Green,
                    fontSize = 10.sp
                )
            }

            // Display astrological significance
            val astroSignificance = when {
                yatyaza -> "ရက်ရာဇာ"
                thamanyo -> "သမားညို"
                thamaphyu -> "သမားဖြူ"
                nagapor -> "နဂါးပေါ်"
                pyathada && pyathadaValue == 2 -> "မွန်းလွဲ ပြဿဒါး"
                pyathada -> "ပြဿဒါး"
                else -> ""
            }

            if (astroSignificance.isNotEmpty()) {
                Text(
                    text = astroSignificance,
                    color = if (pyathada) Color.Red else Color.Green,
                    fontSize = 10.sp
                )
            }

            // Display holidays
            if (holidays.isNotEmpty()) {
                Text(
                    text = holidays.first(),
                    color = Color.Red,
                    fontSize = 9.sp,
                    lineHeight = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CalendarBottomNav() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF333333))
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "M>E Calendar",
            color = Color(0xFFA280FF),
            fontSize = 14.sp
        )

        Text(
            text = "About",
            color = Color(0xFFA280FF),
            fontSize = 14.sp
        )
    }
}

data class DayData(
    val day: Int,
    val weekDay: Int = 0,
    val myanmarDate: MyanmarDate? = null,
    val astro: Astro? = null,
    val holidays: List<String>? = null,
    val isSunday: Boolean = false,
    val isSaturday: Boolean = false,
    val isToday: Boolean = false
)