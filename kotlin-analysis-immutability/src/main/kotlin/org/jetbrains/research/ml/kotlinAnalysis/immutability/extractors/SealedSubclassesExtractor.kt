package org.jetbrains.research.ml.kotlinAnalysis.immutability.extractors

import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.research.ml.kotlinAnalysis.immutability.Dependencies
import org.jetbrains.research.ml.kotlinAnalysis.immutability.dependencies.SealedSubclass
import org.jetbrains.research.ml.kotlinAnalysis.immutability.resolveToDescriptorIfAny

class SealedSubclassesExtractor(private val resolutionFacade: ResolutionFacade?) : Extractor<Dependencies> {
    override fun fromClass(psiElement: KtClass): Dependencies =
        psiElement.resolveToDescriptorIfAny(resolutionFacade)?.let { descriptor ->
            val parents = descriptor.sealedSubclasses.map {
                SealedSubclass.fromDescriptor(it)
            }
            parents
        } ?: resolveErrorFor(psiElement)

    override fun fromObject(psiElement: KtObjectDeclaration): Dependencies = listOf()
}
