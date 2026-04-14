import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object JsonZipUtil {

    /**
     * 將 JSON 字串寫成檔案（UTF-8），回傳檔案路徑
     *
     * @param dir 運用 context.cacheDir 或 context.filesDir
     * @param fileName 例如 "user_001.json"
     */
    fun jsonToFile(dir: File, fileName: String, json: String): File {
        require(fileName.endsWith(".json", ignoreCase = true)) {
            "fileName 建議使用 .json 副檔名"
        }
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)
        file.outputStream().buffered().writer(Charsets.UTF_8).use { it.write(json) }
        return file
    }

    /**
     * 把多個檔案壓成同一個 ZIP
     *
     * @param files 來源檔案清單（會以各自的檔名成為 ZipEntry）
     * @param zipFile 目標 ZIP 檔
     * @param insideFolder 可選：壓縮包內的子資料夾名稱（例如 "payload/"），無則填 null
     * @param overwrite 若目標已存在，是否覆寫
     */
    fun filesToZip(
        files: List<File>,
        zipFile: File,
        insideFolder: String? = null,
        deleteZipsWithPrefix: String? = null,   // ← 新增：壓縮前先刪
        overwrite: Boolean = true
    ): File {
        val parent = zipFile.parentFile ?: error("zipFile must have a parent directory")

        if (!zipFile.parentFile.exists()) zipFile.parentFile.mkdirs()

        // 先刪除同資料夾下指定前綴的 .zip
        deleteZipsWithPrefix?.let { prefix ->
            parent.listFiles { f ->
                f.isFile && f.extension.equals("zip", ignoreCase = true) && f.nameWithoutExtension.startsWith(prefix, true)
            }?.forEach { it.delete() }
        }

        if (zipFile.exists()) {
            if (overwrite) zipFile.delete() else error("ZIP already exists: ${zipFile.absolutePath}")
        }

        // 避免 ZipEntry 重名：若有重複檔名，自動加索引
        val usedNames = mutableMapOf<String, Int>()

        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zos ->
            files.forEach { f ->
                require(f.exists()) { "Source file not found: ${f.absolutePath}" }
                val base = (insideFolder ?: "") + f.name
                val entryName = nextUniqueName(base, usedNames)

                FileInputStream(f).use { fis ->
                    val entry = ZipEntry(entryName)
                    zos.putNextEntry(entry)
                    fis.copyTo(zos)
                    zos.closeEntry()
                }
            }
        }
        return zipFile
    }

    private fun nextUniqueName(name: String, used: MutableMap<String, Int>): String {
        if (name !in used) {
            used[name] = 0; return name
        }
        val idx = (used[name] ?: 0) + 1
        used[name] = idx
        val dot = name.lastIndexOf('.')
        return if (dot > 0) {
            val base = name.substring(0, dot)
            val ext = name.substring(dot) // 包含 .
            nextUniqueName("${base}(${idx})$ext", used)
        } else {
            nextUniqueName("${name}(${idx})", used)
        }
    }

    /** 分享 ZIP（搭配 FileProvider） */
    fun shareZip(context: Context, zipFile: File, chooserTitle: String = "匯出日誌") {
        val authority = "${context.packageName}.fileprovider"
        val uri = FileProvider.getUriForFile(context, authority, zipFile)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/zip"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, zipFile.name.replace(".zip", "")) // 主旨
        }
        context.startActivity(Intent.createChooser(intent, chooserTitle))
    }
}
