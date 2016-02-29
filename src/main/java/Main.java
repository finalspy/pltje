import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Bot de vote PLTJE
 */
public class Main {

    private static final String FIRST_URL = "http://pltje.latribune.fr/les-finalistes/";
    private static final String VOTE_URL = "http://pltje.latribune.fr/wp-content/themes/tribune/front-ajax.php";

    private static final String CANDIDAT_GEOKAPS = "2097";

    private static final String UA = "Mozilla/5.0 (Windows NT 6.1; rv:38.0) Gecko/20100101 Firefox/38.0";

    private static final int VOTES_SIZE = 200;
    private static final int POOL_SIZE = 10;
    private static final int CONNECT_TIMEOUT_IN_MILLIS = 60000; // 1 minute

    private static long n = 1;

    public static void main(String[] args) throws IOException, InterruptedException {
        while (true) {
            vote();
        }
    }

    private static void vote() throws InterruptedException {
        Future[] futures = new Future[VOTES_SIZE];
        ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);

        try {
            // Retrieve page + php session cookies
            Map<String, String> ck = Jsoup.connect(FIRST_URL)
                    .userAgent(UA)
                    .method(Connection.Method.GET)
                    .timeout(CONNECT_TIMEOUT_IN_MILLIS)
                    .execute().cookies();

            // start to vote with same session (like multiple clicks)
            for (int i = 0; i < futures.length; i++) {
                futures[i] = executor.submit(new Callable<String>() {
                    public String call() {
                        try {
                            return Jsoup.connect(VOTE_URL)
                                    .ignoreContentType(true)
                                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                                    .header("DNT", "1")
                                    .header("X-Requested-With", "XMLHttpRequest")
                                    .data("action", "load_voter_candidat")
                                    .data("iCandidatId", CANDIDAT_GEOKAPS)
                                    .referrer(FIRST_URL)
                                    .cookies(ck)
                                    .userAgent(UA)
                                    .method(Connection.Method.POST)
                                    .execute().body();
                        } catch (IOException e) {
                            System.err.println("call() " + e.getMessage());
                            return "err";
                        }
                    }
                });
            }
            // wait till all requests complete
            waitUntilAllFuturesComplete(futures);
        } catch (IOException e) {
            System.err.println("vote() " + e.getMessage());
        } finally {
            System.out.println("\t lot[" + (n++) + "] terminé");
            executor.shutdown();
        }
    }

    private static void waitUntilAllFuturesComplete(Future<String>[] futures) throws InterruptedException {
        boolean allDone = false;
        while (!allDone) {
            allDone = true;
            for (Future<String> result : futures) {
                if (!result.isDone()) {
                    allDone = false;
                    break;
                }
            }
            Thread.sleep(50);
        }
        // as an indicator display the last future result (may not be the last answered).
        try {
            System.out.println("\t\t "+futures[futures.length -1].get()+"");
        } catch (ExecutionException e) {
            System.err.println("control() " + e.getMessage());
        }
    }

}