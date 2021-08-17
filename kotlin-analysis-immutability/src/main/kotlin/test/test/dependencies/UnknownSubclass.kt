package test.test.dependencies

import org.jetbrains.kotlin.types.KotlinType
import test.test.Dependency
import test.test.ImmutabilityMap
import test.test.ImmutabilityProperty
import test.test.reasons.mutable.MayHaveMutableSubclass

class UnknownSubclass : Dependency() {
    override fun recalculate(resolve: (KotlinType) -> ImmutabilityMap.Result): ImmutabilityProperty {
        return ImmutabilityProperty.Mutable(MayHaveMutableSubclass())
    }
}
