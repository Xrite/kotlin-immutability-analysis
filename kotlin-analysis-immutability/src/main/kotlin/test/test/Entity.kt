package test.test

import com.tylerthrailkill.helpers.prettyprint.pp
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.idea.refactoring.move.postProcessMoveUsages

sealed class Entity
//data class ParameterizedClassTemplate(val desc: ClassDescriptor, val dependencies: Dependencies) : Entity()
data class ClassTemplate(val desc: DeclarationDescriptor, val dependencies: Dependencies) : Entity() {
    override fun toString(): String {
        return desc.toString() + "\n" +
                dependencies.map{ it.toString() }.joinToString(separator = "\n", prefix = "[", postfix = "]")
    }

}
data class ObjectTemplate(val desc: DeclarationDescriptor, val dependencies: Dependencies) : Entity() {
    override fun toString(): String {
        return desc.toString() + "\n" +
                dependencies.map{ it.toString() }.joinToString(separator = "\n", prefix = "[", postfix = "]")
    }

}
object ErrorTemplate : Entity()
