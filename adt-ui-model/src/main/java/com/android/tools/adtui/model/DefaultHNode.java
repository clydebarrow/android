/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.android.tools.adtui.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DefaultHNode<T> implements HNode<T> {

  private long myStart;
  private long myEnd;
  @Nullable private T myData;
  @NotNull private List<DefaultHNode<T>> myChildren;

  /**
   * The parent of its child is set to it when it is added {@link #addChild(DefaultHNode)}
   */
  @Nullable private DefaultHNode<T> myParent;

  /**
   * The shortest distance from the root.
   */
  private int myDepth;

  public DefaultHNode() {
    this(null, 0, 0);
  }

  public DefaultHNode(@Nullable T data, long start, long end) {
    myChildren = new ArrayList<>();
    myData = data;
    myStart = start;
    myEnd = end;
  }

  public void addChild(DefaultHNode<T> node) {
    myChildren.add(node);
    node.myParent = this;
  }

  @Override
  public int getChildCount() {
    return myChildren.size();
  }

  @NotNull
  @Override
  public DefaultHNode<T> getChildAt(int index) {
    return myChildren.get(index);
  }

  @Nullable
  @Override
  public DefaultHNode<T> getParent() {
    return myParent;
  }

  @Override
  public long getEnd() {
    return myEnd;
  }

  public void setEnd(long end) {
    myEnd = end;
  }

  @Override
  public long getStart() {
    return myStart;
  }

  public void setStart(long start) {
    myStart = start;
  }

  @Nullable
  @Override
  public T getData() {
    return myData;
  }

  public void setData(@Nullable T data) {
    myData = data;
  }

  @Override
  public int getDepth() {
    return myDepth;
  }

  public void setDepth(int depth) {
    myDepth = depth;
  }

  @NotNull
  public List<DefaultHNode<T>> getChildren() {
    return myChildren;
  }
}
