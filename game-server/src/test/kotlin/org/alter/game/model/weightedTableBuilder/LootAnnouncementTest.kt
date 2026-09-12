package org.alter.game.model.weightedTableBuilder

import net.rsprot.protocol.message.OutgoingGameMessage
import org.alter.game.DevContext
import org.alter.game.GameContext
import org.alter.game.model.PlayerUID
import org.alter.game.model.Tile
import org.alter.game.model.World
import org.alter.game.model.entity.Player
import org.alter.game.saving.formats.SaveFormatType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class LootAnnouncementTest {
    private class CapturingPlayer(world: World) : Player(world) {
        val messages = mutableListOf<OutgoingGameMessage>()

        override fun write(vararg messages: OutgoingGameMessage) {
            this.messages += messages
        }
    }

    private fun newWorld(rareDropAnnouncements: Boolean = false): World {
        val gameContext =
            GameContext(
                initialLaunch = false,
                name = "loot-announcement-test",
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
                rareDropAnnouncements = rareDropAnnouncements,
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

    private fun newPlayer(world: World): CapturingPlayer =
        CapturingPlayer(world).apply { uid = PlayerUID("loot-announcement-test-player") }

    @Test
    fun `default configuration produces no announcement`() {
        val world = newWorld()
        assertFalse(world.gameContext.rareDropAnnouncements)

        val killer = newPlayer(world)
        announceRareDrop(
            killer,
            Loot(item = 995, min = 1, max = 1, announce = true, description = "from the King Black Dragon"),
        )

        assertEquals(0, killer.messages.size)
    }

    @Test
    fun `enabled configuration produces exactly one message for an announced drop`() {
        val world = newWorld(rareDropAnnouncements = true)
        val killer = newPlayer(world)

        announceRareDrop(
            killer,
            Loot(item = 995, min = 1, max = 1, announce = true, description = "from the King Black Dragon"),
        )

        assertEquals(1, killer.messages.size)
    }

    @Test
    fun `enabled configuration stays silent for a non-announced drop`() {
        val world = newWorld(rareDropAnnouncements = true)
        val killer = newPlayer(world)

        announceRareDrop(killer, Loot(item = 995, min = 1, max = 1, announce = false))

        assertEquals(0, killer.messages.size)
    }

    @Test
    fun `blank description produces a clean message without dangling punctuation`() {
        val announced = Loot(item = 995, min = 1, max = 1, announce = true, description = "   ")

        val message = announced.rareDropMessage(enabled = true, itemName = "Dragon axe")

        assertNotNull(message)
        assertEquals("Rare drop: Dragon axe!", message)
        assertFalse(message.contains("()"))
        assertFalse(message.contains("  "))
        assertFalse(message.endsWith(" "))
    }

    @Test
    fun `description is included only when nonblank`() {
        val blank = Loot(item = 995, min = 1, max = 1, announce = true, description = "")
        val described = Loot(item = 995, min = 1, max = 1, announce = true, description = "from the King Black Dragon")

        assertEquals("Rare drop: Dragon axe!", blank.rareDropMessage(enabled = true, itemName = "Dragon axe"))
        assertEquals(
            "Rare drop: Dragon axe! from the King Black Dragon",
            described.rareDropMessage(enabled = true, itemName = "Dragon axe"),
        )
    }

    @Test
    fun `no description and no resolvable name still yields a clear message`() {
        val loot = Loot(item = 995, min = 1, max = 1, announce = true)

        val message = loot.rareDropMessage(enabled = true, itemName = null)

        assertNotNull(message)
        assertEquals("You received a rare drop!", message)
        assertFalse(message.contains("()"))
    }

    @Test
    fun `non-announced entry never builds a message even when enabled`() {
        val loot = Loot(item = 995, min = 1, max = 1, announce = false)

        assertNull(loot.rareDropMessage(enabled = true, itemName = "Dragon axe"))
    }
}
