package win.shangyh.pros.dbutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

/**
 * Base on https://blog.csdn.net/wudiyong22/article/details/78893613
 */

public abstract class BaseDaoTest {

    private static IDatabaseConnection conn;
 
    private File tempFile;
 
    public static final String ROOT_URL = System.getProperty("user.dir") + "/tmp/dbutils";

    private AccountedTransactionManager transactionManager=new AccountedTransactionManager();

    private String jdbcUrl="jdbc:h2:file:~/tmp/dbutils/dbunit;AUTO_SERVER=TRUE";

    private String user="sa";

    private String password="";
 
    @Before
    public void setup() throws Exception {
        //get DataBaseSourceConnection
        conn = new DatabaseConnection(transactionManager.getConnection(jdbcUrl,user, password));
        
        //config database as oracle
        DatabaseConfig dbConfig = conn.getConfig();
        dbConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,  new H2DataTypeFactory());
    }
 
    @After
    public void teardown() throws Exception {
        if (conn != null) {
            conn.close();
        }
    }
 
    /**
     * 从xml文件中获取数据集，该文件保存的是用于断言的期望数据
     * 下一步可以通过ITable dbTable = xmlDataSet.getTable("表名")获取表结构和表数据，
     * FlatXmlDataSet用XML文件中该表的第一行数据来制定表的结构，所以，如果表的第一行
     * 有字段为空，则得到的表结构会与数据库表结构不一致，可能是字段顺序不同，也可以能
     * 是字段个数不同，因为xml文件中该表的后续的行会补充字段，如xml内容如下：
     * <dataset> 
     * <USER_INFO ID="1257436A2B3F4D31B4DF657043C30175" AGE="23" PHONE="12345678921"/>
     * <USER_INFO ID="AB2470AACFCA4E0BB6AF03B713DFDB99" NAME="休息休息" AGE="24"/>
     * </dataset>
     * 则得到的表结构为ID,AGE,PHONE,NAME
     * 而实际上数据库的表结构为ID,NAME,AGE,PHONE，可能还有更多字段，因为这些字段值都为空
     * 从而导致断言失败，因为表结构不一致，解决方法：
     * 首先修改xml文件，把表的第一行字段为空的用"[null]"占位符补上：
     * <USER_INFO ID="1257436A2B3F4D31B4DF657043C30175" NAME="[null]" AGE="23" ADDR="[null]" PHONE="12345678921"/>
     * 然后把ITable xmlTable = xmlDataSet.getTable(TABLE_USER_INFO);改成如下方式：
     * ReplacementDataSet replacementDataSet = createReplacementDataSet(xmlDataSet);
     * ITable xmlTable = replacementDataSet.getTable(TABLE_USER_INFO);
     */
    protected IDataSet getXmlDataSet(String name) throws DataSetException, IOException {
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        return builder.build(new FileInputStream(new File(ROOT_URL + name)));
    }
 
    /**
     * 从数据库中获取数据集
     * 下一步可以通过ITable dbTable = dbDataSet.getTable("表名")获取表结构和表数据，
     * 这里直接从数据库获取表结构，所以，不管字段是否为空，表结构都与数据库一致
     */
    protected IDataSet getDBDataSet() throws SQLException {
        return conn.createDataSet();
    }
 
    /**
     * 从数据库获取查询数据集
     * 通过自己的query语句到数据库查询结果，代码如下：
     * QueryDataSet queryDataSet = getQueryDataSet();
     * queryDataSet.addTable("tableName or other String", "select * from yourTableName");
     * ITable dbTable = queryDataSet.getTable("tableName or other String");
     * 根据需要，该结果集可以作为期望值，也可以作为实际值
     * 作为期望值：比如，要测试一个查询方法，则可以把该结果与被测试的查询方法返回的结果做比较
     * 作为实际值：拿该结果与xml文件设置的期望值对比
     */
    protected QueryDataSet getQueryDataSet() throws SQLException {
        return new QueryDataSet(conn);
    }
 
    /**
     * 从Excel获取数据集
     * @param name Excel文件名
     */
    protected XlsDataSet getXlsDataSet(String name) throws SQLException, DataSetException,
            IOException {
        InputStream is = new FileInputStream(new File(ROOT_URL + name));
        return new XlsDataSet(is);
    }
 
    /**
     * 备份整个数据库
     */
    protected void backupAll() throws Exception {
        // create DataSet from database.
        IDataSet ds = conn.createDataSet();
        // create temp file
        tempFile = File.createTempFile("temp", "xml");
        // write the content of database to temp file
        FlatXmlDataSet.write(ds, new FileWriter(tempFile), "UTF-8");
    }
 
    /**
     * 备份指定的表
     */
    protected void backupCustom(String... tableName) throws Exception {
        // back up specific files
        QueryDataSet qds = new QueryDataSet(conn);
        for (String str : tableName) {
            qds.addTable(str);
        }
        tempFile = File.createTempFile("temp", ".xml");
        FlatXmlDataSet.write(qds, new FileWriter(tempFile), "UTF-8");
    }
 
    /**
     * 回滚数据库
     * CLEAN_INSERT：DELETE_ALL和INSERT的组合，即先删除数据库的全部记录，然后再通过备份文件恢复；
     * UPDATE：用备份文件还原数据库中被修改的记录，前提是数据库表的数据要存在，如果缺少数据则会报错，
     * 如果有新增的数据，该数据不会被删掉；
     * 所以，用CLEAN_INSERT不管什么情况都可以回滚数据库，但如果有外键依赖会怎样？
     */
    protected void rollback() throws Exception {
        // get the temp file
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        IDataSet ds =builder.build(new FileInputStream(tempFile));
        // recover database
        DatabaseOperation.CLEAN_INSERT.execute(conn, ds);
    }
 
 
    /**
     * 清空指定表的数据，很少使用
     */
    protected void clearTable(String tableName) throws Exception {
        DefaultDataSet dataset = new DefaultDataSet();
        dataset.addTable(new DefaultTable(tableName));
        DatabaseOperation.DELETE_ALL.execute(conn, dataset);
    }
 
    /**
     * 验证指定的表是否为空
     */
    protected void verifyTableEmpty(String tableName) throws DataSetException, SQLException {
        Assert.assertEquals(0, conn.createDataSet().getTable(tableName).getRowCount());
    }
 
    /**
     * 验证指定的表是否非空
     */
    protected void verifyTableNotEmpty(String tableName) throws DataSetException, SQLException {
        Assert.assertNotEquals(0, conn.createDataSet().getTable(tableName).getRowCount());
    }
 
    /**
     * 如果xml文件中的数据集存在列的值为空，可以用一个"[null]"占位符代替，然后调用该方法把"[null]"替换成null
     */
    protected ReplacementDataSet createReplacementDataSet(IDataSet dataSet) {
        ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);
        // Configure the replacement dataset to replace '[NULL]' strings with null.
        replacementDataSet.addReplacementObject("[null]", null);
        return replacementDataSet;
    }
    
}