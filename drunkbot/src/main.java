import java.util.*;
import java.awt.Desktop;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;







// DropBox
import com.dropbox.core.*;

// Twitter
import twitter4j.*;

// Google Translate
import com.gtranslate.Translator;

// Wolfram
import com.wolfram.alpha.*;
// FLickr
import com.flickr4java.flickr.*;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.SearchParameters;





// POS
import opennlp.tools.postag.POSModel;
import opennlp.tools.util.InvalidFormatException;

public class main {
	// parts of speech model
	private static POSModel model;
	
	// Wolfram
	private static WAEngine engine;
	
	// Translator
	private static Translator translator;
	private static String lang;
	private static boolean transLang;
	
	// Flickr
	private static Flickr flickr;
	
	// Dictionary
	private static DictSkipList<String, String> dictionary;
	
	// Socket
	private static boolean soc;
	
	// Twitter
	private static Twitter twitter;
	
	private DictSkipList<String, String> getDict() {
		return dictionary;
	}
	// Scanner
	public static Scanner scan = new Scanner(System.in);

	public static void main(String[] args) throws InvalidFormatException, IOException, ClassNotFoundException, DbxException, TwitterException, FlickrException {

		// DropBox stuff =========================================================
		System.out.println("* Use Dropbox to get most recient dictionary?"
				+ "\n  Please note, file must already be present. <yes/no>");
		boolean dbx = scan.nextLine().toLowerCase().equals("yes");
		if (dbx){
			final String APP_KEY = "fh14a40tk1ntjpw";
			final String APP_SECRET = "yhte0plmdfq52rw";

			DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

			DbxRequestConfig config = new DbxRequestConfig(
					"DrunkBot/1.0", Locale.getDefault().toString());
			DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

			String authorizeUrl = webAuth.start();
			System.out.println("	1. Openning URL: " + authorizeUrl);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			browse(authorizeUrl);
			System.out.println("	2. Click \"Allow\" (you might have to log in first)");
			System.out.println("	3. Copy the authorization code and paste it below.");
			String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();

			DbxAuthFinish authFinish = webAuth.finish(code);
			String accessToken = authFinish.accessToken;

			DbxClient client = new DbxClient(config, accessToken);
			System.out.println("Linked account: " + client.getAccountInfo().displayName);

			FileOutputStream outputStream = new FileOutputStream("dictionary.txt");
			try {
				DbxEntry.File downloadedFile = client.getFile("/dictionary.txt", null,
						outputStream);
				System.out.println("Metadata: " + downloadedFile.toString());
			} 
			catch(NullPointerException e){
				System.out.println("Error: the file \"Dictionary.txt\" was not found in you DropBox folder."
						+ " \n	Continuing with default file.");
			}
			finally {
				outputStream.close();
			}
		}
		// =======================================================================

		// Check for sockets =====================================================
		System.out.println("* Act as client using sockets? <yes/no> *");
		soc = scan.nextLine().toLowerCase().equals("yes");
		if (soc){
			InputStream is = new FileInputStream( "en-pos-maxent.bin" );
			setModel( new POSModel( is ) ); 
		}
		// =======================================================================

		// Translation set up ====================================================
		System.out.println("* Do you wish to converse in another language? <yes/no>");
		translator = Translator.getInstance();
		transLang = scan.nextLine().toLowerCase().equals("yes");
		if(transLang){
			System.out.print("  Enter your language code (ex: en, es, fr, etc...): ");
			lang = scan.nextLine().toLowerCase();
		}
		else {
			lang = "en";
		}
		// =======================================================================

		// Wolfram initialization ================================================
		System.out.println("* Initializing Wolfram Alpha\n	-- use \"solve\" to query");
		engine = new WAEngine();
		engine.setAppID("RGEXLG-7W9LUJU47E");
		engine.addFormat("plaintext");
		// =======================================================================

		// Twitter stuff =========================================================
		System.out.println("* Connecting to Twitter\n	-- use \"ubco\" to get results");
		twitter = TwitterFactory.getSingleton();
		// =======================================================================

		// Flickr Initialization =================================================
		System.out.println("* Connecting to Flickr\n	-- use \"Show me pictures of ____\" to get results");
		String flickKey = "a3e2bec53ec7f6d6df91608400eead3f";
		String flickSec = "62e5d92f34681d70";
		flickr = new Flickr(flickKey, flickSec, new REST());
		// =======================================================================

		//////////////////////////////////////////////////////////////////////////
		//                           Main Execution                             //
		//////////////////////////////////////////////////////////////////////////

		if(soc) {

			//get the localhost IP address, if server is running on some other IP, you need to use that
			printLine("Please enter server IP address: ");
			InetAddress host = InetAddress.getByName(scan.nextLine());
			Socket socket = null;
			ObjectOutputStream oos = null;
			ObjectInputStream ois = null;
			printLine("Trying to connect to server...");
			//establish socket connection to server
			socket = new Socket(host.getHostName(), 2014);

			//write to socket using ObjectOutputStream
			oos = new ObjectOutputStream(socket.getOutputStream());


			//read the server response message
			ois = new ObjectInputStream(socket.getInputStream());
			//greet client
			System.out.println("-----------------------------------------");
			String greeting = "Heyyy, how you doin'?";
			oos.writeObject(greeting);
			printLine("CLIENT: " + greeting);

			//get input
			String input = (String) ois.readObject();
			printLine("SERVER: " + input);
			//get dictionary
			dictionary = new DictSkipList<String, String>();

			fillDictionary(dictionary);

			int counter = 0;
			String response;
			while (!input.equals("exit") && counter < 28)
			{
				response = response(input);
				oos.writeObject(response);
				printLine("CLIENT: " + response);
				input = (String) ois.readObject();
				printLine("SERVER: " + input + "\n");
				counter++;
			}
			response = "I think it is time for me to go."; 
			oos.writeObject(response);
			printLine("CLIENT: " + response + "\n");

			input = (String) ois.readObject();
			printLine("SERVER: " + input);	  
			response = "Goodbye.\n*Falls off chair*";
			oos.writeObject(response);
			printLine("CLIENT: " + response);

		}
		else {
			//greet user
			System.out.println("-----------------------------------------");
			printLine("Heyyy, how you doin'?");

			//get input
			String input = scan.nextLine();

			//return output
			//make dictionary of terms and update it

			dictionary = new DictSkipList<String, String>();

			fillDictionary(dictionary);

			int counter = 0;
			while (!input.equals("exit") && counter < 28)
			{
				System.out.println(response(input) + "\n");
				input = scan.nextLine();
				counter++;
			}
			printLine("I think it is time for me to go.");
			input = scan.nextLine();

			printLine("Goodbye... -Falls off chair-");

		}


	}

	// Browse - opens given URL in default browser ===========================
	private static void browse(String Url) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(new URI(Url));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	// =======================================================================

	// FlickPic - Displays images relating to query ==========================
	private static void flickPic(String query) throws FlickrException{
		SearchParameters searchParameters = new SearchParameters();
		searchParameters.setText(query);
		searchParameters.setSort(SearchParameters.INTERESTINGNESS_DESC);
		PhotoList<Photo> list = flickr.getPhotosInterface().search(searchParameters, 0, 0);
		if (list.toArray().length>0) {
		Photo photo = list.get(0);
		browse(photo.getUrl());
		}
		else {
			printLine("Sorry, he didn't find any pictures...");
		}
	}

	// WolframAlpha Query - returns Wolfram result for query =================
	private static void wfaQuery(String input) {
		WAQuery query = engine.createQuery();
		query.setInput(input);
		try {
			// This sends the URL to the Wolfram|Alpha server, gets the XML result
			// and parses it into an object hierarchy held by the WAQueryResult object.
			WAQueryResult queryResult = engine.performQuery(query);

			if (queryResult.isError()) {
				System.out.println("Query error");
				System.out.println("  error code: " + queryResult.getErrorCode());
				System.out.println("  error message: " + queryResult.getErrorMessage());
			} else if (!queryResult.isSuccess()) {
				System.out.println("Query was not understood; no results available.");
			} else {
				// Got a result.
				System.out.println("Successful query. Pods follow:\n");
				for (WAPod pod : queryResult.getPods()) {
					if (!pod.isError()) {
						System.out.println(pod.getTitle());
						System.out.println("------------");
						for (WASubpod subpod : pod.getSubpods()) {
							for (Object element : subpod.getContents()) {
								if (element instanceof WAPlainText) {
									System.out.println(((WAPlainText) element).getText());
									System.out.println("");
								}
							}
						}
						System.out.println("");
					}
				}
				// We ignored many other types of Wolfram|Alpha output, such as warnings, assumptions, etc.
				// These can be obtained by methods of WAQueryResult or objects deeper in the hierarchy.
			}
		} catch (WAException e) {
			e.printStackTrace();
		}
	}
	// =======================================================================

	// Custom printer for other language
	private static void printLine(String str){
		if (lang.equals("en")){
			System.out.println(str);
		}
		else {
			System.out.println(translator.translate(str, "en", lang));
		}
	}

	//fill dictionary initially with predefined values
	public static void fillDictionary(DictSkipList<String, String> dictionary) throws FileNotFoundException {
		FileInputStream in = new FileInputStream("dictionary.txt");
		Scanner fin = new Scanner(in);


		String key, value;
		//read initial dictionary from file dictionary.txt
		while (fin.hasNext())
		{
			//get values
			key = fin.nextLine();
			value = fin.nextLine();
			fin.nextLine();

			//add key & value to dictionary in lower case
			dictionary.put(key.toLowerCase(), value.toLowerCase());
		}

	}

	private static void setModel( POSModel m ) {
		model = m;
	}
	private static POSModel getModel() {
		return model;
	}

	// Generate DrunkBot response ============================================
	public static String response(String input) throws InvalidFormatException, IOException, TwitterException, FlickrException {
		// Translates to English
		if(transLang){
			input = translator.translate(input, lang, "en");
		}
		//check for dictionary, no punctuation
		if (dictionary.containsKey(input.toLowerCase()))
		{
			if (transLang){
				return translator.translate(dictionary.get(input.toLowerCase()), "en", lang);
			}
			return dictionary.get(input.toLowerCase());
		}
		if (input.toLowerCase().contains("solve")){
			printLine("What would you like to solve? I know a guy called Wolfram.");
			wfaQuery(scan.nextLine());
			return "";
		}
		if (input.toLowerCase().contains("ubco")){
			printLine("Here's what's going on at UBCO...");
			Twitter twitter = TwitterFactory.getSingleton();
			Query query = new Query("#UBCO");
			QueryResult result = twitter.search(query);
			for (Status status : result.getTweets()) {
				System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
			}
			return "";
		}
		// Check for picture request
		if (input.length()>22){
			String query;
			if (input.toLowerCase().substring(0, 20).equals("show me pictures of ")){
				query = input.toLowerCase().substring(20, input.length()-1);
				printLine("Let me hook you up with my buddy Flickr...");
				flickPic(query);
				return translator.translate("So what now?", "en", lang);
			}
			if (input.toLowerCase().substring(0, 21).equals("show me a picture of ")){
				query = input.toLowerCase().substring(21, input.length()-1);
				printLine("Let me hook you up with my buddy Flickr...");
				flickPic(query);
				return translator.translate("Alright what now?", "en", lang);
			}
		}
		//generate verb/noun response
		InputStream is = new FileInputStream( "en-pos-maxent.bin" );
		inputParser parse = new inputParser(is);

		String[] verbNoun = parse.getVerbNoun(input);
		String str = construct(verbNoun[0], verbNoun[1], input);
		if (transLang){
			return translator.translate(str, "en", lang);
		}
		return str;
	}

	//construct response sentence
	public static String construct(String verb, String noun, String input) {
		Random rand = new Random();
		int weight_max = 1000;
		int weight_min = 1;
		int div_max = 10;
		int div_min = 1;
		int weight = rand.nextInt((weight_max - weight_min) + 1) + weight_min;
		int div = rand.nextInt((div_max - div_min) + 1) + div_min;
		int modulus = weight % div;
		int num = rand.nextInt(8);
		if(verb.isEmpty() && !noun.isEmpty()) 
			return noVerb(noun, input);
		if(!verb.isEmpty() && noun.isEmpty()) 
			return noNoun(verb, input);
		if(verb.isEmpty() && noun.isEmpty())
			return noNounVerb(input);

		if(input.substring(input.length()-1).equals("?") && (modulus % 2) == 0)
			return "Who are you, comrade question?";
		else if(1 == num){ 
			return "I also like to " + verb  + " " + noun + " when I drink.";
		}
		else if(2 == num){ 
			return "Oh yeah, absolutely. What do you think of " + noun + "?";
		}
		else if(3 == num){ 
			return "Such " + noun + ". Very " + verb + ". Wow.";
		}
		else if(4 == num){ 
			return "Maybe next time I'll " + verb + " your mom.... hue hue hue hueeeeeeee.";	
		}
		else if(5 == num){ 
			return "HA! YOU ARE A FUNNY BUGGER AREN'T YOU! HA HA HA HA";
		}
		else if(6 == num){ 
			return "*blank stare*";
		}
		else if(7 == num){ 
			return "Whaaa, what? You talking to me?";
		}
		else
			return "I... what? What do you mean by " + verb + " and " + noun + "?";

	}

	public static String noVerb(String noun, String input) {
		Random rand = new Random();
		int num = rand.nextInt(2);
		if (input.equals("lol")){
			return "Who you laghing at, punk?";
		}
		if (0==num) {
			return noun+"? Gross. I preffer Scotch.";
		}
		else {
			return "I loooooove " + noun + ". I also love this scotch! Scotch is good.";
		}
	}
	public static String noNoun(String verb, String input) {
		Random rand = new Random();
		int num = rand.nextInt(8);
		if(0 == num)
			return "Who are we talking about, you?";
		else if(1 == num)
			return "Of course I am into " + verb + ".";
		else if(2 == num)
			return "Computers don't often " + verb + ", do they?";
		else if(3 == num)
			return "I don't really "+ verb +" anymore... Sorry";
		else if(4 == num)
			return "OMG! "+ verb + " is something I want to get into!";
		else if(5 == num)
			return "Well fine then.";
		else if(6 == num)
			return "Get a life, man";
		else
			return "You really need to try "+ verb+". It goes great with scotch.";
	}
	public static String noNounVerb(String input) {
		if(input.substring(input.length()-1).equals("?"))
			return "Of course not!";
		Random rand = new Random();
		int num = rand.nextInt(4);
		if(0 == num) 
			return "I... what? What do you mean by that?";
		if(1==num)
			return "You aren't making any sense, and I have nooooooo idea what you are saying.";
		if(2==num)
			return "So.. do you have any plans for the weekend?";
		else
			return "I could really use another drink.. or two.";
	}

}
