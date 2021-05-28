# xoom-schemata - GraalVM Support

The VLINGO XOOM Schema Registry.

## Getting started

Prerequisites:
* Java JDK 8 or greater
* Maven
* [GraalVM 21.1.0 Java 8/11](https://www.graalvm.org/docs/getting-started/)

## Maven build
```bash
mvn clean package -Pfrontend
```
- Generate native image resources Configs
```bash
java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image/io.vlingo.xoom/xoom-schemata -jar target/xoom-schemata-<version>-SNAPSHOT-jar-with-dependencies.jar dev
```
### Tips to load more resources and reflection for the agent
- Reload the web page from the browser
- Test max functionality of the app
- Adding missing serialization:
```json

  {
    "name": "java.sql.Timestamp"
  },
  {
    "name": "java.lang.Object"
  },
  {
    "name": "java.lang.Boolean"
  },
  {
    "name": "java.lang.Character"
  },
  {
    "name": "java.lang.Double"
  },
  {
    "name": "java.lang.Float"
  },
  {
    "name": "java.lang.Long"
  },
  {
    "name": "java.lang.Integer"
  },
  {
    "name": "java.lang.Short"
  },
  {
    "name": "java.lang.Byte"
  },
  {
    "name": "java.lang.String"
  },
```

## Native Image Maven Plugin & GraalVM SDK
```
<properties>
    ...
    <exec.mainClass>io.vlingo.xoom.schemata.XoomInitializer</exec.mainClass>
    <graalvm.version>21.1.0</graalvm.version>
    ...
</properties>
<dependencies>
    ...
    <dependency>
      <groupId>org.graalvm.sdk</groupId>
      <artifactId>graal-sdk</artifactId>
      <version>${graalvm.version}</version>
      <scope>provided</scope>
    </dependency>
    ...
</dependencies>

```
```
<profiles>
    ...
    <profile>
      <id>native-image</id>
      <build>
        <plugins>
          <plugin>
            <groupId>org.graalvm.nativeimage</groupId>
            <artifactId>native-image-maven-plugin</artifactId>
            <version>${graalvm.version}</version>
            <executions>
              <execution>
                <goals>
                  <goal>native-image</goal>
                </goals>
                <phase>package</phase>
              </execution>
            </executions>
            <configuration>
              <imageName>${project.name}</imageName>
              <mainClass>${exec.mainClass}</mainClass>
              <buildArgs>
                --no-fallback --no-server --enable-url-protocols=http -H:+AllowIncompleteClasspath
                -H:ReflectionConfigurationFiles=classes/META-INF/native-image/reflect-config.json
                -H:ResourceConfigurationFiles=classes/META-INF/native-image/resource-config.json
                -H:SerializationConfigurationFiles=classes/META-INF/native-image/serialization-config.json
                --initialize-at-run-time=io.netty
                --initialize-at-run-time=io.vlingo.xoom.common.identity.IdentityGeneratorType
                --report-unsupported-elements-at-runtime
                --allow-incomplete-classpath
              </buildArgs>
            </configuration>
          </plugin>
        </plugins>
      </build>
    </profile>
    ...
</profiles>
```
- To build the native image run:
```bash
mvn clean package -Pfrontend -Pnative-image
```
```bash
./target/xoom-schemata
```
- On native image runtime, an exception is always thrown, issue described here: [ISSUE](https://github.com/RuedigerMoeller/fast-serialization/issues/313)

## Docker build and run
- Increase the Docker Memory Resource to +8Go.
- First build the jar file:
```bash
mvn clean package -Pfrontend
```
- Build the docker image
```bash
docker build -f Dockerfile.native -t vlingo/xoom-schemata .
```
- Run the docker image
```bash
docker run -it --rm -p '9019:9019' vlingo/xoom-schemata
```
- On native image runtime, a netty native transport epoll issue: [PR](https://github.com/netty/netty/pull/11163)
