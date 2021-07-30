package test.test.extractors

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.isObjectLiteral
import test.test.*

class OuterClassesExtractor(private val resolutionFacade: ResolutionFacade?) : ClassOrObjectExtractor<Dependencies>() {
    override fun fromClassOrObject(psiElement: KtClassOrObject): Dependencies =
        psiElement.resolveToDescriptorIfAny(resolutionFacade)?.let { descriptor ->
            when {
                descriptor.isInner -> listOf(Dependency.Outer(descriptor.containingDeclaration as ClassifierDescriptor))
                psiElement.isObjectLiteral() -> psiElement.containingClassOrObject?.let {
                    it.resolveToDescriptorIfAny(resolutionFacade)?.let {
                        listOf(Dependency.Outer(it))
                    } ?: resolveErrorFor(it)
                } ?: listOf()
                else -> listOf()
            }
        } ?: resolveErrorFor(psiElement)
}
