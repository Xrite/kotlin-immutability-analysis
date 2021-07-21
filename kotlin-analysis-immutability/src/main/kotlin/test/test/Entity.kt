package test.test

import com.tylerthrailkill.helpers.prettyprint.pp
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.idea.refactoring.move.postProcessMoveUsages

sealed class Entity

data class ClassTemplate(
    val desc: DeclarationDescriptor,
    val dependencies: Dependencies,
    val parameters: List<TypeParameterDescriptor> = listOf()
) : Entity() {
    override fun toString(): String {
        return desc.toString() + "\n" +
                dependencies.joinToString(separator = "\n", prefix = "[", postfix = "]") { it.toString() }
    }

}

data class ObjectTemplate(val desc: DeclarationDescriptor, val dependencies: Dependencies) : Entity() {
    override fun toString(): String {
        return desc.toString() + "\n" +
                dependencies.joinToString(separator = "\n", prefix = "[", postfix = "]") { it.toString() }
    }

}

object ErrorTemplate : Entity()
