package de.morrigan.dev.muphin.examples.ex1;

import de.morrigan.dev.muphin.core.cmd.GenericCmdQueryBuilder;

public class MyGenericCmdQueryBuilder extends GenericCmdQueryBuilder<MyGenericCmdQueryBuilder> {

  public MyGenericCmdQueryBuilder startMyApp() {
    addCustomCommand("start myApp.exe");
    return getBuilder();
  }

  @Override
  protected MyGenericCmdQueryBuilder getBuilder() {
    return this;
  }

  public static void main(String[] args) {
    String command = new MyGenericCmdQueryBuilder()
        .changeDirectory("myapp")
        .startMyApp()
        .getCommand();
  }
}
