package test.test.extractors

import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import test.test.Dependencies
import test.test.dependencies.Parent
import test.test.dependencies.SealedSubclass
import test.test.resolveToDescriptorIfAny

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
