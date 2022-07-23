package io.reisub.unethicalite.utils.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.widgets.WidgetID;

@RequiredArgsConstructor
@Getter
public enum HouseTeleport {

  // portals
  BARROWS("Barrows", 0),
  HARMONY_ISLAND("Harmony Island", 37589),
  TROLL_STRONGHOLD("Troll Stronghold", 33179),
  WEISS("Weiss", 37581),

  // digsite pendant
  DIGSITE("Digsite", TeleportItem.DIGSITE_PENDANT, 3),
  FOSSIL_ISLAND("Fossil Island", TeleportItem.DIGSITE_PENDANT, 3),
  LITHKREN("Lithkren", TeleportItem.DIGSITE_PENDANT, 3),

  // jewellery box
  PVP_ARENA("PvP Arena", TeleportItem.JEWELLERY_BOX, 2),
  CASTLE_WARS("Castle Wars", TeleportItem.JEWELLERY_BOX, 2),
  FEROX_ENCLAVE("Ferox Enclave", TeleportItem.JEWELLERY_BOX, 2),

  BURTHORPE("Burthorpe", TeleportItem.JEWELLERY_BOX, 3),
  BARBARIAN_OUTPOST("Barbarian Outpost", TeleportItem.JEWELLERY_BOX, 3),
  CORPOREAL_BEAST("Corporeal Beast", TeleportItem.JEWELLERY_BOX, 3),
  TEARS_OF_GUTHIX("Tears of Guthix", TeleportItem.JEWELLERY_BOX, 3),
  WINTERTODT_CAMP("Wintertodt Camp", TeleportItem.JEWELLERY_BOX, 3),

  WARRIORS_GUILD("Warriors' Guild", TeleportItem.JEWELLERY_BOX, 4),
  CHAMPIONS_GUILD("Champions' Guild", TeleportItem.JEWELLERY_BOX, 4),
  MONASTERY("Monastery", TeleportItem.JEWELLERY_BOX, 4),
  RANGING_GUILD("Ranging Guild", TeleportItem.JEWELLERY_BOX, 4),

  FISHING_GUILD("Fishing Guild", TeleportItem.JEWELLERY_BOX, 5),
  MINING_GUILD("Mining Guild", TeleportItem.JEWELLERY_BOX, 5),
  CRAFTING_GUILD("Crafting Guild", TeleportItem.JEWELLERY_BOX, 5),
  COOKING_GUILD("Cooking Guild", TeleportItem.JEWELLERY_BOX, 5),
  WOODCUTTING_GUILD("Woodcutting Guild", TeleportItem.JEWELLERY_BOX, 5),
  FARMING_GUILD("Farming Guild", TeleportItem.JEWELLERY_BOX, 5),

  MISCELLANIA("Miscellania", TeleportItem.JEWELLERY_BOX, 6),
  GRAND_EXCHANGE("Grand Exchange", TeleportItem.JEWELLERY_BOX, 6),
  FALADOR_PARK("Falador Park", TeleportItem.JEWELLERY_BOX, 6),
  DONDAKANS_ROCK("Dondakan's Rock", TeleportItem.JEWELLERY_BOX, 6),

  EDGEVILLE("Edgeville", TeleportItem.JEWELLERY_BOX, 7),
  KARAMJA("Karamja", TeleportItem.JEWELLERY_BOX, 7),
  DRAYNOR_VILLAGE("Draynor Village", TeleportItem.JEWELLERY_BOX, 7),
  AL_KHARID("Al Kharid", TeleportItem.JEWELLERY_BOX, 7),
  ;

  private final String name;
  private final int portalId;
  private final TeleportItem item;
  private final int widgetGroupId;
  private final int widgetId;

  HouseTeleport(String name, int portalId) {
    this(name, portalId, null, 0, 0);
  }

  HouseTeleport(String name, TeleportItem item, int widgetId) {
    this(name, 0, item, item.widgetGroupId, widgetId);
  }

  @Getter
  public enum TeleportItem {
    DIGSITE_PENDANT(WidgetID.ADVENTURE_LOG_ID, "Teleport menu", "Digsite Pendant"),
    JEWELLERY_BOX(WidgetID.JEWELLERY_BOX_GROUP_ID,
        "Teleport Menu", "Basic Jewellery Box", "Fancy Jewellery Box", "Ornate Jewellery Box"),
    ;

    private final int widgetGroupId;
    private final String action;
    private final String[] names;

    TeleportItem(int widgetGroupId, String action, String... names) {
      this.widgetGroupId = widgetGroupId;
      this.action = action;
      this.names = names;
    }
  }

}
