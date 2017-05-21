package com.sfdc.vlookup;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

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
    String securitytoken;
    String proxyhost;
    String proxyport;
    String proxyuser;
    String proxypass;
    String authEndPoint;
    String filename;
    String csvColumnToSearch;
    String ObjectToLookup;
    String FieldToSearch;
    String SecondFieldToSearch;    
    String addFilterCriteria;
    String traceDebug;
    String casesensitive = "Y";
    JLabel StatusLabel = new JLabel("Please fill detail as necessary and click the button. Required fields are highlighted in Red");

    //we start here
	public static void main(String[] args) {
		vlookup vl = new vlookup();
		vl.getProperties();
		vl.createUI(vl);
	}

	private void executeLogic(vlookup vl){
		if (vl.login() == true){
			vl.getLookupFields();			
			vl.updateOutputData();
		}
		else {
			System.out.println("login failed. Please check supplied login credentials and proxy details");
		};
	}
	private void createUI(vlookup vl){
		JFrame guiFrame = new JFrame();
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guiFrame.setTitle("Vlookup Salesforce");
        JLabel msglabel1 = new JLabel("This is a lightweight utility to lookup Salesforce Ids based on a search field.", JLabel.CENTER);
        JLabel msglabel2 = new JLabel("It is built to query data like Profiles,Roles etc where total row count is less than 50,000.", JLabel.CENTER);
        JLabel msglabel3 = new JLabel(" If querying an object like Account which has large amount of records, please add a filter criteria to reduce data taken from Salesforce.", JLabel.CENTER);
        JLabel msglabel4 = new JLabel("Please keep number of records queried below 100000.");
        JLabel msglabel5 = new JLabel("Some details below are populated by reading the Properties.txt in your folder.");
        msglabel5.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        JLabel statuslabel1 = new JLabel("Progress:");
        StatusLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JLabel UserNameLabel = new JLabel("Enter salesforce org user name: ");
        JTextField UserNameText = new JTextField(username, 20);
        UserNameLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
        JLabel UserPwdLabel = new JLabel("Enter salesforce org user password: ");
        JPasswordField UserPwdText = new JPasswordField(password, 20);
        UserPwdLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
        JLabel UserSecTokLabel = new JLabel("Enter salesforce org user security token: ");
        JTextField UserSecTokText = new JTextField(securitytoken, 20);
        UserSecTokLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
        JLabel SandboxLabel = new JLabel("Enter Y if this is Sandbox, else enter N: ");
        JTextField SandboxText = new JTextField("N", 1);
        JLabel ProxyhostLabel = new JLabel("Enter proxy host (leave blank if no proxy): ");
        JTextField ProxyhostText = new JTextField(proxyhost, 10);
        ProxyhostLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
        JLabel ProxyPortLabel = new JLabel("Enter proxy port: ");
        JTextField ProxyPortText = new JTextField(proxyport, 2);
        JLabel ProxyUserLabel = new JLabel("Enter proxy user name: ");
        JTextField ProxyUserText = new JTextField(proxyuser, 10);
        JLabel ProxyPwdLabel = new JLabel("Enter proxy user password: ");
        JPasswordField ProxyPwdText = new JPasswordField(proxypass, 10);
        JLabel fileLabel = new JLabel("Enter name of input csv file in this directory:");
        JTextField fileText = new JTextField("",10);
        fileLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
        JLabel ButtonLabel = new JLabel("Click this button after you filled any necessary information");
        JTextField csvColumnText = new JTextField("",10);
        JLabel csvColumnLabel = new JLabel("Enter column name that needs to be used for search (Ex: A or M):");
        csvColumnLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
        JTextField objectText = new JTextField("",10);
        JLabel objectLabel = new JLabel("Enter the object api name you want to search (Case sensitive):");
        objectLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
        JTextField Field1Text = new JTextField("",10);
        JLabel Field1Label = new JLabel("Enter the field api name you want to search with (Case sensitive):");
        JTextField Field2Text = new JTextField("",10);
        Field1Label.setBorder(BorderFactory.createLineBorder(Color.RED));
        JLabel Field2Label = new JLabel("If you want to search with two fields, enter 2nd field api name (Case sensitive):");
        JTextField filterText = new JTextField("",10);
        JLabel filterLabel = new JLabel("Enter a filter criteria if you are querying huge amount of data (for ex: shippingcountrycode = 'US'):");
        JTextField caseText = new JTextField("Y",1);
        JLabel caseLabel = new JLabel("Should matching be case insenstive? (If not, enter N)");
      
        JButton button = new JButton("Click Me");
        JPanel panel1 = new JPanel();
        GridLayout layout1 = new GridLayout(0,2);
        panel1.setLayout(layout1);
        layout1.setHgap(5);
        layout1.setVgap(5);
        panel1.add(UserNameLabel);
        panel1.add(UserNameText);
        panel1.add(UserPwdLabel);
        panel1.add(UserPwdText);
        panel1.add(UserSecTokLabel);
        panel1.add(UserSecTokText);
        panel1.add(SandboxLabel);
        panel1.add(SandboxText);
        panel1.add(ProxyhostLabel);
        panel1.add(ProxyhostText);
        panel1.add(ProxyPortLabel);
        panel1.add(ProxyPortText);
        panel1.add(ProxyUserLabel);
        panel1.add(ProxyUserText);
        panel1.add(ProxyPwdLabel);
        panel1.add(ProxyPwdText);
        panel1.add(fileLabel);
        panel1.add(fileText);
        panel1.add(csvColumnLabel);
        panel1.add(csvColumnText);
        panel1.add(objectLabel);
        panel1.add(objectText);
        panel1.add(Field1Label);
        panel1.add(Field1Text);
        panel1.add(Field2Label);
        panel1.add(Field2Text);
        panel1.add(filterLabel);
        panel1.add(filterText);
        panel1.add(caseLabel);
        panel1.add(caseText);        
        panel1.add(ButtonLabel);
        panel1.add(button);
        panel1.add(statuslabel1);
        panel1.add(StatusLabel);
        guiFrame.setLayout(new FlowLayout());
        guiFrame.add(msglabel1);
        guiFrame.add(msglabel2);
        guiFrame.add(msglabel3);
        guiFrame.add(msglabel4);
        guiFrame.add(msglabel5);
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        guiFrame.add(controlPanel);
        controlPanel.add(panel1);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
			    username = UserNameText.getText();
			    char[] passwordarray = UserPwdText.getPassword();
			    password = Arrays.toString(passwordarray);
			    password = password.substring(1, password.length() - 1).replace(",", "").replace(" ", "");
			    securitytoken = UserSecTokText.getText();
			    if (SandboxText.getText().equals("Y")){
			    	 authEndPoint = "https://test.salesforce.com/services/Soap/u/39.0";
			    }
			    else if (!SandboxText.getText().equals("")){
			    	authEndPoint = "https://login.salesforce.com/services/Soap/u/39.0";
			    }
			    StatusLabel.setText("Working!");
			    proxyhost = ProxyhostText.getText();
			    proxyport = ProxyPortText.getText();
			    proxyuser = ProxyUserText.getText();
			    char[] proxypassarray = ProxyPwdText.getPassword();
			    proxypass = Arrays.toString(proxypassarray);
			    proxypass = proxypass.substring(1, proxypass.length() - 1).replace(",", "").replace(" ", "");
			    filename = fileText.getText();
			    csvColumnToSearch = csvColumnText.getText();
			    ObjectToLookup = objectText.getText();
			    FieldToSearch = Field1Text.getText();
			    SecondFieldToSearch = Field2Text.getText();
			    addFilterCriteria = filterText.getText();
			    if (caseText.getText().equals("N")){
			    	casesensitive = "N";
			    }
			    else
			    	casesensitive = "Y";
			    vl.executeLogic(vl);
            }          
         });
        guiFrame.setVisible(true);
        guiFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        return;
	}
	
	private void getProperties(){
		Properties prop = new Properties();
		InputStream input = null;
			try {
					input = new FileInputStream("Properties.txt");
					// load a properties file
					prop.load(input);
					// get the property value and print it out
				    username = prop.getProperty("username");
				    password = prop.getProperty("password");
				    securitytoken = prop.getProperty("securitytoken");
				    authEndPoint = prop.getProperty("authEndPoint");
				    proxyhost = prop.getProperty("proxyhost");
				    proxyport = prop.getProperty("proxyport");
				    proxyuser = prop.getProperty("proxyuser");
				    proxypass = prop.getProperty("proxypass");
				    traceDebug = prop.getProperty("DebugLogin");
				}
				catch (IOException ex) {
						System.out.println("\n Properties.txt does not exist or does not have required data");
						//ex.printStackTrace();
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
	
	private boolean login() {
        boolean success = false;
        Integer intProxyPort = 80;
        if (proxyport != null && proxyport.length() != 0){
        	intProxyPort = Integer.valueOf(proxyport);        	
        }
        if (authEndPoint == null || authEndPoint.equals(""))
        	authEndPoint = "https://login.salesforce.com/services/Soap/u/39.0";
        	
        try {
        	StatusLabel.setText("Attempting to login");
          ConnectorConfig config = new ConnectorConfig();
          config.setUsername(username);
          config.setPassword(password + securitytoken);
          config.setAuthEndpoint(authEndPoint);
          if (traceDebug != null && traceDebug.toUpperCase().equals("TRUE")){
              config.setTraceFile("traceLogs.txt");
              config.setTraceMessage(true);
              config.setPrettyPrintXml(true);
          }
          if (proxyhost != null && (!proxyhost.equals(""))){
              config.setProxy(proxyhost, intProxyPort);
              config.setProxyUsername(proxyuser);
              config.setProxyPassword(proxypass);
          }
		  if (!filename.endsWith(".csv"))
			  filename = filename + ".csv";
		  csvFile = filename;
		  if (csvColumnToSearch == null || csvColumnToSearch.equals("")){
			  StatusLabel.setText("All necessary details not provided");
			  return success;
		  }
          partnerConnection = Connector.newConnection(config);
          success = true;
        } catch (ConnectionException ce) {
          success= false;
      	StatusLabel.setText("login failed. For details check stack trace in cmd prompt window");
          ce.printStackTrace();
        } catch (FileNotFoundException fnfe) {
          success = false;
         StatusLabel.setText("no input file found. For details check stack trace in cmd prompt window");
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
  
	      // Set query batch size
          partnerConnection.setQueryOptions(2000);
        
          // SOQL query to use
          String soqlQuery = "SELECT Id," + FieldToSearch + " FROM " + ObjectToLookup;
          if (SecondFieldToSearch != null && !SecondFieldToSearch.equals("")){
        	  soqlQuery = "SELECT Id," + FieldToSearch + "," + SecondFieldToSearch + " FROM " + ObjectToLookup;
          }
          
          if (addFilterCriteria != null && !addFilterCriteria.equals("")){
        	  soqlQuery = soqlQuery + " where " + addFilterCriteria;
          }
          soqlQuery = soqlQuery + " limit 100000";
          System.out.println(soqlQuery);
          // Make the query call and get the query results
          QueryResult qr;
          try {
			qr = partnerConnection.query(soqlQuery);
	          boolean done = false;
	          int loopCount = 0;
	          // Loop through the batches of returned results
	          while (!done) {
	                StatusLabel.setText("\n Querying data .." + loopCount++);
	                SObject[] records = qr.getRecords();
	                // Process the query results
	                for (int i = 0; i < records.length; i++) {
	                	SObject sobj = records[i];
	                    if (SecondFieldToSearch != null && !SecondFieldToSearch.equals("")){
	                    	String fieldvalue1 = "";
	                    	String fieldvalue2 = "";
	                    	if (sobj.getField(FieldToSearch) != null)
	                    		fieldvalue1 = sobj.getField(FieldToSearch).toString();
	                    	if (sobj.getField(SecondFieldToSearch) != null)
	                    		fieldvalue2 = sobj.getField(SecondFieldToSearch).toString();
	                    	if (casesensitive == "Y"){
	                    		inputMap.put(sobj.getField("Id"), fieldvalue1.toUpperCase() + fieldvalue2.toUpperCase());
	                    	}
	                    	else
	                    		inputMap.put(sobj.getField("Id"), fieldvalue1 + fieldvalue2);	                    	
	                    }
	                    else {
	                    	if (casesensitive == "Y"){
	                    		if (sobj.getField(FieldToSearch) != null){
	                    			inputMap.put(sobj.getField("Id"), sobj.getField(FieldToSearch).toString().toUpperCase());
	                    		//	System.out.println(sobj.getField(FieldToSearch).toString().toUpperCase());
	                    		}
	                    			
	                    	}
	                    	else
	                    		inputMap.put(sobj.getField("Id"), sobj.getField(FieldToSearch));
	                    }
	                    	
	                }
	                if (qr.isDone()) {
	                      done = true;
	                  } else {
	                      qr = partnerConnection.queryMore(qr.getQueryLocator());
	                  }
	              }
		}
		catch (ConnectionException e1) {
			StatusLabel.setText("Object and field details entered are incorrect. Please recheck");
			e1.printStackTrace();
		} 
		
		System.out.println("\n Query execution completed.");
		try {
			partnerConnection.logout();
		} catch (ConnectionException e1) {
			e1.printStackTrace();
		}
	  }
	  
		private static short convert2ColumnIndex(String columnName) {
			columnName = columnName.toUpperCase();
			short value = 0;
			for (int i = 0, k = columnName.length() - 1; i < columnName.length(); i++, k--) {
				int alpabetIndex = ((short) columnName.charAt(i)) - 64;
				int delta = 0;
				// last column simply add it
				if (k == 0) {
					delta = alpabetIndex - 1;
				} else { // aggregate
					if (alpabetIndex == 0)
						delta = (26 * k);
					else
						delta = (alpabetIndex * 26 * k);					
				}
				value += delta;
			}
			return value;
		}
		
	  private void updateOutputData(){

		    String line = "";
   
		    Path p = Paths.get(csvFile); 
			List<String> fileContent;
			try {
				
				fileContent = new ArrayList<>(Files.readAllLines(p, StandardCharsets.UTF_8));
                Short inputSearchNumber = convert2ColumnIndex(csvColumnToSearch);
                System.out.println(inputSearchNumber);
				for (int i = 0; i < fileContent.size(); i++) {
				    if ((line = fileContent.get(i)) != null) {
		                String[] inputdata = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		                String inputfieldvalue = "";
		                if (inputdata.length >= inputSearchNumber){
			                if (inputdata[inputSearchNumber] != null && !inputdata[inputSearchNumber].equals("")){
				                inputfieldvalue = inputdata[inputSearchNumber].toString().replace("\"","");
				                if (casesensitive == "Y"){
				                	inputfieldvalue = inputfieldvalue.toUpperCase();
				                }
				            //    System.out.println("Input field = " + inputfieldvalue + " " + getKeyByValue(inputMap, inputfieldvalue));
				                if (inputdata[inputSearchNumber] != null && inputdata[inputSearchNumber] != ""){
				                	if (i == 0){
					                	fileContent.set(i, line + "," + ObjectToLookup + ".Id");		                		
				                	}
				                	else
					                	fileContent.set(i, line + "," + getKeyByValue(inputMap, inputfieldvalue));
				                }
			                }
		                }

				    }
				}
			Files.write(p, fileContent, StandardCharsets.UTF_8);
			StatusLabel.setText("Data Extraction complete!");
			}
			catch(IOException e){
				System.out.println("\n Unable to open file and update it. Please check if Input file is correctly populated.");
				StatusLabel.setText("Error. Please check if Input file is open in another application. Else try after saving the file in UTF-8 format once");
				e.printStackTrace();
			}

	  }
	  
}
