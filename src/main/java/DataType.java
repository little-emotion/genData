import java.util.Random;

/**
 * Created by stefanie on 29/07/2017.
 */
public class DataType {

    static String[] typeArray={"INT32","INT64","BOOLEAN", "FLOAT", "DOUBLE", "TEXT"};

    public static String getTypeArray() {
        int index = RandomNum.getRandomInt(0, typeArray.length - 1);
        return typeArray[index];
    }
}
