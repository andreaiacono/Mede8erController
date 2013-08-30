package org.aitek.movies.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import org.aitek.movies.core.MoviesManager;
import org.aitek.movies.loaders.Progressable;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/24/13
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProgressIndicator {

    private ProgressDialog progressBar;
    private int progressBarStatus;


    public void progress(final String message, final Progressable progressable) {

        progressBar = new ProgressDialog(progressable.getActivity());
        progressBar.setCancelable(true);
        progressBar.setMessage(message);
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        progressBarStatus = 0;

        final Handler progressBarHandler = new Handler();

        new Thread(new Runnable() {
            public void run() {

                try {
                    progressable.setup(progressable.getActivity());

                    while (progressBarStatus < 100) {

                        progressBarStatus = progressable.next();
                        progressBarHandler.post(new Runnable() {
                            public void run() {
                                progressBar.setProgress(progressBarStatus);
                            }
                        });
                    }

                    if (progressBarStatus >= 100) {

                        progressBar.dismiss();
                        progressable.finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
