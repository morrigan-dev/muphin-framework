package de.morrigan.dev.muphin.core.cmd;

public class CmdResponse {
   private int exitValue;
   private String message;
   private Exception exception;

   public CmdResponse(int exitValue, String message, Exception exception) {
      super();
      this.exitValue = exitValue;
      this.message = message;
      this.exception = exception;
   }

   public String getMessage() {
      return this.message;
   }

   public Exception getException() {
      return this.exception;
   }

   public int getExitValue() {
      return this.exitValue;
   }
}
