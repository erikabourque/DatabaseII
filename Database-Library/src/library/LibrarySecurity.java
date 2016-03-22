package library;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.sql.*;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class LibrarySecurity {
	private static SecureRandom random = new SecureRandom();
	private String dbuser = "CS1141669";
	private String dbname = "CS1141669";
	private String dbpassword = "biamsepi";
	
	
	//Takes a username and password and creates and account for that user
	public void newUser(String username, String password) throws SQLException{
		Connection connection = null;
		PreparedStatement prepStat = null;
		ResultSet results;
		
		String salt;
		byte[] hashed;
		
		try
		{
			connection = getConnection();
			
			// Checking if user already exists
			prepStat = connection.prepareStatement("SELECT * FROM users WHERE userid = ?");
			prepStat.setString(1, username);
			results = prepStat.executeQuery();
			
			// Checking to make sure results is empty.
			if (results.next())
			{
				throw new IllegalArgumentException("newUser error - Username already exists: " + username);
			}
			
			// Preparing password for storage
			salt = this.getSalt();
			hashed = this.hash(password, salt);
			
			// Insert data into users table
			// Do not need transactions as it is one statement.
			prepStat = connection.prepareStatement("INSERT INTO users (userid, salt, pwhash) VALUES (?, ?, ?)");
			prepStat.setString(1, username);
			prepStat.setString(2, salt);
			prepStat.setBytes(3, hashed);
			prepStat.executeUpdate();
		}
		catch (SQLException sqle)
		{
			System.out.println(sqle);
		}
		finally
		{
			if (prepStat != null)
			{
				prepStat.close();
			}
		}
	}
	
	
	//Prompts the user to input a username and password, and creates an account for that user.
	public void newUser() throws SQLException{
		
	}
	
	//Takes a username and password returns true if they belong to a valid user
	public boolean login(String username, String password)throws SQLException{
		Connection connection = null;
		PreparedStatement prepStat = null;
		ResultSet results;
		
		String salt = null;
		byte[] dbhashed = null;
		byte[] pwhashed = null;
		
		try
		{
			connection = getConnection();
			
			// Getting user information
			prepStat = connection.prepareStatement("SELECT * FROM users WHERE userid = ?");
			prepStat.setString(1, username);
			results = prepStat.executeQuery();
			
			while (results.next())
			{
				salt = results.getString("salt");
				dbhashed = results.getBytes("pwhash");
			}
			
			// Making sure username existed, if not returns false
			if (salt != null)
			{
				// Preparing given password for comparison
				pwhashed = this.hash(password, salt);
				
				// Comparing
				if (Arrays.equals(pwhashed, dbhashed))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		catch (SQLException sqle)
		{
			System.out.println(sqle);
		}
		finally
		{
			if (prepStat != null)
			{
				prepStat.close();
			}
		}
		return false;
	}
	
	//Prompts the user to input their login info, returns true if they are a valid user, false otherwise
	public boolean login() throws SQLException{
		return false;
	}
	
	
	//Feel free to use a main method to test your code!
	public static void main(String[] args) throws SQLException{
		LibrarySecurity ls = new LibrarySecurity();
		
	}
	
	
	//Helper Functions below:
	//getConnection() - obtains a connection
	//getSalt() - creates a randomly generated string 
	//hash() - takes a password and a salt as input and then computes their hash
	
	//Creates a connection to the database using the information given by the user
	private Connection getConnection() throws SQLException{
		Connection c = null;
		c = DriverManager.getConnection("jdbc:mysql://waldo2.dawsoncollege.qc.ca/" + dbname, dbuser, dbpassword);
		System.out.println("Connected to database");
		return c;
	}
	
	//Creates a randomly generated String
	public String getSalt(){
		return new BigInteger(140, random).toString(32);
	}
	
	//Takes a password and a salt a performs a one way hashing on them, returning an array of bytes.
	public byte[] hash(String password, String salt){
		try{
			SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
	        
			/*When defining the keyspec, in addition to passing in the password and salt, we also pass in
			a number of iterations (1024) and a key size (256). The number of iterations, 1024, is the
			number of times we perform our hashing function on the input. Normally, you could increase security
			further by using a different number of iterations for each user (in the same way you use a different
			salt for each user) and storing that number of iterations. Here, we just use a constant number of
			iterations. The key size is the number of bits we want in the output hash*/ 
			PBEKeySpec spec = new PBEKeySpec( password.toCharArray(), salt.getBytes(), 1024, 256 );

			SecretKey key = skf.generateSecret( spec );
	        byte[] hash = key.getEncoded( );
	        return hash;
        }catch( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException( e );
        }
	}
}
