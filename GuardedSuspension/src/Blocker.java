import java.util.concurrent.Callable;

public interface Blocker {
	
	/**
	 * �ڱ�����������ʱִ�ж���������������ǰ�̣߳�ֱ����������������
	 * @param guardedAction ������������Ŀ������
	 * @return
	 * @throws Exception
	 */
	<V> V callWithGuard(GuardedAction<V> guardedAction) throws Exception;
	
	/**
	 * ִ��stateOperation��ָ���Ĳ����󣬾����Ƿ��ѱ�Blocker
	 * ���ݹҵ������߳��е�һ���̡߳�
	 * 
	 * @param stateOperation ����״̬�Ĳ�������call�����ķ���ֵΪtrueʱ���÷����Żỽ�ѱ��ݹҵ��߳�
	 * @throws Exception
	 */
	void signalAfter(Callable<Boolean> stateOperation) throws Exception;
	
	void signal() throws InterruptedException;
	
	/**
	 * ִ��stateOperation��ָ���Ĳ����󣬾����Ƿ��ѱ�Blocker
	 * ���ݹҵ������̡߳�
	 * 
	 * @param stateOperation Ҫ��״̬�Ĳ�������call�����ķ���ֵΪtrueʱ���÷����Żỽ�ѱ��ݹҵ��߳�
	 * @throws Exception
	 */
	void broadcastAfter(Callable<Boolean> stateOperation) throws Exception;
}