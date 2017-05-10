package com.sfdc.vlookup;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;

import com.sforce.soap.partner.*;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.*;

public class vlookup {

    PartnerConnection partnerConnection = null;
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    Map<Object,Object> inputMap = new HashMap<Object,Object>();
    String csvFile ;
    String username;
    String password;
    String proxyhost;
    String proxyport;
    String proxyuser;
    String proxypass;
    String authEndPoint;
    
	public static void main(String[] args) {
		vlookup vl = new vlookup();
		System.out.println("This is a lightweight utility to lookup Salesforce Ids based on search field. \nIt is built to query data like Profiles,Roles etc where total row count is less than 50,000");
		System.out.println("If querying an object like Account which has large amount of records,\n please add a filter criteria to reduce data taken from Salesforce for analysis to below 50,000 records");
		vl.getProperties();
		if (vl.login() == true){
			vl.getLookupFields();			
		}
		else {
			System.out.println("login failed. Please check supplied login credentials and proxy details");
		};
		vl.updateOutputData();
		
	}
	
	private String getUserInput(String prompt) {
        String result = "";
        try {
          System.out.print(prompt);
          result = reader.readLine();
        } catch (IOException ioe) {
          ioe.printStackTrace();
        }
        return result;
    }

	private void getProperties(){
		Properties prop = new Properties();
		InputStream input = null;
		String checkProperties = getUserInput("Enter Y if Properties.txt is present with login credentials, else enter N: ");
		if (checkProperties.equals("Y")){
			try {
					input = new FileInputStream("Properties.txt");
					// load a properties file
					prop.load(input);
					// get the property value and print it out
				    username = prop.getProperty("username");
				    password = prop.getProperty("password") + prop.getProperty("securitytoken");
				    authEndPoint = prop.getProperty("authEndPoint");
				    proxyhost = prop.getProperty("proxyhost");
				    proxyport = prop.getProperty("proxyport");
				    proxyuser = prop.getProperty("proxyuser");
				    proxypass = prop.getProperty("proxypass");
				}
				catch (IOException ex) {
						System.out.println("Properties.txt does not exist or does not have required data");
						ex.printStackTrace();
					} finally {
						if (input != null) {
							try {
								input.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
			}
		}
	}
	
	private boolean login() {
        boolean success = false;
        System.out.println(username);
        if (username == null || username.equals(""))
        	username = getUserInput("Enter username: ");
        if (password == null || password.equals(""))
        	password = getUserInput("Enter password: ");
        if (proxyhost == null || proxyhost.equals(""))
        	proxyhost = getUserInput("Enter proxy host address: ");
        if (proxyport == null || proxyport.equals(""))
        	proxyport = getUserInput("Enter proxy host port: ");
        Integer intProxyPort = 80;
        if (proxyport != null && proxyport.length() != 0){
        	intProxyPort = Integer.valueOf(proxyport);        	
        }
        if (proxyuser == null || proxyuser.equals(""))
        	proxyuser = getUserInput("Enter proxy authentication user name: ");
        if (proxypass == null || proxypass.equals(""))
        	proxypass = getUserInput("Enter proxy authentication password: ");
        if (authEndPoint == null || authEndPoint.equals(""))
        	authEndPoint = "https://login.salesforce.com/services/Soap/u/39.0";
        try {
          ConnectorConfig config = new ConnectorConfig();
          config.setUsername(username);
          config.setPassword(password);
          config.setAuthEndpoint(authEndPoint);
          config.setTraceFile("traceLogs.txt");
     //     config.setTraceMessage(true);
     //     config.setPrettyPrintXml(true);
          if (proxyhost != null && (!proxyhost.equals(""))){
              config.setProxy(proxyhost, intProxyPort);
              config.setProxyUsername(proxyuser);
              config.setProxyPassword(proxypass);
          }
		  String filename = getUserInput("Enter name of csv input file: ");
		  filename = filename + ".csv";
		  csvFile = filename;
          partnerConnection = Connector.newConnection(config);
          success = true;
        } catch (ConnectionException ce) {
          success= false;
          ce.printStackTrace();
        } catch (FileNotFoundException fnfe) {
          success = false;
          fnfe.printStackTrace();
        }
        return success;
      }

		public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
		    for (Entry<T, E> entry : map.entrySet()) {
		        if (Objects.equals(value, entry.getValue())) {
		            return entry.getKey();
		        }
		    }
		    return null;
		}	 
		
	  private void getLookupFields(){
		  String ObjectToLookup = getUserInput("Enter object api name to lookup: ");
		  String FieldToSearch = getUserInput("Enter field api name to search with: ");
	      String addFilterCriteria = getUserInput("If this object has lot of data, please enter an additional criteria to add in search soql: (ex: Country__c like '%US%' ) ");
	      
	      // Set query batch size
          partnerConnection.setQueryOptions(2000);
          //check recordcount 
         
          // SOQL query to use
          String soqlQuery = "SELECT Id," + FieldToSearch + " FROM " + ObjectToLookup;
          if (addFilterCriteria != null && !addFilterCriteria.equals("")){
        	  soqlQuery = soqlQuery + " where " + addFilterCriteria;
          }
          soqlQuery = soqlQuery + " limit 100000";
          
         // System.out.println(soqlQuery);
          // Make the query call and get the query results
          QueryResult qr;
          try {
			qr = partnerConnection.query(soqlQuery);
	          boolean done = false;
	          int loopCount = 0;
	          // Loop through the batches of returned results
	          while (!done) {
	                System.out.println("Querying data .." + loopCount++);
	                SObject[] records = qr.getRecords();
	                // Process the query results
	                for (int i = 0; i < records.length; i++) {
	                	SObject sobj = records[i];
	                    inputMap.put(sobj.getField("Id"), sobj.getField(FieldToSearch));
	                }
	                if (qr.isDone()) {
	                      done = true;
	                  } else {
	                      qr = partnerConnection.queryMore(qr.getQueryLocator());
	                  }
	              }
		}
		catch (ConnectionException e1) {
			System.out.println("Object and field details entered are incorrect. Please recheck");
			e1.printStackTrace();
		} 
		
		System.out.println("\nQuery execution completed.");
		try {
			partnerConnection.logout();
		} catch (ConnectionException e1) {
			e1.printStackTrace();
		}
	  }
	  private void updateOutputData(){
	      	String line = "";
	      	Path p = Paths.get(csvFile); 
			List<String> fileContent;
			try {
				
				fileContent = new ArrayList<>(Files.readAllLines(p, StandardCharsets.UTF_8));
				for (int i = 0; i < fileContent.size(); i++) {
				    if ((line = fileContent.get(i)) != null) {
		                String[] inputdata = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		                String inputfieldvalue = inputdata[0].replace("\"","");
		                System.out.println("Input field = " + inputfieldvalue + " " + getKeyByValue(inputMap, inputdata[0].toString().replace("\"","")));
		                if (inputdata[0] != null && inputdata[0] != ""){
		                	if (i == 0){
			                	fileContent.set(i, inputdata[0] + ", Id");		                		
		                	}
		                	else
			                	fileContent.set(i, inputdata[0] + "," + getKeyByValue(inputMap, inputdata[0].toString().replace("\"","")));
		                }

				    }
				}
			Files.write(p, fileContent, StandardCharsets.UTF_8);
			}
			catch(IOException e){
				System.out.println("Unable to open file and update it. Please check if Input file is correctly populated.");
				e.printStackTrace();
			}
	  }
	  
}
