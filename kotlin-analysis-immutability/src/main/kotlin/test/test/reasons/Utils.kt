package test.test.reasons

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils
import org.jetbrains.kotlin.psi.psiUtil.getTextWithLocation
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils

val DeclarationDescriptor.location: String?
    get() = DescriptorToSourceUtils.getSourceFromDescriptor(this)?.let { PsiDiagnosticUtils.atLocation(it) }

val DeclarationDescriptor.textWithLocation: String?
    get() = DescriptorToSourceUtils.getSourceFromDescriptor(this)?.getTextWithLocation()

val DeclarationDescriptor.basicInfo: Array<Pair<String, *>>
    get() = arrayOf(
        "descriptor" to this.toString(),
        "textWithLocation" to this.textWithLocation,
        "location" to this.location
    )
