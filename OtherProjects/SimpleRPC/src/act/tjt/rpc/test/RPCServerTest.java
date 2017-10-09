package act.tjt.rpc.test;
import act.tjt.rpc.server.RPCServer;

public class RPCServerTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		RPCServer server = new RPCServer();
		server.registService(new HelloWorld());
		try {
			server.startServer(8080);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
