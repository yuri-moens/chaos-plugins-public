package io.reisub.unethicalite.birdhouse.tasks;

import io.reisub.unethicalite.birdhouse.BirdHouse;
import io.reisub.unethicalite.birdhouse.Config;
import io.reisub.unethicalite.utils.enums.TeleportLocation;
import io.reisub.unethicalite.utils.tasks.Task;
import javax.inject.Inject;
import net.unethicalite.api.items.Inventory;

public class Teleport extends Task {

  @Inject
  private BirdHouse plugin;
  @Inject
  private Config config;

  @Override
  public String getStatus() {
    return "Teleporting";
  }

  @Override
  public boolean validate() {
    return config.tpLocation() != TeleportLocation.NOWHERE
        && !config.farmSeaweed()
        && plugin.getEmptied().size() == 4;
  }

  @Override
  public int execute() {
    config.tpLocation().teleport(config.goThroughHouse(), config.goThroughHouseFallback());

    Inventory.getAll((i) -> i.hasAction("Search")).forEach((i) -> i.interact("Search"));

    plugin.stop();

    return 1;
  }
}
