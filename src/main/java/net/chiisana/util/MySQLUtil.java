package net.chiisana.util;

import java.sql.*;

public class MySQLUtil {
	private Connection conn = null;
	private String connectionString = null;

	public MySQLUtil(String Server, int Port, String Database, String User, String Password) {
		// Set Connection String
		this.connectionString = "jdbc:mysql://" + Server
				+ ":" + Port
				+ "/" + Database
				+ "?user=" + User
				+ "&password=" + Password
				+ "&autoReconnect=true";
	}

	public boolean openConnection() {
		if (this.conn != null) {
			try {
				if (!this.conn.isClosed()) {
					// if not closed, recycle
					return true;
				}
			} catch (SQLException e) {
				// well, we know its closed, then. Continue.
			}
		}
		try {
			this.conn = DriverManager.getConnection(connectionString);
			return true;
		} catch (SQLException e) {
			System.err.println("MySQL Connection failed, please double check configurations");
			System.err.println("Connection String: " + connectionString);
			e.printStackTrace();
			return false;
		}
	}

	public ResultSet runSelectQuery(String Query, boolean isRetry) {
		try {
			if ((this.conn == null) || (this.conn.isClosed())) {
				// Connection not instantiated
				if (!this.openConnection()) {
					return null;    // sorry captain, can't talk to the database
				}
			}
		} catch (Exception e) {
			// We could run into all sorts of exceptions here from the this.conn check, just discard them for now.
		}
		try {
			Statement stmt = this.conn.createStatement();
			return stmt.executeQuery(Query);
		} catch (Exception ex) {
			if (isRetry) {
				return null;
			} else {
				return (this.runSelectQuery(Query, true));
			}
		}
	}

	public ResultSet runSelectQuery(String Query) {
		return runSelectQuery(Query, false);
	}

	public int runUpdateQuery(String Query, boolean isRetry) {
		try {
			if ((this.conn == null) || (this.conn.isClosed())) {
				// Connection not instantiated
				if (!this.openConnection()) {
					return -1;    // sorry captain, can't talk to the database
				}
			}
		} catch (Exception e) {
			// We could run into all sorts of exceptions here from the this.conn check, just discard them for now.
		}
		try {
			Statement stmt = this.conn.createStatement();
			return stmt.executeUpdate(Query);
		} catch (Exception ex) {
			if (isRetry) {
				return -1;
			} else {
				return (this.runUpdateQuery(Query, true));
			}
		}
	}

	public int runUpdateQuery(String Query) {
		return this.runUpdateQuery(Query, false);
	}

	private void closeConnection() {
		try {
			this.conn.close();
		} catch (SQLException e) {
			// We don't really care at this point what happens
			// if it fails to close, it will eventually timeout
		}
	}
}
