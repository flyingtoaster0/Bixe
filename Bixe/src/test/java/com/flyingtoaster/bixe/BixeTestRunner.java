package com.flyingtoaster.bixe;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.FileFsFile;
import org.robolectric.res.FsFile;

public class BixeTestRunner extends RobolectricTestRunner {
  public BixeTestRunner(Class<?> testClass) throws InitializationError {
    super(testClass);
  }

    protected AndroidManifest getAppManifest(Config config) {
        AndroidManifest appManifest = super.getAppManifest(config);
        FsFile androidManifestFile = appManifest.getAndroidManifestFile();

        if (androidManifestFile.exists()) {
            return appManifest;
        } else {
            String moduleRoot = getModuleRootPath(config);
            androidManifestFile = FileFsFile.from(moduleRoot, appManifest.getAndroidManifestFile().getPath().replace("bundles", "manifests/full"));
            FsFile resDirectory = FileFsFile.from(moduleRoot, appManifest.getResDirectory().getPath().replace("/res", "").replace("bundles", "res"));
            FsFile assetsDirectory = FileFsFile.from(moduleRoot, appManifest.getAssetsDirectory().getPath().replace("/assets", "").replace("bundles", "assets"));
            return new AndroidManifest(androidManifestFile, resDirectory, assetsDirectory);
        }
    }

    private String getModuleRootPath(Config config) {
        String moduleRoot = config.constants().getResource("").toString().replace("file:", "");
        return moduleRoot.substring(0, moduleRoot.indexOf("/build"));
    }
}