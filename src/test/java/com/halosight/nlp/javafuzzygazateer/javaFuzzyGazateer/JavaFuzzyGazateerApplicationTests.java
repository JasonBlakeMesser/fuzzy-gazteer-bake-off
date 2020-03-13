package com.halosight.nlp.javafuzzygazateer.javaFuzzyGazateer;

import com.intuit.fuzzymatcher.component.MatchService;
import com.intuit.fuzzymatcher.domain.Document;
import com.intuit.fuzzymatcher.domain.Element;
import com.intuit.fuzzymatcher.domain.Match;
import com.intuit.fuzzymatcher.domain.Score;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static com.intuit.fuzzymatcher.domain.ElementType.TEXT;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
class JavaFuzzyGazateerApplicationTests {

	private List<String> productList = new ArrayList<>();

	@Test
	void contextLoads() {
	}

	private void setUp() {

		Collections.addAll(productList, "iPod Touch", "iPod Mini", "iPad", "iPad Pro", "iPad Mini", "iPad Air",
				"MacBook", "MacBook Pro", "MacBook Air", "Mac Mini", "Mac Pro", "iMac", "iMac Pro", "iPhone 11 Pro",
				"iPhone 11", "iPhone Xr", "iPhone 8", "Apple Card", "Air Pods", "Air Pods Pro", "Apple Watch Series 5",
				"Apple Watch Nike", "Apple Watch Hermes", "Apple Watch Edition", "Apple Watch Series 3", "Apple TV 4K");

	}


	@Test
	public void GeneralMatchTest() {
		setUp();

		String testTitles = "I always wanted to be a pop-starp, but then a boatmann came and told me that I was destined" +
				"to be a salvage-supervisor.";

		String testSentence = "I really love my new macbook pro, but I am not a super big fan of the Ipad pro, I " +
				"honestly feel like it is sort of a waste to have both. I bought them with my new Apple card and I thought " +
				"about getting the new Air pods pro too just because you get 5 percent cash back right now";

		String testSentenceEasy = "I love my macbook";

		List<Document> documentProductList = productList.stream()
				.map(string -> new Document.Builder(string)
						.addElement(new Element.Builder().setType(TEXT).setValue(string).createElement())
						.createDocument()).collect(Collectors.toList());

		List<String> sentenceList = Arrays.asList(testSentence.split(" "));

		List<Document> documentSentence = sentenceList.stream()
				.map(string -> new Document.Builder(string)
						.addElement(new Element.Builder().setType(TEXT).setValue(string).createElement())
						.createDocument()).collect(Collectors.toList());

		MatchService matchService = new MatchService();

		Map<Document, List<Match<Document>>> matches = matchService.applyMatch(documentSentence, documentProductList);

		for (Map.Entry<Document, List<Match<Document>>> match : matches.entrySet()) {
			System.out.println("Value = " + match.getValue());
			for (Match specificMatch : match.getValue()) {
				System.out.println("Matched with " + specificMatch.getMatchedWith());
			}
		}

	}

	private List<Document> createTitlesDocument() throws FileNotFoundException {
		List<Document> titlesDocument = new ArrayList<>();
		File titlesFile = new File("/Users/jasonmesser/IdeaProjects/javaFuzzyGazateer/src/test/titles.txt");
		Scanner fileReader = new Scanner(titlesFile);
		int counter = 0;
		while (fileReader.hasNextLine()) {
			String title = fileReader.nextLine();

			titlesDocument.add(new Document.Builder(Integer.toString(counter))
					.addElement(new Element.Builder().setType(TEXT).setValue(title).createElement())
					.createDocument());

			++counter;
		}

		return titlesDocument;
	}

	@Test
	public void testAllTitles() throws FileNotFoundException {
		String testTitles = "I always wanted to be a pop-starp, but then a boatmann came and told me that I was destined " +
				"to be a salvage-supervisor.";
		String testTitles100 = "I always wanted to be a pop-starp, but then a boatmann came and told me that I was destined " +
				"to be a salvage-supervisor. I really love my new macbook pro, but I am not a super big fan of the Ipad pro, I " +
				"honestly feel like it is sort of a waste to have both. I bought them with my new Apple card and I thought " +
				"about getting the new Air pods pro too author just because you get 5 percent cash back right now";


		//This probably will take some time but we can cache it
		StopWatch stopWatch = StopWatch.createStarted();
		List<Document> titlesDocument = createTitlesDocument();
		stopWatch.stop();
		System.out.println("Time to turn list of titles into documents " + stopWatch.getTime() + " Milliseconds");

		List<String> titlesSentenceList = Arrays.asList(testTitles.split(" "));

		List<Document> documentSentence = titlesSentenceList.stream()
				.map(string -> new Document.Builder(string)
						.addElement(new Element.Builder().setType(TEXT).setValue(string).setThreshold(.9).createElement()).setThreshold(.9)
						.createDocument()).collect(Collectors.toList());

		MatchService matchService = new MatchService();

		stopWatch.reset();
		stopWatch.start();

		Map<Document, List<Match<Document>>> matches = matchService.applyMatch(documentSentence, titlesDocument);
		stopWatch.stop();
		System.out.println("Time to run fuzzy match " + stopWatch.getTime() + " Milliseconds");

		for (Map.Entry<Document, List<Match<Document>>> match : matches.entrySet()) {
			System.out.println("Value = " + match.getValue());
			for (Match specificMatch : match.getValue()) {
				System.out.println("Matched with " + specificMatch.getMatchedWith());
			}
		}

	}

	@Test
	public void FuzzyMatch() {
		Document document1 = new Document.Builder("1")
				.addElement(new Element.Builder().setType(TEXT).setValue("microsof").createElement())
				.createDocument();

		Document document2 = new Document.Builder("2")
				.addElement(new Element.Builder().setType(TEXT).setValue("Microsoft").createElement())
				.createDocument();
		Document document3 = new Document.Builder("3")
				.addElement(new Element.Builder().setType(TEXT).setValue("Appple").createElement())
				.createDocument();

		List<Document> documents = new ArrayList<>();
		List<Document> documentsMightMatch = new ArrayList<>();
		documents.add(document1);
		documentsMightMatch.add(document2);
		documentsMightMatch.add(document3);

		MatchService matchService = new MatchService();


		Map<Document, List<Match<Document>>> score = matchService.applyMatch(document1, documentsMightMatch);

		// how to get log.debug to work?
		if (score.get(document1) != null) {
			score.get(document1).forEach(match -> {
				match.getMatchedWith().getElements().forEach(item -> {
					log.info("Match is  {}", item.getValue());
				});
				//System.out.println(match.getResult());
				log.info(" Score is {}", match.getResult());
			});
		}

	}

}


//		for (Document document : documentSentence) {
//			Map<Document, List<Match<Document>>> matches = matchService.applyMatch(document, titlesDocument);
//			for (Map.Entry<Document, List<Match<Document>>> match : matches.entrySet()) {
//				System.out.println("Value = " + match.getValue());
//				for (Match specificMatch : match.getValue()) {
//					System.out.println("Result " + specificMatch.getResult());
//					System.out.println("Matched with " + specificMatch.getMatchedWith());
//				}
//			}
//
//		}
//		System.out.println("Time to run fuzzy match " + stopWatch.getTime() + " Milliseconds");
//		stopWatch.stop();


//		String testTitles1000 = "I always wanted to be a pop-starp, but then a boatmann came and told me that I was destined " +
//				"to be a salvage-supervisor. I really love my new macbook pro, but I am not a super big fan of the Ipad pro, I " +
//				"honestly feel like it is sort of a waste to have both. I bought them with my new Apple card and I thought " +
//				"about getting the new Air pods pro too author just because you get 5 percent cash back right now, " +
//				"I always wanted to be a pop-starp, but then a boatmann came and told me that I was destined \" +\n" +
//				"\t\t\t\t\"to be a salvage-supervisor. I really love my new macbook pro, but I am not a super big fan of the Ipad pro, I \" +\n" +
//				"\t\t\t\t\"honestly feel like it is sort of a waste to have both. I bought them with my new Apple card and I thought \" +\n" +
//				"\t\t\t\t\"about getting the new Air pods pro too author just because you get 5 percent cash back right now" +
//				"I always wanted to be a pop-starp, but then a boatmann came and told me that I was destined \" +\n" +
//				"\t\t\t\t\"to be a salvage-supervisor. I really love my new macbook pro, but I am not a super big fan of the Ipad pro, I \" +\n" +
//				"\t\t\t\t\"honestly feel like it is sort of a waste to have both. I bought them with my new Apple card and I thought \" +\n" +
//				"\t\t\t\t\"about getting the new Air pods pro too author just because you get 5 percent cash back right now" +
//				"I always wanted to be a pop-starp, but then a boatmann came and told me that I was destined \" +\n" +
//				"\t\t\t\t\"to be a salvage-supervisor. I really love my new macbook pro, but I am not a super big fan of the Ipad pro, I \" +\n" +
//				"\t\t\t\t\"honestly feel like it is sort of a waste to have both. I bought them with my new Apple card and I thought \" +\n" +
//				"\t\t\t\t\"about getting the new Air pods pro too author just because you get 5 percent cash back right now" +
//				"I always wanted to be a pop-starp, but then a boatmann came and told me that I was destined \" +\n" +
//				"\t\t\t\t\"to be a salvage-supervisor. I really love my new macbook pro, but I am not a super big fan of the Ipad pro, I \" +\n" +
//				"\t\t\t\t\"honestly feel like it is sort of a waste to have both. I bought them with my new Apple card and I thought \" +\n" +
//				"\t\t\t\t\"about getting the new Air pods pro too author just because you get 5 percent cash back right now" +
//				"I always wanted to be a pop-starp, but then a boatmann came and told me that I was destined \" +\n" +
//				"\t\t\t\t\"to be a salvage-supervisor. I really love my new macbook pro, but I am not a super big fan of the Ipad pro, I \" +\n" +
//				"\t\t\t\t\"honestly feel like it is sort of a waste to have both. I bought them with my new Apple card and I thought \" +\n" +
//				"\t\t\t\t\"about getting the new Air pods pro too author just because you get 5 percent cash back right now" +
//				"I always wanted to be a pop-starp, but then a boatmann came and told me that I was destined \" +\n" +
//				"\t\t\t\t\"to be a salvage-supervisor. I really love my new macbook pro, but I am not a super big fan of the Ipad pro, I \" +\n" +
//				"\t\t\t\t\"honestly feel like it is sort of a waste to have both. I bought them with my new Apple card and I thought \" +\n" +
//				"\t\t\t\t\"about getting the new Air pods pro too author just because you get 5 percent cash back right now" +
//				"I always wanted to be a pop-starp, but then a boatmann came and told me that I was destined \" +\n" +
//				"\t\t\t\t\"to be a salvage-supervisor. I really love my new macbook pro, but I am not a super big fan of the Ipad pro, I \" +\n" +
//				"\t\t\t\t\"honestly feel like it is sort of a waste to have both. I bought them with my new Apple card and I thought \" +\n" +
//				"\t\t\t\t\"about getting the new Air pods pro too author just because you get 5 percent cash back right now" +
//				"I always wanted to be a pop-starp, but then a boatmann came and told me that I was destined \" +\n" +
//				"\t\t\t\t\"to be a salvage-supervisor. I really love my new macbook pro, but I am not a super big fan of the Ipad pro, I \" +\n" +
//				"\t\t\t\t\"honestly feel like it is sort of a waste to have both. I bought them with my new Apple card and I thought \" +\n" +
//				"\t\t\t\t\"about getting the new Air pods pro too author just because you get 5 percent cash back right now" +
//				"I always wanted to be a pop-starp, but then a boatmann came and told me that I was destined \" +\n" +
//				"to be a salvage-supervisor. I really love my new macbook pro, but I am not a super big fan of the Ipad pro, I " +
//				"honestly feel like it is sort of a waste to have both. I bought them with my new Apple card and I thought " +
//				"about getting the new Air pods pro too author just because you get 5 percent cash back right now";
