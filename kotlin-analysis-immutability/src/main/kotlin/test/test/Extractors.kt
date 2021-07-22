package test.test

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.idea.search.usagesSearch.descriptor
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.isPropertyParameter
import org.jetbrains.research.ml.kotlinAnalysis.psi.PsiProvider

interface Extractor<R> {
    fun fromClass(psiElement: KtClass): R
    fun fromObject(psiElement: KtObjectDeclaration): R
}

class BasicExtractor(private val resolutionFacade: ResolutionFacade?) : Extractor<Dependencies> {
    private fun fromClassOrObject(psiElement: KtClassOrObject): Dependencies =
        psiElement.resolveToDescriptorIfAny(resolutionFacade)?.let { type ->
            val properties = psiElement.body?.properties?.map { ktProperty ->
                //val ctx = ktProperty.analyzeWithContentAndGetResult(ktProperty.getResolutionFacade()).bindingContext
                //val some = ktProperty.kotlinType(ctx)
                ktProperty.resolveToDescriptorIfAny(resolutionFacade)?.let { desc ->
                    if (desc.isVar) {
                        Dependency.VarTo.fromDescriptor(desc)
                    } else {
                        Dependency.ValTo.fromDescriptor(desc)
                    }
                } ?: Dependency.Error("Cant resolve $ktProperty")
            }.orEmpty()
            val parents = type.typeConstructor.supertypes.map {
                Dependency.Parent.fromKotlinType(it)
            }
            val outer = if (type.isInner) {
                listOf(Dependency.Outer(type.containingDeclaration as ClassifierDescriptor))
            } else {
                listOf()
            }
            parents + properties + outer
        } ?: listOf(Dependency.Error("Cannot resolve descriptor for $psiElement"))


    override fun fromClass(psiElement: KtClass): Dependencies {
        //return fromClassOrObject(psiElement)
        // val context = resolutionFacade?.let { psiElement.analyzeWithContent(it) } ?: psiElement.analyzeWithContent()
        return psiElement.resolveToDescriptorIfAny(resolutionFacade)?.let { type ->
            val parameters = psiElement.primaryConstructorParameters.filter {
                it.isPropertyParameter()
            }.mapNotNull { parameter ->
                parameter.descriptor?.let {
                    if (parameter.isMutable) {
                        Dependency.VarParameter.fromDescriptor(it)
                    } else {
                        Dependency.ValParameter.fromDescriptor(it)
                    }
                }
            }
            val properties = psiElement.getProperties().map { ktProperty ->
                //val ctx = ktProperty.analyzeWithContentAndGetResult(ktProperty.getResolutionFacade()).bindingContext
                //val some = ktProperty.kotlinType(ctx)
                ktProperty.resolveToDescriptorIfAny(resolutionFacade)?.let { desc ->
                    if (desc.isVar) {
                        Dependency.VarTo.fromDescriptor(desc)
                    } else {
                        Dependency.ValTo.fromDescriptor(desc)
                    }
                } ?: Dependency.Error("Cant resolve $ktProperty")
            }.orEmpty()
            val parents = type.typeConstructor.supertypes.map {
                Dependency.Parent.fromKotlinType(it)
            }
            val outer = if (type.isInner) {
                listOf(Dependency.Outer(type.containingDeclaration as ClassifierDescriptor))
            } else {
                listOf()
            }
            parameters + parents + properties + outer
        } ?: listOf(Dependency.Error("Cannot resolve descriptor for $psiElement"))
    }

    override fun fromObject(psiElement: KtObjectDeclaration): Dependencies {
        return fromClassOrObject(psiElement)
        /*
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
        val outer = type?.containingDeclaration?.let {
            listOf(Dependency.Outer(it))
        } ?: listOf()
        return parents + properties + outer
         */
    }
}

fun multipleExtractors(extractors: List<Extractor<Dependencies>>) = object : Extractor<Dependencies> {
    override fun fromClass(psiElement: KtClass): Dependencies = extractors.flatMap { it.fromClass(psiElement) }

    override fun fromObject(psiElement: KtObjectDeclaration): Dependencies =
        extractors.flatMap { it.fromObject(psiElement) }
}

class Packer(val resolutionFacade: ResolutionFacade?) {
    fun packClass(ktClass: KtClass, deps: Dependencies): Entity {
        val descriptor = ktClass.resolveToDescriptorIfAny(resolutionFacade)
        return descriptor?.let {
            ClassTemplate(it, deps, it.typeConstructor.parameters)
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
