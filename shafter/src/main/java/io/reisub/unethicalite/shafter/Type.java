package io.reisub.unethicalite.shafter;

import lombok.Getter;

@Getter
public enum Type {
  NORMAL("Tree", "Dying tree", "Dead tree", "Evergreen", "Jungle Tree"),
  ACHEY("Achey Tree"),
  OAK("Oak tree"),
  WILLOW("Willow tree"),
  MAPLE("Maple tree"),
  YEW("Yew tree"),
  MAGIC("Magic tree"),
  REDWOOD("Redwood tree");

  Type(String... names) {
    this.names = names;
  }

  private final String[] names;
}
