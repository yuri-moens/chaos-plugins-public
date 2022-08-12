package io.reisub.unethicalite.tabmaker.tasks;

import io.reisub.unethicalite.tabmaker.Config;
import io.reisub.unethicalite.tabmaker.TabMaker;
import io.reisub.unethicalite.tabmaker.data.PluginActivity;
import io.reisub.unethicalite.utils.api.Activity;
import io.reisub.unethicalite.utils.api.ChaosMovement;
import io.reisub.unethicalite.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Widgets;
import net.unethicalite.client.Static;

public class MakeTabs extends Task {

  @Inject
  private TabMaker plugin;
  @Inject
  private Config config;

  @Override
  public String getStatus() {
    return "Making tabs";
  }

  @Override
  public boolean validate() {
    return Inventory.contains(ItemID.SOFT_CLAY)
        && plugin.isCurrentActivity(Activity.IDLE);
  }

  @Override
  public void execute() {
    if (!Static.getClient().isInInstancedRegion()) {
      ChaosMovement.teleportToHouse();
      Time.sleepTicks(2);
    }

    final TileObject lectern = TileObjects.getNearest("Lectern");

    if (lectern == null) {
      return;
    }

    lectern.interact("Study");

    if (!Time.sleepTicksUntil(() -> Players.getLocal().isMoving()
        || Widgets.isVisible(Widgets.get(79, 0)), 3)) {
      return;
    }

    Time.sleepTicksUntil(() -> Widgets.isVisible(Widgets.get(79, 0)), 50);

    final Widget tabWidget = Widgets.get(79, config.tab().getId());

    if (tabWidget == null) {
      return;
    }

    plugin.setActivity(PluginActivity.MAKING_TABS);
    tabWidget.interact(0);
  }
}
