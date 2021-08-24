package test.test.extractors

import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.idea.analysis.computeTypeInContext
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.caches.resolve.getResolutionFacade
import org.jetbrains.kotlin.idea.refactoring.fqName.fqName
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.idea.util.getResolutionScope
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.descriptorUtil.isExtension
import org.jetbrains.kotlin.resolve.descriptorUtil.isExtensionProperty
import org.jetbrains.kotlin.utils.addToStdlib.firstNotNullResult
import test.test.*
import test.test.dependencies.*


class PropertiesExtractor(
    private val resolutionFacade: ResolutionFacade?,
    private val priority: List<(KtProperty, VariableDescriptor) -> Dependency?>,
) :
    ClassOrObjectExtractor<Dependencies>() {
    override fun fromClassOrObject(psiElement: KtClassOrObject): Dependencies =
        psiElement.resolveToDescriptorIfAny(resolutionFacade)?.let { classifierDesc ->
            val properties = psiElement.body?.properties?.map { prop ->
                prop.resolveToDescriptorIfAny(resolutionFacade)?.let { desc ->
                    priority.firstNotNullResult { it(prop, desc) }
                } ?: Error("Cannot resolve $prop")
            }.orEmpty()
            properties
        } ?: resolveErrorFor(psiElement)

}

typealias F = (KtProperty, VariableDescriptor) -> Dependency?

fun extractBase(property: KtProperty, descriptor: VariableDescriptor): Dependency =
    when {
        descriptor.isExtensionProperty -> ExtensionProperty.fromDescriptor(descriptor)
        descriptor.isVar -> VarProperty.fromDescriptor(descriptor)
        else -> ValProperty.fromDescriptor(descriptor)
    }

fun extractDelegate(property: KtProperty, descriptor: VariableDescriptor): Dependency? =
    property.delegate?.let {
        DelegatedValProperty.fromDescriptor(descriptor, it)
    }

fun extractLazyDelegate(property: KtProperty, descriptor: VariableDescriptor): Dependency? =
    property.delegate?.let {
        val expression = it.expression ?: TODO()
        val bindingContext = expression.analyze()
        //val scope = expression.getResolutionScope(bindingContext, expression.getResolutionFacade())
        val scope = expression.getResolutionScope()
        val expressionType = expression.computeTypeInContext(scope) ?: return null
        if (expressionType.fqName?.asString() == "kotlin.Lazy") {
            LazyValProperty.fromDescriptor(descriptor, it)
        } else {
            null
        }
    }

fun extractGetter(property: KtProperty, descriptor: VariableDescriptor): Dependency? =
    property.getter?.let { PropertyWithGetter.fromDescriptor(descriptor) }
