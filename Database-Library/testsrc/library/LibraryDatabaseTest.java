package library;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LibraryDatabaseTest {

	public static void main(String[] args) {
		//testGetBook();
		//testBookReport();
		//testNewPatron();
		//testNewBook();
		//testNewBookGivenABook();		
		//testLoan();
		//testReturnBook();
		//testRenewBooks();
		testRecommendBooks();
	}
	
	private static void testGetBook()
	{
		testGetBook("Case 1: exists", true, 12345);
		testGetBook("Case 2: does not exist", false, 123456);
	}
	
	private static void testGetBook(String testcase, boolean valid, int isbn)
	{
		LibraryDatabase db = new LibraryDatabase();
		Book book = null;
		
		System.out.println(testcase);
		
		try {
			book = db.getBook(isbn);
			System.out.println("Got the book.");
			
			System.out.println(book.getISBN());
			System.out.println(book.getTitle());
			System.out.println(book.getGenre());
			System.out.println(book.getAuthors());
			System.out.println(book.getPubDate());
						
			if (!valid)
			{
				System.out.println("---Expected exception. Test failed.---");
			}
			else
			{
				System.out.println("Test passed.");
			}
		} 
		catch (Exception e) 
		{
			System.out.println("Caught exception: " + e);
			
			if (valid)
			{
				System.out.println("---Expected success. Test failed.---");
			}
			else
			{
				System.out.println("Test passed.");
			}
		}
		System.out.println();
	}
	
	// No multiple cases to test
	private static void testBookReport()
	{
		LibraryDatabase db = new LibraryDatabase();
		
		try 
		{
			db.bookReport();
			System.out.println("Test passed.");
		} 
		catch (Exception e) 
		{
			System.out.println("Caught exception: " + e);
			System.out.println("---Expected success. Test failed.---");
		}
		System.out.println();
	}
	
	// Real-time testing, cannot code test cases
	private static void testNewPatron()
	{
		LibraryDatabase db = new LibraryDatabase();
		
		try 
		{
			db.newPatron();
		} 
		catch (Exception e) 
		{
			System.out.println("Caught exception: " + e);
		}
		System.out.println();
	}
	
	// Real-time testing, cannot code test cases
	private static void testNewBook()
	{
		LibraryDatabase db = new LibraryDatabase();
		
		try 
		{
			db.newBook();
		} 
		catch (Exception e) 
		{
			System.out.println("Caught exception: " + e);
		}
		System.out.println();
	}
	
	// Note - running test more than once without changes will cause unexpected 
	// errors as "new" books will have been already added.
	// Change isbn for each test case required, or change boolean to false
	private static void testNewBookGivenABook()
	{
		List<String> fname = new ArrayList<String>();
		fname.add("Erika");		
		List<String> lname = new ArrayList<String>();
		lname.add("Tester");		
		
		Book book1 = new Book(1, "Testing Creating New Book Given A Book Object", 
				"nonfiction", fname, lname, "1994-01-01");
		Book book3 = new Book(3, "Testing Creating New Book Given A Book Object", 
				"nonfiction", fname, lname, "");
		Book book4 = new Book(4, "Testing Creating New Book Given A Book Object", 
				"autobiography", fname, lname, "1994-01-01");
		
		testNewBookGivenABook("Case 1: One Author", true, book1);
		
		fname.add("Simon");
		lname.add("Testertwo");
		Book book2 = new Book(2, "Testing Creating New Book Given A Book Object", 
				"nonfiction", fname, lname, "1994-01-01");
		
		testNewBookGivenABook("Case 2: Multiple Authors", true, book2);		
		testNewBookGivenABook("Case 3: No Date", true, book3);
		testNewBookGivenABook("Case 4: New Genre", true, book4);
		testNewBookGivenABook("Case 5: Null book", false, null);
		testNewBookGivenABook("Case 6: Same isbn as another book", false, book1);
	}
	
	private static void testNewBookGivenABook(String testcase, boolean valid, Book book)
	{
		LibraryDatabase db = new LibraryDatabase();
		
		System.out.println(testcase);
		
		try 
		{
			db.newBook(book);
			
			if (!valid)
			{
				System.out.println("---Expected exception. Test failed.---");
			}
			else
			{
				System.out.println("Test passed.");
			}
		} 
		catch (Exception e) 
		{
			System.out.println("Caught exception: " + e);
			
			if (valid)
			{
				System.out.println("---Expected success. Test failed.---");
			}
			else
			{
				System.out.println("Test passed.");
			}
		}
		System.out.println();
	}
	
	// Note - running test more than once without changing valid
	// test case will result in any error.
	private static void testLoan()
	{
		try 
		{
			LibraryDatabase db = new LibraryDatabase();
			Book testbook;
			testbook = db.getBook(1);
			
			Patron testpatron = new Patron (4, "Justin", "Trudeau", 0);
			
			List<String> fname = new ArrayList<String>();
			fname.add("Erika");		
			List<String> lname = new ArrayList<String>();
			lname.add("Tester");		
			Book bookfail = new Book(12, "I Don't Actually Exist", 
					"nonfiction", fname, lname, "1994-01-01");
			
			Patron patronfail = new Patron (7, "Erika", "Tester", 0);
			
			testLoan("Case 1: Book and Patron exist", true, testbook, testpatron);
			testLoan("Case 2: Book already taken out", false, testbook, testpatron);
			testLoan("Case 3: Book exists, Patron does not", false, testbook, patronfail);
			testLoan("Case 4: Book does not exist, Patron exists", false, bookfail, testpatron);
			testLoan("Case 5: Book and Patron do not exist", false, bookfail, patronfail);
			testLoan("Case 6: Book is null, Patron exists", false, null, testpatron);
			testLoan("Case 7: Book exists, Patron is null", false, testbook, null);
		} 
		catch (SQLException e) 
		{
			System.out.println("Cannot test: " + e);
		}		
	}
	
	private static void testLoan(String testcase, boolean valid, Book book, Patron patron)
	{
		LibraryDatabase db = new LibraryDatabase();
		
		System.out.println(testcase);
		
		try 
		{
			db.loan(book, patron);
			
			if (!valid)
			{
				System.out.println("---Expected exception. Test failed.---");
			}
			else
			{
				System.out.println("Test passed.");
			}
		} 
		catch (Exception e) 
		{
			System.out.println("Caught exception: " + e);
			
			if (valid)
			{
				System.out.println("---Expected success. Test failed.---");
			}
			else
			{
				System.out.println("Test passed.");
			}
		}
		System.out.println();
	}
	
	// Note - running test more than once without changing valid
	// test case will result in any error.
	private static void testReturnBook()
	{
		try 
		{
			LibraryDatabase db = new LibraryDatabase();
			Book testbook;
			testbook = db.getBook(1);
			
			List<String> fname = new ArrayList<String>();
			fname.add("Erika");		
			List<String> lname = new ArrayList<String>();
			lname.add("Tester");		
			Book bookfail = new Book(12, "I Don't Actually Exist", 
					"nonfiction", fname, lname, "1994-01-01");
			
			testReturnBook("Case 1: Book exists and loaned", true, testbook);
			testReturnBook("Case 2: Book exists and not loaned", false, testbook);
			testReturnBook("Case 3: Book does not exist", false, bookfail);
			testReturnBook("Case 4: Book is null", false, null);
		} 
		catch (SQLException e) 
		{
			System.out.println("Cannot test: " + e);
		}
	}
	
	private static void testReturnBook(String testcase, boolean valid, Book book)
	{
		LibraryDatabase db = new LibraryDatabase();
		
		System.out.println(testcase);
		
		try 
		{
			db.returnBook(book);
			
			if (!valid)
			{
				System.out.println("---Expected exception. Test failed.---");
			}
			else
			{
				System.out.println("Test passed.");
			}
		} 
		catch (Exception e) 
		{
			System.out.println("Caught exception: " + e);
			
			if (valid)
			{
				System.out.println("---Expected success. Test failed.---");
			}
			else
			{
				System.out.println("Test passed.");
			}
		}
		System.out.println();
	}
	
	private static void testRenewBooks()
	{
		Patron testpatron1 = new Patron (1, "Justin", "Trudeau", 0);
		Patron testpatron2 = new Patron (5, "George", "Washington", 0);
		Patron patronfail = new Patron (7, "Erika", "Tester", 0);
		
		testRenewBooks("Case 1: Patron exists", true, testpatron1);
		testRenewBooks("Case 2: Patron has no books to renew", true, testpatron2);
		testRenewBooks("Case 3: Patron null", false, null);
		testRenewBooks("Case 4: Patron does not exist", false, patronfail);
	}
	
	private static void testRenewBooks(String testcase, boolean valid, Patron patron)
	{
		LibraryDatabase db = new LibraryDatabase();
		
		System.out.println(testcase);
		
		try 
		{			
			db.renewBooks(patron);
			
			if (!valid)
			{
				System.out.println("---Expected exception. Test failed.---");
			}
			else
			{
				System.out.println("Test passed.");
			}
		} 
		catch (Exception e) 
		{
			System.out.println("Caught exception: " + e);
			
			if (valid)
			{
				System.out.println("---Expected success. Test failed.---");
			}
			else
			{
				System.out.println("Test passed.");
			}
		}
		System.out.println();
	}

	private static void testRecommendBooks()
	{
		Patron testpatron1 = new Patron (1, "Joe", "Sho", 0);
		Patron testpatron2 = new Patron (5, "George", "Washington", 0);
		
		testRecommendBooks("Case 1: Valid patron", true, testpatron1);
	}
	
	private static void testRecommendBooks(String testcase, boolean valid, Patron patron)
	{
		LibraryDatabase db = new LibraryDatabase();
		List<Book> test;
		
		System.out.println(testcase);
		
		try 
		{			
			test = db.recommendBooks(patron);
			
			System.out.println(test);
			
			if (!valid)
			{
				System.out.println("---Expected exception. Test failed.---");
			}
			else
			{
				System.out.println("Test passed.");
			}
		} 
		catch (Exception e) 
		{
			System.out.println("Caught exception: " + e);
			
			if (valid)
			{
				System.out.println("---Expected success. Test failed.---");
			}
			else
			{
				System.out.println("Test passed.");
			}
		}
		System.out.println();
	}
}
