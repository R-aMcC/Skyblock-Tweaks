package mrfast.sbt.managers

import com.google.gson.Gson
import com.google.gson.JsonObject
import mrfast.sbt.SkyblockTweaks
import mrfast.sbt.config.categories.CustomizationConfig
import mrfast.sbt.customevents.SlotClickedEvent
import mrfast.sbt.utils.ChatUtils
import mrfast.sbt.utils.GuiUtils.chestName
import mrfast.sbt.utils.ScoreboardUtils
import mrfast.sbt.utils.Utils
import mrfast.sbt.utils.Utils.clean
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@SkyblockTweaks.EventComponent
object LocationManager {
    var inSkyblock = false
    var inDungeons = false
    var currentIsland = ""
    var currentArea = ""
    var dungeonFloor = 0
    var inMasterMode = false
    var selectedDungeonFloor = ""

    private var newWorld = false
    private var listeningForLocraw = false

    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Load) {
        if (newWorld) return

        newWorld = true
        listeningForLocraw = true
        inSkyblock = false
        inMasterMode = false
        limboCount = 0
        currentArea = ""
        currentIsland = ""

        Utils.setTimeout({
            if (!listeningForLocraw) return@setTimeout

            newWorld = false
            updatePlayerLocation()
            ChatUtils.sendPlayerMessage("/locraw")
        }, 1000)
    }

    @SubscribeEvent
    fun onSlotClick(event: SlotClickedEvent) {
        if (event.gui.chestName() == "Catacombs Gate" && event.slot.stack.displayName.contains("Floor")) {
            val clean = event.slot.stack.displayName.clean()
            val floorString = clean.split(" - ")[1]

            val floorNum = when (floorString) {
                "Entrance" -> 0
                "Floor I" -> 1
                "Floor II" -> 2
                "Floor III" -> 3
                "Floor IV" -> 4
                "Floor V" -> 5
                "Floor VI" -> 6
                "Floor VII" -> 7
                else -> -1
            }
            selectedDungeonFloor = floorNum.toString()
        }
    }

    private val gson = Gson()
    private var limboCount = 0

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (!listeningForLocraw) return

        if (event.message.formattedText.clean().startsWith("{\"server\":\"")) {
            val clean = event.message.formattedText.substring(0, event.message.formattedText.indexOf("}") + 1).clean()
            val obj = gson.fromJson(clean, JsonObject::class.java)

            event.isCanceled = true

            if (obj.has("map")) {
                listeningForLocraw = false
                currentIsland = obj.get("map").asString
            }

            if (obj.has("mode")) {
                inDungeons = (obj.get("mode").asString == "dungeon")
            }

            if (obj.has("gametype")) {
                inSkyblock = (obj.get("gametype").asString == "SKYBLOCK")
            }
            if (obj.get("server").asString == "limbo") {
                if (limboCount > 2) {
                    listeningForLocraw = false
                    if (CustomizationConfig.developerMode) println("Player is actually on afk limbo")
                    return
                }

                limboCount++
                Utils.setTimeout({
                    ChatUtils.sendPlayerMessage("/locraw")
                }, 400)
            }
        }
    }

    private fun updatePlayerLocation() {
        for (line in ScoreboardUtils.getSidebarLines(true)) {
            val clean = line.clean()

            if (clean.contains("⏣")) {
                currentArea = clean.split("⏣ ")[1]

                if (currentArea.contains("The Catacombs (")) {
                    if (currentArea.contains("(M")) {
                        inMasterMode = true
                    }

                    dungeonFloor = currentArea.replace("[^0-9]".toRegex(), "").toInt()
                }
            }
        }
    }
}