package act.tjt.rpc.test;

public class HelloWorld implements IHellowWorld {

	@Override
	public String sayHello(String name) {
		// TODO Auto-generated method stub
		return "hello " + name + "!";
	}

}
