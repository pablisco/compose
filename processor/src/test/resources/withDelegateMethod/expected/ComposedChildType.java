package test;

import test.ChildType;

class ComposedChildType extends ParentType {

  private ChildType delegate = new ChildType();

  public boolean getValue() {
    return delegate.getValue();
  }

}
