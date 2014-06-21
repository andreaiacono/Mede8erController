package org.aitek.controller.mede8er;

import org.aitek.controller.mede8er.net.Response;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 21/06/14
 * Time: 23.00
 */
public interface Callbackable {

    public void callback(Response response);
}
