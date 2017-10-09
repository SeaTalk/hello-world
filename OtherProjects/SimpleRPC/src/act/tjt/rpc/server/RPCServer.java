package act.tjt.rpc.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class RPCServer {

	private static final ExecutorService taskPool = Executors.newFixedThreadPool(50);
	
	/**
	 * 服务接口对象库
	 * key:接口名 
	 * value:接口实现
	 */
	private static final ConcurrentHashMap<String,Object> serviceTargets = 
			new ConcurrentHashMap<String,Object>();
	private static AtomicBoolean run = new AtomicBoolean(false);
	
	
	/**
	 * 注册服务
	 * 
	 */
	public void registService(Object service){
		Class<?>[] interfaces = service.getClass().getInterfaces();
		if(interfaces == null){
			throw new IllegalArgumentException("服务对象必须实现接口");
		}
		Class<?> interfacez = interfaces[0];
		String interfaceName = interfacez.getName();
		serviceTargets.put(interfaceName, service);
	}
	
	/**
	 * 启动Server
	 * @throws Exception 
	 */
	public void startServer(int port) throws Exception{
		EventLoopGroup boosGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try{
			ServerBootstrap b = new ServerBootstrap();
			b.group(boosGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 1024)
				.childHandler(new TaskHandler());
			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		}finally{
			boosGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	/*public void startServer(final int port){
		Runnable lifeThread = new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ServerSocket lifeSocket = null;
				Socket client = null;
				ServiceTask serviceTask = null;
				try{
					lifeSocket = new ServerSocket(port);
					run.set(true);
					while(run.get()){
						client = lifeSocket.accept();
						serviceTask = new ServiceTask(client);
						serviceTask.accept();
					}
				}catch(IOException e){
					e.printStackTrace();
				}
			}
			
		};
		taskPool.execute(lifeThread);
		System.out.println("服务启动成功...");
	}*/
	
	public void stopServer(){
		run.set(false);
		taskPool.shutdown();
	}
	
	private class TaskHandler extends ChannelInitializer<SocketChannel>{

		@Override
		protected void initChannel(SocketChannel arg0) throws Exception {
			// TODO Auto-generated method stub
			arg0.pipeline().addLast(new ServerHandler());
		}
		
	}
	
	
	public static final class ServiceTask implements Runnable{

		private Socket client;
		
		public ServiceTask(Socket client){
			this.client = client;
		}
		
		public void accept(){
			taskPool.execute(this);
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			InputStream is = null;
			ObjectInput oi = null;
			OutputStream os = null;
			ObjectOutput oo = null;
			try{
				is = client.getInputStream();
				os = client.getOutputStream();
				oi = new ObjectInputStream(is);
				String serviceName = oi.readUTF();
				String methodName = oi.readUTF();
				Class<?>[] paramTypes = (Class[]) oi.readObject();
				Object[] arguments = (Object[]) oi.readObject();
				System.out.println("serviceName:"+serviceName+" methodName:"+methodName);
				Object targetService = serviceTargets.get(serviceName);
				if(targetService ==null){
					throw new ClassNotFoundException(serviceName + "服务未找到!");
				}
				
				Method targetMethod = targetService.getClass().getMethod(methodName, paramTypes);
				Object result = targetMethod.invoke(targetService, arguments);
				
				oo = new ObjectOutputStream(os);
				oo.writeObject(result);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					if(oo!=null){
						oo.close();
					}
					if(os != null){
						os.close();
					}
					if(is != null){  
                        is.close() ;  
                    }  
                    if(oi != null){  
                        oi.close() ;  
                    }  
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
	}
}
