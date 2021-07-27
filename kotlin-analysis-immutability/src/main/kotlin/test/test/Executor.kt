package test.test

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.resolve.isInlineClass
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.research.ml.kotlinAnalysis.AnalysisExecutor
import org.jetbrains.research.ml.kotlinAnalysis.PrintWriterResourceManager
import org.jetbrains.research.ml.kotlinAnalysis.ResourceManager
import org.jetbrains.research.ml.kotlinAnalysis.psi.PsiProvider
import java.nio.file.Path

class ImmutabilityAnalysisExecutor(outputDir: Path) : AnalysisExecutor() {
    private val dataWriter = CSVWriterResourceManager(outputDir, "results.csv")
    override val controlledResourceManagers: Set<ResourceManager> = setOf(dataWriter)

    override fun analyse(project: Project) {
        //println(project.basePath)

        /*
        val kcs = KotlinCacheService.getInstance(project)

        val modules = com.intellij.openapi.module.ModuleManager.getInstance(project).modules

        val moduleInfos = modules.mapNotNull { module ->
            module.productionSourceInfo()
        }

        val firstModule = moduleInfos.firstOrNull()

        val rf = if (firstModule != null) {
            kcs.getResolutionFacadeByModuleInfo(firstModule, JvmPlatforms.jvm18)
        } else {
            val files = PsiProvider.extractElementsOfTypeFromProject(project, KtFile::class.java)

            val a = Collections.singletonMap(1, 1)

            kcs.getResolutionFacade(files, JvmPlatforms.jvm18)
        }
        */

        val rf: ResolutionFacade? = null
        /*
        val properties = PsiProvider.extractElementsOfTypeFromProject(project, KtProperty::class.java).forEach {
            val desc = it.resolveToDescriptorIfAny()
            println(desc to desc?.i)
        }
         */

        PsiProvider.extractElementsOfTypeFromProject(project, KtClass::class.java).forEach {
            val desc = it.resolveToDescriptorIfAny()
            println(desc to it.containingClassOrObject)
        }

        /*
        val objects = PsiProvider.extractElementsOfTypeFromProject(project, KtObjectDeclaration::class.java).forEach() {
            val desc = it.resolveToDescriptorIfAny()
            println(desc to desc?.typeConstructor?.parameters)
        }
         */

        PsiProvider.extractElementsOfTypeFromProject(project, KtObjectDeclaration::class.java).forEach {
            val desc = it.resolveToDescriptorIfAny()
            println(desc to it.containingClassOrObject)
        }


        val extractor = MultipleExtractors(
            PropertiesExtractor(rf),
            ValueParametersExtractor(rf),
            ParentsExtractor(rf),
            OuterClassesExtractor(rf)
        )
        val entities = makeEntities(rf, project, extractor)
        //println(entities)
        val result = solve(entities, KotlinBasicTypes, JavaAssumedImmutableTypes, KotlinCollections, KotlinFunctions)
        println("ok")
        //println(properties)
        //println(classifiers)
        val stats = Statistics(result)
        println(stats.percentage())
        dataWriter.addResult(project.name, result)
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
