package org.aitek.controller.parsers;

import org.aitek.controller.core.Movie;
import org.aitek.controller.utils.MiscUtils;
import org.aitek.controller.utils.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/18/13
 * Time: 3:53 PM
 */
public class XmlParser {

    private static String text;

    /**
     * reads an XML file as an input stream and returns the movie object
     * mapped by the file
     *
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static void parseMovie(InputStream inputStream, Movie movie) throws Exception {

        XmlPullParserFactory factory;
        XmlPullParser parser;

        String title = "";
        String sortingTitle = null;
        String date = "01/01/1970";
        StringBuilder names = new StringBuilder();
        StringBuilder genres = new StringBuilder();

        try {

            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            parser = factory.newPullParser();

            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                String tagName = parser.getName();

                switch (eventType) {

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagName.equalsIgnoreCase("title")) {
                            title = text;
                        } else if (tagName.equalsIgnoreCase("sorting_title")) {
                            sortingTitle = StringUtils.removeWords(text.toLowerCase(), StringUtils.articles);;
                        }else if (tagName.equalsIgnoreCase("genre")) {
                            genres.append(text).append(" ");
                        } else if (tagName.equalsIgnoreCase("name")) {
                            names.append(text).append(" ");
                        } else if (tagName.equalsIgnoreCase("actor")) {
                            names.append(text).append(" ");
                        } else if (tagName.equalsIgnoreCase("date")) {
                            date = text;
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }

        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        movie.setGenres(genres.toString());
        movie.setPersons(names.toString());
        movie.setDate(MiscUtils.getDateFromString(date));
        movie.setTitle(title);
        movie.setSortingTitle(sortingTitle != null ? sortingTitle : StringUtils.removeWords(title.toLowerCase(), StringUtils.articles));
    }
}
