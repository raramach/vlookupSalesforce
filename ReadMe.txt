This is a lightweight utility to lookup Salesforce Ids based on a search field.
It is built to query data like Profiles,Roles etc where total row count is less than 50,000.
It will be useful for folks who need load data into Salesforce and are often in need to do exports and vlookups in Excel to get the Salesforce record Ids.
These are very typical while populating lookup fields, parent records etc.

If querying an object like Account which has large amount of records,
please add a filter criteria to reduce data taken from Salesforce for analysis to below 50,000 records.

Operation:
Click the RunVlookup.bat file on your windows machine to invoke the logic in the JAR file

Credentials:
Properties.txt can be populated with details of login username, etc
If this file is not populated, the utility will prompt you for the details
If connecting to a sandbox, please populate authendpoint in Properties.txt.
For ex: 
authEndPoint = https://test.salesforce.com/services/Soap/u/39.0

Input file - the input file should be a CSV file. It should contain the column which has the field data to be used to lookup in Salesforce.
A sample input file is provided "Inputfile". The Id will be populated in the last column of the CSV
Place the input file in same folder containing the utility files.

Other details related to Input file - 
user name = login for Salesforce
password = password to login to salesforce
security token = security token required to establish connection to Salesforce
authEndPoint  = endpoint url for the Salesforce org
proxyhost = proxy host name or ip address used if behind a corporate firewall. Leave blank if not required.
proxyport = port for the proxy host mentioned above. Leave blank if not required.
proxyuser = user name of the network (windows desktop) user used to access the proxy. Leave blank if not required.
proxypass = password of the network user used to access the proxy. Leave blank if not required.

