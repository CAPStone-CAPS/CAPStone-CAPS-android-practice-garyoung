package com.example.capstone_2.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

// 색상 정의
val RomanticBlue = Color(0xFFAAD1E7)
val LightBabyBlue = Color(0xFFA6DAF4)
val SkypeBlue = Color(0xFF00AFF0)
val SierraBlue = Color(0xFFBFDAF7)

data class AppUsage(
    val appName: String,
    val usageTime: String,
    val usageMinutes: Int,
    val color: Color
)

data class DayUsageData(
    val date: LocalDate,
    val totalUsage: String,
    val totalMinutes: Int,
    val apps: List<AppUsage>,
    val aiSummary: String? = null,
    val selfFeedback: String? = null,
    val satisfactionRating: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: (() -> Unit)? = null,
    showTopBar: Boolean = false
) {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    val sampleData = remember { getSampleData() }

    // MainActivity에서 호출될 때는 Scaffold 없이, 독립적으로 호출될 때는 Scaffold 사용
    if (showTopBar) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("과거 기록") },
                    navigationIcon = onNavigateBack?.let { callback ->
                        {
                            IconButton(onClick = callback) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            HistoryContent(
                modifier = Modifier.padding(paddingValues),
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it },
                currentMonth = currentMonth,
                onMonthChanged = { currentMonth = it },
                usageData = sampleData
            )
        }
    } else {
        // MainActivity에서 호출될 때 - Scaffold 없이 바로 콘텐츠
        HistoryContent(
            modifier = Modifier.fillMaxSize(),
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it },
            currentMonth = currentMonth,
            onMonthChanged = { currentMonth = it },
            usageData = sampleData
        )
    }
}

@Composable
private fun HistoryContent(
    modifier: Modifier = Modifier,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    currentMonth: YearMonth,
    onMonthChanged: (YearMonth) -> Unit,
    usageData: Map<LocalDate, DayUsageData>
) {
    Column(
        modifier = modifier.background(Color.White)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = SkypeBlue,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = SkypeBlue
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { onTabSelected(0) }
            ) {
                Text(
                    "일별", 
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = if (selectedTab == 0) SkypeBlue else Color.Gray
                )
            }
            Tab(
                selected = selectedTab == 1,
                onClick = { onTabSelected(1) }
            ) {
                Text(
                    "통계", 
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = if (selectedTab == 1) SkypeBlue else Color.Gray
                )
            }
        }

        when (selectedTab) {
            0 -> DailyTab(
                selectedDate = selectedDate,
                onDateSelected = onDateSelected,
                currentMonth = currentMonth,
                onMonthChanged = onMonthChanged,
                usageData = usageData
            )
            1 -> StatisticsTab()
        }
    }
}

@Composable
fun DailyTab(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    currentMonth: YearMonth,
    onMonthChanged: (YearMonth) -> Unit,
    usageData: Map<LocalDate, DayUsageData>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CalendarCard(
                selectedDate = selectedDate,
                onDateSelected = onDateSelected,
                currentMonth = currentMonth,
                onMonthChanged = onMonthChanged,
                usageData = usageData
            )
        }

        item {
            val dayData = usageData[selectedDate]
            if (dayData != null) {
                DayDetailsCard(dayData = dayData)
            } else {
                NoDataCard(selectedDate = selectedDate)
            }
        }
    }
}

@Composable
fun CalendarCard(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    currentMonth: YearMonth,
    onMonthChanged: (YearMonth) -> Unit,
    usageData: Map<LocalDate, DayUsageData>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Month Navigation Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onMonthChanged(currentMonth.minusMonths(1)) },
                    enabled = currentMonth > YearMonth.of(2020, 1)
                ) {
                    Icon(
                        Icons.Rounded.ArrowBackIos, 
                        contentDescription = "이전 달",
                        tint = SkypeBlue
                    )
                }

                Text(
                    text = "${currentMonth.monthValue.toString().padStart(2, '0')}월 ${currentMonth.year}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                IconButton(
                    onClick = { onMonthChanged(currentMonth.plusMonths(1)) },
                    enabled = currentMonth < YearMonth.now()
                ) {
                    Icon(
                        Icons.Rounded.ArrowForwardIos, 
                        contentDescription = "다음 달",
                        tint = SkypeBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Day Headers
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Calendar Grid
            val days = remember(currentMonth) {
                val first = currentMonth.atDay(1)
                val lastDay = currentMonth.lengthOfMonth()
                val offset = first.dayOfWeek.value % 7
                List(offset) { null } + (1..lastDay).map { currentMonth.atDay(it) }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(240.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                userScrollEnabled = false
            ) {
                items(days) { date ->
                    val isSelected = date == selectedDate
                    val isToday = date == LocalDate.now()
                    val isFuture = date != null && date.isAfter(LocalDate.now())
                    val hasData = date != null && usageData.containsKey(date)

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> SkypeBlue
                                    hasData -> LightBabyBlue
                                    else -> Color.Transparent
                                }
                            )
                            .clickable(enabled = date != null && !isFuture) {
                                date?.let { onDateSelected(it) }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date?.dayOfMonth?.toString() ?: "",
                            color = when {
                                isSelected -> Color.White
                                isFuture -> Color.LightGray
                                isToday -> SkypeBlue
                                hasData -> SkypeBlue
                                else -> Color.Black
                            },
                            fontSize = 16.sp,
                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayDetailsCard(dayData: DayUsageData) {
    val isToday = dayData.date == LocalDate.now()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header with total usage time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "총 사용시간",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
                
                if (!isToday && dayData.satisfactionRating > 0) {
                    RatingBar(score = dayData.satisfactionRating)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Large time display with proper formatting
            val formattedTime = if (dayData.totalUsage.contains(":") && dayData.totalUsage.length > 5) {
                dayData.totalUsage // Already in H:MM:SS format
            } else {
                dayData.totalUsage.padStart(5, '0') // Ensure MM:SS format
            }
            
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = SkypeBlue,
                fontSize = 48.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Expandable sections for non-today dates
            if (!isToday) {
                if (dayData.aiSummary != null) {
                    var showAi by remember { mutableStateOf(false) }
                    ExpandCard(
                        title = "AI 요약과 피드백 보기",
                        expanded = showAi,
                        onToggle = { showAi = !showAi },
                        bg = RomanticBlue
                    ) {
                        Text(
                            dayData.aiSummary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (dayData.selfFeedback != null) {
                    var showSelf by remember { mutableStateOf(false) }
                    ExpandCard(
                        title = "자가 피드백",
                        expanded = showSelf,
                        onToggle = { showSelf = !showSelf },
                        bg = SierraBlue
                    ) {
                        Text(
                            dayData.selfFeedback,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // App usage breakdown
            dayData.apps.forEach { app ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = app.appName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = app.usageTime,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun NoDataCard(selectedDate: LocalDate) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = RomanticBlue
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "이 날짜의 데이터가 없습니다.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("M월 d일")),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatisticsTab() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = SkypeBlue
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "통계 화면",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "사용시간 통계 그래프가 여기에 표시됩니다",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun RatingBar(score: Int) {
    Row {
        repeat(5) { idx ->
            Icon(
                imageVector = if (idx < score) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = if (idx < score) Color(0xFFFFD700) else Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ExpandCard(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    bg: Color,
    content: @Composable () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Surface(
            color = bg,
            shape = RoundedCornerShape(8.dp),
            onClick = onToggle,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "숨기기" else "펼치기",
                    tint = SkypeBlue
                )
            }
        }
        if (expanded) {
            Surface(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(8.dp),
                color = LightBabyBlue.copy(alpha = 0.3f)
            ) {
                Box(Modifier.padding(16.dp)) { content() }
            }
        }
    }
}

private fun getSampleData(): Map<LocalDate, DayUsageData> {
    return mapOf(
        LocalDate.of(2025, 8, 1) to DayUsageData(
            date = LocalDate.of(2025, 8, 1),
            totalUsage = "05:42",
            totalMinutes = 342,
            satisfactionRating = 3,
            aiSummary = "오늘은 소셜미디어 사용이 평소보다 높았습니다. 특히 Instagram과 YouTube 사용시간이 증가했으며, 학업 관련 앱은 상대적으로 적게 사용했습니다.",
            selfFeedback = "휴일이라 좀 더 여유롭게 폰을 사용했다. 유익한 콘텐츠도 있었지만 무의식적으로 스크롤한 시간이 많았던 것 같다.",
            apps = listOf(
                AppUsage("App 1", "02:00", 120, Color.Red),
                AppUsage("App 2", "01:30", 90, Color.Blue),
                AppUsage("App 3", "01:00", 60, Color.Green),
                AppUsage("App 4", "00:20", 20, Color.Orange),
                AppUsage("App 5", "00:10", 10, Color.Purple)
            )
        ),
        LocalDate.of(2025, 8, 17) to DayUsageData(
            date = LocalDate.of(2025, 8, 17),
            totalUsage = "05:00:00",
            totalMinutes = 300,
            satisfactionRating = 5,
            aiSummary = "균형잡힌 사용 패턴을 보였습니다. 업무 시간에는 생산성 앱을, 여가 시간에는 엔터테인먼트 앱을 적절히 사용했습니다.",
            selfFeedback = "학업에 집중하며 건전하게 사용한 것 같다. 점심시간과 일과 후 유튜브로 스트레스를 잘 해소했다.",
            apps = listOf(
                AppUsage("App 1", "02:00", 120, Color.Red),
                AppUsage("App 2", "01:30", 90, Color.Blue),
                AppUsage("App 3", "01:00", 60, Color.Green),
                AppUsage("App 4", "00:20", 20, Color.Orange),
                AppUsage("App 5", "00:10", 10, Color.Purple)
            )
        ),
        LocalDate.of(2025, 8, 3) to DayUsageData(
            date = LocalDate.of(2025, 8, 3),
            totalUsage = "02:36",
            totalMinutes = 156,
            apps = listOf(
                AppUsage("App 1", "00:42", 42, Color.Red),
                AppUsage("App 2", "00:38", 38, Color.Blue),
                AppUsage("App 3", "00:31", 31, Color.Green),
                AppUsage("App 4", "00:25", 25, Color.Orange),
                AppUsage("App 5", "00:12", 12, Color.Purple)
            )
        )
    )
}