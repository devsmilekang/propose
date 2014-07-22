package com.kms.api.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**************************************************/
/* 제작자 : 강민서 v 1.0 Api					      */
/* JDK = 1.5									  */
/**************************************************/

/********************************************************************************************/
/* DBConnetion Api					
/* Class : Kms_DbConnetion
/* 설명 : DB Connetion Api로써 Oracle, Mssql, Mysql, Cubrid 를 Connection 할 수 있다.
/* 필요파일 : lib 폴더에 있는 파일들의 jar 파일도 Api와 같이 삽입한다.
/* Oracle -> ojdbc14.jar
/* Mssql -> jtds-1.2.jar
/* Mysql -> mysql-connector-java-3.1.12-bin.jar
/* Cubrid -> JDBC-9.1.0.0212-cubrid.jar
/* 위의 jar 기반으로 작성하였다. 각 메소드의 설명은 아래정의하였다.
/********************************************************************************************/
public class KmsDbConnetion {
	private Connection conn;
	private Statement stmt;
	
	public KmsDbConnetion (){
		this.conn = null;
		this.setStmt(null);
	}
	
	public boolean OracleConn(String url, String port, String sid,String user, String passwd) {
		if(!DBConnetClose()){
			System.out.println("Connect Close fail");
			return false;
		}
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}
		catch(ClassNotFoundException e){
			System.out.println("DB not found driver error");
			System.out.println(e);
			return false;
		}
		try{
			conn = DriverManager.getConnection("jdbc:oracle:thin:@"+url+":"+port+":"+sid, user, passwd);
		}
		catch(SQLException e){
			System.out.println("DB not connect");
			System.out.println("url, port, sid, user, password check");
			System.out.println(e);
			return false;
		}
		System.out.println("Success Oracle Connect");
		if(!stmtCreate()){
			return false;
		}
		return true;
	}
	
	public boolean MssqlConn(String url, String port, String user, String passwd) {
		if(!DBConnetClose()){
			System.out.println("Connect Close fail");
			return false;
		}
		try{
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
		}
		catch(ClassNotFoundException e){
			System.out.println("DB not found driver error");
			System.out.println(e);
			return false;
		}
		try{
			conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+url+":"+port+"/", user, passwd);
		}
		catch(SQLException e){
			System.out.println("DB not connect");
			System.out.println("url, port, user, password check");
			System.out.println(e);
			return false;
		}
		System.out.println("Success Mssql Connect");
		if(!stmtCreate()){
			return false;
		}
		return true;
	}
	public boolean MysqlConn(String url, String port, String db_name, String user, String passwd) {
		if(!DBConnetClose()){
			System.out.println("Connect Close fail");
			return false;
		}
		try{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch(ClassNotFoundException e){
			System.out.println("DB not found driver error");
			e.printStackTrace();
			return false;
		}
		try{
			conn = DriverManager.getConnection("jdbc:mysql://"+url+":"+port+"/"+db_name, user, passwd);
		}
		catch(SQLException e){
			System.out.println("DB not connect");
			System.out.println("url, port, db_name, user, password check");
			System.out.println(e);
			return false;
		}
		System.out.println("Success Mysql Connect");
		if(!stmtCreate()){
			return false;
		}
		return true;
	}
	public boolean CubridConn(String url, String port, String db_name, String user, String passwd) {
		if(!DBConnetClose()){
			System.out.println("Connect Close fail");
			return false;
		}
		try{
			Class.forName("cubrid.jdbc.driver.CUBRIDDriver");
		}
		catch(ClassNotFoundException e){
			System.out.println("DB not found driver error");
			System.out.println(e);
			return false;
		}
		try{
			conn = DriverManager.getConnection("jdbc:cubrid:"+url+":"+port+":"+db_name+":::", user, passwd);
		}
		catch(SQLException e){
			System.out.println("DB not connect");
			System.out.println("url, port, db_name, user, password check");
			System.out.println(e);
			return false;
		}
		System.out.println("Success Cubird Connect");
		if(!stmtCreate()){
			return false;
		}
		return true;
	}
	
	public boolean CubridConn(String url, String port, String db_name, String charater, String user, String passwd) {
		if(!DBConnetClose()){
			System.out.println("Connect Close fail");
			return false;
		}
		try{
			Class.forName("cubrid.jdbc.driver.CUBRIDDriver");
		}
		catch(ClassNotFoundException e){
			System.out.println("DB not found driver error");
			System.out.println(e);
			return false;
		}
		try{
			conn = DriverManager.getConnection("jdbc:cubrid:"+url+":"+port+":"+db_name+":::?charset="+charater, user, passwd);
		}
		catch(SQLException e){
			System.out.println("DB not connect");
			System.out.println("url, port, db_name, user, password check");
			System.out.println(e);
			return false;
		}
		System.out.println("Success Cubird Connect");
		if(!stmtCreate()){
			return false;
		}
		return true;
	}
	
	protected Connection getConn(){
		return conn;
	}
	protected Connection setConn(){
		return conn;
	}
	
	public boolean DBConnetClose(){
		if(this.stmt != null){
			try{
				this.stmt.close();
			}
			catch(SQLException e){
				System.out.println("Connect close error");
				System.out.println(e);
				return false;
			}
		}
		if(this.conn != null){
			try{
				this.conn.close();
			}
			catch(SQLException e){
				System.out.println("Connect close error");
				System.out.println(e);
				return false;
			}
		}
		return true;
	}
	private boolean stmtCreate(){
		try{
			this.setStmt(conn.createStatement());
		}
		catch(SQLException e){
			System.out.println("createStatement error");
			System.out.println(e);
			return false;
		}
		return true;
	}

	protected void setStmt(Statement stmt) {
		this.stmt = stmt;
	}

	protected Statement getStmt() {
		return stmt;
	}
}
