import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class AdbService {
    private suspend fun executeCommand(vararg command: String): String = withContext(Dispatchers.IO) {
        try {
            val process = ProcessBuilder(*command)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            val output = process.inputStream.bufferedReader().readText()
            val error = process.errorStream.bufferedReader().readText()
            process.waitFor()

            return@withContext output.ifEmpty { error }
        } catch (e: Exception) {
            return@withContext "Error: ${e.message}"
        }
    }

    actual suspend fun connect(ip: String): String = executeCommand("adb", "connect", ip)
    actual suspend fun disconnect(): String = executeCommand("adb", "disconnect")
    actual suspend fun devices(): String = executeCommand("adb", "devices")
    actual suspend fun install(path: String): String = executeCommand("adb", "install", path)
    actual suspend fun uninstall(packageName: String): String = executeCommand("adb", "uninstall", packageName)
    actual suspend fun reboot(): String = executeCommand("adb", "reboot")
    actual suspend fun clear(packageName: String): String = executeCommand("adb", "shell", "pm", "clear", packageName)
    actual suspend fun push(sourcePath: String, destPath: String): String = executeCommand("adb", "push", sourcePath, destPath)
    actual suspend fun screenshot(path: String): String = executeCommand("adb", "shell", "screencap", "-p", path)
} 