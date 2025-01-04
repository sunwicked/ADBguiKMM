import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

actual class AdbService {
    private var screenRecordProcess: Process? = null
    private var logcatProcess: Process? = null
    private val defaultPushLocation = "/sdcard/"
    
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

    actual suspend fun connect(ip: String): String = executeCommand("adb", "connect", "$ip:5555")
    actual suspend fun disconnect(): String = executeCommand("adb", "disconnect")
    actual suspend fun devices(): String = executeCommand("adb", "devices")
    actual suspend fun install(path: String): String = executeCommand("adb", "install", path)
    actual suspend fun uninstall(packageName: String): String = executeCommand("adb", "uninstall", packageName)
    actual suspend fun reboot(): String = executeCommand("adb", "reboot")
    actual suspend fun clear(packageName: String): String = executeCommand("adb", "shell", "pm", "clear", packageName)
    actual suspend fun push(sourcePath: String, destPath: String): String = 
        executeCommand("adb", "push", sourcePath, destPath.ifEmpty { defaultPushLocation })

    actual suspend fun screenshot(path: String): String = withContext(Dispatchers.IO) {
        try {
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
            val fileName = "screenshot_$timestamp.png"
            val devicePath = "/sdcard/$fileName"
            
            executeCommand("adb", "shell", "screencap", "-p", devicePath)
            executeCommand("adb", "pull", devicePath, path + File.separator + fileName)
            
            "Screenshot saved: ${path + File.separator + fileName}"
        } catch (e: Exception) {
            "Error taking screenshot: ${e.message}"
        }
    }

    actual suspend fun startScreenRecord(path: String): String = withContext(Dispatchers.IO) {
        try {
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
            val fileName = "screenrecord_$timestamp.mp4"
            val devicePath = "/sdcard/$fileName"
            
            screenRecordProcess = ProcessBuilder("adb", "shell", "screenrecord", devicePath)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()
            
            "Screen recording started..."
        } catch (e: Exception) {
            "Error starting screen record: ${e.message}"
        }
    }

    actual suspend fun stopScreenRecord(): String = withContext(Dispatchers.IO) {
        try {
            screenRecordProcess?.destroy()
            screenRecordProcess?.waitFor()
            screenRecordProcess = null
            "Screen recording stopped"
        } catch (e: Exception) {
            "Error stopping screen record: ${e.message}"
        }
    }

    actual suspend fun startLogcat(path: String): String = withContext(Dispatchers.IO) {
        try {
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
            val fileName = "logcat_$timestamp.txt"
            val fullPath = path + File.separator + fileName
            
            val logFile = File(fullPath)
            logcatProcess = ProcessBuilder("adb", "logcat")
                .redirectOutput(ProcessBuilder.Redirect.to(logFile))
                .redirectError(ProcessBuilder.Redirect.to(logFile))
                .start()
            
            "Logcat started: $fullPath"
        } catch (e: Exception) {
            "Error starting logcat: ${e.message}"
        }
    }

    actual suspend fun stopLogcat(): String = withContext(Dispatchers.IO) {
        try {
            logcatProcess?.destroy()
            logcatProcess?.waitFor()
            logcatProcess = null
            "Logcat stopped"
        } catch (e: Exception) {
            "Error stopping logcat: ${e.message}"
        }
    }

    actual suspend fun getPackageFromApk(apkPath: String): String = executeCommand("aapt", "dump", "badging", apkPath)
} 