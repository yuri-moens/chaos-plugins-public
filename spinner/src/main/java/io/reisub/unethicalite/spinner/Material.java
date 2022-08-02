package io.reisub.unethicalite.spinner;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;

@RequiredArgsConstructor
@Getter
public enum Material {
  FLAX(ItemID.FLAX, ItemID.BOW_STRING, 3),
  ;

  private final int id;
  private final int productId;
  private final int productionIndex;
}
