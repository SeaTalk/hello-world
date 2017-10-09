import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

public class AlarmAgent {
	// 用于记录AlarmAgent是否连接上告警服务器
	private volatile boolean connectedToServer = false;
	
	// 模式角色：GuardedSuspension.Predicate
	private final Predicate agentConnected = new Predicate(){

		@Override
		public boolean evaluate() {
			// TODO Auto-generated method stub
			return connectedToServer;
		}
		
	};
	
	// 模式角色：GuardedSuspension.Blocker
	private final Blocker blocker = new ConditionVarBlocker();
	
	// 心跳定时器
	private final Timer heartbeatTimer = new Timer(true);
	
	// 省略其他代码
	
	/**
	 * 发送告警信息
	 * @param alarm 告警信息
	 * @throws Exception
	 */
	public void sendAlarm(final String alarm) throws Exception{
		// 可能需要等待，直到AlarmAgent连接上告警服务器（或者连接中断后重新连上服务器）
		// 模式角色：GuardedSuspension.GuardedAction
		GuardedAction<Void> guardedAction = new GuardedAction<Void>(agentConnected){

			@Override
			public Void call() throws Exception {
				doSendAlarm(alarm);
				return null;
			}
			
		};
		blocker.callWithGuard(guardedAction);
	}
	
	private void doSendAlarm(String alarm){
		// 省略其他代码
		
		// 模拟发送告警至服务器的耗时
		try{
			Thread.sleep(50);
		}catch(Exception e){
			
		}
	}
	
	public void init(){
		// 省略其他代码
		
		// 告警连接线程
		Thread connectingThread = new Thread(new ConnectingTask());
		connectingThread.start();
		
		heartbeatTimer.schedule(new HeartbeatTask(), 60000, 2000);
		
	}
	
	public void disconnect(){
		// ......
		connectedToServer = false;
	}
	
	protected void onConnected(){
		try{
			blocker.signalAfter(new Callable<Boolean>(){

				@Override
				public Boolean call() throws Exception {
					connectedToServer = true;
					return Boolean.TRUE;
				}
				
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void onDisconnected(){
		connectedToServer = false;
	}
	
	private class ConnectingTask implements Runnable{

		@Override
		public void run() {
			// 模拟连接操作耗时
			try{
				Thread.sleep(100);
			}catch(InterruptedException e){
				;
			}
			onConnected();
		}
		
	}
	
	private class HeartbeatTask extends TimerTask{

		@Override
		public void run() {
			if (!testConnection()){
				onDisconnected();
				reconnect();
			}
			
		}
		
		private boolean testConnection(){
			// ......
			
			return true;
		}
		
		private void reconnect(){
			ConnectingTask connectingThread = new ConnectingTask();
			
			// 直接在心跳定时器线程中执行
			connectingThread.run();
		}
	}
}
