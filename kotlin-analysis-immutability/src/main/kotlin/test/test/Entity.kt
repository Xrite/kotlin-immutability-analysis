package test.test

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor

sealed class Entity

data class ClassTemplate(
    val desc: DeclarationDescriptor,
    val classType: ClassType,
    val dependencies: Dependencies,
    val parameters: List<TypeParameterDescriptor> = listOf()
) : Entity()

object ErrorTemplate : Entity()

enum class ClassType {
    INTERFACE,
    CLASS,
    SEALED_CLASS,
    DATA_CLASS,
    ANNOTATION_CLASS,
    ENUM_CLASS,
    ENUM_ENTRY,
    OBJECT,
    COMPANION_OBJECT,
    ANONYMOUS_OBJECT
}
