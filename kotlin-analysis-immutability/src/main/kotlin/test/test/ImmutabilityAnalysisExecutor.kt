package test.test

import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtil
import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.PropertySource
import com.sksamuel.hoplite.decoder.BooleanDecoder
import com.sksamuel.hoplite.decoder.DataClassDecoder
import com.sksamuel.hoplite.decoder.ListDecoder
import com.sksamuel.hoplite.decoder.StringDecoder
import com.sksamuel.hoplite.yaml.YamlParser
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.descriptorUtil.isExtensionProperty
import org.jetbrains.research.ml.kotlinAnalysis.AnalysisExecutor
import org.jetbrains.research.ml.kotlinAnalysis.ResourceManager
import org.jetbrains.research.ml.kotlinAnalysis.psi.PsiProvider
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
    inputDir: Path,
) : AnalysisExecutor() {
    private val file = File(inputDir.toFile(), "config.yaml")

    private val config = ConfigLoader.Builder()
        .addSource(PropertySource.file(file))
        .addDecoder(ListDecoder())
        .addDecoder(DataClassDecoder())
        .addDecoder(BooleanDecoder())
        .addDecoder(StringDecoder())
        .addFileExtensionMapping("yaml", YamlParser())
        .build()
        .loadConfigOrThrow<Config>()

    private val withWriter = config.tasks.map { it to CSVWriterResourceManager(outputDir, it.outputFileName) }
    override val controlledResourceManagers: Set<ResourceManager> = withWriter.map { it.second }.toSet()

    @OptIn(ExperimentalTime::class)
    override fun analyse(project: Project) {
        val rf: ResolutionFacade? = null

        withWriter.forEach { (configuration, dataWriter) ->
            val ex = mutableListOf<Extractor<Dependencies>>()
            if (configuration.analyzeSealedSubclasses) {
                ex.add(SealedSubclassesExtractor(rf))
            }
            if (configuration.assumeNastyInheritors) {
                ex.add(UnknownSubclassExtractor(rf))
            }
            val fs = mutableListOf<F>()
            if (configuration.treatLazyAsImmutable) {
                fs.add(::extractLazyDelegate)
            }
            fs.add(::extractDelegate)
            if (!configuration.assumeGoodGetters) {
                fs.add(::extractGetter)
            }
            fs.add(::extractBase)
            ex.add(PropertiesExtractor(rf, fs))
            ex.add(ValueParametersExtractor(rf))
            ex.add(ParentsExtractor(rf))
            ex.add(OuterClassesExtractor(rf))
            val extractor = MultipleExtractors(ex)

            val time = measureTime {
                println("Output: ${configuration.outputFileName}")
                print("Extracting entities...")
                val (entities, type) = makeEntities(rf, project, extractor, configuration.includeTests)
                println("done")

                if (!validateEntities(entities)) {
                    println("Failed to validate project")
                    return
                }

                //println(entities)
                val result =
                    solve(
                        entities,
                        KotlinBasicTypes,
                        JavaAssumedImmutableTypes,
                        KotlinCollections(configuration.treatCollectionsAsMutable),
                        KotlinFunctions
                    )
                //println(properties)
                //println(classifiers)
                val stats = Statistics(result)
                println(stats.percentage())
                dataWriter.addResult(project.name, type, result)
            }
            println("Analysis done in $time")
            println()
        }

        /*
        val extractor = MultipleExtractors(
            PropertiesExtractor(rf, ::extractLazyDelegate, ::extractDelegate, ::extractGetter, ::extractBase),
            ValueParametersExtractor(rf),
            ParentsExtractor(rf),
            OuterClassesExtractor(rf)
        )
         */
    }
}
