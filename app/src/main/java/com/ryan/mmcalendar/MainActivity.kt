package com.ryan.mmcalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ryan.myanmarcalendar.data.repository.CalendarRepositoryImpl
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
        Text("Gregorian Month: ${result.gregorianMonth}")
        Text("Myanmar Month: ${result.myanmarMonth}")
        Spacer(modifier = Modifier.height(16.dp))
    }
}