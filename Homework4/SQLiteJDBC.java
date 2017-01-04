import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class SQLiteJDBC
{
    public static void main(String [] args) throws SQLException
    {

	  Scanner input = new Scanner(System.in);
	  Connection conn_db = null;
    Statement stmt = null;
    ResultSet statement = null;

    //Check if there is an errors while setting up connection to chinook.db
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

    //Displays the options that are available to the user
    int option = 0;

    while (option < 6)
    {
		System.out.print("\nChoose From the Following Options:\n"
				+ "(1) Show Album title(s) based on Artist name.\n"
				+ "(2) Show Track(s) of an Album title\n"
				+ "(3) Show Purchase History of a Customer\n"
				+ "(4) Update Individual Track Price\n"
				+ "(5) Update Batch Track Price\n"
				+ "(6) Exit\n"
				+ "Enter Choice (1 - 6): ");
		option = input.nextInt();
		String optionI = input.nextLine();
    System.out.println();

    /* Option 1:
    * Enter artist's name
    * display the album id and 
    * album title associated with the artist's name
    */
		if (option == 1)
    {
			//Prompt user for Artist's name
			System.out.print("Enter Artist Name:");
			String artistName = input.nextLine();

			conn_db.setAutoCommit(false);
			stmt = conn_db.createStatement();

			//Query for getting album id and album title with the user-input the artist's name
			statement = stmt.executeQuery(  "SELECT AR.ArtistId, AR.Name, AL.AlbumId, AL.Title "
										                + "FROM ARTIST AR, ALBUM AL "
										                + "WHERE AR.ArtistId = AL.ArtistId;");
			int count = 0;
			while (statement.next())
      {
				 int arId = statement.getInt("ArtistId");
			   String  name = statement.getString("Name");
			   int alId = statement.getInt("AlbumId");
			   String title = statement.getString("Title");

			    //Makes user input not case-sensitive
			    if (name.toLowerCase().equals(artistName.toLowerCase()))
          {
              System.out.println( "------ ArtistID: " + arId + "------  ");

			    	//Displays the results of the query
            System.out.println("Album ID: " + alId);
            System.out.println("Album Title: " + title);
				    System.out.println();
				    count++;
			    }
			}
			//if there are invalid input, display error message
			if (count == 0)
      {
        System.out.println("\nNo records found.\n");
      }
		}

     /* Option 2:
      * (a)Enter album title
      *After that display the track name, trackid, genre name, and
      *unitprice
      * (b)If there are records, ask user if he/she wants to purchase a track using
      *    trackid and quantity
      * (c)
      */

    else if (option == 2)
    {
			//Prompt for  album title
			System.out.print("Enter Album Title: ");
			String albumTitle = input.nextLine();
			conn_db.setAutoCommit(false);
			stmt = conn_db.createStatement();

			//Query for getting track name, trackid, genre name, and unitprice
      statement = stmt.executeQuery( "SELECT AL.AlbumId, AL.Title, T.TrackId, T.Name AS TName, T.UnitPrice, G.Name AS GName "
              			               + "FROM ALBUM AL, TRACK T, GENRE G "
              			               + "WHERE AL.AlbumId = T.AlbumId AND T.GenreId = G.GenreId; ");

			int count = 0, tracks [] = new int [100], index = 0;
			while (statement.next())
      {
			    int alId = statement.getInt("AlbumId");
			    String  trackName = statement.getString("TName");
			    int trackId = statement.getInt("TrackId");
			    tracks[index] = trackId;
			    String genreName = statement.getString("GName");
			    double unitPrice = statement.getDouble("UnitPrice");
			    String  title = statement.getString("Title");

			    //Makes user input not case sensitive
			    if (albumTitle.toLowerCase().equals(title.toLowerCase()))
          {
				    System.out.println("Track ID:" + trackId);
            System.out.println("Track Name:" + trackName);
            System.out.println("Genre Name:" + genreName);
            System.out.println("UnitPrice:" + unitPrice);
				    System.out.println();
				    count++;
				    index++;
			    }
			}
			//if there are invalid input, display error message
			if (count == 0)
      {
				System.out.print("No records found.");
			}
			else
      {
				//Asks the user if he/she would like to purchase any of the tracks
				System.out.print("Want to purchase any of the tracks? (1: yes, 2: no): ");
				int purchase = input.nextInt();
				if (purchase == 1)
        {
					System.out.print("Input Track ID: ");
					int trackID = input.nextInt();
					int inArray = 0;

					//Confirm the validity of  purchase track and quantity
					while (inArray == 0)
          {
						for (int i = 0; i < tracks.length; i++)
							if (tracks[i] != 0 && tracks[i] == trackID)
								inArray = 1;
						if (inArray != 1)
            {
							System.out.print("Error: Input Track ID is invalid\nInput Track ID: ");
							trackID = input.nextInt();
						}
					}
					System.out.print("Input quantity: ");
					int quantity = input.nextInt();
					while (quantity <= 0)
          {
						System.out.print("Error: Quantity has to be greater than 0\nInput quantity: ");
						quantity = input.nextInt();
					}
					conn_db.setAutoCommit(false);
					stmt = conn_db.createStatement();

					//Query for getting unit ptrice
					statement = stmt.executeQuery( "SELECT T.UnitPrice "
												               + "FROM TRACK T "
												               + "WHERE T.TrackId = " + trackID);
					double total = 0, unitPrice = 0;
					while ( statement.next() )
          {
						//Calculates the total
						unitPrice = statement.getDouble("UnitPrice");
						total = unitPrice * quantity;
					}
					  conn_db.setAutoCommit(false);
				    stmt = conn_db.createStatement();

				    //insert into tables
				    String sql = "INSERT INTO INVOICE (CUSTOMERID,InvoiceDate,BillingAddress,BillingCity,BillingState,BillingCountry,BillingPostalCode,Total)" +
				                   "VALUES (874, 2016-05-12, '345 Hogwarts Street', 'New York City', 'NY', 'USA', 05131, " + total + ");";
				    stmt.executeUpdate(sql);
				    stmt.close();
				    conn_db.commit();
					  conn_db.setAutoCommit(false);
				    stmt = conn_db.createStatement();

				    //insert into tables
				    sql = "INSERT INTO INVOICELINE (InvoiceId,TrackId,UnitPrice,Quantity)" +
				          "VALUES (413, " + trackID + ", " + unitPrice + ", " + quantity + ");";
				    stmt.executeUpdate(sql);
				    stmt.close();
				    conn_db.commit();
				}
			}
		}

  /* Option 3:
     * Enter customer id
     *display his/her purchase history
     */

		else if (option == 3)
    {
			//Prompt for customer id
			System.out.print("Enter Customer ID: ");
			int cutomerId = input.nextInt();
			conn_db.setAutoCommit(false);
			stmt = conn_db.createStatement();

			//Query for finding the purchase history
      statement = stmt.executeQuery( "select c.customerid, name, title, quantity, invoicedate "
                   + "From Customer c, invoice i, invoiceline il, album a, track t "
                   + "WHERE c.customerid = i.customerid and i.invoiceid = il.invoiceid "
                   + "and il.trackid = t.trackid and t.albumid = a.albumid;");

			int count = 0;

			while ( statement.next() )
      {
			    String name = statement.getString("Name");
			    String title = statement.getString("Title");
			    int quantity = statement.getInt("Quantity");
			    int Invoicedate = statement.getInt("InvoiceDate");
			    int cId = statement.getInt("CustomerId");
			    if (cId == cutomerId)
          {
				    System.out.println("Track Name: " + name);
            System.out.println("Album Title: " + title);
            System.out.println("Quantity: " + quantity);
            System.out.println("Invoice Date: " + Invoicedate);
				    System.out.println();
				    count++;
			    }
			}

			//if there are invalid input, display error message
			if (count == 0) System.out.print("No records found");
		}

    /* Option 4:
     * Enter trackid 
     *update its price
     */

		else if (option == 4)
    {
			//Prompt for trackid
		  System.out.print("Enter Track ID: ");
			int trackID = input.nextInt();

			conn_db.setAutoCommit(false);
			stmt = conn_db.createStatement();

			//Query for finding the trackid
			statement = stmt.executeQuery( "select t.unitprice "
										               + "from track t "
										               + "where trackid = " + trackID);

			//Display current price of the given trackid
			while ( statement.next())
      {
				double unitPrice = statement.getDouble("UnitPrice");
				System.out.println( "Track ID: " + trackID + ", ");
				System.out.printf("Unit Price: $%.2f \n",  unitPrice);
				System.out.println();
			}

			//User prompt for new price for the track id
			System.out.print("Enter new unit price: ");
			double newUnitPrice = input.nextDouble();

			conn_db.setAutoCommit(false);
			stmt = conn_db.createStatement();

			//Update the track price 
			String sql = "UPDATE Track "
						     + "SET UnitPrice = " + newUnitPrice + " "
						     + "WHERE TrackId = " + trackID;
			stmt.executeUpdate(sql);
			conn_db.commit();
			conn_db.setAutoCommit(false);

			//Show the updated unitprice of trackid
			statement = stmt.executeQuery( "select t.unitprice "
										+ "From track t "
										+ "where trackid = " + trackID);
			while ( statement.next() )
      {
				double unitPrice = statement.getDouble("UnitPrice");
				System.out.print( "UPDATE: Track ID: " + trackID + ", ");
				System.out.printf("Unit Price: $%.2f \n",  unitPrice);
				System.out.println();
			}
		}

    // Option 5:

		else if (option == 5)
    {
			//Prompt the user for a percentage
		    System.out.print("Enter percentage (-100 to 100): ");
		    double percentage = input.nextDouble();
		    //Check to see if user input is valid
		    while(percentage > 100 || percentage < -100){
		    	System.out.println("Error: Percentage must be between -100 to 100 ");
		    	System.out.print("Enter percentage (-100 to 100): ");
				percentage = input.nextDouble();
		    }
			int index = 0;
			double oldPrice[] = new double[3503];
			conn_db.setAutoCommit(false);
			stmt = conn_db.createStatement();
			//Query to count how many tracks are going to be changed
			statement = stmt.executeQuery( "Select t.unitprice "
										 + "From track t");
			while ( statement.next() )
      {
			    double  unitPrice = statement.getDouble("UnitPrice");
			    oldPrice[index] = unitPrice;
			    //Increase counter
				index++;
			}
			conn_db.setAutoCommit(false);
		    Statement s = null;
			s = conn_db.createStatement();
			//Update all track prices by user percentage
			String sql = "UPDATE Track "
					   + "SET UnitPrice = Unitprice + " + (percentage/100 + "* Unitprice");
			s.executeUpdate(sql);
			conn_db.commit();
			conn_db.setAutoCommit(false);
			//Print out how many record were updated
			System.out.println(index + " records updated");
		}
    	System.out.println();


    }

    //Close all connections after program is terminated
    statement.close();
	  stmt.close();
	  conn_db.close();
    System.out.println("Program Terminated!");
    System.exit(0);

  }

}
