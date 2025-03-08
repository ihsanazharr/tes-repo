package com.example.opendatajabar.ui.screen.dataList

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.opendatajabar.data.local.DataEntity
import com.example.opendatajabar.ui.theme.GradientBackground
import com.example.opendatajabar.viewmodel.DataViewModel
import kotlinx.coroutines.launch

private const val TAG = "DataListScreen"

@Composable
fun DataListScreen(navController: NavHostController, viewModel: DataViewModel) {
    Log.d(TAG, "DataListScreen composable started")

    val dataList by viewModel.dataList.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<DataEntity?>(null) }

    var selectedFilter by remember { mutableStateOf("Semua") }
    val uniqueKabupatenKota = remember(dataList) {
        listOf("Semua") + dataList.map { it.namaKabupatenKota }.distinct()
    }

    var currentPage by remember { mutableStateOf(0) }

    val filteredData = remember(dataList, selectedFilter) {
        if (selectedFilter == "Semua") dataList else dataList.filter { it.namaKabupatenKota == selectedFilter }
    }

    val totalPages = if (filteredData.isEmpty()) 1 else ((filteredData.size - 1) / 10) + 1

    val paginatedData = filteredData.drop(currentPage * 10).take(10)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        GradientBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Filter:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        DropdownMenuFilter(
                            selectedFilter = selectedFilter,
                            options = uniqueKabupatenKota,
                            onFilterSelected = {
                                selectedFilter = it
                                currentPage = 0
                            }
                        )
                    }
                    Text(
                        text = "Jumlah Data: ${filteredData.size}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    filteredData.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No Data Available",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
                        ) {
                            items(paginatedData) { item ->
                                DataItemCard(
                                    item = item,
                                    onEditClick = { navController.navigate("edit/${item.id}") },
                                    onDeleteClick = {
                                        selectedItem = item
                                        showDialog = true
                                    }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { if (currentPage > 0) currentPage-- },
                                enabled = currentPage > 0
                            ) {
                                Text("Previous")
                            }
                            Text("Page ${currentPage + 1} of $totalPages")
                            Button(
                                onClick = { if (currentPage < totalPages - 1) currentPage++ },
                                enabled = currentPage < totalPages - 1
                            ) {
                                Text("Next")
                            }
                        }
                    }
                }
            }
        }
    }

    DeleteConfirmationDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onConfirm = {
            selectedItem?.let {
                viewModel.deleteData(it)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Data berhasil dihapus")
                }
            }
            showDialog = false
        }
    )
}

@Composable
fun DropdownMenuFilter(
    selectedFilter: String,
    options: List<String>,
    onFilterSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentWidth()) {
        Button(
            onClick = { expanded = true },
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 3.dp, vertical = 8.dp)
        ) {
            Text(selectedFilter)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { filter ->
                DropdownMenuItem(
                    text = { Text(filter) },
                    onClick = {
                        onFilterSelected(filter)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DataItemCard(
    item: DataEntity,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.onTertiary)
                .padding(16.dp)
        ) {
            Text(
                text = "Provinsi: ${item.namaProvinsi} (${item.kodeProvinsi})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Kabupaten/Kota: ${item.namaKabupatenKota} (${item.kodeKabupatenKota})",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Total: ${item.rataRataLamaSekolah} ${item.satuan}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tahun: ${item.tahun}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onEditClick) {
                    Text(text = "Edit")
                }
                Spacer(modifier = Modifier.width(5.dp))
                Button(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = "Delete")
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah Anda Yakin Untuk Menghapus Data Ini?") },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Ya")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Tidak")
                }
            }
        )
    }
}