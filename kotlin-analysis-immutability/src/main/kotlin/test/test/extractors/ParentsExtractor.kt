package test.test.extractors

import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtClassOrObject
import test.test.*
import test.test.dependencies.Parent

class ParentsExtractor(private val resolutionFacade: ResolutionFacade?) : ClassOrObjectExtractor<Dependencies>() {
    override fun fromClassOrObject(psiElement: KtClassOrObject): Dependencies =
        psiElement.resolveToDescriptorIfAny(resolutionFacade)?.let { descriptor ->
            val parents = descriptor.typeConstructor.supertypes.map {
                Parent.fromKotlinType(it)
            }
            parents
        } ?: resolveErrorFor(psiElement)
}
