package de.morrigan.dev.muphin.core.cmd;

public class DefaultCmdQueryBuilder {

   private StringBuilder cmdQuery;

   protected DefaultCmdQueryBuilder() {
      super();
      this.cmdQuery = new StringBuilder();
   }

   public DefaultCmdQueryBuilder changeDirectory(String directoryPath) {
      prepareForNextCommand();
      this.cmdQuery.append("cd ").append(directoryPath);
      return this;
   }

   /**
    * {@code git config --global credential.helper manager} needed for https connection!
    *
    * @return
    * @see https://snede.net/git-does-not-remember-username-password/
    */
   public DefaultCmdQueryBuilder gitPull() {
      prepareForNextCommand();
      this.cmdQuery.append("git pull");
      return this;
   }

   public DefaultCmdQueryBuilder addCustomCommand(String command) {
      prepareForNextCommand();
      this.cmdQuery.append(command);
      return this;
   }

   public String getCommand() {
      return this.cmdQuery.toString();
   }

   protected void prepareForNextCommand() {
      if (this.cmdQuery.length() > 0) {
         this.cmdQuery.append(" && ");
      }
   }
}
