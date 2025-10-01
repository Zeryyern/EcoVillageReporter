package com.example.ecovillage

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.ecovillage.ui.theme.EcoVillageTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Report model
data class Report(
    val photoUri: String?,
    val description: String,
    val latitude: Double?,
    val longitude: Double?
)

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            var darkTheme by remember { mutableStateOf(false) }
            var language by remember { mutableStateOf("en") }
            var description by remember { mutableStateOf("") }
            var photoUri by remember { mutableStateOf<String?>(null) }
            var location by remember { mutableStateOf<Location?>(null) }
            var history = remember { mutableStateListOf<Report>() }
            var showHistory by remember { mutableStateOf(false) }
            val context = LocalContext.current

            // Temporary URI for captured photo
            var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

            // Camera launcher
            val cameraLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicture()
            ) { success ->
                if (success && tempPhotoUri != null) {
                    photoUri = tempPhotoUri.toString()
                } else {
                    Toast.makeText(context, "Photo capture cancelled", Toast.LENGTH_SHORT).show()
                }
            }

            // Function to capture photo
            fun capturePhoto() {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val photoFile = File(
                        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
                    )
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        photoFile
                    )
                    tempPhotoUri = uri
                    cameraLauncher.launch(uri)
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA),
                        100
                    )
                }
            }

            EcoVillageTheme(darkTheme = darkTheme) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(if (showHistory) "Report History" else "EcoVillage Reporter") },
                            actions = {
                                // 🌐 Language
                                var expanded by remember { mutableStateOf(false) }
                                Box {
                                    IconButton(onClick = { expanded = true }) {
                                        Icon(Icons.Filled.Language, contentDescription = "Language")
                                    }
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }) {
                                        DropdownMenuItem(
                                            text = { Text("English") },
                                            onClick = {
                                                language = "en"; setLocale("en"); expanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Bahasa Indonesia") },
                                            onClick = {
                                                language = "id"; setLocale("id"); expanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Javanese") },
                                            onClick = {
                                                language = "jv"; setLocale("jv"); expanded = false
                                            }
                                        )
                                    }
                                }
                                // 🌙 Theme
                                IconButton(onClick = { darkTheme = !darkTheme }) {
                                    Icon(
                                        if (darkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                        contentDescription = "Toggle Theme"
                                    )
                                }
                                // 📜 History toggle
                                IconButton(onClick = { showHistory = !showHistory }) {
                                    Icon(Icons.Filled.History, contentDescription = "History")
                                }
                            }
                        )
                    }
                ) { padding ->
                    if (showHistory) {
                        // 📜 Report History Screen
                        LazyColumn(
                            modifier = Modifier
                                .padding(padding)
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            if (history.isEmpty()) {
                                item {
                                    Text("No reports yet.", style = MaterialTheme.typography.bodyLarge)
                                }
                            } else {
                                items(history) { report ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 12.dp),
                                        elevation = CardDefaults.cardElevation(4.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            report.photoUri?.let {
                                                Image(
                                                    painter = rememberAsyncImagePainter(it),
                                                    contentDescription = "Report Photo",
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(200.dp)
                                                )
                                                Spacer(Modifier.height(8.dp))
                                            }
                                            Text("Description: ${report.description}")
                                            Spacer(Modifier.height(4.dp))
                                            Text("Location: ${report.latitude}, ${report.longitude}")
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // 📝 Report Form Screen
                        Column(
                            modifier = Modifier
                                .padding(padding)
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // 📷 Photo Section
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(6.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Take Waste Photo", style = MaterialTheme.typography.titleMedium)
                                    Spacer(Modifier.height(8.dp))
                                    photoUri?.let {
                                        Image(
                                            painter = rememberAsyncImagePainter(it),
                                            contentDescription = "Captured Photo",
                                            modifier = Modifier
                                                .size(220.dp)
                                                .padding(8.dp)
                                        )
                                    }
                                    Button(onClick = { capturePhoto() }) {
                                        Icon(Icons.Filled.CameraAlt, contentDescription = "Camera")
                                        Spacer(Modifier.width(8.dp))
                                        Text("Capture")
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // 📝 Description Section
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(6.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Waste Description", style = MaterialTheme.typography.titleMedium)
                                    Spacer(Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = description,
                                        onValueChange = { description = it },
                                        label = { Text("Describe the waste...") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // 📍 Location Section
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(6.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Location", style = MaterialTheme.typography.titleMedium)
                                    Spacer(Modifier.height(8.dp))
                                    location?.let {
                                        Text("Lat: ${it.latitude}, Lng: ${it.longitude}")
                                    }
                                    Button(onClick = {
                                        getCurrentLocation { loc ->
                                            location = loc
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Location: ${loc?.latitude}, ${loc?.longitude}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }) {
                                        Icon(Icons.Filled.LocationOn, contentDescription = "Location")
                                        Spacer(Modifier.width(8.dp))
                                        Text("Get Location")
                                    }
                                }
                            }

                            Spacer(Modifier.height(24.dp))

                            // ✅ Submit Report
                            Button(
                                onClick = {
                                    if (description.isNotBlank()) {
                                        history.add(
                                            Report(
                                                photoUri = photoUri,
                                                description = description,
                                                latitude = location?.latitude,
                                                longitude = location?.longitude
                                            )
                                        )
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Report Submitted Successfully!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        description = ""
                                        photoUri = null
                                    } else {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Please add description",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = "Submit")
                                Spacer(Modifier.width(8.dp))
                                Text("Submit Report")
                            }
                        }
                    }
                }
            }
        }
    }

    // 📍 Location Function
    private fun MainActivity.getCurrentLocation(callback: (Location?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            callback(location)
        }
    }

    // 🌐 Language Function
    private fun MainActivity.setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        recreate()
    }
}
