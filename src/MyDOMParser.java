import java.io.IOException;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MyDOMParser {
    Map<String, ParsedMovie> myMovies;
    Map<String, ParsedStar> myStars;
    Document dom;
    List<String> inconsistencies;

    public MyDOMParser() {
        myMovies = new HashMap<>();
        inconsistencies = new ArrayList<>();
    }

    public void run() {
        parseXmlFile("stanford-movies/mains243.xml");
        parseMainDocument();

        parseXmlFile("stanford-movies/casts124.xml");
        parseCastsDocument();

        printData();
    }

    private void parseXmlFile(String file) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();

            dom = db.parse(file);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void parseMainDocument() {
        Element docEle = dom.getDocumentElement();

        NodeList nl = docEle.getElementsByTagName("directorfilms");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                Element el = (Element) nl.item(i);
                addDirectorMovies(el);
            }
        }
    }

    private void addDirectorMovies(Element el) {
        String director = getTextValue(el, "dirname");

        NodeList filmElList = el.getElementsByTagName("film");

        if (filmElList != null && filmElList.getLength() > 0) {
            for (int i = 0; i < filmElList.getLength(); i++) {
                Element filmEl = (Element) filmElList.item(i);

                ParsedMovie m = new ParsedMovie();

                try {
                    String id = getTextValue(filmEl, "fid");
                    m.setId(id);
                } catch (Exception e) {
                    inconsistencies.add("Error: Tried to add movie with id value='" + e.getMessage());
                }

                if (director != null) {
                    m.setDirector(director);
                } else {
                    inconsistencies.add("Error: Director value ='null' for movie " + m.getId());
                }

                try {
                    String title = getTextValue(filmEl, "t");
                    m.setTitle(title);
                } catch (Exception e) {
                    inconsistencies.add("Error: Tried to add movie with title value='" + e.getMessage() + "' for movie " + m.getId());
                }

                parseGenres(m, filmEl);

                try {
                    int year = getIntValue(filmEl, "year");
                    m.setYear(year);
                } catch (Exception e) {
                    inconsistencies.add("Error: Tried to add movie with title year='" + e.getMessage() + "' for movie " + m.getId());
                }

                myMovies.put(m.getId(), m);
            }
        }
    }

    private void parseGenres(ParsedMovie m, Element filmEl) {
        NodeList catElList = filmEl.getElementsByTagName("cat");
        if (catElList != null && catElList.getLength() > 0) {
            for (int i = 0; i < catElList.getLength(); i++) {
                Element catEl = (Element) catElList.item(i);
                try {
                    String genre = catEl.getFirstChild().getNodeValue();
                    m.addGenre(genre);
                } catch (Exception e) {
                    inconsistencies.add("Error: Tried to add movie with genre value='" + e.getMessage() + "' for movie " + m.getId());
                }
            }
        }
    }

    private void parseCastsDocument() {
        Element docEle = dom.getDocumentElement();

        NodeList nl = docEle.getElementsByTagName("filmc");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                Element el = (Element) nl.item(i);
                addFilmCast(el);
            }
        }
    }

    private void addFilmCast(Element el) {
        String fid = getTextValue(el, "f");
        ParsedMovie m = myMovies.get(fid);

        if (m == null) {
            inconsistencies.add("Error: Movie with id='" + fid + "' does not exist");
            return;
        }

        NodeList ml = el.getElementsByTagName("m");
        if (ml != null && ml.getLength() > 0) {
            for (int i = 0; i < ml.getLength(); i++) {
                Element actorEl = (Element) ml.item(i);
                try {
                    String star = getTextValue(actorEl, "a");
                    m.addStar(star);
                } catch (Exception e) {
                    inconsistencies.add("Error: Tried to add movie with actor name='" + e.getMessage() + "' for movie " + fid);
                }
            }
        }
    }

    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }
        return textVal;
    }

    private int getIntValue(Element ele, String tagName) {
        return Integer.parseInt(getTextValue(ele, tagName));
    }

    private void printData() {
        System.out.println("No of Movies '" + myMovies.size() + "'.");
        Iterator<ParsedMovie> it = myMovies.values().iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }

        System.out.println("\nInconsistencies:");

        for (String i : inconsistencies) {
            System.out.println(i);
        }
    }

    public static void main(String[] args) {
        MyDOMParser parser = new MyDOMParser();
        parser.run();
    }

}