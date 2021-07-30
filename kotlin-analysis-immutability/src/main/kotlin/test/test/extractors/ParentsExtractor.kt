package test.test.extractors

import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtClassOrObject
import test.test.*

class ParentsExtractor(private val resolutionFacade: ResolutionFacade?) : ClassOrObjectExtractor<Dependencies>() {
    override fun fromClassOrObject(psiElement: KtClassOrObject): Dependencies =
        psiElement.resolveToDescriptorIfAny(resolutionFacade)?.let { descriptor ->
            val parents = descriptor.typeConstructor.supertypes.map {
                Dependency.Parent.fromKotlinType(it)
            }
            parents
        } ?: resolveErrorFor(psiElement)
}
