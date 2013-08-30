package org.aitek.movies.utils;

import android.app.Activity;
import android.content.Context;
import org.aitek.movies.core.Movie;
import org.aitek.movies.core.MoviesManager;
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

    public static Movie parse(InputStream in, Activity activity) throws Exception {

        Mede8erCommander mede8erCommander = Mede8erCommander.getInstance(activity);

        XmlPullParserFactory factory;
        XmlPullParser parser;

        String title = "";
        StringBuffer names = new StringBuffer();
        StringBuffer genres = new StringBuffer();

        try {

            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            parser = factory.newPullParser();

            parser.setInput(in, null);

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
                            mede8erCommander.getMoviesManager().insertMovieGenre(text);
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
        mede8erCommander.getMoviesManager().insertMovie(movie);
        return movie;
    }
}
