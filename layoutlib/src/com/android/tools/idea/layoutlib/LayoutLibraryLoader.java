/*
 * Copyright 2000-2010 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.tools.idea.layoutlib;

import com.android.SdkConstants;
import com.android.ide.common.rendering.api.LayoutLog;
import com.android.layoutlib.bridge.Bridge;
import com.android.sdklib.IAndroidTarget;
import com.android.sdklib.SdkVersionInfo;
import com.android.sdklib.internal.project.ProjectProperties;
import com.android.tools.io.BufferingFileWrapper;
import com.android.utils.ILogger;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Loads a {@link LayoutLibrary}
 */
public class LayoutLibraryLoader {
  private static final Logger LOG = Logger.getInstance("#org.jetbrains.android.uipreview.LayoutLibraryLoader");

  private LayoutLibraryLoader() {
  }

  @Nullable
  public static LayoutLibrary load(@NotNull IAndroidTarget target,
                                   @NotNull Map<String, Map<String, Integer>> enumMap) throws RenderingException, IOException {
    final String fontFolderPath = FileUtil.toSystemIndependentName((target.getPath(IAndroidTarget.FONTS)));
    final VirtualFile fontFolder = LocalFileSystem.getInstance().findFileByPath(fontFolderPath);
    if (fontFolder == null || !fontFolder.isDirectory()) {
      throw new RenderingException(
        LayoutlibBundle.message("android.directory.cannot.be.found.error", FileUtil.toSystemDependentName(fontFolderPath)));
    }

    final String platformFolderPath = target.isPlatform() ? target.getLocation() : target.getParent().getLocation();
    final File platformFolder = new File(platformFolderPath);
    if (!platformFolder.isDirectory()) {
      throw new RenderingException(
        LayoutlibBundle.message("android.directory.cannot.be.found.error", FileUtil.toSystemDependentName(platformFolderPath)));
    }

    final File buildProp = new File(platformFolder, SdkConstants.FN_BUILD_PROP);
    if (!buildProp.isFile()) {
      throw new RenderingException(
        LayoutlibBundle.message("android.file.not.exist.error", FileUtil.toSystemDependentName(buildProp.getPath())));
    }

    if (!SystemInfo.isJavaVersionAtLeast(8, 0, 0) && target.getVersion().getFeatureLevel() >= 24) {
      // From N, we require to be running in Java 8
      throw new UnsupportedJavaRuntimeException(LayoutlibBundle.message("android.layout.preview.unsupported.jdk",
                                                                        SdkVersionInfo.getCodeName(target.getVersion().getFeatureLevel())));
    }

    LayoutLibrary library;
    final ILogger logger = new LogWrapper(LOG);
    // We instantiate the local Bridge implementation and pass it to the LayoutLibrary instance
    library = LayoutLibrary.load(new Bridge(), new LayoutlibClassLoader(LayoutLibraryLoader.class.getClassLoader()));

    final Map<String, String> buildPropMap = ProjectProperties.parsePropertyFile(new BufferingFileWrapper(buildProp), logger);
    final LayoutLog layoutLog = new LayoutLogWrapper(LOG);
    if (library.init(buildPropMap, new File(fontFolder.getPath()), enumMap, layoutLog)) {
      return library;
    } else {
      return null;
    }
  }
}
