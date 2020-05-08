package dev.steyn.brigadierkt

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.ArgumentCommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import java.util.function.Predicate

/**
 * Register a new command
 * @param literal The name of the command.
 */
inline fun <S, C : CommandDispatcher<S>> C.command(literal: String, action: LiteralArgumentBuilder<S>.() -> Unit): LiteralCommandNode<S> {
    return this.register(LiteralArgumentBuilder.literal<S>(literal).apply(action))
}

/**
 * Register a new Literal Command Node
 * @see com.mojang.brigadier.builder.LiteralArgumentBuilder
 * @return The current command Node
 * @param literal The name of the current Literal Node.
 */
inline fun <S> ArgumentBuilder<S, *>.literal(literal: String, action: LiteralArgumentBuilder<S>.() -> Unit): LiteralCommandNode<S> {
    val result = LiteralArgumentBuilder.literal<S>(literal).apply(action).build()
    this.then(result)
    return result
}

/**
 * Register a new Literal Command Node
 * @see com.mojang.brigadier.builder.LiteralArgumentBuilder
 * @return The current command Node
 * @param literal The name of the current Literal Node.
 */
inline fun <S> ArgumentBuilder<S, *>.literal(literal: String, aliases: Array<String>, action: LiteralArgumentBuilder<S>.() -> Unit): LiteralCommandNode<S> {
    val node = literal(literal, action)
    for (alias in aliases) {
        this.then(LiteralArgumentBuilder.literal<S>(alias).redirect(node).build())
    }
    return node
}

/**
 * Register a new Argument Command Node
 * @see com.mojang.brigadier.builder.RequiredArgumentBuilder
 * @return The current command Node
 * @param argumentName The name of the argument
 * @param type The type of the argument
 */
inline fun <S, reified T> ArgumentBuilder<S, *>.argument(argumentName: String, type: ArgumentType<T>, action: RequiredArgumentBuilder<S, T>.(Argument<T>) -> Unit): ArgumentCommandNode<S, T> {
    val argument = Argument(argumentName, T::class.java)
    val node = RequiredArgumentBuilder.argument<S, T>(argumentName, type).apply {
        action(argument)
    }.build()
    this.then(node)
    return node
}

/**
 * Add a requirement to this Node.
 *
 * The current Mojang system only allows one requirement.
 * This method allows you to add multiple requirements.
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