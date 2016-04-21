import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Preprocess {
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static final String TOKEN = ",";
	private static final int ID = 0;
	private static final int ACCOUNT_CREATED = 1;
	private static final int FIRST_ACTIVE = 2;
	private static final int FIRST_BOOKING = 3;
	private static final int GENDER = 4;
	private static final int AGE = 5;
	private static final int SIGNUP_METHOD = 6;
	private static final int SIGNUP_FLOW = 7;
	private static final int LANGUAGE = 8;
	private static final int AFFLIATE_CHANNEL = 9;
	private static final int AFFLIATE_PROVIDER = 10;
	private static final int FIRST_AFFLIATE_TRACKED = 11;
	private static final int SIGNUP_APP = 12;
	private static final int FIRST_DEVICE_TYPE = 13;
	private static final int FIRST_BROWSER = 14;
	private static final int COUNTRY_DESTINATION = 15;
	
	public Map<String, Set<String>> attributeMap = new HashMap<String, Set<String>>();
	public Map<String, Integer> destinationMap = new HashMap<String, Integer>();
	public List<String> processedData; 
	
	private double[] condMean = new double[11];
	private double[] count = new double[11];
	
	
	private class Example {
		public String booking2CreateLag;
		public String booking2ActiveLag;
		public String gender;
		public double age;
		public String signupMethod;
		public int signupFlow;
		public String language;
		public String channel;
		public String provider;
		public String tracked;
		public String signupApp;
		public String deviceType;
		public String browser;
		public String destination;
		
		public Example(String[] fields) {
			this.booking2CreateLag = getLag1(fields[ACCOUNT_CREATED], fields[FIRST_BOOKING]);
			this.booking2ActiveLag = getLag2(fields[FIRST_ACTIVE], fields[FIRST_BOOKING]);
			this.gender = fields[GENDER];
			this.age = (fields[AGE].isEmpty()?-1:Integer.parseInt(fields[AGE]));
			this.signupMethod = fields[SIGNUP_METHOD];
			this.signupFlow = Integer.parseInt(fields[SIGNUP_FLOW]);
			this.language = fields[LANGUAGE];
			this.channel = fields[AFFLIATE_CHANNEL];
			this.provider = fields[AFFLIATE_PROVIDER];
			this.tracked = (fields[FIRST_AFFLIATE_TRACKED].isEmpty()?"NA":fields[FIRST_AFFLIATE_TRACKED]);
			this.signupApp = fields[SIGNUP_APP];
			this.deviceType = fields[FIRST_DEVICE_TYPE];
			this.browser = fields[FIRST_BROWSER];
			this.destination = fields[COUNTRY_DESTINATION];
		}
		
		private String getLag1(String s1, String s2) {
			if (s2.isEmpty()) return "NA";
			Date date1 = null;
			Date date2 = null;
			long diff = 0;
			try {
				date1 = dateFormat.parse(s1);
				date2 = dateFormat.parse(s2);
				diff = date2.getTime() - date1.getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			diff /= (24 * 60 * 60 * 1000);
			if (diff == 0) {
				return "A";
			} else if (diff > 0) {
				return "B";
			} else {
				return "C";
			}
		}
		
		private String getLag2(String s1, String s2) {
			if (s2.isEmpty()) return "NA";
			Date date1 = null;
			Date date2 = null;
			long diff = 0;
			try {
				date1 = timeFormat.parse(s1);
				date2 = dateFormat.parse(s2);
				diff = date2.getTime() - date1.getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			diff /= (24 * 60 * 60 * 1000);
			if (diff == 0) {
				return "A";
			} else if (diff > 0) {
				return "B";
			} else {
				return "C";
			}
		}
		
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append(this.booking2CreateLag);
			builder.append(TOKEN);
			builder.append(this.booking2ActiveLag);
			builder.append(TOKEN);
			builder.append(this.gender);
			builder.append(TOKEN);
			builder.append(this.age);
			builder.append(TOKEN);
			builder.append(signupMethod);
			builder.append(TOKEN);
			builder.append(signupFlow);
			builder.append(TOKEN);
			builder.append(this.language);
			builder.append(TOKEN);
			builder.append(this.channel);
			builder.append(TOKEN);
			builder.append(this.provider);
			builder.append(TOKEN);
			builder.append(this.tracked);
			builder.append(TOKEN);
			builder.append(this.signupApp);
			builder.append(TOKEN);
			builder.append(this.deviceType);
			builder.append(TOKEN);
			builder.append(this.browser);
			builder.append(TOKEN);
			builder.append(this.destination);
			return builder.toString();
		}
	}
	
	public Preprocess() {
		this.processedData = new ArrayList<String>();
		this.attributeMap.put("booking2CreateLag", new HashSet<String>());
		this.attributeMap.put("booking2ActiveLag", new HashSet<String>());
		this.attributeMap.put("gender", new HashSet<String>());
		this.attributeMap.put("signupMethod", new HashSet<String>());
		this.attributeMap.put("language", new HashSet<String>());
		this.attributeMap.put("channel", new HashSet<String>());
		this.attributeMap.put("provider", new HashSet<String>());
		this.attributeMap.put("tracked", new HashSet<String>());
		this.attributeMap.put("signupApp", new HashSet<String>());
		this.attributeMap.put("deviceType", new HashSet<String>());
		this.attributeMap.put("browser", new HashSet<String>());
		this.attributeMap.put("destination", new HashSet<String>());
		this.destinationMap.put("AU", 0);
		this.destinationMap.put("CA", 1);
		this.destinationMap.put("DE", 2);
		this.destinationMap.put("ES", 3);
		this.destinationMap.put("FR", 4);
		this.destinationMap.put("GB", 5);
		this.destinationMap.put("IT", 6);
		this.destinationMap.put("NL", 7);
		this.destinationMap.put("PT", 8);
		this.destinationMap.put("US", 9);
		this.destinationMap.put("other", 10);
		
		List<Example> dataList = new ArrayList<Example>();
		this.load(dataList);
		this.process(dataList);
	}
	
	private void load(List<Example> dataList) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("/Users/hengxu/Documents/NEU/Classes/DataMining/DataMiningProject/Data/Users.csv"));
			String line = br.readLine();
			while((line = br.readLine()) != null) {
				String[] fields = line.split(",");
				if (fields[COUNTRY_DESTINATION].equals("NDF")) {
					continue;
				} 
				
				Example example = new Example(fields);
				dataList.add(example);	
				this.attributeMap.get("booking2CreateLag").add(example.booking2CreateLag);
				this.attributeMap.get("booking2ActiveLag").add(example.booking2ActiveLag);
				this.attributeMap.get("gender").add(example.gender);
				this.attributeMap.get("signupMethod").add(example.signupMethod);
				this.attributeMap.get("language").add(example.language);
				this.attributeMap.get("channel").add(example.channel);
				this.attributeMap.get("provider").add(example.provider);
				this.attributeMap.get("tracked").add(example.tracked);
				this.attributeMap.get("signupApp").add(example.signupApp);
				this.attributeMap.get("deviceType").add(example.deviceType);
				this.attributeMap.get("browser").add(example.browser);
				this.attributeMap.get("destination").add(example.destination);
				if (example.age >= 0) {
					int labelIndex = this.destinationMap.get(example.destination);
					this.condMean[labelIndex] += example.age;
					this.count[labelIndex] += 1;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				
			}
		}
	}
	
	private void process(List<Example> dataList) {
		int size = this.condMean.length;
		for (int i = 0; i < size; i++) {
			this.condMean[i] /= this.count[i];
		}
		
		for (Example example: dataList) {
			if (example.age == -1) {
				int index = this.destinationMap.get(example.destination);
				example.age = this.condMean[index];
			}
			this.processedData.add(example.toString());
		}
	}
	
	
	public static void main(String[] args) {
		Preprocess p = new Preprocess();
		System.out.println("Total intances: " + p.processedData.size());
		for (String key: p.attributeMap.keySet()) {
			System.out.println(p.attributeMap.get(key));
		}
		
		for (String example: p.processedData) {
			System.out.println(example.toString());
		}
	}
}
