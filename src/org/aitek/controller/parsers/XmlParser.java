package org.aitek.controller.parsers;

import android.content.Context;
import org.aitek.controller.core.Movie;
import org.aitek.controller.mede8er.Mede8erCommander;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/18/13
 * Time: 3:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class XmlParser {

    private static String text;

    /**
     * reads an XML file as an input stream and returns the movie object
     * mapped by the file
     * @param inputStream
     * @param context
     * @return
     * @throws Exception
     */
    public static Movie parse(InputStream inputStream, Context context) throws Exception {

        Mede8erCommander mede8erCommander = Mede8erCommander.getInstance(context);

        XmlPullParserFactory factory;
        XmlPullParser parser;

        String title = "";
        StringBuffer names = new StringBuffer();
        StringBuffer genres = new StringBuffer();

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
                        } else if (tagName.equalsIgnoreCase("genre")) {
                            genres.append(text).append(" ");
                            mede8erCommander.getMoviesManager().insertGenre(text);
                        } else if (tagName.equalsIgnoreCase("name")) {
                            names.append(text).append(" ");
                        } else if (tagName.equalsIgnoreCase("actor")) {
                            names.append(text).append(" ");
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Movie movie = new Movie(null, title, null, genres.toString(), names.toString());
        mede8erCommander.getMoviesManager().insert(movie);
        return movie;
    }
}
