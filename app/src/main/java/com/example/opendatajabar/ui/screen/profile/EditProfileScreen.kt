package com.example.opendatajabar.ui.screen.profile

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.opendatajabar.ui.theme.GradientBackground
import com.example.opendatajabar.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController, viewModel: ProfileViewModel) {
    val profile by viewModel.profile.collectAsState()
    val context = LocalContext.current

    var studentName by remember { mutableStateOf(profile?.name ?: "Mahasiswa JTK") }
    var studentId by remember { mutableStateOf(profile?.studentId ?: "22222") }
    var studentEmail by remember { mutableStateOf(profile?.email ?: "mahasiswa@jtk.polban.ac.id") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var hasChanges by remember { mutableStateOf(false) }

    val profileImageBitmap = profile?.image?.let { imageData ->
        BitmapFactory.decodeByteArray(imageData, 0, imageData.size)?.asImageBitmap()
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
        uri?.let { viewModel.updateProfileImage(it, context) }
        hasChanges = true
    }

    fun rollbackChanges() {
        studentName = profile?.name ?: "Mahasiswa JTK"
        studentId = profile?.studentId ?: "22222"
        studentEmail = profile?.email ?: "mahasiswa@jtk.polban.ac.id"
        hasChanges = false
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Konfirmasi") },
            text = { Text("Apakah Anda yakin untuk membatalkan perubahan?") },
            confirmButton = {
                Button(
                    onClick = {
                        rollbackChanges()
                        showDialog = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Ya", color = Color.White)
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Tidak")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (hasChanges) {
                            showDialog = true
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            )
        }
    ) { paddingValues ->
        GradientBackground {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Card(
                        modifier = Modifier
                            .size(160.dp)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        shape = CircleShape,
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        if (profileImageBitmap != null) {
                            Image(
                                bitmap = profileImageBitmap,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Default Profile Picture",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(100.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = {
                            studentName = it
                            hasChanges = true
                        },
                        label = { Text("Nama Lengkap") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                    OutlinedTextField(
                        value = studentId,
                        onValueChange = {
                            studentId = it
                            hasChanges = true
                        },
                        label = { Text("NIM") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = studentEmail,
                        onValueChange = {
                            studentEmail = it
                            hasChanges = true
                        },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val nameRegex = Regex("^[A-Za-z ]+\$")
                            val idRegex = Regex("^[0-9]+\$")
                            val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")

                            if (!nameRegex.matches(studentName)) {
                                Toast.makeText(context, "Student Name hanya boleh huruf", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (!idRegex.matches(studentId)) {
                                Toast.makeText(context, "Student ID harus angka", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (!emailRegex.matches(studentEmail)) {
                                Toast.makeText(context, "Format email tidak valid", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            viewModel.updateProfile(studentName, studentId, studentEmail)
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}