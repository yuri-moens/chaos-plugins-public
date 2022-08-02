package io.reisub.unethicalite.utils;

import io.reisub.unethicalite.utils.api.ChaosMovement;
import io.reisub.unethicalite.utils.enums.Activity;
import io.reisub.unethicalite.utils.tasks.Task;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Skill;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.WidgetHiddenChanged;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.plugins.LoopedPlugin;
import net.unethicalite.api.utils.MessageUtils;
import net.unethicalite.api.widgets.Dialog;
import net.unethicalite.api.widgets.Widgets;
import net.unethicalite.client.Static;

@Slf4j
public abstract class TickPlugin extends LoopedPlugin implements KeyListener {

  @Inject
  private Config utilsConfig;
  @Inject
  private KeyManager keyManager;
  private final List<Task> tasks = new ArrayList<>();
  private final Map<Skill, Activity> idleCheckSkills = new HashMap<>();
  @Getter
  private Activity currentActivity;
  @Getter
  private Activity previousActivity;
  private boolean running;
  @Setter
  private int lastActionTimeout = 6;
  private int lastActionTick;
  @Getter
  private int lastLoginTick;
  private int lastExperienceTick;
  private int lastInventoryChangeTick;
  @Setter
  private boolean idleCheckInventoryChanged;
  @Getter
  @Setter
  private int lastHopTick;

  @Subscribe
  private void onConfigButtonPressed(ConfigButtonClicked event) {
    String name = this.getName().replaceAll(" ", "").toLowerCase();

    if (event.getGroup().equals(name) && event.getKey().equals("startButton")) {
      if (running) {
        onStop();
      } else {
        onStart();
      }
    }
  }

  @Subscribe
  private void onWidgetHiddenChanged(WidgetHiddenChanged event) {
    if (Widgets.isVisible(Widgets.get(WidgetInfo.LEVEL_UP_LEVEL))) {
      Dialog.continueSpace();
      Dialog.continueSpace();
      setActivity(Activity.IDLE);
    }
  }

  @Subscribe
  private void onStatChanged(StatChanged event) {
    if (!isRunning() || !Utils.isLoggedIn()) {
      return;
    }

    for (Skill skill : idleCheckSkills.keySet()) {
      if (event.getSkill() == skill) {
        setActivity(idleCheckSkills.get(skill));
        lastExperienceTick = Static.getClient().getTickCount();
      }
    }
  }

  @Subscribe
  private void onItemContainerChanged(ItemContainerChanged event) {
    if (!isRunning() || !Utils.isLoggedIn()) {
      return;
    }

    if (event.getItemContainer() != Static.getClient().getItemContainer(InventoryID.INVENTORY)) {
      return;
    }

    if (idleCheckInventoryChanged) {
      lastInventoryChangeTick = Static.getClient().getTickCount();
    }
  }

  @Subscribe
  private void onGameStateChanged(GameStateChanged event) {
    if (event.getGameState() == GameState.LOGGED_IN) {
      lastLoginTick = Static.getClient().getTickCount();
    }
  }

  @Override
  protected int loop() {
    if (!running) {
      return -1;
    }

    log.debug("Game tick: " + Static.getClient().getTickCount());

    for (Task t : tasks) {
      if (t.validate()) {
        log.info(t.getStatus());
        final int delay = t.execute();

        checkActionTimeout();

        if (t.isBlocking()) {
          log.debug("Sleeping for {} ticks after task", Math.abs(delay));
          return -Math.abs(delay);
        }
      }
    }

    return -1;
  }

  @Override
  protected final void startUp() {
    Static.getKeyManager().registerKeyListener(this);
  }

  @Override
  protected final void shutDown() {
    Static.getKeyManager().unregisterKeyListener(this);
  }

  public void onStart() {
    log.info("Starting " + this.getName());

    previousActivity = Activity.IDLE;
    currentActivity = Activity.IDLE;

    running = true;
  }

  /**
   * Stop running the plugin without unregistering it from the LoopedPluginManager.
   *
   * @param message message to send the user on stopping the plugin
   */
  public void onStop(String message) {
    MessageUtils.addMessage(message);
    onStop();
  }

  /**
   * Stop running the plugin without unregistering it from the LoopedPluginManager.
   */
  public void onStop() {
    log.info("Stopping " + this.getName());
    running = false;

    for (Task task : tasks) {
      if (task.subscribe()) {
        Static.getEventBus().unregister(task);
      }
    }

    tasks.clear();
  }

  /**
   * Stop running the plugin. Gets called from the LoopedPluginManager when the plugin gets
   * toggled off.
   */
  @Override
  public void stop() {
    onStop();
    super.stop();
  }

  @Override
  public boolean isRunning() {
    return running;
  }

  public final void addTask(Task task) {
    if (task.subscribe()) {
      Static.getEventBus().register(task);
    }

    tasks.add(task);
  }

  public final <T extends Task> void addTask(Class<T> type) {
    addTask(injector.getInstance(type));
  }

  public void setActivity(Activity activity) {
    if (activity == Activity.IDLE && currentActivity != Activity.IDLE) {
      previousActivity = currentActivity;
    }

    currentActivity = activity;

    if (activity != Activity.IDLE) {
      lastActionTick = Static.getClient().getTickCount();
    }
  }

  public void addIdleCheckSkill(Skill skill, Activity activity) {
    idleCheckSkills.put(skill, activity);
  }

  private void checkActionTimeout() {
    if (currentActivity == Activity.IDLE) {
      return;
    }

    final int currentTick = Static.getClient().getTickCount();

    if (currentTick - lastExperienceTick < lastActionTimeout) {
      return;
    }

    if (currentTick - lastInventoryChangeTick < lastActionTimeout) {
      return;
    }

    if (!Players.getLocal().isIdle()) {
      lastActionTick = currentTick;
      return;
    }

    if (currentTick - lastActionTick >= lastActionTimeout) {
      setActivity(Activity.IDLE);
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyPressed(KeyEvent e) {
    if (utilsConfig.walkingInterruptHotkey().matches(e)) {
      ChaosMovement.interrupted = true;
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {}
}
