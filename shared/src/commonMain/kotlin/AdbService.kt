expect class AdbService {
    suspend fun connect(ip: String): String
    suspend fun disconnect(): String
    suspend fun devices(): String
    suspend fun install(path: String): String
    suspend fun uninstall(packageName: String): String
    suspend fun reboot(): String
    suspend fun clear(packageName: String): String
    suspend fun push(sourcePath: String, destPath: String): String
    suspend fun screenshot(path: String): String
} 