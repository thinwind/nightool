package win.shangyh.pros.concurent.queue;

import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
 
/**
 * From https://blog.csdn.net/hxpjava1/article/details/44245593
 * 
 * Modified by shangyh
 */
public class SecretWorkFetch {
	private static class Work implements Runnable{
		// private static Object object=new Object();
		private static AtomicInteger count=new AtomicInteger(0);
		private final int id;
		private long putThread; 
		public Work(){
			id=count.incrementAndGet();
		}
		@Override
		public void run() {
			long threadId=Thread.currentThread().getId();
			if(threadId!=putThread){
				System.out.println("===================================================");
			}
			System.out.println(threadId+":"+putThread+"// finish job "+id);
		}
		public long getPutThread() {
			return putThread;
		}
		public void setPutThread(long putThread) {
			this.putThread = putThread;
		}
	}
	public static Work generateWork(){
		return new Work();
	}
	private static class ConsumerAndProducer implements Runnable{
		private Random random=new Random();
		private final LinkedBlockingDeque<Work> deque;
		private final LinkedBlockingDeque<Work> otherWork;
		public ConsumerAndProducer(LinkedBlockingDeque<Work> deque,LinkedBlockingDeque<Work> otherWork){
			this.deque=deque;
			this.otherWork=otherWork;
		}
		@Override
		public void run() {
			while(!Thread.interrupted()){
				try {
					Thread.sleep(200);
					if(random.nextBoolean()){
						int count=random.nextInt(5);
						for(int i=0;i<count;i++){
							Work w=generateWork();
							w.setPutThread(Thread.currentThread().getId());
							deque.putLast(w);
						}
					}
					if(deque.isEmpty()){
						if(!otherWork.isEmpty()){
							otherWork.takeLast().run();;
						}
					}else{
						deque.takeFirst().run();
					}
				} catch (InterruptedException e) {
					
				}
			}
		}
	}
	
	
	public static void main(String[] args) {
		LinkedBlockingDeque<Work> deque=new LinkedBlockingDeque<Work>();
		LinkedBlockingDeque<Work> other=new LinkedBlockingDeque<Work>();
		new Thread(new ConsumerAndProducer(deque,other)).start();
		new Thread(new ConsumerAndProducer(deque,other)).start();
		
		new Thread(new ConsumerAndProducer(other,deque)).start();
		new Thread(new ConsumerAndProducer(other,deque)).start();
	}
 
}