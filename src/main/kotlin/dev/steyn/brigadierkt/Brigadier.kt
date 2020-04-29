package dev.steyn.brigadierkt

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import java.util.function.Predicate

inline fun <S, A : ArgumentBuilder<S, A>> A.literal(literal: String, action: LiteralArgumentBuilder<S>.() -> Unit) {
    this.then(LiteralArgumentBuilder.literal<S>(literal).apply(action))
}

inline fun <S, A : ArgumentBuilder<S, A>, reified T> A.argument(argumentName: String, type: ArgumentType<T>, action: RequiredArgumentBuilder<S, T>.(Argument<T>) -> Unit) {
    val argument = Argument(argumentName, T::class.java)
    val node = RequiredArgumentBuilder.argument<S, T>(argumentName, type)
    node.action(argument)
    this.then(node)
}

fun <S, A : ArgumentBuilder<S, A>> A.require(requirement: Predicate<S>) {
    val req = this.requirement
    if(req is MultiPredicate<S>) {
        req.predicates.add(requirement)
    } else {
        this.requires(MultiPredicate(arrayListOf(req, requirement)))
    }
}