package test.test

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.idea.caches.project.productionSourceInfo
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.refactoring.fqName.fqName
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.ir.util.originalTypeParameter
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.research.ml.kotlinAnalysis.*
import org.jetbrains.research.ml.kotlinAnalysis.psi.PsiProvider
import java.lang.Exception
import java.nio.file.Path
import java.util.*

fun KtClassOrObject.resolveToDescriptorIfAny(
    resolutionFacade: ResolutionFacade?,
    bodyResolveMode: BodyResolveMode = org.jetbrains.kotlin.resolve.lazy.BodyResolveMode.FULL
): ClassDescriptor? =
    if (resolutionFacade == null) resolveToDescriptorIfAny(bodyResolveMode)
    else resolveToDescriptorIfAny(resolutionFacade, bodyResolveMode)

fun KtProperty.resolveToDescriptorIfAny(
    resolutionFacade: ResolutionFacade?,
    bodyResolveMode: BodyResolveMode = org.jetbrains.kotlin.resolve.lazy.BodyResolveMode.FULL
): VariableDescriptor? =
    if (resolutionFacade == null) resolveToDescriptorIfAny(bodyResolveMode)
    else resolveToDescriptorIfAny(resolutionFacade, bodyResolveMode)

class ImmutabilityAnalysisExecutor(outputDir: Path) : AnalysisExecutor() {
    private val dataWriter = PrintWriterResourceManager(outputDir, "test_output.txt")
    override val controlledResourceManagers: Set<ResourceManager> = setOf(dataWriter)

    override fun analyse(project: Project) {

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
        val properties = PsiProvider.extractElementsOfTypeFromProject(project, KtProperty::class.java).map {
            val desc = it.resolveToDescriptorIfAny()
            desc?.name to desc?.type?.arguments?.map {
                it.type.fqName?.asString()
            }
        }

        val classifiers = PsiProvider.extractElementsOfTypeFromProject(project, KtClass::class.java).map {
            val desc = it.resolveToDescriptorIfAny()
            desc?.typeConstructor?.parameters?.map { it.index }
            desc?.defaultType?.arguments?.map { it.type.fqName }
            desc to desc?.isInner
        }

        PsiProvider.extractElementsOfTypeFromProject(project, KtObjectDeclaration::class.java).forEach {
            val desc = it.resolveToDescriptorIfAny()
            println(desc to null)
        }



        val extractor = MultipleExtractors(
            PropertiesExtractor(rf),
            ValueParametersExtractor(rf),
            ParentsExtractor(rf),
            OuterClassExtractor(rf)
        )
        val entities = makeEntities(rf, project, extractor)
        //println(entities)
        val result = solve(entities, KotlinBasicTypes, JavaAssumedImmutableTypes, KotlinCollections, KotlinFunctions)
        println("ok")
        println(properties)
        println(classifiers)
        val stats = Statistics(result)
        println(stats.percentage())
        dataWriter.writer.println(stats.writeCSV())
        dataWriter.writer.println()
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
