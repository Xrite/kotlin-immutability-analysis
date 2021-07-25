package test.test

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor

sealed class Entity

data class ClassTemplate(
    val desc: DeclarationDescriptor,
    val classType: ClassType,
    val dependencies: Dependencies,
    val parameters: List<TypeParameterDescriptor> = listOf()
) : Entity() {
    /*
    override fun toString(): String {
        return desc.toString() + "\n" +
                dependencies.joinToString(separator = "\n", prefix = "[", postfix = "]") { it.toString() }
    }
     */

}

/*
data class ObjectTemplate(val desc: DeclarationDescriptor, val objectType: ObjectType, val dependencies: Dependencies) : Entity() {
    /*
    override fun toString(): String {
        return desc.toString() + "\n" +
                dependencies.joinToString(separator = "\n", prefix = "[", postfix = "]") { it.toString() }
    }
     */

}
 */

object ErrorTemplate : Entity()

enum class ClassType {
    INTERFACE,
    CLASS,
    SEALED_CLASS,
    DATA_CLASS,
    ENUM_CLASS,
    OBJECT,
    COMPANION_OBJECT,
    ANONYMOUS_OBJECT
}

enum class ObjectType {
}
