package io.reisub.unethicalite.birdhouse.tasks;

import io.reisub.unethicalite.birdhouse.BirdHouse;
import io.reisub.unethicalite.utils.Constants;
import io.reisub.unethicalite.utils.tasks.Task;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.GameThread;
import net.unethicalite.api.items.Inventory;

@RequiredArgsConstructor
public class PlantSeaweed extends Task {
  private final BirdHouse plugin;

  private TileObject patch;

  @Override
  public String getStatus() {
    return "Planting seaweed";
  }

  @Override
  public boolean validate() {
    return plugin.isUnderwater() && (patch = TileObjects.getNearest("Seaweed patch")) != null;
  }

  @Override
  public int execute() {
    Item spore = Inventory.getFirst(ItemID.SEAWEED_SPORE);
    if (spore == null) {
      return 1;
    }

    int quantity = spore.getQuantity();

    spore.useOn(patch);
    Time.sleepTicksUntil(() -> Inventory.getCount(true, ItemID.SEAWEED_SPORE) < quantity, 10);
    Time.sleepTicks(3);

    Item compost = Inventory.getFirst(Predicates.ids(Constants.COMPOST_IDS));

    if (compost == null) {
      return 1;
    }

    patch = TileObjects.getNearest("Seaweed");
    if (patch == null) {
      return 1;
    }

    GameThread.invoke(() -> compost.useOn(patch));
    return 4;
  }
}
