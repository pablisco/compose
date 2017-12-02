package compose.demo;

import compose.Compose;

@Compose(ParentType.class)
public class ChildType {

    static ParentType create() {
        return new ComposedChildType();
    }

}
