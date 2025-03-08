package com.example.opendatajabar.ui.screen.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.opendatajabar.data.local.DataEntity
import com.example.opendatajabar.ui.screen.dataList.DataItemCard
import com.example.opendatajabar.ui.screen.dataList.DeleteConfirmationDialog
import com.example.opendatajabar.ui.screen.dataList.DropdownMenuFilter
import com.example.opendatajabar.ui.theme.GradientBackground
import com.example.opendatajabar.viewmodel.DataViewModel
import kotlinx.coroutines.launch

private const val TAG = "HomeScreen"

@Composable
fun HomeScreen(navController: NavHostController, viewModel: DataViewModel) {
    Log.d(TAG, "HomeScreen composable started")

    val dataList by viewModel.dataList.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<DataEntity?>(null) }

    var selectedFilter by remember { mutableStateOf("Pilih Kota/Kabupaten") }
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

                )
                {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Selamat datang!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 10.dp, end = 8.dp, )
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween

                )
                {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Akses data penduduk Jawa Barat dengan mudah!",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 5.dp, end = 8.dp, )
                        )
                    }
                }


                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 15.dp, end = 8.dp, )
                )
                {
                    Text(
                        text = "Menampilkan:",
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
                                text = "Tidak ada data",
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
