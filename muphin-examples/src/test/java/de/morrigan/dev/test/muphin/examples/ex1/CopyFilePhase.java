package de.morrigan.dev.test.muphin.examples.ex1;

import de.morrigan.dev.muphin.core.phase.AbstractPhase;
import de.morrigan.dev.test.muphin.examples.ex1.CopyFilePhase.CopyFileData;

public class CopyFilePhase extends AbstractPhase<CopyFileData> {

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
    super("Copy file");
  }

  @Override
  protected boolean execute(CopyFileData data) {
    // TODO Auto-generated method stub
    return false;
  }

}
