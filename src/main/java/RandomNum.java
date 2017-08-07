/**
 * Created by stefanie on 26/07/2017.
 */
import java.util.Random;
import java.time.LocalDate;

public class RandomNum {

    static Random random = new Random();

    public static long getRandomLong(long min, long max) {
        return Math.abs(random.nextLong()) % (max-min+1) + min;
    }

    public static int getRandomInt(int min, int max) {
        return Math.abs(random.nextInt() % (max-min+1) + min);
    }

    public static float getRandomFloat(float min, float max) {

        Random random = new Random();
        return (random.nextFloat() * (max-min+1) + min);
    }

    public static void main(String[] argc) {

//        for (int i = 0; i < 10; i++) {
//            //System.out.println(r.getRandomLong(1,100));
//            System.out.println(RandomNum.getRandomDouble(1,5));
//        }
//        System.out.println(System.currentTimeMillis());
    }

}