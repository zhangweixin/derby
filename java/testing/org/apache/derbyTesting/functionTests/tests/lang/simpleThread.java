/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derbyTesting.functionTests.tests.lang
   (C) Copyright IBM Corp. 1999, 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package org.apache.derbyTesting.functionTests.tests.lang;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.DriverManager;
import org.apache.derby.tools.ij;

/*
	This is from a bug found by a beta customer.
 */
public class simpleThread implements Runnable { 
	/**
		IBM Copyright &copy notice.
	*/
	private static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_1999_2004;

        private static Connection _connection = null;
        private static boolean _inUse = false;
        private static Object _lock = new Object();

        private long _wait = 0;
        private long _myCount = 0;
        private static int _count = 0;
        private synchronized static int getCount() { return(_count++); }
        private String _query;

        public simpleThread( String query, long waitTime) {
                _wait = waitTime;
                _myCount = getCount();
                _query = query;
                new Thread(this).start();
        }

        public void run() {
				int rows = 0;
				boolean caught = false;
                try {
                        Thread.currentThread().sleep(_wait);
                        Connection conn = GetConnection();
                        Statement stmt = conn.createStatement();
                        String query = _query;
                        ResultSet rs = stmt.executeQuery( query );
                        ResultSetMetaData rsmd = rs.getMetaData();
                        //int cols = rsmd.getColumnCount();
                        while(rs.next()) {
							rows++;
                                //System.out.print(_myCount + ":");
                                //for( int x=0;x<cols;x++) {
                                 //       String s = rs.getString(x+1);
                                  //      if( x > 0) System.out.print(",");
                                   //     System.out.print(s);
                                //}
                                //System.out.println();
                        }
                        stmt.close();
                        ReturnConnection(conn);
                } catch (Exception ex) {
					// we expect some threads to get exceptions
					caught = true;
                }
				if (rows == 3 || caught)
				{
					//System.out.println("This thread's okay!");
			    }
				else
				{
					System.out.println("FAIL: thread "+_myCount+" only got "+rows+" rows and caught was "+caught);
		        }
        }


        public simpleThread(String argv[]) throws Exception {
            
			ij.getPropertyArg(argv);
			_connection = ij.startJBMS();

			Connection conn = GetConnection();

            Statement stmt = conn.createStatement();

            stmt.execute("create table people(name varchar(255), address varchar(255), phone varchar(64))");
            stmt.execute("insert into people VALUES ('mike', 'mikes address', '123-456-7890')");
            stmt.execute("insert into people VALUES ('adam', 'adams address', '123-456-1234')");
            stmt.execute("insert into people VALUES ('steve', 'steves address', '123-456-4321')");
            stmt.close();

            ReturnConnection(conn);

            String query = "SELECT * from people ORDER by name";

            try {
                String[] retval = new String[4];
                new simpleThread(query,0);
                new simpleThread(query,10000);
                new simpleThread(query,10100);
                new simpleThread(query,20000);
            } catch (Exception ex) {
                System.err.println(ex.toString() );
            }
        }

        public static Connection GetConnection() {
                synchronized(_lock) {
                        _inUse = true;
                }
                return _connection;
        }
        public static void ReturnConnection(Connection c) {
                synchronized(_lock) {
                        _inUse = false;
                        _lock.notifyAll();
                }
        }
}
