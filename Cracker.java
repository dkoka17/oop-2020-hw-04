import java.security.*;
import java.security.spec.ECField;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Cracker {
	private static MessageDigest message;
	public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789.,-!".toCharArray();
	private static ArrayList<pair> pairs;
	static class pair{
		int start;
		int end;
		public  pair(int start,int end){
			this.start = start;
			this.end = end;
		}
		public int getStart(){
			return this.start;
		}
		public int getEnd(){
			return this.end;
		}

		@Override
		public boolean equals(Object o) {
			pair pair = (pair) o;
			return start == pair.start &&
					end == pair.end;
		}

		@Override
		public int hashCode() {
			return Objects.hash(start, end);
		}
	}

	public static String hexToString(byte[] bytes) {
		StringBuffer buff = new StringBuffer();
		for (int i=0; i<bytes.length; i++) {
			int val = bytes[i];
			val = val & 0xff;  // remove higher bits, sign
			if (val<16) buff.append('0'); // leading 0
			buff.append(Integer.toString(val, 16));
		}
		return buff.toString();
	}
	public static byte[] hexToArray(String hex) {
		byte[] result = new byte[hex.length()/2];
		for (int i=0; i<hex.length(); i+=2) {
			result[i/2] = (byte) Integer.parseInt(hex.substring(i, i+2), 16);
		}
		return result;
	}

	private static class Worker extends Thread {
		private List<String> saver;
		private CountDownLatch signal;
		private byte[] hex;
		private int length;
		private  pair p;

		public Worker(byte[] hex, int length, pair p , CountDownLatch signal){
			this.hex = hex;
			this.length = length;
			this.p = p;
			this.saver = new ArrayList<>();
			this.signal = signal;

		}

		private void generateNew(String str){
			MessageDigest message = null;
			try {
				message = MessageDigest.getInstance("SHA");
			} catch (NoSuchAlgorithmException nae) {
				nae.printStackTrace();
			}
			message.update(str.getBytes());
			if(Arrays.equals(hex, message.digest())){
				saver.add(str);
			}
			if(str.length() == length)return;

			for (int i = 0; i < CHARS.length; i++){
				generateNew(str + CHARS[i]);
			}
		}
		public List<String> getList() {
			return saver;
		}

		public void run() {
			for (int i = p.getStart(); i<=p.getEnd(); i++){
				generateNew("" + CHARS[i]);
			}
			signal.countDown();
		}
	}

	public static ArrayList<pair> generatePairsWithReturn(int numThreads){
		ArrayList<pair> pairsToReturn  = new ArrayList<pair>();
		int singleThreadChars = CHARS.length / numThreads;
		for(int i = 0; i < numThreads; i++){
			int startInd = i * singleThreadChars;
			int endInd = (i + 1) * singleThreadChars - 1;
			if(i >= numThreads - 1) endInd = CHARS.length - 1;
			pair p = new pair(startInd,endInd);
			pairsToReturn.add(p);
		}
		return pairsToReturn;
	}

	public static void connectDigest(String dest){
		try {
			message = MessageDigest.getInstance(dest);
		} catch (Exception ex) {
			throw  new RuntimeException();
		}
	}

	public static void generatePairs(int numThreads){
		int singleThreadChars = CHARS.length / numThreads;
		for(int i = 0; i < numThreads; i++){
			int startInd = i * singleThreadChars;
			int endInd = (i + 1) * singleThreadChars - 1;
			if(i >= numThreads - 1) endInd = CHARS.length - 1;
			pair p = new pair(startInd,endInd);
			pairs.add(p);
			System.out.println(p.toString());
		}
	}
	public static void main(String[] args){
		if(args.length == 1) {
			connectDigest("SHA");
			String Hex = args[0];
			message.update(Hex.getBytes());
			byte[] resulToPrint = message.digest();
			System.out.println(hexToString(resulToPrint));
		} else {
			pairs = new ArrayList<pair>();
			int numThreads = 1;
			numThreads = Integer.parseInt(args[2]);
			String Hex = args[0];
			int length = Integer.parseInt(args[1]);
			CountDownLatch signal = new CountDownLatch(numThreads);
			Worker[] workers = new Worker[numThreads];
			generatePairs(numThreads);
			for(int i = 0; i < numThreads; i++){
				workers[i] = new Worker(hexToArray(Hex),length, pairs.get(i), signal);
				workers[i].start();
			}

			try {
				signal.await();
			} catch (InterruptedException ex) {};

			for(int i = 0; i<numThreads; i++){
				List<String> resToIter = workers[i].getList();
				for(int k = 0; k < resToIter.size(); k++) {
					System.out.println(resToIter.get(k));
				}
			}
			System.out.println("All Done!");
		}
	}

}