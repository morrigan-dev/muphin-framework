package de.morrigan.dev.muphin.core;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.helpers.MessageFormatter;

import de.morrigan.dev.muphin.core.phase.AbstractPhase;
import de.morrigan.dev.muphin.core.workflow.AbstractWorkflow;

/**
 * Serves as a data container that remains available throughout the entire runtime and in which cross-test data can be
 * stored. It also contains the current workflow and the current phase that are being executed. In addition, any other
 * data can be added and retrieved.
 *
 * @author morrigan
 * @since 0.0.1
 */
public class MuphinSession {

  private static final MuphinSession INSTANCE = new MuphinSession();

  /**
   * @return only instance of this session
   */
  public static final MuphinSession getInstance() {
    return INSTANCE;
  }

  private Map<String, Object> dataCache;

  // use your own variables so that the user does not accidentally and randomly overwrite this internal information
  private AbstractWorkflow currentWorkflow;
  private AbstractPhase currentPhase;

  private MuphinSession() {
    super();
    this.dataCache = new ConcurrentHashMap<>();
  }

  /**
   * Adds data under a unique key to the session.
   *
   * @param key a key that uniquely identifies this data
   * @param data data to be stored
   * @since 0.0.1
   */
  public void putData(String key, Object data) {
    this.dataCache.put(key, data);
  }

  /**
   * Removes data that is stored under the given key from the session.
   *
   * @param key a key that uniquely identifies the data
   * @since 0.0.1
   */
  public void remove(String key) {
    this.dataCache.remove(key);
  }

  /**
   * Clears all data in this session.
   *
   * @since 0.0.1
   */
  public void clear() {
    this.dataCache.clear();
    this.currentWorkflow = null;
    this.currentPhase = null;
  }

  /**
   * Returns data with the given type in a generic way to the given key. The type is used to check whether the data in
   * the session actually has the expected type. If this is not the case, an {@code IllegalArgumentException} is thrown.
   *
   * @param <T> a type of your data
   * @param key a key that uniquely identifies the data
   * @param type a type to verify the type of the data
   * @return the data that is stored for the key
   * @since 0.0.1
   */
  @SuppressWarnings("unchecked")
  public <T> Optional<T> getData(String key, Class<T> type) {
    Object data = this.dataCache.get(key);
    if (data != null) {
      if (data.getClass().isAssignableFrom(type)) {
        return Optional.of((T) data);
      } else {
        String msg = MessageFormatter
            .arrayFormat("Type of data with the key {} does not match with the expected type {}",
                new Object[] {
                    key, type
                }).getMessage();
        throw new IllegalArgumentException(msg);
      }
    }
    return Optional.empty();
  }

  /**
   * @return the current workflow
   * @since 0.0.1
   */
  public AbstractWorkflow getCurrentWorkflow() {
    return this.currentWorkflow;
  }

  /**
   * Sets the current workflow that is executed by this framework.
   * <p>
   * This method has package-default visibility and is designed for internal usage. The {@link WorkflowRunner} should be
   * the only instance that sets this current workflow instance!
   *
   * @param currentWorkflow a workflow
   * @since 0.0.1
   */
  void setCurrentWorkflow(AbstractWorkflow currentWorkflow) {
    this.currentWorkflow = currentWorkflow;
  }

  /**
   * @return the current phase
   * @since 0.0.1
   */
  public AbstractPhase getCurrentPhase() {
    return this.currentPhase;
  }

  /**
   * Sets the current phase that is executed ba this framework.
   * <p>
   * This method has package-default visibility and is designed for internal usage. The {@link WorkflowRunner} should be
   * the only instance that sets this current phase instance!
   *
   * @param currentPhase a phase
   * @since 0.0.1
   */
  void setCurrentPhase(AbstractPhase currentPhase) {
    this.currentPhase = currentPhase;
  }
}
