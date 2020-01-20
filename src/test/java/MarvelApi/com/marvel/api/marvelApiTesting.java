package MarvelApi.com.marvel.api;
import static com.jayway.restassured.RestAssured.given;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class marvelApiTesting extends baseTest {
	public List<String> seriesIdList = new ArrayList<String>();
	public List<String> charIdListWithoutDesc = new ArrayList<String>();
	private static Logger logger = LogManager.getLogger(marvelApiTesting.class);
	public String baseurl = "https://gateway.marvel.com:443/v1/public/";
	
	/*******************Get the list of all the characters which have description and get the list of series those
characters are involved.  
	 * @throws IOException **************************************************/
	@Test(priority=0)
	public void getCharactersWithDescription() throws IOException {
		HashMap<String,String> hmap = new HashMap<String, String>();
		HashMap<String,String> idhmap = new HashMap<String, String>();
		HashMap<String,List<String>> serieshmap = new HashMap<String, List<String>>();
		
		Response getCharactersWithDescription = given()
				.contentType(ContentType.JSON)
				.header("Host","gateway.marvel.com:443")
				.header("Accept","application/json")
				.urlEncodingEnabled(false)
				.log()
				.everything()
				.expect()
				.statusCode(200)
				.log()
				.all()
				.when()
				.get(baseurl+"characters?apikey=d2018571c38d1ed54ff519619c7a5f1c&hash=c77d98b9336be2c6b41722b2f6db58a3&ts=1");
		System.out.println("Response***********" + getCharactersWithDescription.asString());
		       		        
		JsonPath jsonpath = new JsonPath(getCharactersWithDescription.asString());
		System.out.println(jsonpath);
		List<String> test=jsonpath.getList("data.results.description");
		List<String> test1=jsonpath.getList("data.results.name");
		List<Integer> test2=jsonpath.getList("data.results.id");
		
        for (int i=0;i<test.size();i++) {
        	if (test.get(i).isEmpty()!=true) {
        		
            	hmap.put(test1.get(i), test.get(i));
            	idhmap.put(Integer.toString(test2.get(i)), test1.get(i));
            	
        	}else {
        		System.out.println("description is empty for the name "+ test1.get(i));
        		String marvelcharId = test2.get(i).toString().trim();
        		charIdListWithoutDesc.add(marvelcharId);
        		System.out.println(charIdListWithoutDesc);
        	}
        	
           
        }
        
        
        System.out.println(hmap);
        System.out.println(idhmap);
        
        Set<String> keys = idhmap.keySet();
        for (String k : keys) {
            System.out.println("Key: " + k);
            Response getSeriesByCharacterId = given()
    				.contentType(ContentType.JSON)
    				.header("Host","gateway.marvel.com:443")
    				.header("Accept","application/json")
    				.urlEncodingEnabled(false)
    				.log()
    				.everything()
    				.expect()
    				.statusCode(200)
    				.log()
    				.all()
    				.when()
    				.get(baseurl+"characters/"+k+"/series?apikey=d2018571c38d1ed54ff519619c7a5f1c&hash=c77d98b9336be2c6b41722b2f6db58a3&ts=1");
    		System.out.println("Response***********" + getSeriesByCharacterId.asString());
    		
    		JsonPath Seriesjsonpath = new JsonPath(getSeriesByCharacterId.asString());
    		System.out.println(Seriesjsonpath);
    		List<String> titleList=Seriesjsonpath.getList("data.results.title");
    	    List<String>seriesIdListitr=Seriesjsonpath.getList("data.results.id");
    	    seriesIdList.addAll(seriesIdListitr);
    		serieshmap.put(k, titleList);
        }
           
          System.out.println(serieshmap);
          System.out.println(seriesIdList);
		
    		
        }
	
	/***************Randomly select 2 series from the above list and get the list of characters which were
part of those events.******************/
	
	@Test(priority=1)
	public void getCharactersListFromSeriesId() {
		Random rand = new Random(); 
        String firstSeriesId =   String.valueOf(seriesIdList.get(rand.nextInt(seriesIdList.size())));
        String secondSeriesId =  String.valueOf(seriesIdList.get(rand.nextInt(seriesIdList.size())));
        List<String> randSeriesId = new ArrayList<String>();
        randSeriesId.add(firstSeriesId);
        randSeriesId.add(secondSeriesId);
        
        for (int counter=0;counter<randSeriesId.size();counter++) {
        	String getSeriesId = randSeriesId.get(counter);
        	
        	Response getCharacterListFromComicId = given()
    				.contentType(ContentType.JSON)
    				.header("Host","gateway.marvel.com:443")
    				.header("Accept","application/json")
    				.urlEncodingEnabled(false)
    				.log()
    				.everything()
    				.expect()
    				.statusCode(200)
    				.log()
    				.all()
    				.when()
    				.get(baseurl+"series/"+getSeriesId+"/characters?apikey=d2018571c38d1ed54ff519619c7a5f1c&hash=c77d98b9336be2c6b41722b2f6db58a3&ts=1");
    		System.out.println("Response***********" + getCharacterListFromComicId.asString());
    		
    		JsonPath Seriesjsonpath = new JsonPath(getCharacterListFromComicId.asString());
    		logger.info(Seriesjsonpath);
    		List<String> titleList=Seriesjsonpath.getList("data.results.name");
    		logger.info("list of characters in the series having seriesId "+getSeriesId+" : "+titleList.toString());
        }
        
		
       }
	
	 /***************Get the list of stories which does not involve a character with description.******************/
	    @Test(priority=2)
	    public void storieshavingCharactersWithoutDescription() {
	        	
	        	Response storieshavingCharactersWithoutDescription = given()
	    				.contentType(ContentType.JSON)
	    				.header("Host","gateway.marvel.com:443")
	    				.header("Accept","application/json")
	    				.urlEncodingEnabled(false)
	    				.log()
	    				.everything()
	    				.expect()
	    				.statusCode(200)
	    				.log()
	    				.all()
	    				.when()
	    				.get(baseurl+"stories?characters=1011334,1010699,1016823"+"&apikey=d2018571c38d1ed54ff519619c7a5f1c&hash=c77d98b9336be2c6b41722b2f6db58a3&ts=1");
	    		System.out.println("Response***********" + storieshavingCharactersWithoutDescription.asString());
	    		
	    		JsonPath Storiesjsonpath = new JsonPath(storieshavingCharactersWithoutDescription.asString());
	    		logger.info(Storiesjsonpath);
	    		List<String> titleList=Storiesjsonpath.getList("data.results.title");
	    		logger.info("list of stories in the which does not involve characters with description  "+titleList.toString());
	    }
	
	}
        
            
	
	    
	
    
	
		
	


