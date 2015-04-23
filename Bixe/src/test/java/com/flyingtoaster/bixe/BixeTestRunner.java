package com.flyingtoaster.bixe;

import org.junit.runners.model.InitializationError;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;
import org.robolectric.res.FsFile;

public class BixeTestRunner extends RobolectricTestRunner {
  public BixeTestRunner(Class<?> testClass) throws InitializationError {
    super(testClass);
  }

  @Override
  protected AndroidManifest getAppManifest(Config config) {
    String myAppPath = BixeTestRunner.class.getProtectionDomain()
                                                        .getCodeSource()
                                                        .getLocation()
                                                        .getPath();
      String manifestPath = myAppPath + "../../../../../src/main/AndroidManifest.xml";

      FsFile customManifest = Fs.fileFromPath(manifestPath);
      FsFile appBaseDir = customManifest.getParent();

    return new AndroidManifest(customManifest, appBaseDir.join("res"), appBaseDir.join("assets"));
  }
}