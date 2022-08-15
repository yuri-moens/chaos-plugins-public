package io.reisub.unethicalite.utils.api;

import io.reisub.unethicalite.utils.Constants;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.runelite.api.Item;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Bank.WithdrawMode;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;

public class ChaosBank {

  public static void depositAll(String... names) {
    depositAll(true, names);
  }

  public static void depositAll(boolean delay, String... names) {
    depositAll(delay, x -> Arrays.stream(names).anyMatch(name -> x.getName().equals(name)));
  }

  public static void depositAll(int... ids) {
    depositAll(true, ids);
  }

  public static void depositAll(boolean delay, int... ids) {
    depositAll(delay, x -> Arrays.stream(ids).anyMatch(id -> x.getId() == id));
  }

  public static void depositAll(Predicate<Item> filter) {
    depositAll(true, filter);
  }

  public static void depositAll(boolean delay, Predicate<Item> filter) {
    Set<Item> items =
        Bank.Inventory.getAll(filter).stream()
            .filter(ChaosPredicates.distinctByProperty(Item::getId))
            .collect(Collectors.toSet());

    items.forEach(
        (item) -> {
          Bank.depositAll(item.getId());

          if (delay) {
            Time.sleepTick();
          }
        });
  }

  public static void depositAllExcept(String... names) {
    depositAllExcept(true, names);
  }

  public static void depositAllExcept(boolean delay, String... names) {
    depositAllExcept(delay, x -> Arrays.stream(names).anyMatch(name -> x.getName().equals(name)));
  }

  public static void depositAllExcept(int... ids) {
    depositAllExcept(true, ids);
  }

  public static void depositAllExcept(boolean delay, int... ids) {
    depositAllExcept(delay, x -> Arrays.stream(ids).anyMatch(id -> x.getId() == id));
  }

  public static void depositAllExcept(Predicate<Item> filter) {
    depositAllExcept(true, filter);
  }

  public static void depositAllExcept(boolean delay, Predicate<Item> filter) {
    depositAll(delay, filter.negate());
  }

  @Deprecated
  public static void bankInventoryInteract(Item item, String action) {
    item.interact(action);
  }

  public static boolean haveGracefulInBank() {
    return Bank.contains(Predicates.ids(Constants.GRACEFUL_HOOD))
        || Bank.contains(Predicates.ids(Constants.GRACEFUL_CAPE))
        || Bank.contains(Predicates.ids(Constants.GRACEFUL_TOP))
        || Bank.contains(Predicates.ids(Constants.GRACEFUL_LEGS))
        || Bank.contains(Predicates.ids(Constants.GRACEFUL_GLOVES))
        || Bank.contains(Predicates.ids(Constants.GRACEFUL_BOOTS));
  }

  public static boolean withdrawEquipment(ConfigList equipment) {
    if (!withdrawItems(equipment)) {
      return false;
    }

    int actions = 0;

    List<Item> equipmentItems =
        Bank.Inventory.getAll(ChaosPredicates.itemConfigList(equipment, true, false));

    for (Item item : equipmentItems) {
      if (actions == 10) {
        actions = 0;
        Time.sleepTick();
      }

      item.interact("Wield", "Wear");
      actions++;
    }

    Time.sleepTick();
    return haveAllItemsInInventory(equipment);
  }

  public static boolean withdrawItems(ConfigList items) {
    int actions = 0;

    for (Map.Entry<String, Integer> item : items.getStrings().entrySet()) {
      if (actions == 10) {
        actions = 0;
        Time.sleepTick();
      }

      if (!Bank.contains(item.getKey())) {
        continue;
      }

      if (item.getValue() <= 0) {
        Bank.withdrawAll(item.getKey(), WithdrawMode.ITEM);
      } else {
        final int amount = item.getValue() - Inventory.getCount(true, item.getKey());

        if (amount <= 0) {
          continue;
        }

        Bank.withdraw(item.getKey(), amount, WithdrawMode.ITEM);
      }
      actions++;
    }

    for (Map.Entry<Integer, Integer> item : items.getIntegers().entrySet()) {
      if (actions == 10) {
        actions = 0;
        Time.sleepTick();
      }

      if (!Bank.contains(item.getKey())) {
        continue;
      }

      if (item.getValue() <= 0) {
        Bank.withdrawAll(item.getKey(), WithdrawMode.ITEM);
      } else {
        final int amount = item.getValue() - Inventory.getCount(true, item.getKey());

        if (amount <= 0) {
          continue;
        }

        Bank.withdraw(item.getKey(), amount, WithdrawMode.ITEM);
      }
      actions++;
    }

    Time.sleepTick();
    return haveAllItemsInEquipment(items);
  }

  public static boolean haveAllItemsInEquipment(ConfigList items) {
    for (Map.Entry<String, Integer> item : items.getStrings().entrySet()) {
      if (item.getValue() <= 0) {
        if (!Inventory.contains(item.getKey())) {
          return false;
        }
      } else {
        if (Inventory.getCount(true, item.getKey()) < item.getValue()) {
          return false;
        }
      }
    }

    for (Map.Entry<Integer, Integer> item : items.getIntegers().entrySet()) {
      if (item.getValue() <= 0) {
        if (!Inventory.contains(item.getKey())) {
          return false;
        }
      } else {
        if (Inventory.getCount(true, item.getKey()) < item.getValue()) {
          return false;
        }
      }
    }

    return true;
  }

  public static boolean haveAllItemsInInventory(ConfigList items) {
    for (String item : items.getStrings().keySet()) {
      if (!Equipment.contains(item)) {
        return false;
      }
    }

    for (Integer item : items.getIntegers().keySet()) {
      if (!Equipment.contains(item)) {
        return false;
      }
    }

    return true;
  }
}
