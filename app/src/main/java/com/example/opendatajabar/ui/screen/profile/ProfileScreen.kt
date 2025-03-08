package com.example.opendatajabar.ui.screen.profile

import android.graphics.BitmapFactory
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.opendatajabar.ui.theme.GradientBackground
import com.example.opendatajabar.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel) {
    val profile by viewModel.profile.collectAsState()
    val context = LocalContext.current

    val profileImageBitmap = profile?.image?.let { imageData ->
        BitmapFactory.decodeByteArray(imageData, 0, imageData.size)?.asImageBitmap()
    }

    GradientBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier.size(160.dp),
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
                Text(
                    text = profile?.name ?: "Ihsan Ertansa Azhar",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "NIM: ${profile?.studentId ?: "231511014"}",
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = profile?.email ?: "ihsan.ertansa.tif23@polban.ac.id",
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate("edit_profile") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit Profile")
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}