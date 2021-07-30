package test.test.extractors

import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.isPropertyParameter
import test.test.*

class ValueParametersExtractor(private val resolutionFacade: ResolutionFacade?) : Extractor<Dependencies> {
    override fun fromClass(psiElement: KtClass): Dependencies =
        psiElement.resolveToDescriptorIfAny(resolutionFacade)?.let { type ->
            // TODO: check that primaryConstructorParameters returns
            //       empty list for non-existing primary ctor
            val parameters = psiElement.primaryConstructorParameters.filter {
                it.isPropertyParameter()
            }.map { parameter ->
                parameter.resolveToDescriptorIfAny(resolutionFacade)?.let {
                    if (parameter.isMutable) {
                        Dependency.VarParameter.fromDescriptor(it)
                    } else {
                        Dependency.ValParameter.fromDescriptor(it)
                    }
                } ?: Dependency.Error("Cannot resolve $parameter")
            }
            parameters
        } ?: resolveErrorFor(psiElement)

    override fun fromObject(psiElement: KtObjectDeclaration): Dependencies = listOf()
}
