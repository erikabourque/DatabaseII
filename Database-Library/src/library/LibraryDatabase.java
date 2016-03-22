/**
 * Represents a Library's Database.
 */
package library;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// already fixed closing bufferedreader
/**
 * This class represents a library's database.
 * 
 * @author Erika Bourque
 * @version	19/03/2016
 */
public class LibraryDatabase {

	String userName = "CS1141669";
	String password = "biamsepi";
	String serverName = "waldo2.dawsoncollege.qc.ca";
	String portNumber = "3306";
	
	public LibraryDatabase()
	{
	}
	
	/**
	 * Provides a connection to the waldo2 server.
	 * 
	 * @return
	 * @throws SQLException
	 */
	private Connection getConnection() throws SQLException
	{
		Connection connection = null;
		String user = this.userName;
		String password = this.password;
		
		connection = DriverManager.getConnection("jdbc:mysql://" + this.serverName + ":" + this.portNumber + "/" + user, user, password);

		return connection;				
	}
	
	/**
	 * Accesses the database to retrieve the book 
	 * associated to given ISBN.
	 * 
	 * @param isbn		the ISBN of the desired book
	 * @return			the desired book
	 * @throws SQLException
	 */
	public Book getBook(int isbn) throws SQLException
	{
		Book book = null;
		String title = null;
		String pubDate = null;
		int genreID = -1;
		String genreName = null;
		List<String> authorLastNames = new ArrayList<String>();
		List<String> authorFirstNames = new ArrayList<String>();
		
		String query;
		Statement stmt = null;
		ResultSet results;
		
		try {
			Connection connection = getConnection();
			stmt = connection.createStatement();
			
			// Getting book
			query = "SELECT * FROM book WHERE isbn = " + isbn;
			results = stmt.executeQuery(query);
			
			while (results.next())
			{
				title = results.getString("book_title");
				pubDate = results.getString("publication_date");
				genreID = results.getInt("genre");
			}
			
			// Verifying there was a result
			if (title == null)
			{
				throw new IllegalArgumentException("getBook error - Book does not exist: " + isbn);
			}
			
			// Getting genre name
			query = "SELECT * FROM genre WHERE genre_id = " + genreID;
			results = stmt.executeQuery(query);
			
			while (results.next())
			{
				genreName = results.getString("genre_name");
			}
			
			// Getting authors
			query = "SELECT * from author WHERE author_id IN (SELECT author FROM book_authors WHERE book = " + isbn + ")";
			results = stmt.executeQuery(query);
			
			while (results.next())
			{
				authorLastNames.add(results.getString("lastname"));
				authorFirstNames.add(results.getString("firstname"));
			}
			
			// Creating book object
			if (pubDate == null)
			{
				book = new Book(isbn, title, genreName, authorFirstNames, authorLastNames);
			}
			else
			{
				book = new Book(isbn, title, genreName, authorFirstNames, authorLastNames, pubDate);
			}
			
			return book;			
		} 
		catch (SQLException sqle) 
		{
			System.out.println(sqle);
			return null;
		} 
		finally 
		{
			if (stmt != null)
			{
				stmt.close();
			}
		}
	}

	/**
	 * Prints a list detailing all the books in the
	 * database.
	 * 
	 * @throws SQLException
	 */
	public void bookReport() throws SQLException
	{
		List<Integer> isbn = new ArrayList<Integer>();
		List<String> title = new ArrayList<String>();
		List<String> pubDate = new ArrayList<String>();
		List<Integer> genreID = new ArrayList<Integer>();
		String genreName = null;
		List<String> authors;
		
		String query;
		Statement stmt = null;
		ResultSet results;
		
		// Print first line
		System.out.printf("%-11s %-30s %-30s %-20s %-20s\n", "ISBN", "Title", "Author", "Publication Date", "Genre");
		
		try {
			Connection connection = getConnection();
			stmt = connection.createStatement();
			
			// Getting all the books
			query = "SELECT * FROM book";
			results = stmt.executeQuery(query);
			
			while (results.next())
			{
				// Saving all the information from book table
				// Book information for one book is all at the same index.
				isbn.add(results.getInt("isbn"));
				title.add(results.getString("book_title"));
				pubDate.add(results.getString("publication_date"));
				genreID.add(results.getInt("genre"));				
			}
			
			// Looping through for each book
			for (int i = 0; i < isbn.size(); i++)
			{
				int numAdditionalLines;
				
				// Starting new list for authors
				authors = new ArrayList<String>();
				
				// Transforming title for printing on multiple lines if required
				String[] titleSplit = title.get(i).split(" ");
				List<String> newTitle = new ArrayList<String>();
				String partialTitle = titleSplit[0];

				// Already added first word
				for (int j = 1; j < titleSplit.length; j++)
				{
					String temp = partialTitle + " " + titleSplit[j];
					
					// Check if partialTitle would be too long with next word
					if (temp.length() > 28)
					{
						// Add it to newTitle list without adding next word
						newTitle.add(partialTitle);
						partialTitle = titleSplit[j];
					}
					else
					{
						// Keep addition of word
						partialTitle = temp;
					}
				}
				// Add last partial title to list after loop ends
				newTitle.add(partialTitle);
				
				// Getting genre name from genre table
				query = "SELECT * FROM genre WHERE genre_id = " + genreID.get(i);
				results = stmt.executeQuery(query);
				
				while (results.next())
				{
					genreName = results.getString("genre_name");
				}
				
				// Getting authors from book_authors and author tables, ordered by last name
				query = "SELECT * from author WHERE author_id IN (SELECT author FROM book_authors WHERE book = " + isbn.get(i) + ") ORDER BY lastname";
				results = stmt.executeQuery(query);

				while (results.next())
				{
					authors.add(results.getString("lastname") + ", " + results.getString("firstname"));
				}				
				
				// Printing, first author and first part of title
				System.out.printf("%-11s %-30s %-30s %-20s %-20s\n", isbn.get(i), newTitle.get(0), authors.get(0), pubDate.get(i), genreName);
				
				// Compare lengths of newTitle and authors, determines biggest number of additional lines
				if (newTitle.size() >= authors.size())
				{
					numAdditionalLines = newTitle.size();
				}
				else
				{
					numAdditionalLines = authors.size();
				}
				
				// Printing any missing title words and/or other authors, index starts at 1 since already printed first
				// If both title and author are of size 1, for loop is skipped.
				for (int j = 1; j < numAdditionalLines; j++)
				{
					// Both title and author need additional lines
					if ((j < newTitle.size()) && (j < authors.size()))
					{
						System.out.printf("%-11s %-30s %-30s %-20s %-20s\n", "", newTitle.get(j), authors.get(j), "", "");
					}  // If only title needs additional lines
					else if (j < newTitle.size())
					{
						System.out.printf("%-11s %-30s %-30s %-20s %-20s\n", "", newTitle.get(j), "", "", "");
					}  // If only author needs additional lines
					else if (j < authors.size())
					{
						System.out.printf("%-11s %-30s %-30s %-20s %-20s\n", "", "", authors.get(j), "", "");
					}
					
				}
				
				// Add extra line to keep clarity
				System.out.println();
			}
		} 
		catch (SQLException sqle) 
		{
			System.out.println(sqle);
		} 
		finally 
		{
			if (stmt != null)
			{
				stmt.close();
			}
		}
	}

	/**
	 * Creates a new Patron in the database using
	 * information given through keyboard input.
	 * 
	 * @throws SQLException
	 * @throws IOException 
	 */
	public void newPatron() throws SQLException, IOException
	{
		String fname;
		String lname;
		String email;
		
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		Connection connection = null;
		String query;
		Statement stmt = null;
		ResultSet results;
		PreparedStatement prepStat = null;
		
		try
		{
			System.out.println("Creating a new Patron.");
			
			// Asking for fields, validating where required
			System.out.print("First name: ");
			fname = input.readLine();
			if (fname.isEmpty())
			{
				throw new IllegalArgumentException("newPatron error - first name is empty.  New patron not created.");
			}			
			
			System.out.print("Last name: ");
			lname = input.readLine();
			if (lname.isEmpty())
			{
				throw new IllegalArgumentException("newPatron error - last name is empty.  New patron not created.");
			}
			
			System.out.print("Optional email, press enter to decline: ");
			email = input.readLine();
			// Since email is optional, no need to verify if it is empty.
						
			// Checking if patron already exists with same first name and last name
			connection = getConnection();
			stmt = connection.createStatement();
			
			query = "SELECT * FROM patron WHERE firstname = '" + fname + "' AND lastname = '" + lname + "'";
			results = stmt.executeQuery(query);
			
			// If results is empty, it will not go in the loop.
			while (results.next())
			{
				throw new IllegalArgumentException("newPatron error - Patron with same name already exists: " + fname + " " + lname);
			}
			
			// Adding the patron to the database
			// Insert query is one statement, no need for transactions.			
			prepStat = connection.prepareStatement("INSERT INTO patron (firstname, lastname, fees, email) VALUES (?, ?, 0, ?)");
			prepStat.setString(1, fname);
			prepStat.setString(2, lname);
			prepStat.setString(3, email);			
			prepStat.executeUpdate();
			System.out.println("New Patron successfully created.");
		}
		catch (IOException ioe)
		{
			System.out.println(ioe);
		} 
		catch (SQLException sqle) 
		{			
			System.out.println(sqle);
		}
		finally
		{
			if (stmt != null)
			{
				stmt.close();
			}
			if (prepStat != null)
			{
				prepStat.close();
			}
			if (input != null)
			{
				input.close();
			}
		}		
	}

	/**
	 * Creates a new Book in the database using
	 * information given through keyboard input.
	 * 
	 * @throws SQLException
	 * @throws IOException 
	 */
	public void newBook() throws SQLException, IOException
	{
		int isbn;
		int genreID = 0;
		int authorID = 0;
		String title;		
		String genre;
		String dateStr;
		String authorAnswer;
		String fname;
		String lname;
		List<String> authorsFirstName = new ArrayList<String>();
		List<String> authorsLastName = new ArrayList<String>();
		boolean moreAuthors = true;
		Date sqlDate;
		
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		Connection connection = null;
		String query;
		Statement stmt = null;
		ResultSet results;
		PreparedStatement prepStat = null;
		
		try
		{
			System.out.println("Creating a new Book");
			
			// Asking for fields
			System.out.print("ISBN: ");
			isbn = Integer.parseInt(input.readLine());
			
			// Verifying if book already exists with same ISBN
			connection = getConnection();
			stmt = connection.createStatement();
			
			query = "SELECT * FROM book WHERE isbn = " + isbn;
			results = stmt.executeQuery(query);
			
			// Loop will be skipped if results is empty
			while (results.next())
			{
				throw new IllegalArgumentException("newBook error - Book with specified isbn already exists: " + isbn);
			}
			
			System.out.print("Title: ");
			title = input.readLine();
			if (title.isEmpty())
			{
				throw new IllegalArgumentException("newBook error - Title is empty.  New book not created.");
			}
			
			// Entering multiple authors if requested			
			// Loop until no more authors.
			while (moreAuthors)
			{
				System.out.print("Author First Name: ");
				fname = input.readLine();
				if (fname.isEmpty())
				{
					throw new IllegalArgumentException("newBook error - Author first name is empty.  New book not created.");
				}
				authorsFirstName.add(fname);
				
				System.out.print("Author Last Name: ");
				lname = input.readLine();
				if (lname.isEmpty())
				{
					throw new IllegalArgumentException("newBook error - Author last name is empty.  New book not created.");
				}
				authorsLastName.add(lname);
				
				System.out.print("More authors? Enter y for yes, n for no: ");
				authorAnswer = input.readLine();
				
				if (authorAnswer.toLowerCase().equals("n"))
				{
					moreAuthors = false;
				}
				else if (authorAnswer.toLowerCase().equals("y"))
				{
					moreAuthors = true;
				}
				else
				{
					throw new IllegalArgumentException("Invalid answer entered: " + authorAnswer + "\nNew book not created.");
				}
			}
			
			// Asking for remaining fields
			System.out.print("Genre: ");
			genre = input.readLine();
			if (genre.isEmpty())
			{
				throw new IllegalArgumentException("newBook error - Genre is empty.  New book not created.");
			}
			
			System.out.print("Optional Publication Date, Format: YYYY-MM-DD, press enter to decline: ");
			dateStr = input.readLine();
			if (dateStr.isEmpty())
			{
				sqlDate = null;
			}
			else
			{
				// Turning string into sql.Date object
				// If parsing fails, utilDate will be null, which should be kept as null throughout.
				java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
				if (utilDate == null)
				{
					sqlDate = null;
				}
				else
				{
					sqlDate = new Date(utilDate.getTime());
				}
			}
			
			// Making the connection not auto-commit, in order to prevent data loss.
			// New entries in genre, book, author, and book author for the new book
			// shall be committed as one transaction.
			connection.setAutoCommit(false);
			
			// Verifying if genre already exists
			prepStat = connection.prepareStatement("SELECT * FROM genre WHERE genre_name = ?");
			prepStat.setString(1, genre);
			results = prepStat.executeQuery();
			
			while (results.next())
			{
				genreID = results.getInt("genre_id");
			}
			
			// Create new genre if it doesn't exist, retrieve its new id
			if (genreID == 0)
			{
				prepStat = connection.prepareStatement("INSERT INTO genre (genre_name) VALUES (?)");
				prepStat.setString(1, genre);
				prepStat.executeUpdate();
				
				query = "SELECT * FROM genre WHERE genre_name = '" + genre + "'";
				results = stmt.executeQuery(query);
				
				while (results.next())
				{
					genreID = results.getInt("genre_id");
				}
			}

			// Creating the new book entry
			prepStat = connection.prepareStatement("INSERT INTO book (isbn, book_title, genre, publication_date) VALUES (?, ?, ?, ?)");
			prepStat.setInt(1, isbn);
			prepStat.setString(2, title);
			prepStat.setInt(3, genreID);
			prepStat.setDate(4, sqlDate);
			prepStat.executeUpdate();
			
			// For each author specified, check if it exists
			// Create author if it does not exist
			// Add authorID and ISBN to book_authors table
			for(int i = 0; i < authorsFirstName.size(); i++)
			{
				authorID = 0;
				
				query = "SELECT * FROM author WHERE firstname = '" + authorsFirstName.get(i) 
						+ "' AND lastname = '" + authorsLastName.get(i) + "'";
				results = stmt.executeQuery(query);
				
				while (results.next())
				{
					authorID = results.getInt("author_id");					
				}
				
				// Create new author if it doesn't exist, retrieve its new id
				if (authorID == 0)
				{
					prepStat = connection.prepareStatement("INSERT INTO author (firstname, lastname) VALUES (?, ?)");
					prepStat.setString(1, authorsFirstName.get(i));
					prepStat.setString(2, authorsLastName.get(i));
					prepStat.executeUpdate();
					
					query = "SELECT * FROM author WHERE firstname = '" + authorsFirstName.get(i) 
							+ "' AND lastname = '" + authorsLastName.get(i) + "'";
					results = stmt.executeQuery(query);
					
					while (results.next())
					{
						authorID = results.getInt("author_id");
					}
				}
				
				// Creating the new book_authors entry
				prepStat = connection.prepareStatement("INSERT INTO book_authors (book, author) VALUES (?, ?)");
				prepStat.setInt(1, isbn);
				prepStat.setInt(2, authorID);
				prepStat.executeUpdate();
			}
			
			// Genre, book, author, and book_authors have all been completed now
			connection.commit();
			System.out.println("New book successfully created.");
		}
		catch (IOException ioe)
		{
			System.out.println(ioe);
		} 
		catch (SQLException sqle) 
		{			
			connection.rollback();
			System.out.println(sqle);
		} 
		catch (ParseException e) 
		{
			System.out.println(e);
		}
		finally
		{
			if (stmt != null)
			{
				stmt.close();
			}
			if (prepStat != null)
			{
				prepStat.close();
			}
			if (input != null)
			{
				input.close();
			}
		}
	}

	/**
	 * Creates a new Book in the database
	 * using the Book object given in the
	 * parameters.
	 * 
	 * @param book		The book to add to the databse.
	 * @throws SQLException
	 */
	public void newBook(Book book) throws SQLException
	{
		// Validate book is not null.
		if (book == null)
		{
			throw new NullPointerException("newBook error - Given book is null.");
		}
		
		int isbn = book.getISBN();
		String title = book.getTitle();	
		String genre = book.getGenre();
		String dateStr = book.getPubDate();
		List<String> authorCompleteNames = book.getAuthors();
		
		List<String> authorsFirstName = new ArrayList<String>();
		List<String> authorsLastName = new ArrayList<String>();		
		int genreID = 0;
		int authorID = 0;
		Date sqlDate;
		
		Connection connection = null;
		String query;
		Statement stmt = null;
		ResultSet results;
		PreparedStatement prepStat = null;
		
		try
		{
			System.out.println("Creating a new Book using a book object.");
						
			// Verifying if book already exists with same isbn
			connection = getConnection();
			stmt = connection.createStatement();
			
			query = "SELECT * FROM book WHERE isbn = " + isbn;
			results = stmt.executeQuery(query);
			
			// Loop will be skipped if results is empty
			while (results.next())
			{
				throw new IllegalArgumentException("newBook error - Book with specified isbn already exists: " + isbn);
			}
			
			// Turning dateStr into an sql.Date object.
			if (dateStr.isEmpty())
			{
				sqlDate = null;
			}
			else
			{
				// If parsing fails, utilDate will be null, which should be kept as null throughout.
				java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
				if (utilDate == null)
				{
					sqlDate = null;
				}
				else
				{
					sqlDate = new Date(utilDate.getTime());
				}
			}
						
			// Splitting authorCompleteNames into first names and last names
			for (int i = 0; i < authorCompleteNames.size(); i++)
			{
				String[] temp = authorCompleteNames.get(i).split(", ");
				
				authorsLastName.add(temp[0]);
				authorsFirstName.add(temp[1]);
			}
			
			// Making the connection not auto-commit, in order to prevent data loss.
			// New entries in genre, book, author, and book author for the new book
			// shall be committed as one transaction.
			connection.setAutoCommit(false);
			
			// Verifying if genre already exists
			prepStat = connection.prepareStatement("SELECT * FROM genre WHERE genre_name = ?");
			prepStat.setString(1, genre);
			results = prepStat.executeQuery();
			
			while (results.next())
			{
				genreID = results.getInt("genre_id");
			}
			
			// Create new genre if it doesn't exist, retrieve its new id
			if (genreID == 0)
			{
				prepStat = connection.prepareStatement("INSERT INTO genre (genre_name) VALUES (?)");
				prepStat.setString(1, genre);
				prepStat.executeUpdate();
				
				query = "SELECT * FROM genre WHERE genre_name = '" + genre + "'";
				results = stmt.executeQuery(query);
				
				while (results.next())
				{
					genreID = results.getInt("genre_id");
				}
			}

			// Creating the new book entry
			prepStat = connection.prepareStatement("INSERT INTO book (isbn, book_title, genre, publication_date) VALUES (?, ?, ?, ?)");
			prepStat.setInt(1, isbn);
			prepStat.setString(2, title);
			prepStat.setInt(3, genreID);
			prepStat.setDate(4, sqlDate);
			prepStat.executeUpdate();
			
			// For each author specified, check if it exists
			// Create author if it does not exist
			// Add authorID and ISBN to book_authors table
			for(int i = 0; i < authorsFirstName.size(); i++)
			{
				authorID = 0;
				
				query = "SELECT * FROM author WHERE firstname = '" + authorsFirstName.get(i) 
						+ "' AND lastname = '" + authorsLastName.get(i) + "'";
				results = stmt.executeQuery(query);
				
				while (results.next())
				{
					authorID = results.getInt("author_id");					
				}
				
				// Create new author if it doesn't exist, retrieve its new id
				if (authorID == 0)
				{
					prepStat = connection.prepareStatement("INSERT INTO author (firstname, lastname) VALUES (?, ?)");
					prepStat.setString(1, authorsFirstName.get(i));
					prepStat.setString(2, authorsLastName.get(i));
					prepStat.executeUpdate();
					
					query = "SELECT * FROM author WHERE firstname = '" + authorsFirstName.get(i) 
							+ "' AND lastname = '" + authorsLastName.get(i) + "'";
					results = stmt.executeQuery(query);
					
					while (results.next())
					{
						authorID = results.getInt("author_id");
					}
				}
				
				// Creating the new book_authors entry
				prepStat = connection.prepareStatement("INSERT INTO book_authors (book, author) VALUES (?, ?)");
				prepStat.setInt(1, isbn);
				prepStat.setInt(2, authorID);
				prepStat.executeUpdate();
			}
			
			// Genre, book, author, and book_authors have all been completed now
			connection.commit();
			System.out.println("Successfully created a new book using given book object.");
		} 
		catch (SQLException sqle) 
		{			
			connection.rollback();
			System.out.println(sqle);
		} 
		catch (ParseException e) 
		{
			System.out.println(e);
		}
		finally
		{
			if (stmt != null)
			{
				stmt.close();
			}
			if (prepStat != null)
			{
				prepStat.close();
			}
		}
	}

	/**
	 * Allows an existing book in the database to be
	 * "loaned" to the patron, adding an entry in the
	 * book_loan table.  Book cannot be loaned if it
	 * was loaned and not returned.
	 * 
	 * Patron cannot be a new patron, must already exist in the database.
	 * Book cannot be a new book, must already exist in the database.
	 * 
	 * Assuming 0 means false and 1 means true in returned column.
	 * 
	 * @param book
	 * @param patron
	 * @throws SQLException 
	 */
	public void loan(Book book, Patron patron) throws SQLException
	{
		// Validate book is not null.
		if (book == null)
		{
			throw new NullPointerException("Loan error - Given book is null.");
		}
		
		// Validate patron is not null.
		if (patron == null)
		{
			throw new NullPointerException("Loan error - Given patron is null.");
		}
		
		Connection connection = null;
		String query;
		Statement stmt = null;
		ResultSet results;
		PreparedStatement prepStat = null;
				
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			
			// Verify patron exists in database
			query = "SELECT * FROM patron WHERE patron_id = " + patron.getId();
			results = stmt.executeQuery(query);
			
			// Checking if the resultset is empty
			if (!results.next())
			{
				throw new IllegalArgumentException("Loan error - Patron with specified id does not exist: " + patron.getId());
			}

			// Verify book exists in database			
			query = "SELECT * FROM book WHERE isbn = " + book.getISBN();
			results = stmt.executeQuery(query);
			
			// Checking if the resultset is empty
			if (!results.next())
			{
				throw new IllegalArgumentException("Loan error - Book with specified id does not exist: " + book.getISBN());
			}
			
			// Verifying that book has not already been loaned out.
			query = "SELECT * FROM book_loan WHERE book = " + book.getISBN() + " && returned = 0";
			results = stmt.executeQuery(query);
			
			// Checking if the resultset is not empty
			while (results.next())
			{
				throw new IllegalArgumentException("Loan error - Book with specified id has not been returned, "
						+ "loan id: " + results.getString("loan_id"));
			}
			
			System.out.println("Creating a new book loan.");
			
			// Calculating and creating due date.
			Calendar calDueDate = Calendar.getInstance();
			calDueDate.add(Calendar.DAY_OF_YEAR, 14);
			Date sqlDueDate = new Date(calDueDate.getTimeInMillis());
			
			// Adding loan is one statement, thus do not need transactions
			prepStat = connection.prepareStatement("INSERT INTO book_loan (patron_id, book, due_date, returned) VALUES (?, ?, ?, 0)");
			prepStat.setInt(1, patron.getId());
			prepStat.setInt(2, book.getISBN());
			prepStat.setDate(3, sqlDueDate);
			prepStat.executeUpdate();
			
			System.out.println("New book loan created.");
		}
		catch (SQLException sqle) 
		{			
			System.out.println(sqle);
		}
		finally
		{
			if (stmt != null)
			{
				stmt.close();
			}
			if (prepStat != null)
			{
				prepStat.close();
			}
		}
	}
	
	/**
	 * Allows a book that has been loaned out to be returned.
	 * Fees are calculated as 5 cents per day overdue.
	 * 
	 * Assuming fees is stored in cents in database.
	 * 
	 * @param book
	 * @throws SQLException 
	 */
	public void returnBook(Book book) throws SQLException
	{
		// Validate book is not null.
		if (book == null)
		{
			throw new NullPointerException("returnBook error - Given book is null.");
		}
		
		int loan_id = 0;		
		int fees = 0;
		int daysLate;
		int patron_id = 0;
		String strDueDate = null;
		Calendar currentDate = Calendar.getInstance();
		Calendar dueDate = Calendar.getInstance();
		
		Connection connection = null;
		String query;
		Statement stmt = null;
		ResultSet results;
		PreparedStatement prepStat = null;
				
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			
			// Verify book exists in book_loan table.
			query = "SELECT * FROM book_loan WHERE book = " + book.getISBN() + " && returned = 0";
			results = stmt.executeQuery(query);
			
			while (results.next())
			{
				loan_id = results.getInt("loan_id");
				patron_id = results.getInt("patron_id");
				strDueDate = results.getString("due_date");
			}
			
			// Checking if the loan_id exists
			if (loan_id == 0)
			{
				throw new IllegalArgumentException("Loan error - Book with specified ISBN has not been loaned out: " + book.getISBN());
			}
			
			System.out.println("Returning the specified book.");
			
			// Getting existing fees for patron.
			query = "SELECT * FROM patron WHERE patron_id = " + patron_id;
			results = stmt.executeQuery(query);
			
			while (results.next())
			{
				fees = results.getInt("fees");
			}
			
			// Calculating fees.
			dueDate.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(strDueDate));
			
			// Check if book is late
			if (currentDate.after(dueDate))
			{				
				// Checking if same year.
				if (currentDate.get(Calendar.YEAR) != dueDate.get(Calendar.YEAR))
				{
					// Getting remaining days in first year
					daysLate = 365 - dueDate.get(Calendar.DAY_OF_YEAR);
					
					// Adding any years in between if more than 1
					daysLate = daysLate + (365 * (currentDate.get(Calendar.YEAR) - dueDate.get(Calendar.YEAR) - 1));
					
					// Getting num days past in current year
					daysLate = daysLate + currentDate.get(Calendar.DAY_OF_YEAR);
				}
				else
				{
					daysLate = currentDate.get(Calendar.DAY_OF_YEAR) - dueDate.get(Calendar.DAY_OF_YEAR);
				}
				
				fees = fees + (daysLate * 5);
			}			
			
			// Using transactions, updating book_loan and fees for patron.
			connection.setAutoCommit(false);
			
			// Returning book.
			prepStat = connection.prepareStatement("UPDATE book_loan SET returned = 1 WHERE loan_id = ?");
			prepStat.setInt(1, loan_id);
			prepStat.executeUpdate();
			
			// Updating patron fees
			prepStat = connection.prepareStatement("UPDATE patron SET fees = ? WHERE patron_id = ?");
			prepStat.setInt(1, fees);
			prepStat.setInt(2, patron_id);
			prepStat.executeUpdate();
			
			connection.commit();
			System.out.println("Book has been returned.  Outstanding fees: " + fees);
		}
		catch (SQLException sqle) 
		{			
			connection.rollback();
			System.out.println(sqle);
		} 
		catch (ParseException e) 
		{
			System.out.println(e);
		}
		finally
		{
			if (stmt != null)
			{
				stmt.close();
			}
			if (prepStat != null)
			{
				prepStat.close();
			}
		}
	}
	
	/**
	 * Allows a patron to renew all their loaned books, and calculates
	 * their late fees.
	 * 
	 * Assuming 0 means false and 1 means true in returned column.
	 * 
	 * @param patron
	 * @throws SQLException
	 */
	public void renewBooks(Patron patron) throws SQLException
	{	
		// Validate patron is not null.
		if (patron == null)
		{
			throw new NullPointerException("Loan error - Given patron is null.");
		}
		
		int fees = -1;
		int daysLate;
		int patron_id = patron.getId();
		String strDueDate = null;
		Calendar currentDate = Calendar.getInstance();
		Calendar dueDate = Calendar.getInstance();
		
		Connection connection = null;
		String query;
		Statement stmt = null;
		ResultSet results;
		
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			
			// Verify patron exists in database.
			query = "SELECT * FROM patron WHERE patron_id = " + patron_id;
			results = stmt.executeQuery(query);
			
			while (results.next())
			{
				fees = results.getInt("fees");
			}
			
			// Checking if the loan_id exists
			if (fees == -1)
			{
				throw new IllegalArgumentException("renewBooks error - specified Patron does not exist: " + patron_id);
			}
			
			System.out.println("Renewing all loaned books.");
			
			// Calculating and creating new due date.
			Calendar newDueDate = Calendar.getInstance();
			newDueDate.add(Calendar.DAY_OF_YEAR, 14);
			Date sqlNewDueDate = new Date(newDueDate.getTimeInMillis());
			
			// Using transactions to prevent data loss during looping.
			// Also, updating book_loan table, and patron table.
			connection.setAutoCommit(false);
			
			// Using scrollable, updateable resultset.
			stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);			
			query = "SELECT * FROM book_loan WHERE patron_id = " + patron_id + " AND returned = 0";
			ResultSet updateableResults = stmt.executeQuery(query);
			
			while (updateableResults.next())
			{
				strDueDate = updateableResults.getString("due_date");
				
				// Calculating fees.
				dueDate.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(strDueDate));
				
				// Check if book is late
				if (currentDate.after(dueDate))
				{				
					// Checking if same year.
					if (currentDate.get(Calendar.YEAR) != dueDate.get(Calendar.YEAR))
					{
						// Getting remaining days in first year
						daysLate = 365 - dueDate.get(Calendar.DAY_OF_YEAR);
						
						// Adding any years in between if more than 1
						daysLate = daysLate + (365 * (currentDate.get(Calendar.YEAR) - dueDate.get(Calendar.YEAR) - 1));
						
						// Getting num days past in current year
						daysLate = daysLate + currentDate.get(Calendar.DAY_OF_YEAR);
					}
					else
					{
						daysLate = currentDate.get(Calendar.DAY_OF_YEAR) - dueDate.get(Calendar.DAY_OF_YEAR);
					}
					
					fees = fees + (daysLate * 5);
				}
				
				// Updating due date
				updateableResults.updateDate("due_date", sqlNewDueDate);
				updateableResults.updateRow();
			}
			
			// All fees have been added, updating Patron.
			query = "UPDATE patron SET fees = " + fees + " WHERE patron_id = " + patron_id;
			stmt = connection.createStatement();
			stmt.executeUpdate(query);
			
			connection.commit();
			System.out.println("Books have been renewed.  Outstanding fees: " + fees);
		}
		catch (SQLException sqle) 
		{			
			connection.rollback();
			System.out.println(sqle);
		} 
		catch (ParseException e) 
		{
			System.out.println(e);
		}
		finally
		{
			if (stmt != null)
			{
				stmt.close();
			}
		}
	}

	public List<Book> recommendBooks(Patron patron) throws SQLException
	{
		// Validate patron is not null.
		if (patron == null)
		{
			throw new NullPointerException("Loan error - Given patron is null.");
		}
		
		int patronID = patron.getId();
		List<Integer> favoriteGenresID = new ArrayList<Integer>();
		List<Book> list = new ArrayList<Book>();
		List<Integer> genreISBNs = new ArrayList<Integer>();
		
		Connection connection = null;
		String query;
		Statement stmt = null;
		PreparedStatement prepStat = null;
		ResultSet results;
		
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			
			// Verify patron exists in database.
			query = "SELECT * FROM patron WHERE patron_id = " + patronID;
			results = stmt.executeQuery(query);

			// Checking resultset is empty
			if (!results.next())
			{
				throw new IllegalArgumentException("recommendBooks error - specified Patron does not exist: " + patronID);
			}
			
			System.out.println("Searching for books to recommend.");

			// Getting the genreIDs most often borrowed by patron
			prepStat = connection.prepareStatement("SELECT genre FROM "
					+ "(SELECT * FROM book INNER JOIN book_loan "
					+ "ON book.isbn = book_loan.book WHERE patron_id = ?) AS temp "
					+ "HAVING COUNT(genre) = "
					+ "(SELECT MAX(counted) FROM "
					+ "(SELECT Count(genre) as counted FROM "
					+ "(SELECT * FROM book INNER JOIN book_loan "
					+ "ON book.isbn = book_loan.book WHERE patron_id = ?) AS temp2) "
					+ "AS temp3)");
			prepStat.setInt(1, patronID);
			prepStat.setInt(2, patronID);
			results = prepStat.executeQuery();
			
			while (results.next())
			{
				favoriteGenresID.add(results.getInt("genre"));
			}
			
			if (favoriteGenresID.size() == 0)
			{
				throw new IllegalArgumentException("recommendBooks error - Patron has not taken out any books.");
			}
			
			// Preparing query for loop
			prepStat = connection.prepareStatement("SELECT isbn FROM book WHERE genre = ?");
			
			// Getting all the isbns in the genre(s)
			for (int i = 0; i < favoriteGenresID.size(); i++)
			{
				prepStat.setInt(1, favoriteGenresID.get(i));				
				results = prepStat.executeQuery();
				
				while (results.next())
				{
					genreISBNs.add(results.getInt("isbn"));
				}
			}
			
			// Preparing query for loop again
			prepStat = connection.prepareStatement("SELECT * FROM book_loan WHERE book = ? AND (returned = 0 OR patron_id = ?)");
			
			// Checking to make each book has not already been loaned
			// to same patron, or is loaned out currently.
			for (int i = 0; i < genreISBNs.size(); i++)
			{
				prepStat.setInt(1, genreISBNs.get(i));
				prepStat.setInt(2, patronID);
				results = prepStat.executeQuery();
				
				// Checking if query has a result, a result means it is loaned out or already read
				if (!results.next())
				{
					// If it has no result, can recommend!
					list.add(this.getBook(genreISBNs.get(i)));
				}
			}			
			
			return list;
		}
		catch (SQLException sqle) 
		{			
			System.out.println(sqle);
		} 
		finally
		{
			if (stmt != null)
			{
				stmt.close();
			}
		}
		// Reaching here means returning the list didn't work.
		return null;
	}
}
