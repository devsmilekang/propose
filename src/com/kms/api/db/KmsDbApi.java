package com.kms.api.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cubrid.jdbc.driver.CUBRIDResultSet;
import cubrid.jdbc.driver.CUBRIDResultSetMetaData;

public class KmsDbApi extends KmsDbConnetion{
	private ResultSet rs;
	private CUBRIDResultSet c_rs;
	
	public KmsDbApi(){
		rs = null;
		c_rs=null;
	}
	
	public List executeQuery(String query){
		List list = new ArrayList();
		if(super.getStmt()==null){
			return list;
		}
		try{
			this.rs = super.getStmt().executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			int column_cnt = rsmd.getColumnCount();
			while(this.rs.next()){
				Map hm = (Map)new HashMap();
				for(int k=1; k <= column_cnt; k++){
					hm.put(rsmd.getColumnName(k).toUpperCase(), rs.getString(rsmd.getColumnName(k).toUpperCase()));
				}
				list.add(hm);
			}
		}
		catch(SQLException e){
			System.out.println("query error");
			System.out.println(e);
			return null;
		}
		return list;
	}
	
	public int executeUpdate(String query){
		int result = 0;
		if(super.getStmt()==null){
			return result;
		}
		try{
			result = super.getStmt().executeUpdate(query);
		}
		catch(SQLException e){
			System.out.println("query error");
			System.out.println(e);
			return result;
		}
		return result;
	}
	
	public List cubrid_executeQuery(String query){
		List<Map<String, String>> list = new ArrayList();
		if(super.getStmt()==null){
			return list;
		}
		try{
			this.c_rs = (CUBRIDResultSet)super.getStmt().executeQuery(query);
			CUBRIDResultSetMetaData rsmd = (CUBRIDResultSetMetaData)c_rs.getMetaData();
			int column_cnt = rsmd.getColumnCount();
			while(this.c_rs.next()){
				Map hm = (Map)new HashMap();
				for(int k=1; k <= column_cnt; k++){
					hm.put(rsmd.getColumnName(k).toUpperCase(), this.c_rs.getString(rsmd.getColumnName(k).toUpperCase()));
				}
				list.add(hm);
			}
		}
		catch(SQLException e){
			System.out.println("query error");
			System.out.println(e);
			return null;
		}
		finally{
			try{
				rs_close();
			}
			catch(SQLException e){
				System.out.println("rs_close error");
				System.out.println(e);
				return null;
			}
		}
		return list;
	}
	
	public int cubrid_executeUpdate(String query){
		int result = 0;
		if(super.getStmt()==null){
			return result;
		}
		try{
			result = super.getStmt().executeUpdate(query);
		}
		catch(SQLException e){
			System.out.println("query error");
			System.out.println(e);
			return result;
		}
		finally{
			try{
				rs_close();
			}
			catch(SQLException e){
				System.out.println("rs_close error");
				System.out.println(e);
				return result;
			}
		}
		return result;
	}
	
	public void rs_close() throws SQLException{
		if(this.rs!=null){
			this.rs.close();
		}
		if(this.c_rs !=null){
			this.c_rs.close();
		}
	}
}
