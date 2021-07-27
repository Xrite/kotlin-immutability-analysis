package test.test

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.isObjectLiteral
import org.jetbrains.kotlin.psi.psiUtil.isPropertyParameter
import org.jetbrains.research.ml.kotlinAnalysis.psi.PsiProvider

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

class PropertiesExtractor(private val resolutionFacade: ResolutionFacade?) : ClassOrObjectExtractor<Dependencies>() {
    override fun fromClassOrObject(psiElement: KtClassOrObject): Dependencies =
        psiElement.resolveToDescriptorIfAny(resolutionFacade)?.let { classifierDesc ->
            val properties = psiElement.body?.properties?.map { prop ->
                prop.resolveToDescriptorIfAny(resolutionFacade)?.let { desc ->
                    if (desc.isVar) {
                        Dependency.VarTo.fromDescriptor(desc)
                    } else {
                        Dependency.ValTo.fromDescriptor(desc)
                    }
                } ?: Dependency.Error("Cannot resolve $prop")
            }.orEmpty()
            properties
        } ?: resolveErrorFor(psiElement)
}

class ValueParametersExtractor(private val resolutionFacade: ResolutionFacade?) : Extractor<Dependencies> {
    override fun fromClass(psiElement: KtClass): Dependencies =
        psiElement.resolveToDescriptorIfAny(resolutionFacade)?.let { type ->
            // TODO: check that primaryConstructorParameters returns
            //       empty list for non-existing primary ctor
            val parameters = psiElement.primaryConstructorParameters.filter {
                it.isPropertyParameter()
            }.map { parameter ->
                parameter.resolveToDescriptorIfAny(resolutionFacade)?.let {
                    if (parameter.isMutable) {
                        Dependency.VarParameter.fromDescriptor(it)
                    } else {
                        Dependency.ValParameter.fromDescriptor(it)
                    }
                } ?: Dependency.Error("Cannot resolve $parameter")
            }
            parameters
        } ?: resolveErrorFor(psiElement)

    override fun fromObject(psiElement: KtObjectDeclaration): Dependencies = listOf()
}

class ParentsExtractor(private val resolutionFacade: ResolutionFacade?) : ClassOrObjectExtractor<Dependencies>() {
    override fun fromClassOrObject(psiElement: KtClassOrObject): Dependencies =
        psiElement.resolveToDescriptorIfAny(resolutionFacade)?.let { descriptor ->
            val parents = descriptor.typeConstructor.supertypes.map {
                Dependency.Parent.fromKotlinType(it)
            }
            parents
        } ?: resolveErrorFor(psiElement)
}

class OuterClassesExtractor(private val resolutionFacade: ResolutionFacade?) : ClassOrObjectExtractor<Dependencies>() {
    override fun fromClassOrObject(psiElement: KtClassOrObject): Dependencies =
        psiElement.resolveToDescriptorIfAny(resolutionFacade)?.let { descriptor ->
            when {
                descriptor.isInner -> listOf(Dependency.Outer(descriptor.containingDeclaration as ClassifierDescriptor))
                psiElement.isObjectLiteral() -> psiElement.containingClassOrObject?.let {
                    it.resolveToDescriptorIfAny(resolutionFacade)?.let {
                        listOf(Dependency.Outer(it))
                    } ?: resolveErrorFor(it)
                } ?: listOf()
                else -> listOf()
            }
        } ?: resolveErrorFor(psiElement)
}

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
