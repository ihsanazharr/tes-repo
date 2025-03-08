package com.example.opendatajabar.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.opendatajabar.data.local.DataEntity

@Composable
fun CityBarChart(
    dataList: List<DataEntity>,
    modifier: Modifier = Modifier,
    rowHeight: Dp = 30.dp,
    rowSpacing: Dp = 8.dp,
    labelFixedWidth: Dp = 200.dp,
    barMaxWidth: Dp = 150.dp
) {
    val dataByCity: Map<String, Int> = dataList.groupBy { it.namaKabupatenKota }
        .mapValues { it.value.size }
    if (dataByCity.isEmpty()) {
        Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("No data available", fontSize = 10.sp)
        }
        return
    }
    val maxCount = dataByCity.values.maxOrNull() ?: 1

    Box(modifier = modifier.horizontalScroll(rememberScrollState())) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(rowSpacing),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(dataByCity.toList()) { (city, count) ->
                CityBarRow(
                    city = city,
                    count = count,
                    maxCount = maxCount,
                    rowHeight = rowHeight,
                    labelFixedWidth = labelFixedWidth,
                    barMaxWidth = barMaxWidth
                )
            }
        }
    }
}

@Composable
fun CityBarRow(
    city: String,
    count: Int,
    maxCount: Int,
    rowHeight: Dp,
    labelFixedWidth: Dp,
    barMaxWidth: Dp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(rowHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = city,
            modifier = Modifier.width(labelFixedWidth),
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        val proportion = count.toFloat() / maxCount
        val barWidth = barMaxWidth * proportion
        Box(
            modifier = Modifier
                .width(barWidth)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$count",
            fontSize = 12.sp
        )
    }
}