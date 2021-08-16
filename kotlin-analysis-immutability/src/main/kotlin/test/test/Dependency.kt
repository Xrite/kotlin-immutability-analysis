package test.test

import org.jetbrains.kotlin.types.KotlinType

abstract class Dependency {
    abstract fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty
}


typealias Dependencies = List<Dependency>
