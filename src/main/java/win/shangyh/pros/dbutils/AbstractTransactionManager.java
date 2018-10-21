package win.shangyh.pros.dbutils;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractTransactionManager {

    protected static final ThreadLocal<Connection> THREAD_LOCAL=new ThreadLocal<>();

    /**
     * @Method: rollback
     * @Description:回滚事务
     * @Anthor:
     */
    public void rollback() throws SQLException {
        Connection conn= THREAD_LOCAL.get();
        if (conn!=null && !conn.getAutoCommit())
            conn.rollback();
    }

    /**
     * @Method: commit
     * @Description:提交事务
     * @Anthor:
     */
    public void commit() throws SQLException {
        Connection conn= THREAD_LOCAL.get();
        if (conn!=null && !conn.getAutoCommit())
            conn.commit();
    }

    /**
     * @Method: close
     * @Description:关闭数据库连接
     * @Anthor:
     *
     */
    public void close() throws SQLException {
        Connection conn= THREAD_LOCAL.get();
        if (conn!=null && !conn.isClosed()){
            conn.close();
        }
        THREAD_LOCAL.remove();
    }

    /**
     * 关闭数据库连接，忽略异常
     */
    public void closeSilently() {
        Connection conn= THREAD_LOCAL.get();
        try {
            if (conn!=null && !conn.isClosed()){
                conn.close();
            }
        } catch (Exception e) {
            //close sliently
        }
        THREAD_LOCAL.remove();
    }
}