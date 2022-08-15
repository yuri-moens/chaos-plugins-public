package io.reisub.unethicalite.utils.api;

import com.google.common.collect.Sets;
import io.reisub.unethicalite.utils.Constants;
import io.reisub.unethicalite.utils.enums.HouseTeleport;
import io.reisub.unethicalite.utils.enums.HouseTeleport.TeleportItem;
import java.util.Set;
import net.runelite.api.GameState;
import net.runelite.api.ItemID;
import net.runelite.api.Locatable;
import net.runelite.api.MenuAction;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Rand;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.game.GameThread;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.magic.SpellBook.Standard;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.api.widgets.Widgets;
import net.unethicalite.client.Static;

public class ChaosMovement {

  private static final int DEFAULT_TIMEOUT = 100;
  private static final int DESTINATION_DISTANCE = 8;

  public static boolean interrupted;

  public static void walk(WorldPoint destination, final int rand) {
    walk(destination, rand, rand);
  }

  public static void walk(WorldPoint destination, final int x, final int y) {
    destination = destination
        .dx(Rand.nextInt(-x, x + 1))
        .dy(Rand.nextInt(-y, y + 1));

    Movement.walk(destination);
  }

  public static void walkTo(WorldPoint destination) {
    walkTo(destination, 0);
  }

  public static void walkTo(WorldPoint destination, Runnable task) {
    walkTo(destination, 0, task);
  }

  public static void walkTo(WorldPoint destination, int radius) {
    walkTo(destination, radius, null, DEFAULT_TIMEOUT);
  }

  public static void walkTo(WorldPoint destination, int radius, Runnable task) {
    walkTo(destination, radius, task, DEFAULT_TIMEOUT);
  }

  public static void walkTo(WorldPoint destination, int radius, Runnable task, int tickTimeout) {
    walkTo(destination, radius, task, tickTimeout, DESTINATION_DISTANCE);
  }

  public static void walkTo(
      WorldPoint destination, int radius, Runnable task, int tickTimeout, int destinationDistance) {
    int start = Static.getClient().getTickCount();

    if (radius > 0) {
      destination =
          destination.dx(Rand.nextInt(-radius, radius + 1)).dy(Rand.nextInt(-radius, radius + 1));
    }

    do {
      if (!Movement.isWalking() && Static.getClient().getGameState() != GameState.LOADING) {
        Movement.walkTo(destination);

        if (!Players.getLocal().isMoving()) {
          Time.sleepTick();
        }
      } else if (task != null) {
        Static.getClientThread().invoke(task);
      }

      Time.sleepTick();
    } while (!interrupted
        && Players.getLocal().distanceTo(destination) > destinationDistance
        && Static.getClient().getTickCount() <= start + tickTimeout
        && (Static.getClient().getGameState() == GameState.LOADING
        || Static.getClient().getGameState() == GameState.LOGGED_IN));

    interrupted = false;
  }

  public static boolean openDoor(Locatable target) {
    return openDoor(target, Sets.newHashSet());
  }

  public static boolean openDoor(Locatable target, final int maxDistance) {
    return openDoor(target, maxDistance, Sets.newHashSet());
  }

  public static boolean openDoor(Locatable target, final Set<WorldPoint> ignoreLocations) {
    return openDoor(target, Integer.MAX_VALUE, ignoreLocations);
  }

  public static boolean openDoor(Locatable target,
      final int maxDistance, final Set<WorldPoint> ignoreLocations) {
    if (target == null) {
      return false;
    }

    final WorldPoint targetLocation = target.getWorldLocation();

    final TileObject door = TileObjects.getNearest(
        targetLocation,
        o -> o.getName().equals("Door")
            && o.hasAction("Open")
            && o.distanceTo(targetLocation) <= maxDistance
            && !ignoreLocations.contains(o.getWorldLocation())
    );

    if (door == null) {
      return false;
    }

    if (!Reachable.isInteractable(door)) {
      ignoreLocations.add(door.getWorldLocation());

      if (!openDoor(door, maxDistance, ignoreLocations)) {
        return false;
      }
    }

    GameThread.invoke(() -> door.interact("Open"));

    final WorldPoint tile = door.getWorldLocation();

    return Time.sleepTicksUntil(
        () -> TileObjects.getFirstAt(tile, o -> o.hasAction("Open")) == null, 15
    );
  }

  public static boolean teleportToHouse() {
    if (Inventory.contains(Predicates.ids(Constants.CONSTRUCTION_CAPE_IDS))
        || Equipment.contains(Predicates.ids(Constants.CONSTRUCTION_CAPE_IDS))) {
      Interact.interactWithInventoryOrEquipment(
          Constants.CONSTRUCTION_CAPE_IDS,
          "Tele to POH",
          null,
          -1
      );
    } else if (Standard.TELEPORT_TO_HOUSE.canCast()) {
      Standard.TELEPORT_TO_HOUSE.cast();
    } else if (Inventory.contains(ItemID.TELEPORT_TO_HOUSE)) {
      Inventory.getFirst(ItemID.TELEPORT_TO_HOUSE).interact("Break");
    } else {
      return false;
    }

    return Time.sleepTicksUntil(() -> Static.getClient().isInInstancedRegion()
        && TileObjects.getNearest(ObjectID.PORTAL_4525) != null, 10);
  }

  public static boolean teleportThroughHouse(HouseTeleport houseTeleport) {
    return teleportThroughHouse(houseTeleport, false);
  }

  public static boolean teleportThroughHouse(HouseTeleport houseTeleport, int energyThreshold) {
    return teleportThroughHouse(houseTeleport, false, energyThreshold);
  }

  public static boolean teleportThroughHouse(HouseTeleport houseTeleport, boolean forceNexus) {
    return teleportThroughHouse(houseTeleport, forceNexus, 30);
  }

  public static boolean teleportThroughHouse(HouseTeleport houseTeleport,
      boolean forceNexus, int energyThreshold) {
    if (!Static.getClient().isInInstancedRegion()) {
      if (!teleportToHouse()) {
        return false;
      }
      Time.sleepTicks(2);
    }

    drinkFromPool(energyThreshold);

    if (forceNexus) {
      return teleportThroughPortalNexus(houseTeleport);
    }

    if (houseTeleport.getItem() == null) {
      if (TileObjects.getNearest(houseTeleport.getPortalId()) != null) {
        return teleportThroughPortal(houseTeleport);
      } else {
        return teleportThroughPortalNexus(houseTeleport);
      }
    } else {
      return teleportThroughItem(houseTeleport);
    }
  }

  public static void drinkFromPool(final int energyThreshold) {
    if (Movement.getRunEnergy() < energyThreshold) {
      final TileObject pool =
          TileObjects.getNearest(Predicates.ids(Constants.REJUVENATION_POOL_IDS));

      if (pool != null) {
        GameThread.invoke(() -> pool.interact(0));

        Time.sleepTicksUntil(() -> Movement.getRunEnergy() == 100
            && Combat.getMissingHealth() == 0, 10);
        Time.sleepTick();

        if (!Movement.isRunEnabled()) {
          Movement.toggleRun();
        }
      }
    }
  }

  public static boolean teleportThroughPortal(HouseTeleport houseTeleport) {
    final TileObject portal = TileObjects.getNearest(houseTeleport.getPortalId());

    GameThread.invoke(() -> portal.interact("Enter"));

    return Time.sleepTicksUntil(() -> !Static.getClient().isInInstancedRegion(), 30);
  }

  public static boolean teleportThroughPortalNexus(HouseTeleport houseTeleport) {
    final TileObject portalNexus
        = TileObjects.getNearest(Predicates.ids(Constants.PORTAL_NEXUS_IDS));

    if (portalNexus == null) {
      return false;
    }

    final String destination = houseTeleport.getName().toLowerCase();

    for (String action : portalNexus.getActions()) {
      if (action != null && action.toLowerCase().contains(destination)) {
        GameThread.invoke(() -> portalNexus.interact(action));

        return Time.sleepTicksUntil(() -> !Static.getClient().isInInstancedRegion(), 30);
      }
    }

    GameThread.invoke(() -> portalNexus.interact("Teleport Menu"));

    if (!Time.sleepTicksUntil(() -> Widgets.isVisible(Widgets.get(17, 2)), 30)) {
      return false;
    }

    final Widget[] children = Widgets.get(17, 12).getChildren();

    if (children == null) {
      return false;
    }

    int i;

    for (i = 0; i < children.length; i++) {
      if (children[i].getText().toLowerCase().contains(destination)) {
        break;
      }
    }

    final Widget destinationWidget = Widgets.get(17, 13, i);

    if (destinationWidget == null) {
      return false;
    }

    destinationWidget.interact(
        0,
        MenuAction.WIDGET_CONTINUE.getId(),
        destinationWidget.getIndex(),
        destinationWidget.getId()
    );

    return Time.sleepTicksUntil(() -> !Static.getClient().isInInstancedRegion(), 10);
  }

  public static boolean teleportThroughItem(HouseTeleport houseTeleport) {
    final TileObject item = TileObjects.getNearest(houseTeleport.getItem().getNames());

    if (item == null) {
      return false;
    }

    final String destination = houseTeleport.getName().toLowerCase();

    for (String action : item.getActions()) {
      if (action != null && action.toLowerCase().contains(destination)) {
        GameThread.invoke(() -> item.interact(action));

        return Time.sleepTicksUntil(() -> !Static.getClient().isInInstancedRegion(), 30);
      }
    }

    GameThread.invoke(() -> item.interact(houseTeleport.getItem().getAction()));

    final int widgetGroupId = houseTeleport.getWidgetGroupId();
    final int widgetId = houseTeleport.getWidgetId();

    if (!Time.sleepTicksUntil(() -> Widgets.isVisible(Widgets.get(widgetGroupId, widgetId)), 30)) {
      return false;
    }

    final Widget[] children = Widgets.get(widgetGroupId, widgetId).getChildren();

    if (children == null) {
      return false;
    }

    int childId;

    for (childId = 0; childId < children.length; childId++) {
      if (children[childId].getText().toLowerCase().contains(destination)) {
        break;
      }
    }

    final Widget destinationWidget = Widgets.get(widgetGroupId, widgetId, childId);

    if (destinationWidget == null) {
      return false;
    }

    if (houseTeleport.getItem() == TeleportItem.DIGSITE_PENDANT) {
      destinationWidget.interact(
          0,
          MenuAction.WIDGET_CONTINUE.getId(),
          destinationWidget.getIndex(),
          destinationWidget.getId()
      );
    } else {
      destinationWidget.interact(
          1,
          MenuAction.CC_OP.getId(),
          destinationWidget.getIndex(),
          destinationWidget.getId()
      );
    }

    return Time.sleepTicksUntil(() -> !Static.getClient().isInInstancedRegion(), 10);
  }
}
