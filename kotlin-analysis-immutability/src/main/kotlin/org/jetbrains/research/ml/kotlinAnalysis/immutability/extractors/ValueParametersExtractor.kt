package org.jetbrains.research.ml.kotlinAnalysis.immutability.extractors

import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.isPropertyParameter
import org.jetbrains.research.ml.kotlinAnalysis.immutability.Dependencies
import org.jetbrains.research.ml.kotlinAnalysis.immutability.dependencies.Error
import org.jetbrains.research.ml.kotlinAnalysis.immutability.dependencies.ValParameter
import org.jetbrains.research.ml.kotlinAnalysis.immutability.dependencies.VarParameter
import org.jetbrains.research.ml.kotlinAnalysis.immutability.resolveToDescriptorIfAny

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
                        VarParameter.fromDescriptor(it)
                    } else {
                        ValParameter.fromDescriptor(it)
                    }
                } ?: Error("Cannot resolve $parameter")
            }
            parameters
        } ?: resolveErrorFor(psiElement)

    override fun fromObject(psiElement: KtObjectDeclaration): Dependencies = listOf()
}
