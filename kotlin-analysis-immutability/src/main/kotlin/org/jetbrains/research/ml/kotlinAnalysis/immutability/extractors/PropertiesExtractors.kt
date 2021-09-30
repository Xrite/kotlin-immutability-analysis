package org.jetbrains.research.ml.kotlinAnalysis.immutability.extractors

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.idea.analysis.computeTypeInContext
import org.jetbrains.kotlin.idea.refactoring.fqName.fqName
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.idea.util.getResolutionScope
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.descriptorUtil.isExtensionProperty
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.research.ml.kotlinAnalysis.immutability.dependencies.*
import org.jetbrains.research.ml.kotlinAnalysis.immutability.Dependencies
import org.jetbrains.research.ml.kotlinAnalysis.immutability.Dependency
import org.jetbrains.research.ml.kotlinAnalysis.immutability.resolveToDescriptorIfAny

fun extractBase(descriptor: VariableDescriptor): Dependency =
    when {
        descriptor.isExtensionProperty -> ExtensionProperty.fromDescriptor(descriptor)
        descriptor.isVar -> VarProperty.fromDescriptor(descriptor as PropertyDescriptor)
        else -> ValProperty.fromDescriptor(descriptor as PropertyDescriptor)
    }

fun extractGetter(property: KtProperty, descriptor: VariableDescriptor): Dependency? =
    property.getter?.let { PropertyWithGetterIsMutable.fromDescriptor(descriptor) }

class PropertiesExtractorWithFlags(
    private val resolutionFacade: ResolutionFacade?,
    private val treatLazyAsImmutable: Boolean,
    private val assumeGoodGetters: Boolean,
    private val analyzeDelegates: Boolean,
) :
    ClassOrObjectExtractor<Dependencies>() {
    override fun fromClassOrObject(psiElement: KtClassOrObject): Dependencies =
        psiElement.resolveToDescriptorIfAny(resolutionFacade)?.let { classifierDesc ->
            val properties = psiElement.body?.properties?.map { prop ->
                prop.resolveToDescriptorIfAny(resolutionFacade)?.let { desc ->
                    fun getDelegateType(): KotlinType? {
                        return prop.delegate?.let {
                            val expression = it.expression ?: return null
                            val scope = expression.getResolutionScope()
                            return expression.computeTypeInContext(scope)
                        }
                    }

                    val delegateType = getDelegateType()
                    val dependencies = mutableListOf<Dependency>()
                    if (delegateType != null) {
                        if (treatLazyAsImmutable && delegateType.fqName?.asString() == "kotlin.Lazy") {
                            dependencies.add(LazyValProperty.fromDescriptor(desc))
                        } else if (analyzeDelegates) {
                            dependencies.add(DelegatedValProperty.fromDescriptor(desc, prop.delegate!!, delegateType))
                        } else {
                            dependencies.add(DelegatedValPropertyIsMutable.fromDescriptor(desc, prop.delegate!!))
                        }
                    }
                    if (!assumeGoodGetters) {
                        extractGetter(prop, desc)?.let { dependencies.add(it) }
                    }
                    dependencies.add(extractBase(desc))
                    return dependencies
                } ?: Error("Cannot resolve $prop")
            }.orEmpty()
            properties
        } ?: resolveErrorFor(psiElement)
}
