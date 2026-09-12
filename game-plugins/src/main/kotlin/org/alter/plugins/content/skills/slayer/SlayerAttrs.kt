package org.alter.plugins.content.skills.slayer

import org.alter.game.model.attr.AttributeKey

/**
 * Persistent player state for an active Slayer assignment.
 *
 * The keys are declared in the plugin module (rather than the engine's
 * [org.alter.game.model.attr.Attributes]) so that Slayer can be added without
 * touching `game-server`.
 */
val SLAYER_TASK_ATTR = AttributeKey<Int>(persistenceKey = "slayer_task")

val SLAYER_REMAINING_ATTR = AttributeKey<Int>(persistenceKey = "slayer_remaining")

val SLAYER_MASTER_ATTR = AttributeKey<Int>(persistenceKey = "slayer_master")

val SLAYER_POINTS_ATTR = AttributeKey<Int>(persistenceKey = "slayer_points")
