package com.example.opendatajabar.ui.screen.dataEntry

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.opendatajabar.data.local.DataEntity
import com.example.opendatajabar.ui.theme.GradientBackground
import com.example.opendatajabar.viewmodel.DataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataEntryScreen(viewModel: DataViewModel) {
    val context = LocalContext.current

    val dataList by viewModel.dataList.observeAsState(emptyList())

    var kodeProvinsi by remember { mutableStateOf("") }
    var namaProvinsi by remember { mutableStateOf("") }

    var namaKabupatenKota by remember { mutableStateOf("") }
    var kodeKabupatenKota by remember { mutableStateOf("") }

    var isNewCity by remember { mutableStateOf(false) }

    var total by remember { mutableStateOf("") }
    var satuan by remember { mutableStateOf("") }
    var tahun by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(dataList) {
        if (dataList.isNotEmpty()) {
            kodeProvinsi = dataList.first().kodeProvinsi.toString()
            namaProvinsi = dataList.first().namaProvinsi
        }
    }

    val existingCities = dataList
        .map { it.namaKabupatenKota to it.kodeKabupatenKota }
        .distinct()

    val cityOptions = existingCities.map { it.first } + "Tambah Kabupaten/Kota Baru"

    var expandedCityDropdown by remember { mutableStateOf(false) }

    GradientBackground {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Input Data",
                style = MaterialTheme.typography.headlineMedium
            )
            @Composable
            fun CustomTextField(
                text: String,
                onTextChange: (String) -> Unit,
                modifier: Modifier = Modifier,
                placeholder: String = "Masukkan teks"
            ) {
                BasicTextField(
                    value = kodeProvinsi,
                    onValueChange = {},
                    readOnly = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.padding(4.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (text.isEmpty()) {
                                Text(
                                    text = "Kode Provinsi",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            OutlinedTextField(
                value = namaProvinsi,
                onValueChange = {},
                label = { Text("Nama Provinsi") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expandedCityDropdown,
                onExpandedChange = { expandedCityDropdown = !expandedCityDropdown },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = if (!isNewCity) namaKabupatenKota else "(Kabupaten/Kota Baru)",
                    onValueChange = {},
                    label = { Text("Pilih/Tambah Kab/Kota") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCityDropdown)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedCityDropdown,
                    onDismissRequest = { expandedCityDropdown = false }
                ) {
                    cityOptions.forEach { cityName ->
                        DropdownMenuItem(
                            text = { Text(cityName) },
                            onClick = {
                                expandedCityDropdown = false
                                if (cityName == "Tambah Kabupaten/Kota Baru") {
                                    isNewCity = true
                                    namaKabupatenKota = ""
                                    kodeKabupatenKota = ""
                                } else {
                                    isNewCity = false
                                    namaKabupatenKota = cityName
                                    val kode = existingCities
                                        .firstOrNull { it.first == cityName }
                                        ?.second
                                    kodeKabupatenKota = kode?.toString() ?: ""
                                }
                            }
                        )
                    }
                }
            }

            if (isNewCity) {
                OutlinedTextField(
                    value = namaKabupatenKota,
                    onValueChange = { namaKabupatenKota = it },
                    label = { Text("Nama Kabupaten/Kota Baru") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = kodeKabupatenKota,
                    onValueChange = { kodeKabupatenKota = it },
                    label = { Text("Kode Kabupaten/Kota Baru") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                OutlinedTextField(
                    value = kodeKabupatenKota,
                    onValueChange = {},
                    label = { Text("Kode Kabupaten/Kota") },
                    readOnly = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = total,
                onValueChange = { total = it },
                label = { Text("Total") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = satuan,
                onValueChange = { satuan = it },
                label = { Text("Satuan") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = tahun,
                onValueChange = { tahun = it },
                label = { Text("Tahun") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (kodeProvinsi.isBlank() || namaProvinsi.isBlank() ||
                        namaKabupatenKota.isBlank() || kodeKabupatenKota.isBlank() ||
                        total.isBlank() || satuan.isBlank() || tahun.isBlank()
                    ) {
                        showDialog = true
                    } else {
                        if (isNewCity) {
                            val existingCodes = dataList.map { it.kodeKabupatenKota }
                            try {
                                val newCode = kodeKabupatenKota.toInt()
                                if (newCode in existingCodes) {
                                    Toast.makeText(
                                        context,
                                        "Kode Kabupaten/Kota sudah digunakan!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }
                            } catch (e: NumberFormatException) {
                                Toast.makeText(
                                    context,
                                    "Kode Kabupaten/Kota harus angka valid!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                        }

                        try {
                            val data = DataEntity(
                                kodeProvinsi = kodeProvinsi.toInt(),
                                namaProvinsi = namaProvinsi,
                                kodeKabupatenKota = kodeKabupatenKota.toInt(),
                                namaKabupatenKota = namaKabupatenKota,
                                rataRataLamaSekolah = total.toDouble(),
                                satuan = satuan,
                                tahun = tahun.toInt()
                            )
                            viewModel.insertData(data)

                            total = ""
                            satuan = ""
                            tahun = ""
                            if (isNewCity) {
                                namaKabupatenKota = ""
                                kodeKabupatenKota = ""
                                isNewCity = false
                            }
                            Toast.makeText(
                                context,
                                "Data berhasil ditambahkan",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: NumberFormatException) {
                            Toast.makeText(
                                context,
                                "Harap masukkan angka yang valid",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Data")
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text("Input Tidak Lengkap") },
            text = { Text("Harap isi semua data sebelum menyimpan!") }
        )
    }
}