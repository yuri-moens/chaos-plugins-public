package io.reisub.unethicalite.birdhouse;

import io.reisub.unethicalite.utils.enums.Log;
import io.reisub.unethicalite.utils.enums.TeleportLocation;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;

@ConfigGroup("chaosbirdhouse")
public interface Config extends net.runelite.client.config.Config {
  @ConfigItem(
      keyName = "farmSeaweed",
      name = "Farm seaweed",
      description = "Harvest and plant seaweed after a birdhouse run.",
      position = 0)
  default boolean farmSeaweed() {
    return true;
  }

  @ConfigItem(
      keyName = "pickupSpores",
      name = "Pick up spores",
      description = "Pick up spores when underwater.",
      position = 1)
  default boolean pickupSpores() {
    return true;
  }

  @ConfigItem(
      keyName = "birdhouseHotkey",
      name = "Start hotkey",
      description = "Start a birdhouse run from a bank.",
      position = 2)
  default Keybind birdhouseHotkey() {
    return new Keybind(KeyEvent.VK_F11, InputEvent.CTRL_DOWN_MASK);
  }

  @ConfigItem(
      keyName = "logs",
      name = "Logs",
      description = "Select which logs to use.",
      position = 3)
  default Log logs() {
    return Log.YEW;
  }

  @ConfigItem(
      keyName = "equipGraceful",
      name = "Equip graceful",
      description =
          "Deposit current equipment and equip graceful before starting a run",
      position = 5)
  default boolean equipGraceful() {
    return true;
  }

  @ConfigSection(
      keyName = "teleportConfig",
      name = "Teleport Configuration",
      description = "Configure teleportation",
      position = Integer.MAX_VALUE - 5)
  String teleportConfig = "teleportConfig";

  @ConfigItem(
      keyName = "tpAfterRun",
      name = "TP after run",
      description = "Select teleport location after finishing a run. This option only works when "
              + "not farming seaweed.",
      section = "teleportConfig",
      position = Integer.MAX_VALUE - 4)
  default TeleportLocation tpLocation() {
    return TeleportLocation.NOWHERE;
  }

  @ConfigItem(
      keyName = "goThroughHouse",
      name = "Go through house",
      description = "Force teleport usage at your house "
          + "(eg. use mounted digsite pendant instead of item)",
      section = "teleportConfig",
      position = Integer.MAX_VALUE - 3)
  default boolean goThroughHouse() {
    return false;
  }

  @ConfigItem(
      keyName = "goThroughHouseFallback",
      name = "Go through house fallback",
      description = "Use teleport method at house as a fallback when out of items "
          + "(eg. use mounted digsite pendant when out of pendants)",
      section = "teleportConfig",
      position = Integer.MAX_VALUE - 2)
  default boolean goThroughHouseFallback() {
    return true;
  }

  @ConfigItem(
      keyName = "useHouseTab",
      name = "Use house tab",
      description = "Use a house tab rather than runes to teleport to your house",
      section = "teleportConfig",
      position = Integer.MAX_VALUE - 1)
  default boolean useHouseTab() {
    return false;
  }

  @ConfigItem(
      keyName = "startButton",
      name = "Force Start/Stop",
      description =
          "The script should automatically start and stop. Use this button for manual "
              + "overrides.",
      position = Integer.MAX_VALUE)
  default Button startButton() {
    return new Button();
  }
}
