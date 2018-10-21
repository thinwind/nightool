package win.shangyh.pros.dbutils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AccountedTransactionManager extends AbstractTransactionManager{
    
    public Connection getConnection(String url,String user,String pwd) throws SQLException {
        //从当前线程中获取Connection
        Connection conn =THREAD_LOCAL.get();

        if(conn==null || conn.isClosed()){
            //从数据源中获取数据库连接
            conn = DriverManager.getConnection(url,user,pwd);
            //将conn绑定到当前线程
            THREAD_LOCAL.set(conn);
        }
        return conn;
    }

    public Connection getConnectionWithTrans(String url,String user,String pwd) throws SQLException {
        return getConnection(url,user,pwd,true);
    }

    public Connection getConnectionWithoutTrans(String url,String user,String pwd) throws SQLException {
        return getConnection(url,user,pwd,false);
    }
    public Connection getConnection(String url,String user,String pwd,boolean startTrans) throws SQLException {
        Connection connection=getConnection(url,user,pwd);
        connection.setAutoCommit(!startTrans);
        return connection;
    }
    public void startTransaction(String url,String user,String pwd) throws SQLException {
        Connection conn=getConnection(url,user,pwd);
        //开启事务
        conn.setAutoCommit(false);
    }
}