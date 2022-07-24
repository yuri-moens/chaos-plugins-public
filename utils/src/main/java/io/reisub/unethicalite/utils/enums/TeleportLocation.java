package io.reisub.unethicalite.utils.enums;

import io.reisub.unethicalite.utils.Constants;
import io.reisub.unethicalite.utils.api.ChaosMovement;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Bank.WithdrawMode;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.magic.Magic;
import net.unethicalite.api.magic.SpellBook;
import net.unethicalite.api.widgets.Dialog;
import net.unethicalite.api.widgets.Widgets;

@RequiredArgsConstructor
@Getter
@Slf4j
public enum TeleportLocation {
  NOWHERE(),
  HOUSE_TELEPORT(),

  CRAFTING_GUILD_CAPE(Constants.CRAFTING_CAPE_IDS),

  FARMING_GUILD(Constants.SKILL_NECKLACE_IDS, 6, HouseTeleport.FARMING_GUILD),
  EDGEVILLE(Constants.AMULET_OF_GLORY_IDS, 1, HouseTeleport.EDGEVILLE),
  FEROX_ENCLAVE(Constants.DUELING_RING_IDS, 3, HouseTeleport.FEROX_ENCLAVE),

  HOME_TELEPORT(),
  ;

  private final Set<Integer> teleportItemIds;
  private final int optionIndex;
  private final HouseTeleport houseTeleport;

  TeleportLocation() {
    this(null, -1, null);
  }

  TeleportLocation(Set<Integer> teleportItemIds) {
    this(teleportItemIds, -1, null);
  }

  public void withdrawItems(
      boolean goThroughHouse, boolean goThroughHouseFallback, boolean useHouseTab) {
    if (this == HOUSE_TELEPORT || (houseTeleport != null && goThroughHouse)) {
      withdrawHouseTeleportItems(useHouseTab);
      return;
    }

    if (teleportItemIds == null || teleportItemIds.isEmpty()) {
      return;
    }

    if (Bank.contains(Predicates.ids(teleportItemIds))) {
      Bank.withdraw(Predicates.ids(teleportItemIds), 1, WithdrawMode.ITEM);
    } else if (goThroughHouseFallback) {
      withdrawHouseTeleportItems(useHouseTab);
    }
  }

  private void withdrawHouseTeleportItems(boolean useHouseTab) {
    if (Bank.contains(Predicates.ids(Constants.CONSTRUCTION_CAPE_IDS))) {
      Bank.withdraw(Predicates.ids(Constants.CONSTRUCTION_CAPE_IDS), 1, WithdrawMode.ITEM);
    } else if (useHouseTab) {
      Bank.withdraw(ItemID.TELEPORT_TO_HOUSE, 1, WithdrawMode.ITEM);
    } else {
      boolean withdrawnPouch = false;

      if (Bank.contains(ItemID.AIR_RUNE)) {
        Bank.withdraw(ItemID.AIR_RUNE, 1, WithdrawMode.ITEM);
      } else {
        Bank.withdraw(ItemID.RUNE_POUCH, 1, WithdrawMode.ITEM);
        withdrawnPouch = true;
      }

      if (Bank.contains(ItemID.EARTH_RUNE)) {
        Bank.withdraw(ItemID.EARTH_RUNE, 1, WithdrawMode.ITEM);
      } else if (!withdrawnPouch) {
        Bank.withdraw(ItemID.RUNE_POUCH, 1, WithdrawMode.ITEM);
        withdrawnPouch = true;
      }

      if (Bank.contains(ItemID.LAW_RUNE)) {
        Bank.withdraw(ItemID.LAW_RUNE, 1, WithdrawMode.ITEM);
      } else if (!withdrawnPouch) {
        Bank.withdraw(ItemID.RUNE_POUCH, 1, WithdrawMode.ITEM);
      }
    }
  }

  public void teleport(boolean goThroughHouse, boolean goThroughHouseFallback) {
    if (houseTeleport != null) {
      if (goThroughHouse
          || (!Inventory.contains(Predicates.ids(teleportItemIds)) && goThroughHouseFallback)) {
        ChaosMovement.teleportThroughHouse(houseTeleport);
        return;
      }
    }

    final WorldPoint current = Players.getLocal().getWorldLocation();

    switch (this) {
      case HOUSE_TELEPORT:
        ChaosMovement.teleportToHouse();
        return; // teleportToHouse handles sleeping so we can return here
      case CRAFTING_GUILD_CAPE:
        final Item cape = Inventory.getFirst(Predicates.ids(Constants.CRAFTING_CAPE_IDS));

        if (cape == null) {
          return;
        }

        cape.interact("Teleport");
        break;
      case FARMING_GUILD:
        Item tpItem = Inventory.getFirst(Predicates.ids(teleportItemIds));

        if (tpItem == null) {
          return;
        }

        tpItem.interact("Rub");
        Time.sleepTicksUntil(() -> Widgets.isVisible(Widgets.get(187, 3)), 5);

        final Widget farmingGuild = Widgets.get(187, 3, 5);

        if (farmingGuild != null) {
          farmingGuild.interact(
              0,
              MenuAction.WIDGET_CONTINUE.getId(),
              farmingGuild.getIndex(),
              farmingGuild.getId()
          );
        }
        break;
      case EDGEVILLE:
      case FEROX_ENCLAVE:
        tpItem = Inventory.getFirst(Predicates.ids(teleportItemIds));

        if (tpItem == null) {
          return;
        }

        tpItem.interact("Rub");
        Time.sleepTicksUntil(Dialog::isViewingOptions, 5);

        Dialog.chooseOption(optionIndex);
        break;
      case HOME_TELEPORT:
        switch (SpellBook.getCurrent()) {
          case LUNAR:
            Magic.cast(SpellBook.Lunar.LUNAR_HOME_TELEPORT);
            break;
          case ANCIENT:
            Magic.cast(SpellBook.Ancient.EDGEVILLE_HOME_TELEPORT);
            break;
          case STANDARD:
            Magic.cast(SpellBook.Standard.HOME_TELEPORT);
            break;
          case NECROMANCY:
            Magic.cast(SpellBook.Necromancy.ARCEUUS_HOME_TELEPORT);
            break;
          default:
        }
        break;
      case NOWHERE:
        return;
      default:
        log.warn("This teleport is not yet supported: " + this);
        return;
    }

    Time.sleepTicksUntil(() -> !Players.getLocal().getWorldLocation().equals(current), 35);
  }
}
