package org.jetbrains.research.ml.kotlinAnalysis.immutability.extractors

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.research.ml.kotlinAnalysis.immutability.*
import org.jetbrains.research.ml.kotlinAnalysis.psi.extentions.extractElementsOfType
import org.jetbrains.research.ml.kotlinAnalysis.util.isKotlinRelatedFile
import org.jetbrains.research.ml.kotlinAnalysis.immutability.dependencies.Error

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
    listOf(Error("Cannot resolve descriptor for $psiElement"))

class MultipleExtractors(private val extractors: Collection<Extractor<Dependencies>>) : Extractor<Dependencies> {
    override fun fromClass(psiElement: KtClass): Dependencies =
        extractors.flatMap { it.fromClass(psiElement) }

    override fun fromObject(psiElement: KtObjectDeclaration): Dependencies =
        extractors.flatMap { it.fromObject(psiElement) }

    constructor(vararg extractors: Extractor<Dependencies>) : this(extractors.toList())
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
                descriptor.kind == ClassKind.ENUM_ENTRY -> ClassType.ENUM_ENTRY
                descriptor.kind == ClassKind.ANNOTATION_CLASS -> ClassType.ANNOTATION_CLASS
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

            ClassTemplate(it, type, deps, it.typeConstructor.parameters)
        } ?: ErrorTemplate
    }
}

fun extractPsiFilesWithoutTests(project: Project): Set<PsiFile>? {
    val projectPsiFiles = mutableSetOf<PsiFile>()
    val psiManager = PsiManager.getInstance(project)
    ModuleManager.getInstance(project).modules.forEach {
        ModuleRootManager.getInstance(it).getSourceRoots(false).forEach {
            VfsUtilCore.iterateChildrenRecursively(it, null) { virtualFile ->
                if (!virtualFile.isKotlinRelatedFile() || virtualFile.canonicalPath == null) {
                    return@iterateChildrenRecursively true
                }
                val psi = psiManager.findFile(virtualFile) ?: return@iterateChildrenRecursively true
                projectPsiFiles.add(psi)
            }
        }
    }
    return projectPsiFiles.ifEmpty { null }
}

sealed class ExtractionResult<T : PsiElement> {
    data class Opened<T : PsiElement>(val elements: List<T>, val type: TestsType) : ExtractionResult<T>()
    data class Failed(val reason: String) : ExtractionResult<Nothing>()
}

fun extractPsiFilesWithoutTestPrefix(project: Project): Set<PsiFile> {
    val projectPsiFiles = mutableSetOf<PsiFile>()
    val projectRootManager = ProjectRootManager.getInstance(project)
    val psiManager = PsiManager.getInstance(project)

    projectRootManager.contentRoots.mapNotNull { root ->
        VfsUtilCore.iterateChildrenRecursively(root, null) { virtualFile ->
            if (!virtualFile.isKotlinRelatedFile() || virtualFile.canonicalPath == null || virtualFile.canonicalPath!!.contains(
                    "/test/"
                )
            ) {
                return@iterateChildrenRecursively true
            }
            val psi = psiManager.findFile(virtualFile) ?: return@iterateChildrenRecursively true
            projectPsiFiles.add(psi)
        }
    }
    return projectPsiFiles
}

fun <T : PsiElement> extractElementsOfTypeFromProject(
    project: Project,
    psiElementClass: Class<T>,
    includeTests: Boolean
): ExtractionResult.Opened<T> {
    if (!includeTests) {
        val withoutTests = extractPsiFilesWithoutTests(project)
            ?: return ExtractionResult.Opened(extractPsiFilesWithoutTestPrefix(project)
                .map { PsiTreeUtil.collectElementsOfType(it, psiElementClass) }
                .flatten(), TestsType.WITHOUT_TEST_FOLDERS)
        return ExtractionResult.Opened(withoutTests
            .map { PsiTreeUtil.collectElementsOfType(it, psiElementClass) }
            .flatten(), TestsType.WITHOUT_TEST_MODULES)
    } else {
        return ExtractionResult.Opened(
            project.extractElementsOfType(psiElementClass),
            TestsType.WITH_TESTS
        )
    }
}

enum class TestsType {
    WITH_TESTS,
    WITHOUT_TEST_MODULES,
    WITHOUT_TEST_FOLDERS
}

fun makeEntities(
    resolutionFacade: ResolutionFacade?,
    project: Project,
    extractor: Extractor<Dependencies>,
    includeTests: Boolean
): Pair<List<Entity>, TestsType> {
    val packer = Packer(resolutionFacade)
    val (classes, type) = extractElementsOfTypeFromProject(project, KtClass::class.java, includeTests)
    val fromClasses = classes.map {
        packer.packClass(it, extractor.fromClass(it))
    }
    val (objects, type2) = extractElementsOfTypeFromProject(project, KtObjectDeclaration::class.java, includeTests)
    val fromObjects = objects.map {
        packer.packObject(it, extractor.fromObject(it))
    }
    assert(type == type2)
    return fromClasses + fromObjects to type
}

fun makeExtractor(resolutionFacade: ResolutionFacade?, taskConfiguration: TaskConfiguration): Extractor<Dependencies> {
    val ex = mutableListOf<Extractor<Dependencies>>()
    if (taskConfiguration.analyzeSealedSubclasses) {
        ex.add(SealedSubclassesExtractor(resolutionFacade))
    }
    if (taskConfiguration.assumeNastyInheritors) {
        ex.add(UnknownSubclassExtractor(resolutionFacade))
    }
    ex.add(
        PropertiesExtractorWithFlags(
            resolutionFacade,
            treatLazyAsImmutable = taskConfiguration.treatLazyAsImmutable,
            assumeGoodGetters = taskConfiguration.assumeGoodGetters,
            analyzeDelegates = taskConfiguration.analyzeDelegates
        )
    )
    ex.add(ValueParametersExtractor(resolutionFacade))
    ex.add(ParentsExtractor(resolutionFacade))
    ex.add(OuterClassesExtractor(resolutionFacade))
    return MultipleExtractors(ex)
}