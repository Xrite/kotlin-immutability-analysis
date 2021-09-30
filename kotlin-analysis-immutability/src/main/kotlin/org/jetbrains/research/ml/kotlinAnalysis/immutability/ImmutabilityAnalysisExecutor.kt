package org.jetbrains.research.ml.kotlinAnalysis.immutability

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.research.ml.kotlinAnalysis.AnalysisExecutor
import org.jetbrains.research.ml.kotlinAnalysis.ResourceManager
import org.jetbrains.research.ml.kotlinAnalysis.immutability.assumptions.JavaAssumedImmutableTypes
import org.jetbrains.research.ml.kotlinAnalysis.immutability.assumptions.KotlinBasicTypes
import org.jetbrains.research.ml.kotlinAnalysis.immutability.assumptions.KotlinCollections
import org.jetbrains.research.ml.kotlinAnalysis.immutability.assumptions.KotlinFunctions
import org.jetbrains.research.ml.kotlinAnalysis.immutability.extractors.TestsType
import org.jetbrains.research.ml.kotlinAnalysis.immutability.extractors.makeEntities
import org.jetbrains.research.ml.kotlinAnalysis.immutability.extractors.makeExtractor
import org.jetbrains.research.ml.kotlinAnalysis.immutability.output.CSVWriterResourceManager
import java.nio.file.Path
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class ImmutabilityAnalysisExecutor(
    outputDir: Path,
    private val configuration: TaskConfiguration
) : AnalysisExecutor() {

    private val dataWriter = CSVWriterResourceManager(outputDir, configuration.outputFileName)
    override val controlledResourceManagers: Set<ResourceManager> = setOf(dataWriter)

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
