package test.test

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.research.ml.kotlinAnalysis.psi.PsiProvider
import org.jetbrains.research.ml.kotlinAnalysis.util.isKotlinRelatedFile

interface Extractor<R> {
    fun fromClass(psiElement: KtClass): R
    fun fromObject(psiElement: KtObjectDeclaration): R
}

abstract class ClassOrObjectExtractor<R> : Extractor<R> {
    override fun fromClass(psiElement: KtClass): R = fromClassOrObject(psiElement)

    override fun fromObject(psiElement: KtObjectDeclaration): R = fromClassOrObject(psiElement)

    abstract fun fromClassOrObject(psiElement: KtClassOrObject): R
}

fun resolveErrorFor(psiElement: KtElement) =
    listOf(Dependency.Error("Cannot resolve descriptor for $psiElement"))

class MultipleExtractors(private vararg val extractors: Extractor<Dependencies>) : Extractor<Dependencies> {
    override fun fromClass(psiElement: KtClass): Dependencies =
        extractors.flatMap { it.fromClass(psiElement) }

    override fun fromObject(psiElement: KtObjectDeclaration): Dependencies =
        extractors.flatMap { it.fromObject(psiElement) }
}

class Packer(private val resolutionFacade: ResolutionFacade?) {
    fun packClass(ktClass: KtClass, deps: Dependencies): Entity {
        val descriptor = ktClass.resolveToDescriptorIfAny(resolutionFacade)
        return descriptor?.let {
            val type = when {
                ktClass.isData() -> ClassType.DATA_CLASS
                ktClass.isEnum() -> ClassType.ENUM_CLASS
                ktClass.isInterface() -> ClassType.INTERFACE
                ktClass.isSealed() -> ClassType.SEALED_CLASS
                else -> ClassType.CLASS
            }
            ClassTemplate(it, type, deps, it.typeConstructor.parameters)
        } ?: ErrorTemplate
    }

    fun packObject(ktObjectDeclaration: KtObjectDeclaration, deps: Dependencies): Entity {
        val descriptor = ktObjectDeclaration.resolveToDescriptorIfAny(resolutionFacade)
        return descriptor?.let {
            val type = when {
                ktObjectDeclaration.isCompanion() -> ClassType.COMPANION_OBJECT
                ktObjectDeclaration.isObjectLiteral() -> ClassType.ANONYMOUS_OBJECT
                else -> ClassType.OBJECT
            }
            ClassTemplate(it, type, deps)
        } ?: ErrorTemplate
    }
}

fun extractPsiFilesWithoutTests(project: Project): Set<PsiFile> {
    val projectPsiFiles = mutableSetOf<PsiFile>()
    //val projectRootManager = ProjectRootManager.getInstance(project)
    val psiManager = PsiManager.getInstance(project)
    ModuleManager.getInstance(project).modules.forEach {
        println("module: $it")
        ModuleRootManager.getInstance(it).getSourceRoots(false).forEach {
            //println(it)
            VfsUtilCore.iterateChildrenRecursively(it, null) { virtualFile ->
                if (!virtualFile.isKotlinRelatedFile() || virtualFile.canonicalPath == null) {
                    return@iterateChildrenRecursively true
                }
                val psi = psiManager.findFile(virtualFile) ?: return@iterateChildrenRecursively true
                projectPsiFiles.add(psi)
            }
        }
    }
    return projectPsiFiles
}

fun <T : PsiElement> extractElementsOfTypeFromProject(
    project: Project,
    psiElementClass: Class<T>
): List<T> {
    return extractPsiFilesWithoutTests(project)
        .map { PsiTreeUtil.collectElementsOfType(it, psiElementClass) }
        .flatten()
}

fun makeEntities(
    resolutionFacade: ResolutionFacade?,
    project: Project,
    extractor: Extractor<Dependencies>
): List<Entity> {
    val packer = Packer(resolutionFacade)
    val classes = PsiProvider.extractElementsOfTypeFromProject(project, KtClass::class.java).map {
        packer.packClass(it, extractor.fromClass(it))
    }
    val objects = PsiProvider.extractElementsOfTypeFromProject(project, KtObjectDeclaration::class.java).map {
        packer.packObject(it, extractor.fromObject(it))
    }
    return classes + objects
}
