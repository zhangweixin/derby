/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derbyTesting.functionTests.tests.derbynet
   (C) Copyright IBM Corp. 2003, 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package org.apache.derbyTesting.functionTests.tests.derbynet;

import java.sql.*;
import org.apache.derby.drda.NetworkServerControl;
import java.net.InetAddress;
import java.io.PrintWriter;

public class NSinSameJVM {
	/**
		IBM Copyright &copy notice.
	*/
	private static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_2003_2004;

    private static final int NETWORKSERVER_PORT = 20000;
	private static final String INVALID_HOSTNAME = "myhost.nowhere.ibm.com";
    private String databaseFileName = "NSinSameJVMTestDB;create=true";

    public NSinSameJVM() {

        // Load the Cloudscape driver
        try {
            Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
            dbg("Cloudscape drivers loaded");
        } catch (Exception e) {
            e.printStackTrace();
        }

		

		NetworkServerControl serverControl = null;
		boolean started = false;

		try {

			serverControl = new
				NetworkServerControl(InetAddress.getByName("0.0.0.0"),
									 NETWORKSERVER_PORT);

			serverControl.start(new PrintWriter(System.out,true));
			
			for (int i = 1; i < 50; i++)
			{
				Thread.sleep(1000);
				if (isServerStarted(serverControl))
				{
					started = true;
					break;
				}
			}
				
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		if(started)
			dbg("NetworkServer started");
		else
		{
			System.out.println("FAIL Network Server did not start");
			return;
		}

        String url = "jdbc:derby:net://localhost:" + NETWORKSERVER_PORT + "/\"" + databaseFileName + "\"" + ":user=dummyUser;password=dummyPw;";
        Connection connection = null;
		   
        try {
			// Just connect, do something and close the connection
            connection = DriverManager.getConnection(url);

			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("Select  tablename   from  sys.systables");
			
			while (rs.next())
			{
				rs.getString(1);
			}
			rs.close();
            dbg("Connected to database " + databaseFileName);
			// Leave the connection open before shutdown to make 
			// sure the thread closes down.
			// connection.close();
			
			System.out.println("getting ready to shutdown");
			serverControl.shutdown();
			Thread.sleep(5000);

        } catch (Exception e) {
			System.out.print("FAIL: Unexpected exception" + e.getMessage());
            e.printStackTrace();
        }
			
		// 

    }

    private void dbg(String s) {
        System.out.println(Thread.currentThread().getName() + "-NSinSameJVM: " + s);
    }

    public static void main(String[] args) {
        new NSinSameJVM();
    }

	private boolean isServerStarted(NetworkServerControl server)
	{
		try {
			server.ping();
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}
}





