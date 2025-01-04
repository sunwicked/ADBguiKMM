import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun App() {
    var ipAddress by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("") }
    var logOutput by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var isLogging by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val adbService = remember { AdbService() }
    
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column - Controls
                Column(
                    modifier = Modifier.weight(0.6f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Title
                    Text(
                        "ADB GUI Desktop",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Connection Card
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "📱 Device Connection",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            OutlinedTextField(
                                value = ipAddress,
                                onValueChange = { ipAddress = it },
                                label = { Text("IP Address (e.g., 192.168.1.100)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                trailingIcon = { Text(":5555") }
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { scope.launch { logOutput = adbService.devices() } },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Devices, null)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Devices")
                                }
                                Button(
                                    onClick = { scope.launch { logOutput = adbService.connect(ipAddress) } },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Link, null)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Connect")
                                }
                                Button(
                                    onClick = { scope.launch { logOutput = adbService.disconnect() } },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.LinkOff, null)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Disconnect")
                                }
                            }
                        }
                    }

                    // Package Management Card
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "📦 Package Management",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            OutlinedTextField(
                                value = packageName,
                                onValueChange = { packageName = it },
                                label = { Text("Package Name (e.g., com.example.app)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { scope.launch { logOutput = adbService.install("") } },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Add, null)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Install APK")
                                }
                                Button(
                                    onClick = { scope.launch { logOutput = adbService.clear(packageName) } },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Clear, null)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Clear Data")
                                }
                                Button(
                                    onClick = { scope.launch { logOutput = adbService.uninstall(packageName) } },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Delete, null)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Uninstall")
                                }
                            }
                        }
                    }

                    // Device Actions Card
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "🛠️ Device Actions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { scope.launch { logOutput = adbService.reboot() } },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Refresh, null)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Reboot")
                                }
                                Button(
                                    onClick = { scope.launch { logOutput = adbService.push("", "") } },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Upload, null)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Push File")
                                }
                            }
                        }
                    }

                    // Capture Card
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "📷 Screen Capture",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { scope.launch { logOutput = adbService.screenshot("") } },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.PhotoCamera, null)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Screenshot")
                                }
                                Button(
                                    onClick = { 
                                        scope.launch {
                                            if (!isRecording) {
                                                logOutput = adbService.startScreenRecord("")
                                            } else {
                                                logOutput = adbService.stopScreenRecord()
                                            }
                                            isRecording = !isRecording
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        if (isRecording) Icons.Default.Stop else Icons.Default.Videocam,
                                        null
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(if (isRecording) "Stop Recording" else "Record Screen")
                                }
                            }
                        }
                    }

                    // Logcat Card
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "📝 Logcat",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { 
                                        scope.launch {
                                            if (!isLogging) {
                                                logOutput = adbService.startLogcat("")
                                            } else {
                                                logOutput = adbService.stopLogcat()
                                            }
                                            isLogging = !isLogging
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        if (isLogging) Icons.Default.Stop else Icons.Default.PlayArrow,
                                        null
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(if (isLogging) "Stop Logcat" else "Start Logcat")
                                }
                            }
                        }
                    }
                }

                // Right Column - Console Output
                ElevatedCard(
                    modifier = Modifier.weight(0.4f).fillMaxHeight(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "💻 Console Output",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        SelectionContainer(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = logOutput,
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}