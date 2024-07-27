package mrfast.sbt.commands

import com.google.common.collect.Lists
import com.mojang.realmsclient.gui.ChatFormatting
import mrfast.sbt.utils.ChatUtils
import mrfast.sbt.utils.Utils
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import java.util.*

class JoinDungeonCommand : CommandBase() {
    private val numberWords = listOf("ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN")

    override fun getCommandName(): String {
        return "joindungeon"
    }

    override fun getCommandAliases(): List<String> {
        return Lists.newArrayList("jd")
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        return "/jd [1-7]"
    }

    override fun processCommand(sender: ICommandSender, args: Array<out String>) {
        if (args.size != 1) {
            ChatUtils.sendClientMessage(ChatFormatting.RED.toString() + "Invalid Command Usage! Example Usage /jd m4")
            return
        }
        val masterMode = args[0].lowercase(Locale.getDefault()).contains("m")
        val dungeonType = (if (masterMode) "MASTER_" else "") + "CATACOMBS"
        val floorInt = parseInt(args[0].replace("[^0-9]", ""))

        if (floorInt > 7) {
            ChatUtils.sendClientMessage(ChatFormatting.RED.toString() + "Invalid Floor!")
            return
        }

        val dungeonString = dungeonType + "_FLOOR_" + numberWords[floorInt]

        ChatUtils.sendPlayerMessage("/joindungeon $dungeonString")
    }

    override fun canCommandSenderUseCommand(sender: ICommandSender?): Boolean {
        return true
    }
}