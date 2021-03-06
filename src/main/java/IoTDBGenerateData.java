import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created by stefanie on 25/07/2017.
 */

public class IoTDBGenerateData {

    public static final int TIME_INTERVAL = 0;
    public static final int TOTAL_DATA = 400;
    public static final int ABNORMAL_MAX_INT = 0;
    public static final int ABNORMAL_MIN_INT = -10;
    public static final int ABNORMAL_MAX_FLOAT = 0;
    public static final int ABNORMAL_MIN_FLOAT = -10;
    public static final int ABNORMAL_FREQUENCY = Integer.MAX_VALUE;
    public static final int ABNORMAL_LENGTH = 0;
    public static final int MIN_INT = 0;
    public static final int MAX_INT = 14;
    public static final int MIN_FLOAT = 20;
    public static final int MAX_FLOAT = 30;
    public static final int STRING_LENGTH = 5;
    public static final int BATCH_SQL = 5000;
    public static ArrayList<String> storageGroupList = new ArrayList();

    public static HashMap generateTimeseriesMapFromFile(String inputFilePath) throws Exception{

        HashMap timeseriesMap = new HashMap();

        File file = new File(inputFilePath);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {

            String timeseries = line.split(" ")[2];
            String dataType = line.split("DATATYPE = ")[1].split(",")[0].trim();
            String encodingType = line.split("ENCODING = ")[1].split(";")[0].trim();
            //System.out.println(encodingType);
            for(String group : storageGroupList){
            	if(timeseries.contains(group)){
            		timeseriesMap.put(timeseries, dataType+","+encodingType);
            		break;
            	}
            }
            	
            
        }

        return timeseriesMap;

    }

    public static void createTimeseries(Statement statement, HashMap<String, String> timeseriesMap) throws SQLException{

        String createTimeseriesSql = "CREATE TIMESERIES <timeseries> WITH DATATYPE=<datatype>, ENCODING=<encode>";

        int sqlCount = 0;

        for (String key : timeseriesMap.keySet()) {
            String properties = timeseriesMap.get(key);
            String sql = createTimeseriesSql.replace("<timeseries>", key)
                    .replace("<datatype>", Utils.getType(properties))
                    .replace("<encode>", Utils.getEncode(properties));

            statement.addBatch(sql);
            sqlCount++;
            if (sqlCount >= BATCH_SQL) {
                statement.executeBatch();
                statement.clearBatch();
                sqlCount = 0;
            }
        }
    }

    public static void setStorageGroup(Statement statement, ArrayList<String> storageGroupList) throws SQLException {

        String setStorageGroupSql = "SET STORAGE GROUP TO <prefixpath>";
        for (String str : storageGroupList) {
            String sql = setStorageGroupSql.replace("<prefixpath>", str);
            //System.out.println(sql);
            statement.execute(sql);
        }
    }


    public static void randomInsertData(Statement statement, HashMap<String, String> timeseriesMap) throws Exception {

        String insertDataSql = "INSERT INTO <path> (timestamp, <sensor>) VALUES (<time>, <value>)";
        RandomNum r = new RandomNum();
        int abnormalCount = 0;
        int abnormalFlag = 1;

        int sqlCount = 0;

        for (int i = 0; i < TOTAL_DATA; i++) {
            System.out.println("Execute " + i + " loops");
            long time = System.currentTimeMillis();

            if (i % ABNORMAL_FREQUENCY == 250) {
                abnormalFlag = 0;
            }

            for(String key : timeseriesMap.keySet()) {

                String type = Utils.getType(timeseriesMap.get(key));
                String path = Utils.getPath(key);
                String sensor = Utils.getSensor(key);
                String sql = "";

                if(type.equals("INT32")) {
                    int value;
                    if (abnormalFlag == 0) {
                        value = r.getRandomInt(ABNORMAL_MIN_INT, ABNORMAL_MAX_INT);
                    } else {
                        value = r.getRandomInt(MIN_INT, MAX_INT);
                    }
                    sql = insertDataSql.replace("<path>", path)
                            .replace("<sensor>", sensor)
                            .replace("<time>", time+"")
                            .replace("<value>", value+"");
                } else if (type.equals("FLOAT")) {
                    float value;
                    if (abnormalFlag == 0) {
                        value = r.getRandomFloat(ABNORMAL_MIN_FLOAT, ABNORMAL_MAX_FLOAT);
                    } else {
                        value = r.getRandomFloat(MIN_FLOAT, MAX_FLOAT);
                    }
                    sql = insertDataSql.replace("<path>", path)
                            .replace("<sensor>", sensor)
                            .replace("<time>", time+"")
                            .replace("<value>", value+"");
                } else if (type.equals("TEXT")) {
                    String value;
                    value = r.getRandomText(STRING_LENGTH);
                    sql = insertDataSql.replace("<path>", path)
                        .replace("<sensor>", sensor)
                        .replace("<time>", time+"")
                        .replace("<value>", "\"" + value+"\"");
                }

                //TODO: other data type
                statement.addBatch(sql);
                sqlCount++;
                if (sqlCount >= BATCH_SQL) {
                    statement.executeBatch();
                    //System.out.println("Batch execute " + BATCH_SQL + " sql.");
                    statement.clearBatch();
                    sqlCount = 0;
                }
                Thread.sleep(TIME_INTERVAL);
            }

            if (abnormalFlag == 0) {
                abnormalCount += 1;
            }
            if (abnormalCount >= ABNORMAL_LENGTH) {
                abnormalCount = 0;
                abnormalFlag = 1;
            }
        }
    }

    public static void main(String[] args) throws Exception {

        Connection connection = null;
        Statement statement = null;

//        HashMap timeseriesMap =new HashMap<String, String>();
//        timeseriesMap.put("root.yanmoji.shenzhen.d2.axis1pos", "BOOLEAN, PLAIN");
//        timeseriesMap.put("root.yanmoji.shenzhen.d2.axis2pos", "INT32,TS_2DIFF");
//        timeseriesMap.put("root.yanmoji.shenzhen.d2.axis1vel", "INT64,RLE");
//        timeseriesMap.put("root.yanmoji.shenzhen.d2.axis2vel", "FLOAT,GORILLA");
//        timeseriesMap.put("root.yanmoji.shenzhen.d2.axis1torque", "BOOLEAN, RLE");
//        timeseriesMap.put("root.yanmoji.shenzhen.d2.axis2torque", "FLOAT,RLE");
//        timeseriesMap.put("root.yanmoji.shenzhen.d2.axis1set", "BOOLEAN, RLE");
//        timeseriesMap.put("root.yanmoji.shenzhen.d2.axis2set", "FLOAT,RLE");

        

       
        storageGroupList.add("root.vehicle_history");
        storageGroupList.add("root.vehicle_alarm");
        storageGroupList.add("root.vehicle_temp");
        storageGroupList.add("root.range_event");
        HashMap timeseriesMap = generateTimeseriesMapFromFile("/home/hadoop/xuyi/CreateTimeseries.txt");
        try {
            Class.forName("cn.edu.tsinghua.iotdb.jdbc.TsfileDriver");
            connection = DriverManager.getConnection("jdbc:tsfile://192.168.130.18:6667/", "root", "root");
            statement = connection.createStatement();

            setStorageGroup(statement, storageGroupList);
            System.out.println("Finish set storage group.");
            createTimeseries(statement, timeseriesMap);
            System.out.println("Finish create timeseries.");
            randomInsertData(statement, timeseriesMap);

            statement.execute("merge");
            statement.execute("flush");

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



