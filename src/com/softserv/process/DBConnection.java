package com.softserv.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

public class DBConnection {
		HashMap details = new HashMap();
	public HashMap getValues(){
		HashMap hm = new HashMap();
		try {
			String path=System.getProperty("catalina.home").replace("\\", "/").replace("/Tomcat", "")+"/bin";
			File file = new File(path+"/BASServer.props");
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();
			Enumeration enuKeys = properties.keys();
			while (enuKeys.hasMoreElements()) {
				String key = (String) enuKeys.nextElement();
				String value = properties.getProperty(key);
				hm.put(key, value);
				
			}
			String fullURL=(String) hm.get("BootstrapURL");
			if(((String) hm.get("DriverClassName")).equals("com.mysql.jdbc.Driver")){
				details.put("driver", (String) hm.get("DriverClassName"));
				details.put("url", (String)fullURL.split("\\?")[0]);
				details.put("uid",(String) fullURL.split("\\?")[1].split("=")[1].split("&")[0]);
				details.put("password", (String)fullURL.split("\\?")[1].split("=")[2]);

			}
			else if(((String) hm.get("DriverClassName")).equals("com.microsoft.sqlserver.jdbc.SQLServerDriver")){
				details.put("driver", (String) hm.get("DriverClassName"));
				details.put("url", (String)fullURL.split(";")[0]+";databaseName=");
				details.put("uid", (String)fullURL.split(";")[1].split("=")[1]);
				details.put("password", (String)fullURL.split(";")[2].split("=")[1]);

			}else{
				System.out.println("Database not identified or error in reading Basserver.props file");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return details;}
	
	
	
	public Connection getConnection(String databasename) throws Exception{
		Connection con=null;
		DBConnection dbc=new DBConnection(); 
		HashMap temp=dbc.getValues();
		Class.forName((String) temp.get("driver"));
		String url=(String)temp.get("url")+databasename;
		String uid=(String)temp.get("uid");
		String pass=(String)temp.get("password");
		//System.out.println(url+"-"+uid+"-"+pass);
		con=DriverManager.getConnection(url,uid,pass);
		return con;
		
		
	}	
}
