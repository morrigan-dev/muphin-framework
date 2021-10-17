package de.morrigan.dev.test.muphin.examples.ex1;

import de.morrigan.dev.muphin.core.phase.AbstractPhase;

public class CopyFilePhase extends AbstractPhase {

  public class CopyFileData {
    private final String templateFilePath;
    private final String destinationFilePath;

    public CopyFileData(String templateFilePath, String destinationFilePath) {
      super();
      this.templateFilePath = templateFilePath;
      this.destinationFilePath = destinationFilePath;
    }

    public String getTemplateFilePath() {
      return this.templateFilePath;
    }

    public String getDestinationFilePath() {
      return this.destinationFilePath;
    }
  }

  protected CopyFilePhase() {
    super("Test", "Copy file");
  }

  @Override
  public boolean execute() {
    return false;
  }

}
