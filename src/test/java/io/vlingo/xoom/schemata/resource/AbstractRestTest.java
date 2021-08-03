package io.vlingo.xoom.schemata.resource;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.specification.RequestSpecification;
import io.vlingo.xoom.schemata.XoomInitializer;
import io.vlingo.xoom.schemata.codegen.TypeDefinitionCompiler;
import io.vlingo.xoom.schemata.infra.persistence.StorageProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public abstract class AbstractRestTest {

    private XoomInitializer xoom;

    @BeforeClass
    public static void init() {
        StorageProvider.clear();
        RestAssured.defaultParser = Parser.JSON;
    }

    @Before
    public void setUp() throws Exception {
        XoomInitializer.main(new String[]{"test"});
        xoom = XoomInitializer.instance();
        Boolean startUpSuccess = xoom.server().startUp().await(100);
        assertThat(startUpSuccess, is(equalTo(true)));
    }

    @After
    public void cleanUp() throws InterruptedException {
        System.out.println("==== Test Server shutting down ");
        xoom.terminateWorld();
        waitServerClose();
    }

    public RequestSpecification given() {
        return io.restassured.RestAssured.given()
                .port(19090)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON);
    }

    private void waitServerClose() throws InterruptedException {
        while(xoom != null && xoom.server() != null && !xoom.server().isStopped()) {
            Thread.sleep(100);
        }
    }

}