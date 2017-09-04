package com.znt.speaker.jmdns;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import org.apache.http.conn.util.InetAddressUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.znt.speaker.util.NetWorkUtils;

/**
 * Android AirPlay Server Implementation
 * 
 * @author Rafael Almeida
 *
 */
public class AirPlayServer implements Runnable {

//	private static final Logger LOG = Logger.getLogger(AirPlayServer.class.getName());
	
	
	/**
	 * The DACP service type
	 */
//	static final String AIR_REMOTE_CONTROL_SERVICE_TYPE = "_dacp._tcp.local.";
//	static final Map<String, String> AIR_DACP_SERVICE_PROPERTIES = map(
//		"txtvers", "1",
//		"Ver", "131075",
//		"DbId", "63B5E5C0C201542E",
//		"OSsi", "0x1F5"
//	);
	
	/**
	 * The AirTunes/RAOP service type
	 */
	static final String AIR_TUNES_SERVICE_TYPE = "_raop._tcp.local.";
	
	/**
	 * The AirTunes/RAOP M-DNS service properties (TXT record)
	 */
	static final Map<String, String> AIRTUNES_SERVICE_PROPERTIES = map(
		"txtvers", "1",
		"tp", "UDP",
		"ch", "2",
		"ss", "16",
		"sr", "44100",
		"pw", "false",
		"sm", "false",
		"sv", "false",
		"ek", "1",
		"et", "0,1",
		"cn", "0,1",
		"vn", "3"
	);
	Context context;
	private static AirPlayServer instance = null;
	public static AirPlayServer getIstance(Context context){
		if(instance == null){
			instance = new AirPlayServer(context);
		}
		return instance;
	}
	
	/**
	 * Global executor service. Used e.g. to initialize the various netty channel factories 
	 */
	protected ExecutorService executorService;
	
	
	/**
	 * JmDNS instances (one per IP address). Used to unregister the mDNS services
	 * during shutdown.
	 */
	protected List<JmDNS> jmDNSInstances;
	
	/**
	 * The AirTunes/RAOP RTSP port
	 */
	private int rtspPort = 5000; //default value
	
	private AirPlayServer(Context context){
		
		this.context = context ;
		
		//create executor service
		executorService = Executors.newCachedThreadPool();
		
		//list of mDNS services
		jmDNSInstances = new java.util.LinkedList<JmDNS>();
	}

	public int getRtspPort() {
		return rtspPort;
	}

	public void setRtspPort(int rtspPort) {
		this.rtspPort = rtspPort;
	}

	public void run() {
		long start = System.currentTimeMillis() ;
		startService();
	}

	@SuppressLint("NewApi")
	private void startService() 
	{
		/* Make sure AirPlay Server shuts down gracefully */
    	Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				onShutdown();
			}
    	}));
    	
        
//        String hostName = networkUtils.getHostUtils();
        Build bd = new Build();    
        String hostName = "NELDTV_AirPlay_" + bd.MODEL;   
    	Log.w("hefeng", "servName : " + hostName);
    	
		String hardwareAddressString = NetWorkUtils.getLocalIpAddress(context);
        
        try {
            /* Create mDNS responders. */
            synchronized (jmDNSInstances) {
                for (final NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                    if (iface.isLoopback() || iface.isPointToPoint() || !iface.isUp()) {
                        continue;
                    }

                    ArrayList<InetAddress> inets = Collections.list(iface.getInetAddresses());

                    for (final InetAddress addr : inets) {
                        if (!(addr instanceof Inet4Address) && !(addr instanceof Inet6Address)) {
                            continue;
                        }
                        try {
                            /* Create mDNS responder for address */
                            final JmDNS jmDNS = JmDNS.create(addr, hostName + "-jmdns");
                            jmDNSInstances.add(jmDNS);

                            /* Publish RAOP service */
                            final ServiceInfo airTunesServiceInfo = ServiceInfo.create(AIR_TUNES_SERVICE_TYPE,
//					    		hardwareAddressString + "@" + hostName + " (" + iface.getName() + ")",
                                    hardwareAddressString + "@" + hostName, getRtspPort(), 0 /* weight */, 0 /* priority */,
                                    AIRTUNES_SERVICE_PROPERTIES);
                            jmDNS.registerService(airTunesServiceInfo);
                        } catch (final Throwable e) {
                        }
                    }

                }
            }

        } catch (SocketException e) {
        }
	}
	

    public InetAddress getInetAddress() {
        try {
            Enumeration<NetworkInterface> networks;
            Enumeration<InetAddress> inets;
            NetworkInterface network;
            InetAddress inetAddress;

            for (networks = NetworkInterface.getNetworkInterfaces(); networks.hasMoreElements();) {
                network = networks.nextElement();
                for (inets = network.getInetAddresses(); inets.hasMoreElements();) {
                    inetAddress = inets.nextElement();
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {

                        return inetAddress;
                    }
                }

            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

	//When the app is shutdown
	protected void onShutdown() {

		/* Stop all mDNS responders */
		synchronized(jmDNSInstances) {
			for(final JmDNS jmDNS: jmDNSInstances) {
				try {
					jmDNS.unregisterAllServices();
				}
				catch (final Exception e) {
					
				}
			}
		}

		/* Stop the ExecutorService */
		executorService.shutdown();

	}

	/**
	 * Map factory. Creates a Map from a list of keys and values
	 * 
	 * @param keys_values key1, value1, key2, value2, ...
	 * @return a map mapping key1 to value1, key2 to value2, ...
	 */
	private static Map<String, String> map(final String... keys_values) {
		assert keys_values.length % 2 == 0;
		final Map<String, String> map = new java.util.HashMap<String, String>(keys_values.length / 2);
		for(int i=0; i < keys_values.length; i+=2)
			map.put(keys_values[i], keys_values[i+1]);
		return Collections.unmodifiableMap(map);
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

}
