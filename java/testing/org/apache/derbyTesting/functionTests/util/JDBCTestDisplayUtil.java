/*

   Licensed Materials - Property of IBM
   Cloudscape - Package org.apache.derbyTesting.functionTests.util
   (C) Copyright IBM Corp. 2004. All Rights Reserved.
   US Government Users Restricted Rights - Use, duplication or
   disclosure restricted by GSA ADP Schedule Contract with IBM Corp.

 */

package org.apache.derbyTesting.functionTests.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;

import java.util.Properties;
import java.util.Enumeration;
import java.util.Vector;


import org.apache.derby.impl.tools.ij.ijException;

import org.apache.derby.tools.JDBCDisplayUtil;

/**
   Show common format for Network Server and Embedded Exceptions
**/

public class JDBCTestDisplayUtil extends JDBCDisplayUtil {
	/**
		IBM Copyright &copy notice.
	*/
	private static final String copyrightNotice = org.apache.derby.iapi.reference.Copyright.SHORT_2004;

	/**
	   Show common format for Network Server and Embedded Exceptions
	   @param out PrintStream to write to
	   @param e Throwable to print
	*/
	
	static public void ShowCommonSQLException(PrintStream out, Throwable e) {
		if (e == null) return;
		
		if (e instanceof SQLException)
		{
			SQLException se = (SQLException)e;
			if (isDataConversionException(se))
				out.println ("Data Conversion SQLException");
			else if (isResultSetClosedException(se))
				out.println("Result Set Closed Exception");
			else if (isNullSQLStringException(se))
				out.println("Null SQL String Exception");
			else if (isInvalidParameterException(se))
					out.println("Invalid Parameter SQL Exception");
			else if (isValidOnScrollCursorsException(se))
				out.println("Method Only Valid On Scroll Cursors SQL Exception");
			else if (isInvalidMethodReturnException(se))
				out.println("Invalid Method Returning a ResultSet or Row Count SQL Exception");
			else if (isTableDoesNotExistException(se))
					out.println("Table Does Not Exist SQL Exception");
			else if (isReturnsInvalidResultSetException(se))
				out.println("Invalid Method Returning ResultSet SQL Exception");
			else 
				ShowSQLException(out, se);
		}
		else 
			ShowException(out, e);
	}
	
	static private boolean isDataConversionException(SQLException se)
	{
		if ((se.getMessage() != null &&
			 se.getMessage().indexOf("Invalid data conversion") >= 0)
			|| (se.getSQLState() != null &&
				(se.getSQLState().equals("22018")
				 || se.getSQLState().equals("22005")
				 || se.getSQLState().equals("22007"))))
			return true;
		return false;
	}
	
	static private boolean isResultSetClosedException(SQLException se)
	{
		if ((se.getMessage() != null &&
			 se.getMessage().indexOf("Invalid operation: result set closed") >= 0)
			|| (se.getSQLState() != null &&
				(se.getSQLState().equals("XCL16"))))
			return true;
		return false;
	}
	
	static private boolean isNullSQLStringException(SQLException se)
	{
		if ((se.getMessage() != null &&
			 se.getMessage().indexOf("Null SQL string passed.") >= 0)
			|| (se.getSQLState() != null &&
				(se.getSQLState().equals("XJ067"))))
			return true;
		return false;
	}

	static private boolean isInvalidParameterException(SQLException se)
	{
		if ((se.getMessage() != null &&
			 se.getMessage().indexOf("Invalid parameter value") >= 0)
			|| (se.getMessage().indexOf("Invalid fetch size") >= 0)
			|| (se.getMessage().indexOf("Invalid fetch direction") >= 0)
			|| (se.getSQLState() != null &&
				(se.getSQLState().equals("XJ066"))))
			return true;
		return false;
	}
	
	static private boolean isValidOnScrollCursorsException(SQLException se)
	{
		if ((se.getMessage() != null &&
			 se.getMessage().indexOf("' method is only allowed on scroll cursors.") >= 0)
			|| (se.getSQLState() != null &&
				(se.getSQLState().equals("XJ061"))))
			return true;
		return false;
	}
	
	static private boolean isInvalidMethodReturnException(SQLException se)
	{
		if (((se.getMessage() != null &&
			  se.getMessage().indexOf("executeQuery method cannot be used for update.") >= 0)
			 ||  se.getMessage().indexOf("executeUpdate method cannot be used for query.") >= 0)
			|| (se.getSQLState() != null &&
				(se.getSQLState().equals("X0Y78")
				 || se.getSQLState().equals("X0Y79"))))
			return true;
		return false;
	}
	
	static private boolean isTableDoesNotExistException(SQLException se)
	{
		if (se.getSQLState() != null &&
			se.getSQLState().equals("42X05"))
			return true;
		return false;
	}
	
	static private boolean isReturnsInvalidResultSetException(SQLException se)
	{
		if ((se.getMessage() != null &&
			 se.getMessage().indexOf("cannot be called with a statement that returns a ResultSet.") >= 0)
			|| (se.getSQLState() != null &&
				(se.getSQLState().equals("X0Y79"))))
			return true;
		return false;
	}
}
