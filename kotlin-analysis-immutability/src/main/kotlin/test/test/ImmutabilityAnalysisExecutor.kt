package test.test

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.research.ml.kotlinAnalysis.AnalysisExecutor
import org.jetbrains.research.ml.kotlinAnalysis.ResourceManager
import org.jetbrains.research.ml.kotlinAnalysis.psi.PsiProvider
import test.test.extractors.*
import test.test.output.CSVWriterResourceManager
import java.nio.file.Path
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class ImmutabilityAnalysisExecutor(outputDir: Path) : AnalysisExecutor() {
    private val dataWriter = CSVWriterResourceManager(outputDir, "results_tests.csv")
    override val controlledResourceManagers: Set<ResourceManager> = setOf(dataWriter)

    @OptIn(ExperimentalTime::class)
    override fun analyse(project: Project) {
        val rf: ResolutionFacade? = null
        /*
        val properties = PsiProvider.extractElementsOfTypeFromProject(project, KtProperty::class.java).forEach {
            val desc = it.resolveToDescriptorIfAny()
            println(desc)
        }

        PsiProvider.extractElementsOfTypeFromProject(project, KtClass::class.java).forEach {
            val desc = it.resolveToDescriptorIfAny()
            //println(desc to it)
        }
         */


        /*
        val objects = PsiProvider.extractElementsOfTypeFromProject(project, KtObjectDeclaration::class.java).forEach() {
            val desc = it.resolveToDescriptorIfAny()
            println(desc to desc?.typeConstructor?.parameters)
        }
         */

        //ProjectRootManager.getInstance(project).contentRoots.forEach {
        //    println(it)
        //}


        PsiProvider.extractElementsOfTypeFromProject(project, KtObjectDeclaration::class.java).forEach {
            val desc = it.resolveToDescriptorIfAny()
           // println(desc to it.fqName)
        }


        val extractor = MultipleExtractors(
            PropertiesExtractor(rf, ::extractDelegate, ::extractGetter, ::extractBase),
            ValueParametersExtractor(rf),
            ParentsExtractor(rf),
            OuterClassesExtractor(rf)
        )

        val time = measureTime {
            print("Extracting entities...")
            val (entities, type) = makeEntities(rf, project, extractor, false)
            println("done")

            if (!validateEntities(entities)) {
                println("failed to validate project")
                return
            }

            //println(entities)
            val result =
                solve(entities, KotlinBasicTypes, JavaAssumedImmutableTypes, KotlinCollections, KotlinFunctions)
            println("ok")
            //println(properties)
            //println(classifiers)
            val stats = Statistics(result)
            println(stats.percentage())
            dataWriter.addResult(project.name, type, result)
        }
        println("Analysis done in $time")
        //pp(entities)
    }
}
