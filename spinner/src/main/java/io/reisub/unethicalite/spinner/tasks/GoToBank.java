package io.reisub.unethicalite.spinner.tasks;

import io.reisub.unethicalite.spinner.Config;
import io.reisub.unethicalite.spinner.Location;
import io.reisub.unethicalite.utils.api.ChaosMovement;
import io.reisub.unethicalite.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.GameState;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.magic.SpellBook.Standard;
import net.unethicalite.client.Static;

public class GoToBank extends Task {

  @Inject
  private Config config;

  @Override
  public String getStatus() {
    return "Going to bank";
  }

  @Override
  public boolean validate() {
    return !Inventory.contains(config.material().getId())
        && Players.getLocal().distanceTo(config.location().getBankLocation()) >= 10;
  }

  @Override
  public void execute() {
    final WorldPoint current = Players.getLocal().getWorldLocation();
    boolean teleported = false;

    if (config.seersTeleport()
        && config.location() == Location.SEERS) {
      if (Standard.CAMELOT_TELEPORT.canCast()) {
        Standard.CAMELOT_TELEPORT.cast();
        teleported = true;
      } else if (Inventory.contains(ItemID.CAMELOT_TELEPORT)) {
        Inventory.getFirst(ItemID.CAMELOT_TELEPORT).interact("Break");
        teleported = true;
      }
    }

    if (teleported) {
      Time.sleepTicksUntil(() -> Static.getClient().getGameState() != GameState.LOADING
          && !Players.getLocal().getWorldLocation().equals(current), 5);
    } else {
      ChaosMovement.walkTo(config.location().getBankLocation());
    }
  }
}
