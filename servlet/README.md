# Thrift 中使用 Servlet，使用 HTTP 直接访问 

Thrift 支持将服务实现添加到 Servlet，部署在容器中，通过 HTTP 的方式访问；在 Client 中，支持使用 `THttpClient` 作为 Transport 访问服务端；通过使用 `TJSONProtocol`(支持其他协议，需要和服务端一致)，将请求内容序列化为 JSON 格式，然后使用 POST 方式请求服务端；服务端会返回同样的格式作为响应，由客户端解析处理

## Gradle 配置

- build.gradle.kts

修改 Gradle 配置，添加 Servlet 相关的依赖，用于支持容器

```diff
plugins {
    java
    idea
    application
+   war
    id("io.freefair.lombok") version "5.3.0"
    id("org.jruyi.thrift") version "0.4.1"
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

val thriftVersion = "0.13.0-SNAPSHOT"
val slf4jVersion = "1.7.25"
+val tomcatVersion = "9.0.26"
+val servletVersion = "3.0.1"

dependencies {
    implementation("org.apache.thrift:libthrift:${thriftVersion}")
    implementation("org.slf4j:slf4j-api:${slf4jVersion}")
    implementation("org.slf4j:slf4j-simple:${slf4jVersion}")

+   implementation("org.apache.tomcat.embed:tomcat-embed-core:${tomcatVersion}")
+   implementation("org.apache.tomcat.embed:tomcat-embed-jasper:${tomcatVersion}")
+   implementation("javax.servlet:javax.servlet-api:${servletVersion}")

    testImplementation("junit:junit:4.13")
}
```

## 实现 Server 端

### 启动 Tomcat 

- src/main/webapp/WEB-INF/web.xml

```xml
<!DOCTYPE web-app PUBLIC
        "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
        "http://java.sun.com/dtd/web-app_2_3.dtd" >

<web-app>
    <display-name>Thrift Servlet Application</display-name>
</web-app>
```

- Server

```java
@Slf4j
public class ServletServer {

    public static void main(String[] args) {
        try {
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(8080);
            tomcat.getConnector();

            Context context = tomcat.addWebapp("", new File("servlet/src/main/webapp").getAbsolutePath());
            WebResourceRoot resources = new StandardRoot(context);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", new File("servlet/build/classes").getAbsolutePath(), "/"));
            context.setResources(resources);

            tomcat.start();
            tomcat.getServer().wait();
        } catch (Throwable e) {
            log.error("Start server failed: {}", e.getMessage(), e);
        }
    }
}
```

### 将服务添加到 Servlet

定义了路径是 `/hello`，客户端请求时需要访问这个路径

```java
@WebServlet(urlPatterns = "/hello")
@Slf4j
public class HelloServiceServlet extends TServlet {

    public HelloServiceServlet() {
        // 这里的协议要和 Client 端一致
        super(new HelloService.Processor<>(new HelloServiceImpl()), new TJSONProtocol.Factory());
    }
}

@Slf4j
class HelloServiceImpl implements HelloService.Iface {

    @Override
    public HelloResponse sayHello(HelloMessage request) throws TException {
        String message = request.getMessage();
        log.info("接收到请求: {}", message);

        HelloResponse response = new HelloResponse();
        response.setMessage("Hello " + message);
        return response;
    }
}
```

## Client 端

### 使用 Transport

客户端使用 `THttpClient` 类型的 Transport，使用 `TJSONProtocol`，最终会将序列化之后的请求内容作为 Body 发送给服务端

```java
@Slf4j
public class HttpClient {

    public static void main(String[] args) throws InterruptedException {

        try {
            THttpClient transport = new THttpClient("http://localhost:8080/hello");
            // 这里的协议要和 Server 端一致
            TProtocol protocol = new TJSONProtocol(transport);

            HelloService.Client client = new HelloService.Client(protocol);

            HelloMessage request = new HelloMessage();
            request.setMessage("Servlet");

            HelloResponse response = client.sayHello(request);
            log.info("返回响应: {}", response.getMessage());

        } catch (TException e) {
            e.printStackTrace();
        }
        Thread.sleep(3_000);
    }
}
```

### 使用其他方式

只要格式可以被服务端解析，就可以使用其他的方式发送请求：

```shell
curl -X POST \
  http://localhost:8080/hello \
  -H 'Accept: application/x-thrift' \
  -H 'Content-Type: application/x-thrift' \
  -H 'User-Agent: Java/THttpClient' \
  -d '[1,"sayHello",1,1,{"1":{"rec":{"1":{"str":"Servlet"}}}}]'

# 响应 
[1,"sayHello",2,1,{"0":{"rec":{"1":{"str":"Hello Servlet"}}}}]%
```

#### 请求 Body

请求 Body 的相关写入实现参考 [org.apache.thrift.protocol.TJSONProtocol#writeMessageBegin](https://github.com/apache/thrift/blob/077b5fce825e79d84592fff893639b92b637eec7/lib/java/src/org/apache/thrift/protocol/TJSONProtocol.java#L505)

Body `[1,"sayHello",1,1,{"1":{"rec":{"1":{"str":"Servlet"}}}}]` 中的值含义依次为:

- `1`: 代表 `VERSION`
- `sayHello`: 被调用的方法名称
- `1`: TMessage#type，即 `TMessageType.CALL`，代表类型是调用
- `1`: TMessage#seqid，请求的序号
- `{"1":{"rec":{"1":{"str":"Servlet"}}}}` 请求体
    - `1`: TField#id，属性的 ID，即 `io.github.helloworlde.thrift.HelloService.sayHello_args.REQUEST_FIELD_DESC`，值为 `TField("request", org.apache.thrift.protocol.TType.STRUCT, (short)1)`，表示一个结构体
    - `{"rec":{"1":{"str":"Servlet"}}}}`：结构体对象的内容
        - `rec`: 即`org.apache.thrift.protocol.TJSONProtocol.NAME_STRUCT`，表示结构体，即 `HelloMessage`
        - `{"1":{"str":"Servlet"}}`: 表示结构体对象内容
            - `1`: 表示属性的 ID，即 `HelloMessage` 中的 `1: required string message`
            - `{"str":"Servlet"}`: `HelloMessage` 具体的值
                - `str`: 表示 `message` 这个属性是 `string` 类型
                - `Servlet`: 表示请求的 `message` 的实际值 

返回的响应 `[1,"sayHello",2,1,{"0":{"rec":{"1":{"str":"Hello Servlet"}}}}]` 类似：

- `1`: 代表 `VERSION`
- `sayHello`: 被调用的方法名称
- `2`: TMessage#type，即 `TMessageType.REPLY`，代表类型是响应
- `1`: TMessage#seqid，请求的序号
- `{"0":{"rec":{"1":{"str":"Hello Servlet"}}}}` 响应体
    - `0`: TField#id，属性的 ID，即 `io.github.helloworlde.thrift.HelloService.sayHello_result.SUCCESS_FIELD_DESC`，值为 `TField("success", org.apache.thrift.protocol.TType.STRUCT, (short)0)`，表示请求成功
    - `{"rec":{"1":{"str":"Hello Servlet"}}}}`：响应结构体对象的内容
        - `rec`: 即`org.apache.thrift.protocol.TJSONProtocol.NAME_STRUCT`，表示结构体，即 `HelloResponse`
        - `{"1":{"str":"Hello Servlet"}}}`: 表示结构体对象内容
            - `1`: 表示属性的 ID，即 `HelloResponse` 中的 `1: required string message`
            - `{"str":"Hello Servlet"}`: `HelloMessage` 具体的值
                - `str`: 表示 `message` 这个属性是 `string` 类型
                - `Hello Servlet`: 表示请求的 `message` 的实际值 

