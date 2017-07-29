import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by stefanie on 25/07/2017.
 */
public class TsfiledbTest {

    public static void main(String[] args) throws Exception {

        Connection connection = null;
        Statement statement = null;

        int startDevice = 0;
        int deviceNumber = 3;
        int sensorNumber = 2;
        int dataNumber = 30;

//        String dateString = "2017-07-27 17:45:00";
//        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        Date d = f.parse(dateString);
//        long startTime = d.getTime();
        //long endTime = System.currentTimeMillis();
        long startTime = 0;

        double startValue = 20;
        double endValue = 50;

        int insertCount = 0;

        String createTimeseriesSql = "CREATE TIMESERIES <timeseries> WITH DATATYPE=<datatype>, ENCODING=<encode>";
        String setStorageGroupSql = "SET STORAGE GROUP TO <prefixpath>";
        String insertDataSql = "INSERT INTO <timeseries> (timestamp, <sensor>) VALUES (<time>,<value>)";

        RandomNum r = new RandomNum();

        try {
            Class.forName("cn.edu.thu.tsfiledb.jdbc.TsfileDriver");
            connection = DriverManager.getConnection("jdbc:tsfile://localhost:6667/", "root", "root");
            statement = connection.createStatement();

            for (int i = startDevice; i < startDevice + deviceNumber; i++) {

                // Create Timeseries
                for (int j = 0; j < sensorNumber; j++) {
                    String sql = createTimeseriesSql.replace("<timeseries>", "root.laptop.d" + i + ".s" + j)
                            .replace("<datatype>", "INT32")
                            .replace("<encode>", "PLAIN");
                    System.out.println(sql);
                    statement.execute(sql);
                }
                String sql = setStorageGroupSql.replace("<prefixpath>", "root.laptop.d" + i);
                System.out.println(sql);
                statement.execute(sql);
            }

            // Insert Data
            for (int k = 0; k < dataNumber; k++) {

                startTime += 10;

                for (int i = startDevice; i < startDevice + deviceNumber; i++) {
                    for (int j = 0; j < sensorNumber; j++) {
//                        double value = r.getRandomDouble(startValue, endValue);
                        int value = r.getRandomInt(20, 50);
                        String sql = insertDataSql.replace("<timeseries>", "root.laptop.d" + i)
                                .replace("<sensor>", "s" + j)
                                .replace("<time>", startTime+"")
                                .replace("<value>", value+"");
                        System.out.println(sql);
                        statement.execute(sql);
                        insertCount += 1;
                    }
                    //Thread.sleep(10);
                }
            }

            System.out.println("Total insert Record: " + insertCount);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(statement != null){
                statement.close();
            }
            if(connection != null){
                connection.close();
            }
        }
    }
}



