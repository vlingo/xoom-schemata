package io.vlingo.schemata.model.schema;

import io.vlingo.schemata.model.EventsCategory;

public interface SchemaEntity {
    void describeAs(String description);

    void recategorizedAs(EventsCategory.Category category);

    void renameTo(String name);
}
