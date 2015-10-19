/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.tools.idea.uibuilder.handlers;

import com.android.annotations.NonNull;
import com.android.annotations.Nullable;
import com.android.resources.ResourceType;
import com.android.tools.idea.uibuilder.api.InsertType;
import com.android.tools.idea.uibuilder.api.ViewEditor;
import com.android.tools.idea.uibuilder.api.ViewHandler;
import com.android.tools.idea.uibuilder.model.NlComponent;

import java.util.EnumSet;

import static com.android.SdkConstants.ATTR_LAYOUT;

/** Handler for the {@code <include>} tag */
public class IncludeHandler extends ViewHandler {
  @Override
  public boolean onCreate(@NonNull ViewEditor editor,
                          @Nullable NlComponent parent,
                          @NonNull NlComponent newChild,
                          @NonNull InsertType insertType) {
    // When dropping an include tag, ask the user which layout to include.
    if (insertType == InsertType.CREATE) { // NOT InsertType.CREATE_PREVIEW
      String src = editor.displayResourceInput(EnumSet.of(ResourceType.LAYOUT), null);
      if (src != null) {
        newChild.setAttribute(null, ATTR_LAYOUT, src);
        return true;
      }
      else {
        // Remove the view; the insertion was canceled
        return false;
      }
    }

    return true;
  }
}