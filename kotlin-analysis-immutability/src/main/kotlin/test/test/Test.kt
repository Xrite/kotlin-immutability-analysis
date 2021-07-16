package test.test

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.idea.caches.project.productionSourceInfo
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.research.ml.kotlinAnalysis.*
import org.jetbrains.research.ml.kotlinAnalysis.psi.PsiProvider
import java.nio.file.Path
import java.util.*

fun KtClassOrObject.resolveToDescriptorIfAny(
    resolutionFacade: ResolutionFacade?,
    bodyResolveMode: BodyResolveMode = org.jetbrains.kotlin.resolve.lazy.BodyResolveMode.FULL): ClassDescriptor? =
    if (resolutionFacade == null) resolveToDescriptorIfAny(bodyResolveMode)
    else resolveToDescriptorIfAny(resolutionFacade, bodyResolveMode)

fun KtProperty.resolveToDescriptorIfAny(
    resolutionFacade: ResolutionFacade?,
    bodyResolveMode: BodyResolveMode = org.jetbrains.kotlin.resolve.lazy.BodyResolveMode.FULL): VariableDescriptor? =
    if (resolutionFacade == null) resolveToDescriptorIfAny(bodyResolveMode)
    else resolveToDescriptorIfAny(resolutionFacade, bodyResolveMode)

class TestAnalysisExecutor(outputDir: Path) : AnalysisExecutor() {
    private val dependenciesDataWriter = PrintWriterResourceManager(outputDir, "test_output.txt")
    override val controlledResourceManagers: Set<ResourceManager> = setOf(dependenciesDataWriter)

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
            desc?.type?.constructor?.declarationDescriptor?.fqNameSafe
            desc?.type
        }

        val classifiers = PsiProvider.extractElementsOfTypeFromProject(project, KtClass::class.java).map {
            val desc = it.resolveToDescriptorIfAny()
            desc?.typeConstructor?.declarationDescriptor
        }

        val extractor = BasicExtractor(rf)
        val entities = makeEntities(rf, project, extractor)
        val result = solve(entities, KotlinBasicTypes, JavaAssumedImmutableTypes, KotlinCollections)
        //dependenciesDataWriter.writer.println(entities)
        dependenciesDataWriter.writer.println(result)

        dependenciesDataWriter.writer.println("properties")
        dependenciesDataWriter.writer.println(properties)
        dependenciesDataWriter.writer.println("classes")
        dependenciesDataWriter.writer.println(classifiers)
        dependenciesDataWriter.writer.println("")
    }
}
