/*
 * Copyright 2000-2020 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.unity

import com.intellij.openapi.diagnostic.Logger
import com.vdurmont.semver4j.Semver
import jetbrains.buildServer.util.PEReader.PEUtil
import jetbrains.buildServer.util.StringUtil
import java.io.File

class WindowsUnityDetector : UnityDetectorBase() {

    override val editorPath = "Editor"
    override val editorExecutable = "Unity.exe"
    override val appConfigDir = "$userHome/AppData/Roaming"

    override fun findInstallations() = sequence {
        getHintPaths().distinct().forEach { path ->
            LOG.info("Looking for Unity installation in $path")

            val executable = getEditorPath(path)
            if (executable.exists()) LOG.info("$executable exist")
            if (!executable.exists()) return@forEach

//            val version = PEUtil.getProductVersion(executable) ?: return@forEach
//            LOG.info("var: ${version.p1}.${version.p2}.${version.p3}")
//            yield(Semver("${version.p1}.${version.p2}.${version.p3}", Semver.SemverType.LOOSE) to path)
            val version = PEUtil.getProductVersion(executable)
            if(version == null){
                val dirs = path.absolutePath.split("\\")
                if(dirs.isNotEmpty()) {
                    val vers = dirs[dirs.size - 1].split(".")
                    if(vers.size>=3) {
                        var idx = 0
                        for (s in vers[2]) {
                            if (!s.isDigit()) break;
                            idx++;
                        }
                        var v3=0
                        if (idx > 0){
                            v3=vers[2].substring(0, idx).toInt()
                        }

                        vers[2].length
                        LOG.info("new var: ${vers[0]}.${vers[1]}.${v3}")
                        yield(Semver("${vers[0]}.${vers[1]}.${v3}", Semver.SemverType.LOOSE) to path)
                    }else{
                        return@forEach
                    }
                }else {
                    return@forEach
                }
            }else{
                LOG.info("var: ${version.p1}.${version.p2}.${version.p3}.${version.p4}")
                yield(Semver("${version.p1}.${version.p2}.${version.p3}", Semver.SemverType.LOOSE) to path)
            }
        }
    }

    override fun getHintPaths() = sequence {
        yieldAll(super.getHintPaths())

        val programFiles = hashSetOf<String>()

        System.getenv("ProgramFiles")?.let { programFiles.add(it) }
        System.getenv("ProgramFiles(X86)")?.let { programFiles.add(it) }
        System.getenv("ProgramW6432")?.let { programFiles.add(it) }

        programFiles.forEach { path ->
            if (path.isEmpty()) return@forEach
            yieldAll(findUnityPaths(File(path)))
        }
    }

    companion object {
        private val LOG = Logger.getInstance(WindowsUnityDetector::class.java.name)
    }
}
