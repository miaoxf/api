package tianz.bd.api.nosql.rocksdb;

import org.rocksdb.RocksDBException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: Miaoxf
 * @Date: 2021/1/29 17:29
 * @Description:
 */
public class MultiProcessRead {

    public static ExecutorService executorService = Executors.newFixedThreadPool(20);
    public static RocksDBConnectionPool pool = new RocksDBConnectionPool();
    public static String path = null;
    public static String key = "testKey";
    public static String value = "testValue";

    public static void main(String[] args) {
        String mode = "read";
        if (args != null && args.length != 0) {
            mode = args[0];
            if (args.length > 3) {
                path = args[1];
                key = args[2];
                value = args[3];
            }
        }

        if ("write".equalsIgnoreCase(mode)) {
            try (AbstractRocksDBConnection connection = pool.getConnection(Mode.WRITE)) {
                connection.put(key, value);
            } catch (RocksDBException | UnsupportedEncodingException | InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if ("read".equalsIgnoreCase(mode)) {
            while (true) {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        print(path, key);
                    }
                });
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void print(String path, String key) {
        try (AbstractRocksDBConnection connection = pool.getConnection(path)) {
            String read = connection.read(key);
            System.out.println(read);
        } catch (RocksDBException | UnsupportedEncodingException | InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
