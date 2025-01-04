import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun App() {
    var ipAddress by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("") }
    var logOutput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val adbService = remember { AdbService() }
    
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.Start
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

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { 
                    scope.launch { logOutput = adbService.devices() }
                }) {
                    Text("ADB Devices")
                }
                Button(onClick = { 
                    scope.launch { logOutput = adbService.disconnect() }
                }) {
                    Text("ADB Disconnect")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { 
                    scope.launch { logOutput = adbService.reboot() }
                }) {
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
            }

            Spacer(modifier = Modifier.height(16.dp))

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
            }

            Spacer(modifier = Modifier.height(16.dp))

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