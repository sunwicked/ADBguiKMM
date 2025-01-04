import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // IP Address Input Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Enter TV IP:", modifier = Modifier.width(100.dp))
                TextField(
                    value = ipAddress,
                    onValueChange = { ipAddress = it },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { 
                        scope.launch {
                            logOutput = adbService.connect(ipAddress)
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("ADB Connect")
                }
            }

            // First Row of Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { scope.launch { logOutput = adbService.install("") } }) {
                    Text("ADB Install")
                }
                Button(onClick = { scope.launch { logOutput = adbService.devices() } }) {
                    Text("ADB Devices")
                }
                Button(onClick = { scope.launch { logOutput = adbService.disconnect() } }) {
                    Text("ADB Disconnect")
                }
            }

            // Second Row of Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { scope.launch { logOutput = adbService.reboot() } }) {
                    Text("ADB Reboot")
                }
                Button(onClick = { 
                    scope.launch { 
                        if (packageName.isNotEmpty()) {
                            logOutput = adbService.clear(packageName)
                        }
                    }
                }) {
                    Text("ADB Clear")
                }
                Button(onClick = { scope.launch { logOutput = adbService.push("", "") } }) {
                    Text("ADB Push")
                }
            }

            // Screenshot and Screen Record Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { 
                    scope.launch { logOutput = adbService.screenshot("") }
                }) {
                    Text("Screenshot")
                }
                Button(onClick = { 
                    scope.launch {
                        if (!isRecording) {
                            logOutput = adbService.startScreenRecord("")
                        } else {
                            logOutput = adbService.stopScreenRecord()
                        }
                        isRecording = !isRecording
                    }
                }) {
                    Text(if (isRecording) "Stop Recording" else "Start Recording")
                }
            }

            // Logcat Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
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
                    enabled = !isLogging
                ) {
                    Text("ADB Logcat")
                }
                Button(
                    onClick = { 
                        scope.launch {
                            logOutput = adbService.stopLogcat()
                            isLogging = false
                        }
                    },
                    enabled = isLogging
                ) {
                    Text("Stop Logcat")
                }
            }

            // Package Name Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Package:", modifier = Modifier.width(100.dp))
                TextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { 
                        scope.launch {
                            if (packageName.isNotEmpty()) {
                                logOutput = adbService.uninstall(packageName)
                            }
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Uninstall")
                }
            }

            // Log Output
            Text("Log Output:")
            Surface(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                SelectionContainer {
                    Text(
                        text = logOutput,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}