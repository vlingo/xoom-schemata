package io.vlingo.xoom.schemata.resource;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.specification.RequestSpecification;
import io.vlingo.xoom.schemata.XoomInitializer;
import io.vlingo.xoom.schemata.infra.persistence.StorageProvider;

public abstract class AbstractRestTest {
  private int serverPort;  
  private XoomInitializer xoom;

    @BeforeClass
    public static void init() {
        StorageProvider.clear();
        RestAssured.defaultParser = Parser.JSON;
    }

    @Before
    public void setUp() throws Exception {
        XoomInitializer.main(new String[]{ "dev" });
        xoom = XoomInitializer.instance();
        serverPort = xoom.serverPort();
        Boolean startUpSuccess = xoom.server().startUp().await(100);
        assertThat(startUpSuccess, is(equalTo(true)));
    }

    @After
    public void cleanUp() throws InterruptedException {
        System.out.println("==== Test Server shutting down ");
        StorageProvider.clear();
        xoom.terminateWorld();
        waitServerClose();
    }

    public RequestSpecification given() {
        return io.restassured.RestAssured.given()
                .port(serverPort)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON);
    }

    private void waitServerClose() throws InterruptedException {
        while(xoom != null && xoom.server() != null && !xoom.server().isStopped()) {
            Thread.sleep(100);
        }
    }

}