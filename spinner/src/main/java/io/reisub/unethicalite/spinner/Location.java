package io.reisub.unethicalite.spinner;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldPoint;

@RequiredArgsConstructor
@Getter
public enum Location {
  LUMBRIDGE(new WorldPoint(3208, 3220, 2), new WorldPoint(3209, 3213, 1)),
  SEERS(new WorldPoint(2724, 3493, 0), new WorldPoint(2711, 3471, 1)),
  CRAFTING_GUILD(new WorldPoint(2935, 3280, 0), new WorldPoint(2936, 3286, 1))
  ;

  private final WorldPoint bankLocation;
  private final WorldPoint spinLocation;
}
