package io.reisub.unethicalite.spinner;

import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("chaosspinner")
public interface Config extends net.runelite.client.config.Config {

  @ConfigItem(
      keyName = "location",
      name = "Location",
      description = "Select your location",
      position = 0)
  default Location location() {
    return Location.LUMBRDIGE;
  }

  @ConfigItem(
      keyName = "material",
      name = "Material",
      description = "Select the material",
      position = 1)
  default Material material() {
    return Material.FLAX;
  }

  @ConfigItem(
      keyName = "seersTeleport",
      name = "Seers teleport",
      description = "Use Seers bank teleport, make sure you have runes/pouch/tabs withdrawn",
      position = 2)
  default boolean seersTeleport() {
    return false;
  }

  @ConfigItem(
      keyName = "startButton",
      name = "Start/Stop",
      description = "Start the script",
      position = Integer.MAX_VALUE)
  default Button startButton() {
    return new Button();
  }
}
