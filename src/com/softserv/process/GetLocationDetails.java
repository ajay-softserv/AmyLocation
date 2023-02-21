package com.softserv.process;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;

// created by Nagesh Modi ,  date :  20/05/2017  , Company : SoftServ

@WebServlet("/GetLocationDetails")
public class GetLocationDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public GetLocationDetails() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static String getLatLongPositions(String address) throws Exception {
		String Result = null;
		int responseCode = 0;
		String api = "https://maps.googleapis.com/maps/api/geocode/xml?address=" + URLEncoder.encode(address, "UTF-8")
				+ "&sensor=false&key=AIzaSyDuRGpeiv6-7fPJ6fZQVDJGcE5j7Pd9kgs";
		System.out.println("URL : " + api);
		URL url = new URL(api);
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
		httpConnection.connect();
		responseCode = httpConnection.getResponseCode();
		if (responseCode == 200) {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			;
			Document document = builder.parse(httpConnection.getInputStream());
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("/GeocodeResponse/status");
			String status = (String) expr.evaluate(document, XPathConstants.STRING);
			if (status.equals("OK")) {
				expr = xpath.compile("//geometry/location/lat");
				String latitude = (String) expr.evaluate(document, XPathConstants.STRING);
				expr = xpath.compile("//geometry/location/lng");
				String longitude = (String) expr.evaluate(document, XPathConstants.STRING);
				Result = latitude + "," + longitude;
				System.out.println(Result);
				return Result;
			} else {
				System.out.println(status);
				if (status.equals("OVER_QUERY_LIMIT"))
					return status;
				else if (status.equals("ZERO_RESULTS"))
					return status;
				// throw new Exception("Error from the API - response status: "+status);
			}
		}
		return null;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Post");
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out = response.getWriter();
		String jsonData = "", starttime = null;
		Statement stmt = null, stmt1 = null;
		ResultSet rs = null, rs1 = null;
		Connection con = null;
		JSONArray startTime = new JSONArray();
		JSONArray firstName = new JSONArray();
		JSONArray lastName = new JSONArray();
		JSONArray fullAddress = new JSONArray();
		JSONArray Lat = new JSONArray();
		JSONArray Long = new JSONArray();
		JSONObject Result = new JSONObject();

		try {
			// get values from javascript Location.js using Ajax and decode that
			String DBName = StringUtils.newStringUtf8(Base64.decodeBase64(request.getParameter("DBName")));
			String BSVName = StringUtils.newStringUtf8(Base64.decodeBase64(request.getParameter("BSVName")));
			String CustomerID = StringUtils.newStringUtf8(Base64.decodeBase64(request.getParameter("CustomerID")));
			System.out.println("DB Values and customer ID");
			Class.forName("com.mysql.jdbc.Driver");// Driver will be loaded after this step
			// creating connection with database
			DBConnection dbcn = new DBConnection();
			con = (Connection) dbcn.getConnection(DBName);
			// con=(Connection)
			// DriverManager.getConnection("jdbc:mysql://"+HostName+"/"+DBName,DBUserName,DBPassword);
			stmt = (Statement) con.createStatement();
			System.out.println("Connection created");
			// query to fetch data from database
			if (DBName.equalsIgnoreCase("webservice")) {
				System.out.println("Inside webservice");
				rs = (ResultSet) stmt
						.executeQuery("select L.FirstName,L.LastName,L.FullAddress,A.StartTime,RU.TimeZone from "
								+ BSVName + "_LEAD AS L INNER JOIN  " + BSVName
								+ "_APPOINTMENT as A on A.ps_Account_RID = L.ID and A.Type='Appointment' And A.Done <> 1 INNER JOIN "
								+ BSVName + "_SALESREP as SR ON SR.ID = A.ps_SalesRep_RID INNER JOIN " + BSVName
								+ "_REGULARUSER as RU on RU.Filter_ApptRep = SR.Name where A.Canceled=0 AND A.Deleted=0 AND RU.id=" + CustomerID
								+ " and A.StartDate BETWEEN IFNULL(RU.Calendar_Date_From,'') AND IFNULL(RU.Calendar_Date_To, CURRENT_DATE()) ORDER BY A.StartTime");
			} else if (DBName.equalsIgnoreCase("webservice_test")) {
				System.out.println("Inside webservice");
				rs = (ResultSet) stmt.executeQuery(
						"select L.FirstName,L.LastName,L.FullAddress,A.StartTime,RU.TimeZone from BASTESTDOMAIN"
								+ BSVName + "_LEAD AS L INNER JOIN  BASTESTDOMAIN" + BSVName
								+ "_APPOINTMENT as A on A.ps_Account_RID = L.ID and A.Type='Appointment' And A.Done <> 1 INNER JOIN BASTESTDOMAIN"
								+ BSVName + "_SALESREP as SR ON SR.ID = A.ps_SalesRep_RID INNER JOIN BASTESTDOMAIN"
								+ BSVName + "_REGULARUSER as RU on RU.Filter_ApptRep = SR.Name where A.Canceled=0 AND A.Deleted=0 AND RU.id="
								+ CustomerID
								+ " and A.StartDate BETWEEN IFNULL(RU.Calendar_Date_From,'') AND IFNULL(RU.Calendar_Date_To, CURRENT_DATE()) ORDER BY A.StartTime");
			} else {
				System.out.println("Inside else");
				// rs=(ResultSet) stmt.executeQuery("select
				// L.FirstName,L.LastName,L.FullAddress,A.StartTime,RU.TimeZone from Lead AS L
				// INNER JOIN APPOINTMENT as A on A.ps_Account_RID = L.ID and
				// A.Type='Appointment' And A.Done <> 1 INNER JOIN SALESREP as SR ON SR.ID =
				// A.ps_SalesRep_RID INNER JOIN REGULARUSER as RU on RU.Filter_ApptRep = SR.Name
				// where RU.id="+CustomerID+" and A.StartDate BETWEEN
				// IFNULL(RU.Calendar_Date_From,'') AND IFNULL(RU.Calendar_Date_To,
				// CURRENT_DATE()) ORDER BY A.StartTime");
				rs = (ResultSet) stmt.executeQuery(
						"SELECT L.FirstName,L.LastName,L.FullAddress,A.StartTime,RU.TimeZone, SR.Name, SR.ID FROM LEAD AS L INNER JOIN APPOINTMENT AS A ON A.ps_Account_RID = L.ID AND (A.Type='Estimate' OR A.Type='Sales 2nd Visit') AND A.Done <> 1 INNER JOIN SALESREP AS SR ON SR.ID = A.ps_SalesRep_RID \r\n"
								+ "INNER JOIN REGULARUSER AS RU WHERE A.Canceled=0 AND A.Deleted=0 AND RU.id=" + CustomerID
								+ " AND A.StartDate BETWEEN IFNULL(RU.RepAppt_CalendarDateFrom,'') AND IFNULL(RU.RepAppt_CalendarDateTo, CURRENT_DATE()) ORDER BY A.StartTime");
			}
			System.out.println("Query Run success.!");
			
			JSONObject reps = new JSONObject();
			while (rs.next()) {
				JSONArray startTime1 = new JSONArray();
				JSONArray firstName1 = new JSONArray();
				JSONArray lastName1 = new JSONArray();
				JSONArray fullAddress1 = new JSONArray();
				JSONArray Lat1 = new JSONArray();
				JSONArray Long1 = new JSONArray();
				JSONObject Result1 = new JSONObject();
				
				System.out.println("Inside While");
				System.out.println("SlsPerson Name: "+rs.getString(6));
				System.out.println("SlsPerson ID: "+rs.getString(7));
				firstName1.put(rs.getString(1));
				lastName1.put(rs.getString(2));
				fullAddress1.put(rs.getString(3));
				String date_s = rs.getString(4);
				System.out.println(date_s + "date_map");
				String time_zone = rs.getString(5);
				System.out.println(time_zone + "timezone_map");
				if (time_zone != null) {
					System.out.println("Map IN IF condition");
					SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = null;
					try {
						date = dt.parse(date_s);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
					startTime1.put(estToIst(formatter.format(date), time_zone));
				} else {
					System.out.println("Map IN Else condition");
					SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = null;
					try {
						date = dt.parse(date_s);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
					startTime1.put(estToIst(formatter.format(date), "US/Eastern"));
				}
				
				// put data into json object Result
				Result1.put("startTime", startTime1);
				Result1.put("firstName", firstName1);
				Result1.put("lastName", lastName1);
				Result1.put("fullAddress", fullAddress1);
				Result1.put("Lat", Lat1);
				Result1.put("Long", Long1);
				
				//Get Lat Long from fullAddress
				getLatLong(rs.getString(3), Lat1, Long1);
				
				if(!reps.has(rs.getString(7))) {
					reps.put(rs.getString(7),Result1);
				} else {
					System.out.println("Inside getValue");
					System.out.println(reps.getJSONObject(rs.getString(7)).get("firstName"));	
					reps.getJSONObject(rs.getString(7)).append("startTime", startTime1);
					reps.getJSONObject(rs.getString(7)).append("firstName", firstName1);
					reps.getJSONObject(rs.getString(7)).append("lastName", lastName1);
					reps.getJSONObject(rs.getString(7)).append("fullAddress", fullAddress1);
					reps.getJSONObject(rs.getString(7)).append("Lat", Lat1);
					reps.getJSONObject(rs.getString(7)).append("Long", Long1);
				}
			}
/*
			for (int n = 0; n < fullAddress.length(); n++) {
				String result = null;
				result = getLatLongPositions(fullAddress.get(n).toString());
				if (result != null) {
					if (result.equals("ZERO_RESULTS")) {
						Lat.put("");
						Long.put("");
					} else if (result.equals("OVER_QUERY_LIMIT")) {
						n--;
					} else {
						Lat.put(result.split(",")[0]);
						Long.put(result.split(",")[1]);
					}
				}
			} 
*/
			// put data into json object Result
			/*
			Result.put("startTime", startTime);
			Result.put("firstName", firstName);
			Result.put("lastName", lastName);
			Result.put("fullAddress", fullAddress);
			Result.put("Lat", Lat);
			Result.put("Long", Long);
			*/
			Result.put("Reps", reps);
			

			out.print(reps);
			System.out.println("Return Result");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
			;
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}
			;
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
			}
			;
			out.close();
		}
	}
	
	public static void getLatLong(String address, JSONArray Lat, JSONArray Long) {
		String result;
		try {
			result = getLatLongPositions(address);
			if (result != null) {
				if (result.equals("ZERO_RESULTS")) {
					Lat.put("");
					Long.put("");
				} else if (result.equals("OVER_QUERY_LIMIT")) {
					getLatLong(address, Lat, Long);
				} else {
					Lat.put(result.split(",")[0]);
					Long.put(result.split(",")[1]);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static String estToIst(String dateInput, String timezone) throws ParseException {
		Calendar now = Calendar.getInstance();
		return changeTimeZone(dateInput, now.getTimeZone(), TimeZone.getTimeZone(timezone));
	}

	private static String changeTimeZone(String dateInput, TimeZone sourceTimeZone, TimeZone targetTimeZone)
			throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		formatter.setTimeZone(sourceTimeZone);
		Date date = formatter.parse(dateInput);
		formatter.setTimeZone(targetTimeZone);
		return formatter.format(date);
	}
}