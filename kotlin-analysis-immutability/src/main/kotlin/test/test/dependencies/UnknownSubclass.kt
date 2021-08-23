package test.test.dependencies

import test.test.Dependency
import test.test.ImmutabilityProperty
import test.test.ImmutabilityWithContext
import test.test.reasons.mutable.MayHaveMutableSubclass

class UnknownSubclass : Dependency() {
    override fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty {
        return ImmutabilityProperty.Mutable(MayHaveMutableSubclass())
    }
}
