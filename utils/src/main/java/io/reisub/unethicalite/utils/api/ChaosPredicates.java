package io.reisub.unethicalite.utils.api;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import net.runelite.api.Item;
import net.unethicalite.api.SceneEntity;
import net.unethicalite.api.commons.Predicates;

public class ChaosPredicates {

  public static <T> Predicate<T> distinctByProperty(Function<? super T, ?> propertyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(propertyExtractor.apply(t));
  }

  public static <T extends Item> Predicate<T> itemConfigList(ConfigList configList) {
    return itemConfigList(configList, false);
  }

  public static <T extends Item> Predicate<T> itemConfigList(ConfigList configList,
      boolean stringContains) {
    return itemConfigList(configList, stringContains, true);
  }

  public static <T extends Item> Predicate<T> itemConfigList(ConfigList configList,
      boolean stringContains, boolean caseSensitive) {
    final Predicate<T> identifiablePredicate = Predicates.ids(configList.getIntegers().keySet());

    if (!stringContains) {
      final Predicate<T> nameablePredicate = Predicates.names(configList.getStrings().keySet());

      return nameablePredicate.or(identifiablePredicate);
    } else {
      final Predicate<T> nameablePredicate = caseSensitive
          ? Predicates.nameContains(configList.getStrings().keySet())
          : Predicates.nameContains(configList.getStrings().keySet(), false);

      return nameablePredicate.or(identifiablePredicate);
    }
  }

  public static <T extends SceneEntity> Predicate<T> entityConfigList(ConfigList configList) {
    return entityConfigList(configList, false);
  }

  public static <T extends SceneEntity> Predicate<T> entityConfigList(ConfigList configList,
      boolean stringContains) {
    return entityConfigList(configList, stringContains, true);
  }

  public static <T extends SceneEntity> Predicate<T> entityConfigList(ConfigList configList,
      boolean stringContains, boolean caseSensitive) {
    final Predicate<T> identifiablePredicate = Predicates.ids(configList.getIntegers().keySet());

    if (!stringContains) {
      final Predicate<T> nameablePredicate = Predicates.names(configList.getStrings().keySet());

      return nameablePredicate.or(identifiablePredicate);
    } else {
      final Predicate<T> nameablePredicate = caseSensitive
          ? Predicates.nameContains(configList.getStrings().keySet())
          : Predicates.nameContains(configList.getStrings().keySet(), false);

      return nameablePredicate.or(identifiablePredicate);
    }
  }
}
