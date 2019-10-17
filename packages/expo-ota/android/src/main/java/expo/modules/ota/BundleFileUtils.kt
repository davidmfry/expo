package expo.modules.ota

import okhttp3.Response
import java.io.*

fun saveResponseToFile(directory: File, fileName: String): (response: Response, success: (String) -> Unit, error: (Exception) -> Unit) -> Unit {
    return fun(response: Response, success: (String) -> Unit, error: (Exception) -> Unit) {
        if (!directory.exists()) {
            directory.mkdir()
        }
        val sourceFile = File(directory, fileName)
        try {
            val inputStream = response.body()!!.byteStream()
            val fileOutputStream = FileOutputStream(sourceFile)

            copyStream(inputStream, fileOutputStream)

            fileOutputStream.flush()
            fileOutputStream.fd.sync()
            success(sourceFile.absolutePath)
        } catch (e: IOException) {
            error(e)
        }
    }
}

fun removeFile(path: String): Boolean {
    val file = File(path)
    return try {
        if (file.exists()) {
            file.delete()
        } else {
            true
        }
    } catch (ignore: IOException) {
        false
    }
}


@Throws(IOException::class)
private fun copyStream(from: InputStream, to: OutputStream): Long {
    val buf = ByteArray(0x1000)
    var total: Long = 0
    while (true) {
        val r = from.read(buf)
        if (r == -1) {
            break
        }
        to.write(buf, 0, r)
        total += r.toLong()
    }
    return total
}
