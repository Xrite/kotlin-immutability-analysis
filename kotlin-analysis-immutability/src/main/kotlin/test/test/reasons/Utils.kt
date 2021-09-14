package test.test.reasons

import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils
import org.jetbrains.kotlin.psi.psiUtil.getTextWithLocation
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils

val DeclarationDescriptor.location: String?
    get() = DescriptorToSourceUtils.getSourceFromDescriptor(this)?.let { PsiDiagnosticUtils.atLocation(it) }

val DeclarationDescriptor.textWithLocation: String?
    get() = DescriptorToSourceUtils.getSourceFromDescriptor(this)?.getTextWithLocation()

val DeclarationDescriptor.text: String?
    get() = DescriptorToSourceUtils.getSourceFromDescriptor(this)?.getText()

val DeclarationDescriptor.containingFilePath: String?
    get() {
        val descriptor = DescriptorToSourceUtils.getSourceFromDescriptor(this)
        val file = try {
            descriptor?.getContainingFile()
        } catch (_: Exception) {
            null
        }
        val virtualFile = file?.getVirtualFile()
        return virtualFile?.path
    }

val DeclarationDescriptor.basicInfo: Array<Pair<String, *>>
    get() = arrayOf(
        "descriptor" to this.toString(),
        "location" to this.location,
        //"text" to this.text,
        "containingFilePath" to this.containingFilePath,
    )
