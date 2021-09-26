package test.test.output

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.research.ml.kotlinAnalysis.ResourceManager
import test.test.*
import test.test.extractors.TestsType
import test.test.reasons.containingFilePath
import java.io.File
import java.nio.file.Path

class CSVWriterResourceManager(private val directory: Path, private val fileName: String) : ResourceManager {
    private val header =
        listOf("project", "name", "type", "immutability", "tests", "containingFile", "reasonNumber", "reason", "infoKeys", "infoValues") +
                TaskConfiguration.flags.map { "config_$it" }

    fun addResult(
        projectName: String,
        tests: TestsType,
        immutabilityMap: Immutability,
        taskConfiguration: TaskConfiguration
    ) {
        immutabilityMap.results().forEach { (entity, status) ->
            val result = when (status) {
                is ImmutabilityProperty.ConditionallyDeeplyImmutable -> "ConditionallyDeeplyImmutable"
                is ImmutabilityProperty.Immutable -> "Immutable"
                is ImmutabilityProperty.Mutable -> "Mutable"
                is ImmutabilityProperty.ShallowImmutable -> "ShallowImmutable"
            }
            val name = when (entity) {
                is ClassTemplate -> entity.desc.fqNameSafe.asString()
                ErrorTemplate -> "ERROR"
            }
            val type = when (entity) {
                is ClassTemplate -> entity.classType.name
                ErrorTemplate -> "ERROR"
            }
            val containingFile = when (entity) {
                is ClassTemplate -> entity.desc.containingFilePath
                ErrorTemplate -> null
            }
            status.reasons.forEachIndexed { i, reason ->
                val data = reason.csvData
                if (data.info.isEmpty()) {
                    printer.printRecord(
                        projectName,
                        name,
                        type,
                        result,
                        tests,
                        containingFile,
                        i,
                        data.reason,
                        "",
                        "",
                        *taskConfiguration.values.toTypedArray()
                    )
                } else {
                    data.info.forEach { (infoKey, infoValue) ->
                        printer.printRecord(
                            projectName,
                            name,
                            type,
                            result,
                            tests,
                            containingFile,
                            i,
                            data.reason,
                            infoKey,
                            infoValue,
                            *taskConfiguration.values.toTypedArray()
                        )
                    }
                }
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
