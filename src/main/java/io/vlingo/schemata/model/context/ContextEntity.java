package io.vlingo.schemata.model.context;

/**
 * @author Chandrabhan Kumhar
 */
public interface ContextEntity {
    void changeNamespaceTo(String namespace);

    void describeAs(String description);
}
