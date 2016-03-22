/**
 * Represents a book.
 */
package library;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a book.  A book must have an isbn, title, genre and genreID,
 * as well as at least one author.  These attributes do not change, thus the class will 
 * not provide mutator methods (i.e setters).  A Book that is not given a publication date 
 * will have the default of an empty string.  In order to keep author names in same format,
 * list of authors is separated into first names and last names, with the index as indication
 * of which last name belongs to which first name.
 * 
 * @author	Erika Bourque
 * @version 19/03/2016
 */
public class Book {
	
	// Instance variables
	private int isbn;
	private String title;
	private String genre;
	private List<String> authorFirstNames = new ArrayList<String>();
	private List<String> authorLastNames = new ArrayList<String>();
	private String pubDate;
	
	// Prevent instantiation without data.
	private Book() {}
	
	/**
	 * First Constructor, sets pubDate to system date.
	 * 
	 * @param isbn		the book's isbn
	 * @param title		the book's title
	 * @param genre		the book's genre
	 * @param authors	a list of the book's author(s)
	 */
	public Book(int isbn, String title, String genre, List<String> authorFirstNames, List<String> authorLastNames)
	{
		// Data validation
		if (title == null)
		{
			throw new NullPointerException("Book constructor error.  Title is null: " + title);
		}
		
		if (genre == null)
		{
			throw new NullPointerException("Book constructor error.  Genre is null: " + genre);
		}
		
		if (authorFirstNames == null)
		{
			throw new NullPointerException("Book constructor error.  Author first names is null: " + authorFirstNames);
		}
		
		if (authorFirstNames.isEmpty())
		{
			throw new NullPointerException("Book constructor error.  Author first names is empty: " + authorFirstNames);
		}
		
		if (authorLastNames == null)
		{
			throw new NullPointerException("Book constructor error.  Author last names is null: " + authorLastNames);
		}
		
		if (authorLastNames.isEmpty())
		{
			throw new NullPointerException("Book constructor error.  Author last names is empty: " + authorLastNames);
		}
		
		this.isbn = isbn;
		this.title = title;
		this.genre = genre;
		pubDate = "";
		
		// Make copy of author first names for our own purposes
		for (int i = 0; i < authorFirstNames.size(); i++)
		{
			this.authorFirstNames.add(authorFirstNames.get(i));
		}
		
		// Make copy of author last names for our own purposes
		for (int i = 0; i < authorLastNames.size(); i++)
		{
			this.authorLastNames.add(authorLastNames.get(i));
		}
	}
	
	/**
	 * Second Constructor, uses given pubDate instead of system date.
	 * 
	 * @param isbn		the book's isbn
	 * @param title		the book's title
	 * @param genre		the book's genre
	 * @param authors	a list of the book's author(s)
	 * @param pubDate	the book's publication date
	 */
	public Book(int isbn, String title, String genre, List<String> authorFirstNames, List<String> authorLastNames, String pubDate)
	{
		// Data validation
		if (title == null)
		{
			throw new NullPointerException("Book constructor error.  Title is null: " + title);
		}
		
		if (genre == null)
		{
			throw new NullPointerException("Book constructor error.  Genre is null: " + genre);
		}
		
		if (authorFirstNames == null)
		{
			throw new NullPointerException("Book constructor error.  Author first names is null: " + authorFirstNames);
		}
		
		if (authorFirstNames.isEmpty())
		{
			throw new NullPointerException("Book constructor error.  Author first names is empty: " + authorFirstNames);
		}
		
		if (authorLastNames == null)
		{
			throw new NullPointerException("Book constructor error.  Author last names is null: " + authorLastNames);
		}
		
		if (authorLastNames.isEmpty())
		{
			throw new NullPointerException("Book constructor error.  Author last names is empty: " + authorLastNames);
		}
		
		if (pubDate == null)
		{
			throw new NullPointerException("Book constructor error.  Publication date is null: " + pubDate);
		}
		
		this.isbn = isbn;
		this.title = title;
		this.genre = genre;
		this.pubDate = pubDate;
		
		// Make copy of author first names for our own purposes
		for (int i = 0; i < authorFirstNames.size(); i++)
		{
			this.authorFirstNames.add(authorFirstNames.get(i));
		}
		
		// Make copy of author last names for our own purposes
		for (int i = 0; i < authorLastNames.size(); i++)
		{
			this.authorLastNames.add(authorLastNames.get(i));
		}
	}
	
	/**
	 * Returns the book's isbn.
	 * 
	 * @return	the isbn
	 */
	public int getISBN()
	{
		return isbn;
	}

	/**
	 * Returns the book's title.
	 * 
	 * @return	the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Returns the book's genre.
	 * 
	 * @return	the genre
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * Returns a copy of the book's authors.
	 * Authors are in the format of "lastname, firstname"
	 * 
	 * @return	the authors
	 */
	public List<String> getAuthors() {
		List<String> copy = new ArrayList<String>();
		
		for (int i = 0; i < authorFirstNames.size(); i++)
		{
			copy.add(authorLastNames.get(i) + ", " + authorFirstNames.get(i));
		}
		
		return copy;
	}

	/**
	 * Returns the book's publication date.
	 * 
	 * @return	the publication date
	 */
	public String getPubDate() {
		return pubDate;
	}
	
	public String toString()
	{
		String temp = isbn + ", " + title + ", ";
		
		for (int i = 0; i < authorFirstNames.size(); i++)
		{
			temp = temp + authorFirstNames.get(i) + authorLastNames.get(i) + ", ";
		}
		
		temp = temp + genre + ", " + pubDate;
		
		return temp;
		
	}
}
