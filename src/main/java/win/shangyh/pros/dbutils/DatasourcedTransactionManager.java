package win.shangyh.pros.dbutils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DatasourcedTransactionManager extends AbstractTransactionManager{
    
    private DataSource dataSource;

    public Connection getConnection() throws SQLException {
        //从当前线程中获取Connection
        Connection conn =THREAD_LOCAL.get();

        if(conn==null || conn.isClosed()){
            //从数据源中获取数据库连接
            conn = dataSource.getConnection();
            //将conn绑定到当前线程
            THREAD_LOCAL.set(conn);
        }
        return conn;
    }

    public Connection getConnectionWithTrans() throws SQLException {
        return getConnection(true);
    }

    public Connection getConnectionWithoutTrans() throws SQLException {
        return getConnection(false);
    }
    public Connection getConnection(boolean startTrans) throws SQLException {
        Connection connection=getConnection();
        connection.setAutoCommit(!startTrans);
        return connection;
    }
    public void startTransaction() throws SQLException {
        Connection conn=getConnection();
        //开启事务
        conn.setAutoCommit(false);
    }
}