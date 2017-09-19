在开始使用之前，请依次安装TsFile和JDBC，先安装TsFile，再安装JDBC

# 安装TsFile到本地
> git clone git@github.com:thulab/tsfile.git
> cd tsfile
> mvn clean install -Dmaven.test.skip=true


# 安装JDBC到本地

> git clone git@github.com:thulab/iotdb-jdbc.git
> cd iotdb-jdbc
> mvn clean install -Dmaven.test.skip=true


程序运行的主类为TsfiledbTest.java