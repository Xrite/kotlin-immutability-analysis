package org.jetbrains.research.ml.kotlinAnalysis.immutability.extractors

import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.research.ml.kotlinAnalysis.immutability.Dependencies
import org.jetbrains.research.ml.kotlinAnalysis.immutability.dependencies.Parent
import org.jetbrains.research.ml.kotlinAnalysis.immutability.resolveToDescriptorIfAny

class ParentsExtractor(private val resolutionFacade: ResolutionFacade?) : ClassOrObjectExtractor<Dependencies>() {
    override fun fromClassOrObject(psiElement: KtClassOrObject): Dependencies =
        psiElement.resolveToDescriptorIfAny(resolutionFacade)?.let { descriptor ->
            val parents = descriptor.typeConstructor.supertypes.map {
                Parent.fromKotlinType(it)
            }
            parents
        } ?: resolveErrorFor(psiElement)
}
