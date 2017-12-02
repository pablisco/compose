# Compose [WIP]
*Inheritance by Composition*

Removes the need to add strict parental dependency from a super class before compilation. 
This allows to exersice our code in tests without being influenced by the super class.

For example:

```
// ParentType.java
public class ParentType {}

// ChildType.java
@Compose(ParentType.class)
public class ChildType {}
```

Will generate this class for runtime: 

```
class ComposedChildType extends ParentType {}
```

## TODO:

 - Make it usable (top priority :joy: )

 - Provide super class in the constructor so we can interact with it.
 
 ```
 @Compose(ParentType.class)
 public class ChildType {
   ChildType(ParentType parent) { }
 }
 ```
 
 - Enforce Annotation in overloaded methods in a similar fashion to `@Override`
 
```
@OverridesParent
public void methodFromSuper() {}
```
 
 - Allow for custom strategy for calling super implementation in a method.
```
@OverridesParent(call = BEFORE) // default
public void methodFromSuper() {}

@OverridesParent(call = MANUAL)
public void methodFromSuper() {
  parent.methodFromSuper()
}

@OverridesParent(call = AFTER)
public void methodFromSuper() {}
```

- Propagate runtime annotations to final class
