import javax.rmi.CORBA.Util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by stefanie on 25/07/2017.
 */
public class TsfiledbTest {

    public static final int TIME_INTERVAL = 1000;
    public static final int TOTAL_DATA = 10;
    public static final int MIN_INT = 0;
    public static final int MAX_INT = 100;
    public static final int MIN_FLOAT = 0;
    public static final int MAX_FLOAT = 100;

    public static void createTimeseries(Statement statement, HashMap<String, String> timeseriesMap) throws SQLException{

        String createTimeseriesSql = "CREATE TIMESERIES <timeseries> WITH DATATYPE=<datatype>, ENCODING=<encode>";

        for (String key : timeseriesMap.keySet()) {
            String properties = timeseriesMap.get(key);
            String sql = createTimeseriesSql.replace("<timeseries>", key)
                    .replace("<datatype>", Utils.getType(properties))
                    .replace("<encode>", Utils.getEncode(properties));
            System.out.println(sql);
            statement.execute(sql);
        }
    }

    public static void setStorageGroup(Statement statement, ArrayList<String> storageGroupList) throws SQLException {

        String setStorageGroupSql = "SET STORAGE GROUP TO <prefixpath>";
        for (String str : storageGroupList) {
            String sql = setStorageGroupSql.replace("<prefixpath>", str);
            System.out.println(sql);
            statement.execute(sql);
        }
    }


    public static void randomInsertData(Statement statement, HashMap<String, String> timeseriesMap) throws Exception {

        String insertDataSql = "INSERT INTO <path> (timestamp, <sensor>) VALUES (<time>, <value>)";
        RandomNum r = new RandomNum();

        for (int i = 0; i < TOTAL_DATA; i++) {

            long time = System.currentTimeMillis();
            for(String key : timeseriesMap.keySet()) {

                String type = Utils.getType(timeseriesMap.get(key));
                String path = Utils.getPath(key);
                String sensor = Utils.getSensor(key);
                String sql = "";

                if(type.equals("INT32")) {
                    int value = r.getRandomInt(MIN_INT, MAX_INT);
                    sql = insertDataSql.replace("<path>", path)
                            .replace("<sensor>", sensor)
                            .replace("<time>", time+"")
                            .replace("<value>", value+"");
                } else if (type.equals("FLOAT")) {
                    float value = r.getRandomFloat(MIN_FLOAT, MAX_FLOAT);
                    sql = insertDataSql.replace("<path>", path)
                            .replace("<sensor>", sensor)
                            .replace("<time>", time+"")
                            .replace("<value>", value+"");
                }

                System.out.println(sql);
                statement.execute(sql);
                Thread.sleep(TIME_INTERVAL);
            }
        }

    }

    public static void main(String[] args) throws Exception {

        Connection connection = null;
        Statement statement = null;

//        String dateString = "2017-07-27 17:45:00";
//        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        Date d = f.parse(dateString);
//        long startTime = d.getTime();

        HashMap timeseriesMap =new HashMap<String, String>();
        timeseriesMap.put("root.excavator.Beijing.d1.s1", "INT32,RLE");
        timeseriesMap.put("root.excavator.Beijing.d1.s2", "FLOAT,RLE");
        timeseriesMap.put("root.excavator.Beijing.d2.s1", "INT32,RLE");
        timeseriesMap.put("root.excavator.Beijing.d2.s2", "FLOAT,RLE");
        timeseriesMap.put("root.excavator.Shanghai.d3.s1", "INT32,RLE");
        timeseriesMap.put("root.excavator.Shanghai.d3.s2", "FLOAT,RLE");

        ArrayList<String> storageGroupList = new ArrayList();
        storageGroupList.add("root.excavator.Beijing.d1");
        storageGroupList.add("root.excavator.Beijing.d2");
        storageGroupList.add("root.excavator.Shanghai.d3");

        try {
            Class.forName("cn.edu.thu.tsfiledb.jdbc.TsfileDriver");
            connection = DriverManager.getConnection("jdbc:tsfile://localhost:6667/", "root", "root");
            statement = connection.createStatement();

            //createTimeseries(statement, timeseriesMap);
            setStorageGroup(statement, storageGroupList);
            randomInsertData(statement, timeseriesMap);

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



