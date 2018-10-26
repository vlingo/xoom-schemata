package io.vlingo.schemata.model.organization;

/**
 * @author Chandrabhan Kumhar
 */
public interface OrganizationEntity {
    void describeAs(String description);

    void renameTo(String name);
}
