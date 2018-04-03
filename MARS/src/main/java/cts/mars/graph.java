package cts.mars;

import org.neo4j.*;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.exceptions.ClientException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;

public class graph {
	
static Driver drvr;
static Session sess;

	public static Session boltConn(String conn_str, String usr, String pass) {
		//Driver driver = GraphDatabase.driver( "bolt://hobby-fdfkolpgpijggbkeeoajpnal.dbs.graphenedb.com:24786", AuthTokens.basic( "tataiermail", "b.cqf52no6IF9y.2JtKzlvdalluWAfX" ) );
		   drvr = GraphDatabase.driver( conn_str, AuthTokens.basic( usr, pass ) );
		   sess = drvr.session();
		   return sess;
	}
	
	public static StatementResult neo4jExec(String cql, String boltURL, String user, String password) {
		//============ DECLARATION SECTION   ============================================================
		  //String boltURL = "bolt://hobby-fdfkolpgpijggbkeeoajpnal.dbs.graphenedb.com:24786";
		  //String user = "tataiermail";
		  //String password = "b.cqf52no6IF9y.2JtKzlvdalluWAfX";
		//===============================================================================================  

		  Session session = boltConn(boltURL, user, password);
		  //String cql = "CREATE (TheMatrix:Movie {title:'The Matrix', released:1999, tagline:'Welcome to the Real World'}) ";
		  
		  StatementResult result = session.run(cql);
		  System.out.println("CQL executed");
		  return result;
	}
	
		
	
	
	
	public static void destructor() throws ClientException {
	    try {
		sess.close();
	    drvr.close();
	    System.out.println("closed Driver");
	    }
	    catch (ClientException e) {
	    	System.out.println("exception occured = " + e);
	    	drvr.close();
	    }
	    finally {
		    System.out.println("open objects closed");
	    }
	}	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		  
		//============ DECLARATION SECTION   ============================================================
		  String boltURL = "bolt://hobby-fdfkolpgpijggbkeeoajpnal.dbs.graphenedb.com:24786";
		  String user = "tataiermail";
		  String password = "b.cqf52no6IF9y.2JtKzlvdalluWAfX";
		//===============================================================================================
		  
		  
		  
/*		  Session session = boltConn(boltURL, user, password);
		  String cql = "CREATE (TheMatrix:Movie {title:'The Matrix', released:1999, tagline:'Welcome to the Real World'}) ";
		  
		  session.run(cql);
		  
		  StatementResult result = session.run("MATCH (n:Person) RETURN n.name AS name");
		    		//session.run("match (n) return n as ROW");
		    while ( result.hasNext() )
		    {
		      Record record = result.next();
		      System.out.println( record.get("name").asString() );
		      System.out.println( record.toString());
		    }

		    //session.close();
*/		    
		}


}
