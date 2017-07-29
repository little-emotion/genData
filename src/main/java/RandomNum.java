/**
 * Created by stefanie on 26/07/2017.
 */
import java.util.Random;

public class RandomNum {

    static Random random = new Random();

    public static long getRandomLong(long min, long max) {
        return Math.abs(random.nextLong()) % (max-min+1) + min;
    }

    public static int getRandomInt(int min, int max) {
        return Math.abs(random.nextInt() % (max-min+1) + min);
    }

    public static double getRandomDouble(double min, double max) {
        return (min + Math.random() * (max - min));
    }

    public static void main(String[] argc) {

        for (int i = 0; i < 10; i++) {
            //System.out.println(r.getRandomLong(1,100));
            System.out.println(RandomNum.getRandomDouble(1,5));
        }
        System.out.println(System.currentTimeMillis());
    }

}