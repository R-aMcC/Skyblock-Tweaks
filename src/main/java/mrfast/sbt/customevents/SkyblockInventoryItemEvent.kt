package mrfast.sbt.customevents

import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.eventhandler.Event

open class SkyblockInventoryItemEvent : Event() {
    enum class EventType {
        GAINED,
        LOST
    }

    open class InventoryItemEvent(val eventType: EventType, val amount: Int = 1) : SkyblockInventoryItemEvent()

    class ItemStackEvent(eventType: EventType, amount: Int = 1, val itemName: String) : InventoryItemEvent(eventType, amount)

    class SackItemEvent(eventType: EventType, amount: Int = 1, val materialName: String) : InventoryItemEvent(eventType, amount)
}
