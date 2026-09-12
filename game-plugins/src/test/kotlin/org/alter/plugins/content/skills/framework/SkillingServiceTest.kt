package org.alter.plugins.content.skills.framework

import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SkillingServiceTest {

    private val tempDirs = mutableListOf<Path>()

    @AfterTest
    fun cleanUp() {
        tempDirs.forEach { dir -> dir.toFile().deleteRecursively() }
        tempDirs.clear()
    }

    /**
     * Deterministic stand-in for RSCM: the first time a name is seen it is
     * assigned the next integer id.
     */
    private class FakeResolver : (String) -> Int {
        private val ids = LinkedHashMap<String, Int>()

        override fun invoke(name: String): Int = ids.getOrPut(name) { ids.size + 1 }

        operator fun get(name: String): Int = ids.getValue(name)
    }

    private fun newService(vararg definitions: Pair<String, String>): Pair<SkillingService, FakeResolver> {
        val directory = Files.createTempDirectory("skilling-service-test")
        tempDirs.add(directory)
        definitions.forEach { (name, json) ->
            Files.writeString(directory.resolve("$name.json"), json)
        }

        val resolver = FakeResolver()
        val service = SkillingService()
        service.load(directory, resolver)
        return service to resolver
    }

    private fun nodeJson(
        key: String = "object.copper_rock",
        levelRequired: Int = 1,
        xp: Double = 17.5,
        actionTicks: Int = 4,
        animation: Int = 625,
        successLow: Int = 100,
        successHigh: Int = 350,
        respawnTicks: Int = 4,
        depletedKey: String? = "object.empty_rock",
        variants: List<String> = emptyList(),
        lootItem: String = "item.copper_ore",
    ): String {
        val variantJson = variants.joinToString(",") { "\"$it\"" }
        val depletedJson = depletedKey?.let { "\"$it\"" } ?: "null"
        return """
            [
              {
                "key": "$key",
                "levelRequired": $levelRequired,
                "xp": $xp,
                "actionTicks": $actionTicks,
                "animation": $animation,
                "successLow": $successLow,
                "successHigh": $successHigh,
                "respawnTicks": $respawnTicks,
                "depletedKey": $depletedJson,
                "variants": [$variantJson],
                "loot": [
                  { "item": "$lootItem", "min": 1, "max": 1, "chance": 1.0 }
                ]
              }
            ]
        """.trimIndent()
    }

    @Test
    fun `loads a definition keyed by its file name`() {
        val (service, _) = newService("mining" to nodeJson())

        assertEquals(setOf("mining"), service.repository.definitionNames)
        assertEquals(1, service.nodes("mining").size)
        assertTrue(service.nodes("unknown").isEmpty())
    }

    @Test
    fun `resolves the primary key and every variant to object ids`() {
        val (service, resolver) =
            newService("mining" to nodeJson(key = "object.copper_rock", variants = listOf("object.copper_rock_2")))

        val node = service.nodes("mining").single()

        assertEquals(
            listOf(resolver["object.copper_rock"], resolver["object.copper_rock_2"]),
            node.objectIds.toList(),
        )
    }

    @Test
    fun `indexes the node by every resolved object id`() {
        val (service, resolver) =
            newService("mining" to nodeJson(key = "object.copper_rock", variants = listOf("object.copper_rock_2")))

        val node = service.nodes("mining").single()

        assertSame(node, service.nodeForObject(resolver["object.copper_rock"]))
        assertSame(node, service.nodeForObject(resolver["object.copper_rock_2"]))
        assertNull(service.nodeForObject(999_999))
    }

    @Test
    fun `resolves the depleted object and loot item ids`() {
        val (service, resolver) = newService("mining" to nodeJson())

        val node = service.nodes("mining").single()
        val loot = node.loot.single()

        assertEquals(resolver["object.empty_rock"], node.depletedObjectId)
        assertEquals(resolver["item.copper_ore"], loot.itemId)
        assertEquals(1.0, loot.spec.chance)
    }

    @Test
    fun `allows a null depleted key`() {
        val (service, _) = newService("mining" to nodeJson(depletedKey = null))

        assertNull(service.nodes("mining").single().depletedObjectId)
    }

    @Test
    fun `delegates the success chance to the raw node`() {
        val (service, _) = newService("mining" to nodeJson(successLow = 48, successHigh = 90))

        val node = service.nodes("mining").single()

        assertEquals(node.node.successChance(74), node.successChance(74), 1e-9)
        assertEquals(80.0 / 256.0, node.successChance(74), 1e-9)
    }

    @Test
    fun `rejects a duplicate object id across definitions`() {
        assertFailsWith<IllegalArgumentException> {
            newService("first" to nodeJson(key = "object.rock"), "second" to nodeJson(key = "object.rock"))
        }
    }

    @Test
    fun `re-validates nodes deserialised by gson`() {
        val invalid = nodeJson(xp = -1.0)

        assertFailsWith<IllegalArgumentException> { newService("mining" to invalid) }
    }

    @Test
    fun `missing directory yields an empty repository`() {
        val directory = Files.createTempDirectory("skilling-service-empty")
        tempDirs.add(directory)

        val service = SkillingService()
        service.load(directory.resolve("does-not-exist"))

        assertTrue(service.repository.definitionNames.isEmpty())
        assertNotNull(service.nodes("mining"))
    }
}
