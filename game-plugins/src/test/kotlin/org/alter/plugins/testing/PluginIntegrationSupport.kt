package org.alter.plugins.testing

import org.alter.game.DevContext
import org.alter.game.GameContext
import org.alter.game.Server
import org.alter.game.model.PlayerUID
import org.alter.game.model.Tile
import org.alter.game.model.World
import org.alter.game.model.entity.Player
import org.alter.game.saving.formats.SaveFormatType

/**
 * Shared helpers for cache-free plugin integration tests.
 *
 * Plugin constructors register their combat definitions and loot tables through
 * the [org.alter.game.plugin.PluginRepository] attached to [World], so a bare
 * [World] is enough to exercise registration without booting the server, loading
 * the cache, or scanning the classpath.
 */
object PluginIntegrationSupport {

    fun newWorld(name: String = "plugin-test"): World {
        val gameContext =
            GameContext(
                initialLaunch = false,
                name = name,
                revision = 228,
                saveFormat = SaveFormatType.JSON,
                cycleTime = 600,
                playerLimit = 10,
                home = Tile(0, 0, 0),
                skillCount = 25,
                npcStatCount = 10,
                runEnergy = false,
                gItemPublicDelay = 100,
                gItemDespawnDelay = 300,
                preloadMaps = false,
            )
        val devContext =
            DevContext(
                debugExamines = false,
                debugObjects = false,
                debugButtons = false,
                debugItemActions = false,
                debugMagicSpells = false,
                debugPackets = false,
            )
        return World(gameContext, devContext)
    }

    fun newPlayer(world: World, uid: String = "plugin-test-player"): Player =
        Player(world).apply { this.uid = PlayerUID(uid) }

    fun newServer(): Server = Server()
}
