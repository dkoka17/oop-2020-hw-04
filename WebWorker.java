import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Semaphore;


public class WebWorker extends Thread {


    private WebFrame.Worker worker;

    private WebFrame.URLer URLer;

    public WebWorker(WebFrame.URLer url, WebFrame.Worker launcher) {
        this.worker  = launcher;
        this.URLer = url;
    }

    @Override
    public void run() {
        worker.startThread();
        worker.updateInf();
        StartDownload();
        worker.stopThread();
        worker.completeThread();
        worker.updateInf();
        worker.updatere();
        worker.semaphore.release();
    }

    private void StartDownload() {
        InputStream input = null;
        StringBuilder contents = null;
        try {
            long startTime = System.currentTimeMillis();

            URL url = new URL(URLer.getUrll());
            URLConnection connection = url.openConnection();

            // Set connect() to throw an IOException
            // if connection does not succeed in this many msecs.
            connection.setConnectTimeout(5000);

            connection.connect();
            input = connection.getInputStream();

            BufferedReader reader  = new BufferedReader(new InputStreamReader(input));

            char[] array = new char[1000];
            int len;
            contents = new StringBuilder(1000);
            while ((len = reader.read(array, 0, array.length)) > 0) {
                contents.append(array, 0, len);
                Thread.sleep(100);
            }

            long endTime = System.currentTimeMillis();

            String resultMsg = new SimpleDateFormat("HH:mm:ss").format(new Date(startTime))
                    + "   " + (endTime - startTime)
                    + "ms   " + array.length + "bytes";

            URLer.setSt(resultMsg);

        }
        // Otherwise control jumps to a catch...
        catch(Exception e) {
            URLer.setSt("err");
        }

    }


}