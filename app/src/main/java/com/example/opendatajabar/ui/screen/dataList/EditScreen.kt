package com.example.opendatajabar.ui.screen.dataList

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.opendatajabar.data.local.DataEntity
import com.example.opendatajabar.ui.theme.GradientBackground
import com.example.opendatajabar.viewmodel.DataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(viewModel: DataViewModel, navController: NavController, dataId: Int) {
    val context = LocalContext.current

    var kodeProvinsi by remember { mutableStateOf("") }
    var namaProvinsi by remember { mutableStateOf("") }
    var kodeKabupatenKota by remember { mutableStateOf("") }
    var namaKabupatenKota by remember { mutableStateOf("") }
    var total by remember { mutableStateOf("") }
    var satuan by remember { mutableStateOf("") }
    var tahun by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var showBackDialog by remember { mutableStateOf(false) }
    var isDataLoaded by remember { mutableStateOf(false) }
    var isDataChanged by remember { mutableStateOf(false) }

    LaunchedEffect(dataId) {
        val data = viewModel.getDataById(dataId)
        data?.let {
            kodeProvinsi = it.kodeProvinsi.toString()
            namaProvinsi = it.namaProvinsi
            kodeKabupatenKota = it.kodeKabupatenKota.toString()
            namaKabupatenKota = it.namaKabupatenKota
            total = it.rataRataLamaSekolah.toString()
            satuan = it.satuan
            tahun = it.tahun.toString()
            isDataLoaded = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Data") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isDataChanged) {
                            showBackDialog = true
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            )
        }
    ) { paddingValues ->
        GradientBackground {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = kodeProvinsi,
                    onValueChange = {},
                    label = { Text("Kode Provinsi") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = namaProvinsi,
                    onValueChange = {},
                    label = { Text("Nama Provinsi") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = kodeKabupatenKota,
                    onValueChange = {
                        kodeKabupatenKota = it
                        isDataChanged = true
                    },
                    label = { Text("Kode Kabupaten/Kota") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = namaKabupatenKota,
                    onValueChange = {
                        namaKabupatenKota = it
                        isDataChanged = true
                    },
                    label = { Text("Nama Kabupaten/Kota") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = total,
                    onValueChange = {
                        total = it
                        isDataChanged = true
                    },
                    label = { Text("Total") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = satuan,
                    onValueChange = {
                        satuan = it
                        isDataChanged = true
                    },
                    label = { Text("Satuan") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = tahun,
                    onValueChange = {
                        tahun = it
                        isDataChanged = true
                    },
                    label = { Text("Tahun") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (kodeKabupatenKota.isBlank() || namaKabupatenKota.isBlank() ||
                            total.isBlank() || satuan.isBlank() || tahun.isBlank()
                        ) {
                            showDialog = true
                        } else {
                            viewModel.updateData(
                                DataEntity(
                                    id = dataId,
                                    kodeProvinsi = kodeProvinsi.toInt(),
                                    namaProvinsi = namaProvinsi,
                                    kodeKabupatenKota = kodeKabupatenKota.toInt(),
                                    namaKabupatenKota = namaKabupatenKota,
                                    rataRataLamaSekolah = total.toDouble(),
                                    satuan = satuan,
                                    tahun = tahun.toInt()
                                )
                            )
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isDataLoaded
                ) {
                    Text("Simpan Perubahan")
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("OK")
                }
            },
            title = { Text("Input Tidak Lengkap") },
            text = { Text("Harap isi semua data sebelum menyimpan!") }
        )
    }

    if (showBackDialog) {
        AlertDialog(
            onDismissRequest = { showBackDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        showBackDialog = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Ya")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showBackDialog = false }
                ) {
                    Text("Tidak")
                }
            },
            title = { Text("Konfirmasi") },
            text = { Text("Apakah Anda yakin untuk membatalkan perubahan?") }
        )
    }
}