package test.test

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.idea.caches.project.isTestModule
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.idea.util.getSourceRoot
import org.jetbrains.kotlin.idea.util.projectStructure.allModules
import org.jetbrains.kotlin.idea.util.sourceRoots
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.isInlineClass
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.research.ml.kotlinAnalysis.AnalysisExecutor
import org.jetbrains.research.ml.kotlinAnalysis.PrintWriterResourceManager
import org.jetbrains.research.ml.kotlinAnalysis.ResourceManager
import org.jetbrains.research.ml.kotlinAnalysis.psi.PsiProvider
import org.jetbrains.research.ml.kotlinAnalysis.util.isKotlinRelatedFile
import java.nio.file.Path

class ImmutabilityAnalysisExecutor(outputDir: Path) : AnalysisExecutor() {
    private val dataWriter = CSVWriterResourceManager(outputDir, "results2.csv")
    override val controlledResourceManagers: Set<ResourceManager> = setOf(dataWriter)

    override fun analyse(project: Project) {
        val rf: ResolutionFacade? = null
        val properties = PsiProvider.extractElementsOfTypeFromProject(project, KtProperty::class.java).forEach {
            val desc = it.resolveToDescriptorIfAny()
            println(desc)
        }

        PsiProvider.extractElementsOfTypeFromProject(project, KtClass::class.java).forEach {
            val desc = it.resolveToDescriptorIfAny()
            //println(desc to it)
        }

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
