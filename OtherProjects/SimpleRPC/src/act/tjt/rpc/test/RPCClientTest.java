package act.tjt.rpc.test;
import act.tjt.rpc.client.RPCClient;

public class RPCClientTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
		IHellowWorld helloWorld = RPCClient.findService("127.0.0.1", 8080, IHellowWorld.class);
		String result = helloWorld.sayHello("TJT");
		System.out.println(result);
		
		
	}

}
