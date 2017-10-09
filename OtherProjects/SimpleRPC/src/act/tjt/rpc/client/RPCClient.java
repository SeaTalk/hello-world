package act.tjt.rpc.client;

import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RPCClient {

	/**
	 * ���ݽӿ����͵õ�����Ľӿ�ʵ��
	 * host: RPC������IP
	 * port: RPC����˿�
	 * serviceInterface: �ӿ�����
	 * return ������Ľӿ�ʵ��
	 */
	@SuppressWarnings("unchecked")
	public static <T> T findService(final String host, final int port, final Class<T> serviceInterface){
		ClassLoader cl = serviceInterface.getClassLoader();
		Class[] paratype = new Class[]{serviceInterface};
		return (T) Proxy.newProxyInstance(cl, paratype, new InvocationHandler(){

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				// TODO Auto-generated method stub
				
				EventLoopGroup group = new NioEventLoopGroup();
				try{
					Bootstrap b = new Bootstrap();
					b.group(group).channel(NioSocketChannel.class)
						.option(ChannelOption.TCP_NODELAY, true)
						.handler(new ChannelInitializer<SocketChannel>(){

							@Override
							protected void initChannel(SocketChannel arg0) throws Exception {
								// TODO Auto-generated method stub
								arg0.pipeline().addLast(new ClientHandler());
							}
							
						});
				}finally{}
				return null;
				
				/*Socket socket = null;
				InputStream is = null;
				OutputStream os = null;
				ObjectInput oi = null;
				ObjectOutput oo = null;
				try{
					socket = new Socket(host,port);
					os = socket.getOutputStream();
					oo = new ObjectOutputStream(os);
					oo.writeUTF(serviceInterface.getName());
					oo.writeUTF(method.getName());
					oo.writeObject(method.getParameterTypes());
					oo.writeObject(args);
					
					is = socket.getInputStream();
					oi = new ObjectInputStream(is);
					return oi.readObject();
				}catch(Exception e){
					System.out.println("���÷����쳣...");
					return null;
				}finally{
					if(is != null){  
                        is.close() ;  
                    }  
                    if(os != null){  
                        is.close() ;  
                    }  
                    if(oi != null){  
                        is.close() ;  
                    }  
                    if(oo != null){  
                        is.close() ;  
                    }  
                    if(socket != null){  
                        is.close() ;  
                    }
				}*/
			}
			
		});
		
	}
}
