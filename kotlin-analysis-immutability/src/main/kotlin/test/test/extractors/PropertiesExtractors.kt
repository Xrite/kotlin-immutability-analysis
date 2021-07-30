package test.test.extractors

import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.utils.addToStdlib.firstNotNullResult
import test.test.*

class PropertiesExtractor(
    private val resolutionFacade: ResolutionFacade?,
    private vararg val priority: (KtProperty, VariableDescriptor) -> Dependency?
) :
    ClassOrObjectExtractor<Dependencies>() {
    override fun fromClassOrObject(psiElement: KtClassOrObject): Dependencies =
        psiElement.resolveToDescriptorIfAny(resolutionFacade)?.let { classifierDesc ->
            val properties = psiElement.body?.properties?.map { prop ->
                prop.resolveToDescriptorIfAny(resolutionFacade)?.let { desc ->
                    priority.firstNotNullResult { it(prop, desc) }
                } ?: Dependency.Error("Cannot resolve $prop")
            }.orEmpty()
            properties
        } ?: resolveErrorFor(psiElement)
}

fun extractBase(property: KtProperty, descriptor: VariableDescriptor): Dependency =
    if (descriptor.isVar) {
        Dependency.VarProperty.fromDescriptor(descriptor)
    } else {
        Dependency.ValProperty.fromDescriptor(descriptor)
    }

fun extractDelegate(property: KtProperty, descriptor: VariableDescriptor): Dependency? =
    property.delegate?.let {
        Dependency.DelegatedValProperty.fromDescriptor(descriptor, it)
    }

fun extractGetter(property: KtProperty, descriptor: VariableDescriptor): Dependency? =
    property.getter?.let { Dependency.PropertyWithGetter.fromDescriptor(descriptor) }
