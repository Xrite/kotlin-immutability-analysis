package org.jetbrains.research.ml.kotlinAnalysis.immutability

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.caches.resolve.resolveToParameterDescriptorIfAny
import org.jetbrains.kotlin.idea.resolve.ResolutionFacade
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode

inline fun <reified R> Iterable<*>.anyIsInstance(): Boolean = this.any { it is R }

fun KtClassOrObject.resolveToDescriptorIfAny(
    resolutionFacade: ResolutionFacade?,
    bodyResolveMode: BodyResolveMode = BodyResolveMode.FULL
): ClassDescriptor? =
    if (resolutionFacade == null) resolveToDescriptorIfAny(bodyResolveMode)
    else resolveToDescriptorIfAny(resolutionFacade, bodyResolveMode)

fun KtProperty.resolveToDescriptorIfAny(
    resolutionFacade: ResolutionFacade?,
    bodyResolveMode: BodyResolveMode = BodyResolveMode.FULL
): VariableDescriptor? =
    if (resolutionFacade == null) resolveToDescriptorIfAny(bodyResolveMode)
    else resolveToDescriptorIfAny(resolutionFacade, bodyResolveMode)

fun KtParameter.resolveToDescriptorIfAny(
    resolutionFacade: ResolutionFacade?,
    bodyResolveMode: BodyResolveMode = BodyResolveMode.FULL
): ValueParameterDescriptor? =
    if (resolutionFacade == null) resolveToParameterDescriptorIfAny(bodyResolveMode)
    else resolveToParameterDescriptorIfAny(resolutionFacade, bodyResolveMode)
