import java.sql.*;

public class HardParseDemo
{
  Connection conn;
  
  String[] parseStatName = new String[2];
  int[] parseStatValue   = new int[2];
  
  long startTime        = 0;
  long totalTime        = 0;
  int totalRowsReturned = 0;

  public HardParseDemo() {
	  try {
	    Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
        String url = "jdbc:oracle:thin:@localhost:1521/ptest";
        conn = DriverManager.getConnection(url, "myuser", "myuser");
	 
		runTest();
		printTest();
	  } catch (Exception e) {System.err.println(e.getMessage());}
	  
  }
  
  public void runTest() throws Exception {
	startTime = System.nanoTime();
	
	String query = "select id from mytab where id = "; 
	int countRows = 0;
	int fetchRows = 0;
	
	for (int i = 1; i <= 10000; i++) {
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query + i );
		rs.next();
		fetchRows = rs.getInt("id");
		countRows++;
		rs.close();
		st.close();
	}
	
	totalTime = System.nanoTime() - startTime / 1000000;
	totalRowsReturned = countRows;
	
  }
  
  public void printTest() throws Exception {
	  
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery("select b.name, a.value from v$mystat a, v$statname b where b.name in ('parse count (total)', 'parse count (hard)') and a.statistic# = b.statistic# order by 1");
		for (int i = 0; i < parseStatName.length; i++) {
			rs.next();
			parseStatName[i]  = rs.getString(1);
			parseStatValue[i] = rs.getInt(2);
		}
		
		System.out.println("***** Hard Parse Test *****");
		System.out.println("Total Rows Returned: "     + totalRowsReturned);
		System.out.println("Total Elapsed Time (ms): " + totalTime);
		for (int i = 0; i < parseStatName.length; i++) {
		System.out.println(parseStatName[i] + ": " + parseStatValue[i]);
		}
		
  }
  
  public static void main(String[] args)
  {
        new HardParseDemo();
  }
}
