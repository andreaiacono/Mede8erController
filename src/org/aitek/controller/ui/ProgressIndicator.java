package org.aitek.controller.ui;

import android.app.ProgressDialog;
import android.os.Handler;
import org.aitek.controller.utils.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/24/13
 * Time: 12:05 AM
 */
public class ProgressIndicator {

    private ProgressDialog progressBar;
    private int progressBarStatus;


    public void progress(final String message, final GenericProgressIndicator genericProgressIndicator) {

        progressBar = new ProgressDialog(genericProgressIndicator.getContext());
        progressBar.setCancelable(true);
        progressBar.setMessage(message);
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBarStatus = 0;
        progressBar.show();

        final Handler progressBarHandler = new Handler();

        new Thread(new Runnable() {
            public void run() {


                try {
                    genericProgressIndicator.setup();
                    progressBar.setMax(genericProgressIndicator.getMax());


                    while (progressBarStatus < genericProgressIndicator.getMax()) {

                        progressBarStatus = genericProgressIndicator.next();
                        progressBarHandler.post(new Runnable() {
                            public void run() {
                                progressBar.setMessage(genericProgressIndicator.getText());
                                progressBar.setProgress(progressBarStatus);
                            }
                        });
                    }

                    if (progressBarStatus >= genericProgressIndicator.getMax()) {

                        progressBar.dismiss();
                        genericProgressIndicator.finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.log(e.getMessage());
                }
            }
        }).start();
    }
}
