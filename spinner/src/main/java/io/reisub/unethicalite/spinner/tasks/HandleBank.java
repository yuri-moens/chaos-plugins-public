package io.reisub.unethicalite.spinner.tasks;

import io.reisub.unethicalite.spinner.Config;
import io.reisub.unethicalite.spinner.Spinner;
import io.reisub.unethicalite.utils.tasks.BankTask;
import java.time.Duration;
import javax.inject.Inject;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Bank.WithdrawMode;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.client.Static;

public class HandleBank extends BankTask {

  @Inject
  private Spinner plugin;
  @Inject
  private Config config;

  @Override
  public boolean validate() {
    return !Inventory.contains(config.material().getId())
        && isLastBankDurationAgo(Duration.ofSeconds(5));
  }

  @Override
  public void execute() {
    open(true);

    Bank.depositAll(config.material().getProductId());
    Bank.withdrawAll(config.material().getId(), WithdrawMode.ITEM);

    plugin.setLastBankTick(Static.getClient().getTickCount());
  }
}
