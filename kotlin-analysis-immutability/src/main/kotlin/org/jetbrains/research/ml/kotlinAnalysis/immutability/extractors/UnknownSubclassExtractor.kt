package org.jetbrains.research.ml.kotlinAnalysis.immutability.extractors

import org.jetbrains.kotlin.descriptors.isFinalOrEnum
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.research.ml.kotlinAnalysis.immutability.Dependencies
import org.jetbrains.research.ml.kotlinAnalysis.immutability.dependencies.UnknownSubclass
import org.jetbrains.research.ml.kotlinAnalysis.immutability.resolveToDescriptorIfAny

class UnknownSubclassExtractor(private val resolutionFacade: ResolutionFacade?) : Extractor<Dependencies> {
    override fun fromClass(psiElement: KtClass): Dependencies =
        psiElement.resolveToDescriptorIfAny(resolutionFacade)?.let { descriptor ->
            when {
                psiElement.isSealed() -> listOf()
                descriptor.isFinalOrEnum -> listOf()
                else -> listOf(UnknownSubclass(descriptor))
            }
        } ?: resolveErrorFor(psiElement)

    override fun fromObject(psiElement: KtObjectDeclaration): Dependencies = listOf()
}
