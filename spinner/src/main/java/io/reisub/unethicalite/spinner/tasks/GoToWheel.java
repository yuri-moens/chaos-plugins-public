package io.reisub.unethicalite.spinner.tasks;

import io.reisub.unethicalite.spinner.Config;
import io.reisub.unethicalite.spinner.Spinner;
import io.reisub.unethicalite.utils.api.ChaosMovement;
import io.reisub.unethicalite.utils.tasks.Task;
import javax.inject.Inject;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.client.Static;

public class GoToWheel extends Task {

  @Inject
  private Spinner plugin;
  @Inject
  private Config config;

  @Override
  public String getStatus() {
    return "Going to spinning wheel";
  }

  @Override
  public boolean validate() {
    return (Inventory.contains(config.material().getId())
        || Static.getClient().getTickCount() - plugin.getLastBankTick() <= 2)
        && Players.getLocal().distanceTo(config.location().getSpinLocation()) >= 10;
  }

  @Override
  public void execute() {
    ChaosMovement.walkTo(config.location().getSpinLocation());
  }
}
