import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

public class SQLiteJDBCEC
{
  public static void main(String [] args) throws SQLException
  {

	  Scanner input = new Scanner(System.in);
	  Connection conn_db = null;
    Statement stmt = null;
    ResultSet rs = null;

    //Checking for errors
    try
    {
      Class.forName("org.sqlite.JDBC");
      conn_db = DriverManager.getConnection("jdbc:sqlite:chinook.db");

    }
    catch (Exception e)
    {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }

    //Displays the options
    int option = 0;

    while (option != 5)
    {
		  System.out.print("Choose From the Following Options:\n"
			+ "(1) Identify Marketable Population and Material\n"
			+ "(2) Simple Track Recommender\n"
			+ "(3) Top Sellers by Revenue\n"
			+ "(4) Top Sellers by Volume\n"
			+ "(5) Exit\n"
			+ "Enter Choice (1 - 5): ");

		option = input.nextInt();
		String trivial = input.nextLine();
    System.out.println();

      if (option == 1)
      {
        System.out.print("Enter State (ex. CA): ");
        String state = input.nextLine();


        conn_db.setAutoCommit(false);
  			stmt = conn_db.createStatement();


        rs = stmt.executeQuery("SELECT PT.PlaylistId, P.Name, A.title, "
                              + "C.CustomerId, C.FirstName, C.lastName FROM Customer C, "
                              + "Playlist P, PlaylistTrack PT, Track T, Invoice I, "
                              + "InvoiceLine IL, Album A WHERE A.AlbumId = T.AlbumId AND "
                              + "C.CustomerId = I.CustomerId AND "
                              + "I.InvoiceId = IL.InvoiceId AND T.TrackId = IL.TrackId"
                              + " AND PT.TrackId = T.TrackId AND PT.PlaylistId = P.PlaylistId"
                              + " AND C.State <> '" + state + "' GROUP BY PT.TrackId"
                              + " HAVING COUNT(*) >= 1;");
        int count = 0;
        while (rs.next())
        {
        int cid = rs.getInt("customerid");
        int pid = rs.getInt("playlistid");
        String firstName = rs.getString("firstname");
        String lastName = rs.getString("lastname");
        String playlistName = rs.getString("name");
        String title = rs.getString("title");

        System.out.println("\nCustomer ID: " + cid);
        System.out.println("Customer Name: " + firstName + " " + lastName);
        System.out.println("Album title: " + title);
        System.out.println("Playlist Name: " + playlistName);
        System.out.println("Playlist ID: " + pid + "\n");
        count++;
       }
       if (count == 0)
       {
         System.out.println("\nNo records found.\n");
       }
      }
      // option 3: showing album titles based on artist's name
  		else if (option == 3)
      {

  			conn_db.setAutoCommit(false);
  			stmt = conn_db.createStatement();

        rs = stmt.executeQuery("SELECT  DISTINCT A.Name, SUM(IL.Quantity)AS revenue"
                        + " FROM Artist A, Album AL, Track T,"
                        + " InvoiceLine IL WHERE A.ArtistId = AL.ArtistId AND"
                        + " AL.AlbumId = T.AlbumId AND T.Trackid = IL.TrackId"
                        + " group by a.artistid"
                        + " order by revenue desc"
                        + " limit 1;");

  	    System.out.println( "");
  			while (rs.next())
        {
  			    String  arName = rs.getString("Name");
  			    int HighRev = rs.getInt("Revenue");
  				  System.out.println("Artist Name: " + arName);
  				  System.out.println("Revenue: $" + HighRev);
            
  			}
  			  		 }

       else if (option == 4)
       {

   			conn_db.setAutoCommit(false);
   			stmt = conn_db.createStatement();

        rs = stmt.executeQuery("SELECT  DISTINCT A.Name, SUM(IL.Quantity)AS Volume"
                        + " FROM Artist A, Album AL, Track T,"
                        + " InvoiceLine IL WHERE A.ArtistId = AL.ArtistId AND"
                        + " AL.AlbumId = T.AlbumId AND T.Trackid = IL.TrackId"
                        + " group by a.artistid"
                        + " order by volume desc"
                        + " limit 1;");


   	    System.out.println( "");
   			while (rs.next() )
         {

   			    String  arName = rs.getString("Name");
   			    
   				  
   			}
   			
        System.out.println();
   		 }


    }

    //Close all connections after program is terminated
    rs.close();
	  stmt.close();
	  conn_db.close();
    System.exit(0);
	  System.out.println("Program Terminated!");
    }
 }
