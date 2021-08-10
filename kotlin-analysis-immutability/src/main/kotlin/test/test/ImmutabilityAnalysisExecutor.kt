package test.test

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.research.ml.kotlinAnalysis.AnalysisExecutor
import org.jetbrains.research.ml.kotlinAnalysis.ResourceManager
import org.jetbrains.research.ml.kotlinAnalysis.psi.PsiProvider
import test.test.extractors.*
import java.nio.file.Path

class ImmutabilityAnalysisExecutor(outputDir: Path) : AnalysisExecutor() {
    private val dataWriter = CSVWriterResourceManager(outputDir, "results2.csv")
    override val controlledResourceManagers: Set<ResourceManager> = setOf(dataWriter)

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
        val (entities, type) = makeEntities(rf, project, extractor, false)
        if (!validateEntities(entities)) {
            println("failed to validate project")
            return
        }

        entities.forEach {
            when (it) {
                is ClassTemplate -> if (it.desc.fqNameSafe.asString().contains("Library")) {
                   println(it)
                }
                ErrorTemplate -> {}
            }
        }
        //println(entities)
        val result = solve(entities, KotlinBasicTypes, JavaAssumedImmutableTypes, KotlinCollections, KotlinFunctions)
        println("ok")
        //println(properties)
        //println(classifiers)
        val stats = Statistics(result)
        println(stats.percentage())
        dataWriter.addResult(project.name, type, result)
        //pp(entities)
        //dependenciesDataWriter.writer.println(entities)
        //dataWriter.writer.println("entities")
        //dataWriter.writer.println(entities)

        //dependenciesDataWriter.writer.println("properties")
        //dependenciesDataWriter.writer.println(properties)
        //dependenciesDataWriter.writer.println("classes")
        //dependenciesDataWriter.writer.println(classifiers)
        //dependenciesDataWriter.writer.println("")
    }
}
