package test.test

import com.intellij.openapi.project.Project
import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.PropertySource
import com.sksamuel.hoplite.decoder.BooleanDecoder
import com.sksamuel.hoplite.decoder.DataClassDecoder
import com.sksamuel.hoplite.decoder.ListDecoder
import com.sksamuel.hoplite.decoder.StringDecoder
import com.sksamuel.hoplite.yaml.YamlParser
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.research.ml.kotlinAnalysis.AnalysisExecutor
import org.jetbrains.research.ml.kotlinAnalysis.ResourceManager
import test.test.assumptions.JavaAssumedImmutableTypes
import test.test.assumptions.KotlinBasicTypes
import test.test.assumptions.KotlinCollections
import test.test.assumptions.KotlinFunctions
import test.test.extractors.*
import test.test.output.CSVWriterResourceManager
import java.io.File
import java.nio.file.Path
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class ImmutabilityAnalysisExecutor(
    outputDir: Path,
    private val configuration: TaskConfiguration
) : AnalysisExecutor() {

    private val dataWriter = CSVWriterResourceManager(outputDir, configuration.outputFileName)
    override val controlledResourceManagers: Set<ResourceManager> = setOf(dataWriter)

    private var cnt = 0

    @OptIn(ExperimentalTime::class)
    override fun analyse(project: Project) {
        val rf: ResolutionFacade? = null

        val extractor = makeExtractor(rf, configuration)

        val time = measureTime {
            println("Output: ${configuration.outputFileName}")
            print("Extracting entities...")
            val r: Pair<List<Entity>, TestsType>
            val extractionTime = measureTime {
                r = makeEntities(rf, project, extractor, configuration.includeTests)
            }
            println("done in $extractionTime")
            val (entities, type) = r

            if (!validateEntities(entities)) {
                println("Failed to validate project")
                return
            }

            val result =
                solve(
                    entities,
                    KotlinBasicTypes,
                    JavaAssumedImmutableTypes,
                    KotlinCollections(configuration.treatCollectionsAsMutable),
                    KotlinFunctions
                )
            val stats = Statistics(result)
            println(stats.percentage())
            dataWriter.addResult(project.name, type, result, configuration)
        }
        println("Analysis done in $time")
        println()
    }
}
