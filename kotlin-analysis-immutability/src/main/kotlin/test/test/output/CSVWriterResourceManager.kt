package test.test

import com.github.doyaaaaaken.kotlincsv.client.ICsvFileWriter
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.research.ml.kotlinAnalysis.ResourceManager
import java.io.File
import java.io.OutputStream
import java.nio.file.Path

class CSVWriterResourceManager(private val directory: Path, private val fileName: String) : ResourceManager {
    private val header = listOf("project", "name", "type", "immutability", "tests")
    private val writer = csvWriter { lineTerminator = System.lineSeparator() }
    private val actions = mutableListOf<ICsvFileWriter.() -> Unit>()
    fun addResult(projectName: String, tests: TestsType, immutability: Immutability) {
        actions.add {
            immutability.results().forEach {
                val entity = it.first
                val result = when (it.second) {
                    is ImmutabilityStatus.ConditionallyDeeplyImmutable -> "ConditionallyDeeplyImmutable"
                    is ImmutabilityStatus.Immutable -> "Immutable"
                    is ImmutabilityStatus.Mutable -> "Mutable"
                    is ImmutabilityStatus.ShallowImmutable -> "ShallowImmutable"
                }
                val name = when (entity) {
                    is ClassTemplate -> entity.desc.fqNameSafe.asString()
                    ErrorTemplate -> "ERROR"
                }
                val type = when (entity) {
                    is ClassTemplate -> entity.classType.name
                    ErrorTemplate -> "ERROR"
                }
                writeRow(projectName, name, type, result, tests)
            }
        }
    }

    lateinit var stream: OutputStream

    override fun init() {
        stream = File(directory.toFile(), fileName).outputStream()
    }

    override fun close() {
        writer.open(stream) {
            writeRow(header)
            actions.forEach { it() }
        }
    }
}
