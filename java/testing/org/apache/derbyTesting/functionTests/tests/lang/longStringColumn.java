/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derbyTesting.functionTests.tests.lang
   (C) Copyright IBM Corp. 1999, 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package org.apache.derbyTesting.functionTests.tests.lang;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.derby.tools.ij;
import org.apache.derby.tools.JDBCDisplayUtil;
import java.io.*;
import java.sql.PreparedStatement;

/**
 * Test of strings longer than 64K. Need to test it using Clob because long varchars don't accept data longer than 32700.
 */


public class longStringColumn { 
	/**
		IBM Copyright &copy notice.
	*/
	private static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_1999_2004;

	public static PreparedStatement psSet;
	public static PreparedStatement psGet;

    public static void main(String[] args) {

		System.out.println("Test longStringColumn starting");

		String longText;
		StringBuffer buff = new StringBuffer("... ");

		try {
			// use the ij utility to read the property file and
			// make the initial connection.
			ij.getPropertyArg(args);
			Connection conn = ij.startJBMS();

			Statement st2 = conn.createStatement();
			st2.execute("CREATE TABLE TEST(id bigint, body clob(65K))");
			psSet = conn.prepareStatement("insert into test values(?,?)");
			psGet = conn.prepareStatement("select body from test where id=?");

			for (long i = 0; i < 65560; i++) {

				if (i % 10 == 0)
					buff.append(" ");
				else
					buff.append("x");

				// Show something is happening
				if (i % 10000 == 0)
					System.out.println("... " + i );

				// only test after buffer length reaches 65500
				if (buff.length() > 65525) {

					System.out.println("i = " + i + ", testing length: " + buff.length());

					longText = buff.toString();
					// set the text
					setBody(i, longText);

					// now read the text
					String res = getBody(i);
					if (!res.equals(longText)) {
						System.out.println("FAIL -- string fetched is incorrect, length is "
							+ buff.length() + ", expecting string: " + longText
							+ ", instead got the following: " + res);
						break;
					}
				}
			}

			conn.close();

		} catch (SQLException e) {
			dumpSQLExceptions(e);
		} catch (Throwable e) {
			System.out.println("FAIL -- unexpected exception:" + e.toString());
		}

		System.out.println("Test longStringColumn finished");

    }

	private static void setBody(long key, String body) {

		try {
			psSet.setLong(1, key);
			psSet.setString(2, body);
			psSet.executeUpdate();

		} catch (SQLException ex) {
			ex.printStackTrace();

			System.out.println("FAIL -- unexpected exception");
			System.exit(-1);
		}
	}

    private static String getBody(long key) {

        String result="NO RESULT";

        try {
			psGet.setLong(1, key);
			ResultSet rs = psGet.executeQuery();

			if (rs.next())
				result = rs.getString(1);

		} catch (SQLException ex) {
              ex.printStackTrace();
        }

        return result;
    }

	static private void dumpSQLExceptions (SQLException se) {
		System.out.println("FAIL -- unexpected exception: " + se.toString());
		while (se != null) {
			System.out.print("SQLSTATE("+se.getSQLState()+"):");
			se = se.getNextException();
		}
	}

}
