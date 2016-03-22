/**
 * Represents a Patron.
 */
package library;

/**
 * This class represents a Patron. A Patron must have an ID, first name,
 * last name, and fees (in cents).  If an email is not given, email will be an empty
 * String.
 * 
 * @author	Erika Bourque
 * @version 19/03/2016
 */
public class Patron {
	
	// Instance Variables
	private int id;
	private String firstName;
	private String lastName;
	private int fees;
	private String email;
	
	// Prevent Instantiation without data.
	private Patron() {}
	
	/**
	 * First Constructor, sets email to an empty string.
	 * 
	 * @param id			the patron's id
	 * @param firstName		the patron's first name
	 * @param lastName		the patron's last name
	 * @param fees			the patron's fees
	 */
	public Patron (int id, String firstName, String lastName, int fees)
	{
		// Data validation
		if (firstName == null)
		{
			throw new NullPointerException("Patron constructor error.  First name is null: " + firstName);
		}
		
		if (lastName == null)
		{
			throw new NullPointerException("Patron constructor error.  Last name is null: " + lastName);
		}
		
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.fees = fees;
		email = "";
	}
	
	/**
	 * Second Constructor, uses the given email.
	 * 
	 * @param id			the patron's id
	 * @param firstName		the patron's first name
	 * @param lastName		the patron's last name
	 * @param fees			the patron's fees
	 * @param email			the patron's email
	 */
	public Patron(int id, String firstName, String lastName, int fees, String email)
	{
		// Data validation
		if (firstName == null)
		{
			throw new NullPointerException("Patron constructor error.  First name is null: " + firstName);
		}
		
		if (lastName == null)
		{
			throw new NullPointerException("Patron constructor error.  Last name is null: " + lastName);
		}
		
		if (email == null)
		{
			throw new NullPointerException("Patron constructor error.  Email is null: " + email);
		}
		
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.fees = fees;
		this.email = email;
	}

	/**
	 * Returns the patron's id.
	 * 
	 * @return	the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the patron's first name.
	 * 
	 * @return	the first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Returns the patron's last name.
	 * 
	 * @return	the last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Returns the patron's fees.
	 * 
	 * @return	the fees
	 */
	public int getFees() {
		return fees;
	}

	/**
	 * Returns the patron's email.
	 * 
	 * @return	the email
	 */
	public String getEmail() {
		return email;
	}
}
