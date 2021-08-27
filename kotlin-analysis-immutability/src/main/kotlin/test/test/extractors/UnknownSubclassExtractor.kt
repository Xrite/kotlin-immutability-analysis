package test.test.extractors

import org.apache.maven.model.Dependency
import org.jetbrains.kotlin.descriptors.isFinalOrEnum
import org.jetbrains.kotlin.idea.core.isInheritable
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import test.test.Dependencies
import test.test.dependencies.SealedSubclass
import test.test.dependencies.UnknownSubclass
import test.test.resolveToDescriptorIfAny

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
