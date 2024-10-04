package com.exactpro.build

import com.exactpro.util.Paths
import org.eclipse.jgit.internal.storage.file.FileRepository
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

// former [Shsha] 4ed2395bf buildSrc/src/main/java/com/exactpro/build/GitStatus.java
object GitStatus {
    @JvmStatic
    val branch: String
        get() {
            val cwd = Paths.cwd()
            try {
                val dotGitDir = locateGitDirectory(cwd)
                FileRepository(dotGitDir.toFile()).use { gitRepo ->
                    return gitRepo.branch
                }
            } catch (ex: IOException) {
                throw RuntimeException(
                    String.format("Error getting current Git branch: %s", ex.message),
                    ex
                )
            }
        }

    @Throws(IOException::class)
    private fun locateGitDirectory(startPath: Path): Path {
        var path = startPath
        val fsRoot = path.root
        var gitPath: Path? = null
        while (fsRoot != path) {
            gitPath = path.resolve(".git")
            if (Files.exists(gitPath) && Files.isDirectory(gitPath)) {
                break
            }
            gitPath = null
            path = path.parent
        }
        if (gitPath == null) {
            throw IOException(String.format("Unable to locate Git repository for \"%s\"", startPath))
        }
        return gitPath
    }
}
