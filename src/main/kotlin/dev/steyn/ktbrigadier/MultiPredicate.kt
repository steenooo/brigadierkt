package dev.steyn.ktbrigadier

import java.util.function.Predicate

class MultiPredicate<S>(
        val predicates: MutableList<Predicate<S>>
) : Predicate<S> {

    override fun test(t: S): Boolean {
        return predicates.all { it.test(t) }
    }
}