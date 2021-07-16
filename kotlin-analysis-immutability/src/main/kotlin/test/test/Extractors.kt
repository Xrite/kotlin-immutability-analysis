package test.test

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.codegen.kotlinType
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.caches.resolve.analyzeWithContentAndGetResult
import org.jetbrains.kotlin.idea.caches.resolve.getResolutionFacade
import org.jetbrains.kotlin.idea.caches.resolve.resolveToCall
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.research.ml.kotlinAnalysis.psi.PsiProvider

interface Extractor<R> {
    fun fromClass(psiElement: KtClass): R
    fun fromObject(psiElement: KtObjectDeclaration): R
}

class BasicExtractor(private val resolutionFacade: ResolutionFacade?) : Extractor<Dependencies> {
    override fun fromClass(psiElement: KtClass): Dependencies {
        // val context = resolutionFacade?.let { psiElement.analyzeWithContent(it) } ?: psiElement.analyzeWithContent()
        val type = psiElement.resolveToDescriptorIfAny(resolutionFacade)
        val properties = psiElement.getProperties().map { ktProperty ->
            val ctx = ktProperty.analyzeWithContentAndGetResult(ktProperty.getResolutionFacade()).bindingContext
            val some = ktProperty.kotlinType(ctx)
            ktProperty.resolveToDescriptorIfAny(resolutionFacade)?.let { desc ->
                if (desc.isVar) {
                    Dependency.VarTo.fromDescriptor(desc)
                } else {
                    Dependency.ValTo.fromDescriptor(desc)
                }
            } ?: Dependency.Error("Cant resolve $ktProperty")
        }
        val parents = type?.typeConstructor?.supertypes?.map {
            Dependency.Parent.fromKotlinType(it)
        }.orEmpty()
        return parents + properties
    }

    override fun fromObject(psiElement: KtObjectDeclaration): Dependencies {
        val type = psiElement.resolveToDescriptorIfAny(resolutionFacade)
        val properties = psiElement.body?.properties?.map { ktProperty ->
            val ctx = ktProperty.analyzeWithContentAndGetResult(ktProperty.getResolutionFacade()).bindingContext
            val some = ktProperty.kotlinType(ctx)
            ktProperty.resolveToDescriptorIfAny(resolutionFacade)?.let { desc ->
                if (desc.isVar) {
                    Dependency.VarTo.fromDescriptor(desc)
                } else {
                    Dependency.ValTo.fromDescriptor(desc)
                }
            } ?: Dependency.Error("Cant resolve $ktProperty")
        }.orEmpty()
        val parents = type?.typeConstructor?.supertypes?.map {
            Dependency.Parent.fromKotlinType(it)
        }.orEmpty()
        return parents + properties
    }
}

fun multipleExtractors(extractors: List<Extractor<Dependencies>>) = object : Extractor<Dependencies> {
    override fun fromClass(psiElement: KtClass): Dependencies = extractors.flatMap { it.fromClass(psiElement) }

    override fun fromObject(psiElement: KtObjectDeclaration): Dependencies =
        extractors.flatMap { it.fromObject(psiElement) }
}

class Packer(val resolutionFacade: ResolutionFacade?) {
    fun packClass(ktClass: KtClass, deps: Dependencies): Entity {
        val type = ktClass.resolveToDescriptorIfAny(resolutionFacade)
        return type?.let {
            ClassTemplate(it, deps)
        } ?: ErrorTemplate
    }

    fun packObject(ktObjectDeclaration: KtObjectDeclaration, deps: Dependencies): Entity {
        val type = ktObjectDeclaration.resolveToDescriptorIfAny(resolutionFacade)
        return type?.let { ObjectTemplate(it, deps) } ?: ErrorTemplate
    }
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
