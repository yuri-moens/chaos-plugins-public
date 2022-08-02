package io.reisub.unethicalite.spinner.tasks;

import com.google.common.collect.ImmutableSet;
import io.reisub.unethicalite.spinner.Config;
import io.reisub.unethicalite.spinner.Spinner;
import io.reisub.unethicalite.spinner.data.PluginActivity;
import io.reisub.unethicalite.utils.tasks.Task;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.GameThread;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Production;

public class Spin extends Task {

  private static final Set<Integer> SPINNING_WHEEL_IDS = ImmutableSet.of(
      ObjectID.SPINNING_WHEEL,
      ObjectID.SPINNING_WHEEL_8748,
      ObjectID.SPINNING_WHEEL_14889,
      ObjectID.SPINNING_WHEEL_20365,
      ObjectID.SPINNING_WHEEL_21304,
      ObjectID.SPINNING_WHEEL_25824,
      ObjectID.SPINNING_WHEEL_26143,
      ObjectID.SPINNING_WHEEL_30934,
      ObjectID.SPINNING_WHEEL_40735
  );

  @Inject
  private Spinner plugin;
  @Inject
  private Config config;

  @Override
  public String getStatus() {
    return "Spinning " + config.material().toString().toLowerCase();
  }

  @Override
  public boolean validate() {
    return plugin.isCurrentActivity(PluginActivity.SPINNING)
        && Inventory.contains(config.material().getId());
  }

  @Override
  public void execute() {
    plugin.setActivity(PluginActivity.SPINNING);

    final TileObject wheel = TileObjects.getNearest(Predicates.ids(SPINNING_WHEEL_IDS));

    if (wheel == null) {
      return;
    }

    GameThread.invoke(() -> wheel.interact("Spin"));

    Time.sleepTicksUntil(Production::isOpen, 20);

    Production.chooseOption(config.material().getProductionIndex());
  }
}
