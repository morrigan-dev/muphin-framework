package de.morrigan.dev.muphin.core;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.helpers.MessageFormatter;

import de.morrigan.dev.muphin.core.workflow.AbstractWorkflow;

public class MuphinSession {

   public static final String CMD_SESSION = "cmdSession";

   private static final MuphinSession INSTANCE = new MuphinSession();

   public static final MuphinSession getInstance() {
      return INSTANCE;
   }

   private Map<String, Object> dataCache;
   private AbstractWorkflow currentWorkflow;

   private MuphinSession() {
      super();
      this.dataCache = new ConcurrentHashMap<>();
   }

   public void putData(String key, Object data) {
      this.dataCache.put(key, data);
   }

   public void remove(String key) {
      this.dataCache.remove(key);
   }

   @SuppressWarnings("unchecked")
   public <T> Optional<T> getData(String key, Class<T> type) {
      Object data = this.dataCache.get(key);
      if (data != null) {
         if (data.getClass().isAssignableFrom(type)) {
            return Optional.of((T) data);
         } else {
            String msg = MessageFormatter.arrayFormat("Type of data with the key {} does not match with the expected type {}",
                     new Object[] { key, type }).getMessage();
            throw new IllegalStateException(msg);
         }
      }
      return Optional.empty();
   }

   public AbstractWorkflow getCurrentWorkflow() {
      return this.currentWorkflow;
   }

   public void setCurrentWorkflow(AbstractWorkflow currentWorkflow) {
      this.currentWorkflow = currentWorkflow;
   }

}
