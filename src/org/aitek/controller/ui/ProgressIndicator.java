package org.aitek.controller.ui;

import android.app.ProgressDialog;
import android.os.Handler;

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


    public void progress(final String message, final GenericProgressIndicator genericProgressIndicator) {

        progressBar = new ProgressDialog(genericProgressIndicator.getActivity());
        progressBar.setCancelable(true);
        progressBar.setMessage(message);
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBar.setMax(genericProgressIndicator.getMax());
        progressBar.show();
        progressBarStatus = 0;

        final Handler progressBarHandler = new Handler();

        new Thread(new Runnable() {
            public void run() {

                try {
                    genericProgressIndicator.setup();

                    while (progressBarStatus < 100) {

                        progressBarStatus = genericProgressIndicator.next();
                        progressBarHandler.post(new Runnable() {
                            public void run() {
                                progressBar.setMessage(genericProgressIndicator.getText());
                                progressBar.setProgress(progressBarStatus);
                            }
                        });
                    }

                    if (progressBarStatus >= 100) {

                        progressBar.dismiss();
                        genericProgressIndicator.finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
