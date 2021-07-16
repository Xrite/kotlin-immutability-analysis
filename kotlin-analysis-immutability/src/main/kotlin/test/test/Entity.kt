package test.test

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor

sealed class Entity
//data class ParameterizedClassTemplate(val desc: ClassDescriptor, val dependencies: Dependencies) : Entity()
data class ClassTemplate(val desc: DeclarationDescriptor, val dependencies: Dependencies) : Entity()
data class ObjectTemplate(val desc: DeclarationDescriptor, val dependencies: Dependencies) : Entity()
object ErrorTemplate : Entity()
