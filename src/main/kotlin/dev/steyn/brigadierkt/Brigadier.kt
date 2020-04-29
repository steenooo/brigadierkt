package dev.steyn.brigadierkt

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import java.util.function.Predicate

/**
 * Register a new command
 * @param literal The name of the command.
 */
inline fun <S, C : CommandDispatcher<S>> C.command(literal: String, action: LiteralArgumentBuilder<S>.() -> Unit) {
    this.register(LiteralArgumentBuilder.literal<S>(literal).apply(action))
}

/**
 * Register a new Literal Command Node
 * @see com.mojang.brigadier.builder.LiteralArgumentBuilder
 *
 * @param literal The name of the current Literal Node.
 */
inline fun <S, A : ArgumentBuilder<S, A>> A.literal(literal: String, action: LiteralArgumentBuilder<S>.() -> Unit) {
    this.then(LiteralArgumentBuilder.literal<S>(literal).apply(action))
}

/**
 * Register a new Argument Command Node
 * @see com.mojang.brigadier.builder.RequiredArgumentBuilder
 *
 * @param argumentName The name of the argument
 * @param type The type of the argument
 */
inline fun <S, A : ArgumentBuilder<S, A>, reified T> A.argument(argumentName: String, type: ArgumentType<T>, action: RequiredArgumentBuilder<S, T>.(Argument<T>) -> Unit) {
    val argument = Argument(argumentName, T::class.java)
    val node = RequiredArgumentBuilder.argument<S, T>(argumentName, type)
    node.action(argument)
    this.then(node)
}

/**
 * Add a requirement to this Node.
 */
fun <S, A : ArgumentBuilder<S, A>> A.require(requirement: Predicate<S>) {
    val req = this.requirement
    if (req is MultiPredicate<S>) {
        req.predicates.add(requirement)
    } else {
        this.requires(MultiPredicate(arrayListOf(req, requirement)))
    }
}

/**
 * Get the value of an Argument
 * @see CommandContext.getArgument
 * @param argument
 */
operator fun <T, S> CommandContext<S>.get(argument: Argument<T>): T {
    return getArgument(argument.name, argument.type)
}