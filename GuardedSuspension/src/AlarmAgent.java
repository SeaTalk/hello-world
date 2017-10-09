import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

public class AlarmAgent {
	// ���ڼ�¼AlarmAgent�Ƿ������ϸ澯������
	private volatile boolean connectedToServer = false;
	
	// ģʽ��ɫ��GuardedSuspension.Predicate
	private final Predicate agentConnected = new Predicate(){

		@Override
		public boolean evaluate() {
			// TODO Auto-generated method stub
			return connectedToServer;
		}
		
	};
	
	// ģʽ��ɫ��GuardedSuspension.Blocker
	private final Blocker blocker = new ConditionVarBlocker();
	
	// ������ʱ��
	private final Timer heartbeatTimer = new Timer(true);
	
	// ʡ����������
	
	/**
	 * ���͸澯��Ϣ
	 * @param alarm �澯��Ϣ
	 * @throws Exception
	 */
	public void sendAlarm(final String alarm) throws Exception{
		// ������Ҫ�ȴ���ֱ��AlarmAgent�����ϸ澯�����������������жϺ��������Ϸ�������
		// ģʽ��ɫ��GuardedSuspension.GuardedAction
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
		// ʡ����������
		
		// ģ�ⷢ�͸澯���������ĺ�ʱ
		try{
			Thread.sleep(50);
		}catch(Exception e){
			
		}
	}
	
	public void init(){
		// ʡ����������
		
		// �澯�����߳�
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
			// ģ�����Ӳ�����ʱ
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
			
			// ֱ����������ʱ���߳���ִ��
			connectingThread.run();
		}
	}
}
