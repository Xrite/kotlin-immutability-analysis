package test.test

import com.github.doyaaaaaken.kotlincsv.client.ICsvFileWriter
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.research.ml.kotlinAnalysis.ResourceManager
import java.io.File
import java.io.FileWriter
import java.io.OutputStream
import java.nio.file.Path

class CSVWriterResourceManager(private val directory: Path, private val fileName: String) : ResourceManager {
    private val header = listOf("project", "name", "type", "immutability", "tests", "reason", "info")
    fun addResult(projectName: String, tests: TestsType, immutability: Immutability) {
        immutability.results().forEach { (entity, status) ->
            val result = when (status) {
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
            status.reasons.forEach {
                val data = it.csvData
                printer.printRecord(projectName, name, type, result, tests, data.reason, data.info)
            }
        }
    }

    private lateinit var printer: CSVPrinter

    override fun init() {
        printer = CSVPrinter(File(directory.toFile(), fileName).bufferedWriter(), CSVFormat.DEFAULT)
        printer.printRecord(header)
    }

    override fun close() {
        printer.flush()
        printer.close()
    }
}
