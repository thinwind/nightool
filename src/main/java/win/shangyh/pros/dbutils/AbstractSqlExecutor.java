package win.shangyh.pros.dbutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.MapHandler;

public abstract class AbstractSqlExecutor {

    protected static final MapHandler MAP_HANDLER=new MapHandler();

    protected static final ResultSetHandler<Object[]> OBJECT_ARRAY_HANDLER= new ResultSetHandler<Object[]>() {
        public Object[] handle(ResultSet rs) throws SQLException {
            if (!rs.next()) {
                return null;
            }
        
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            Object[] result = new Object[cols];
    
            for (int i = 0; i < cols; i++) {
                result[i] = rs.getObject(i + 1);
            }
    
            return result;
        }
    };
    public List<Map<String, Object>> executeSqlToList(SqlHolder sqlHolder) {
        QueryRunner runner = new QueryRunner();
        return executeSql(sqlHolder, runner,MAP_HANDLER);
    }

    /**
     * 执行SQL
     * 
     * @param sqlHolder      sql with parameters
     * @param pmdKnownBroken Some drivers don't support <br>
     *                       java.sql.ParameterMetaData.getParameterType(int); <br>
     *                       if pmdKnownBroken is set to true, we won't even try it;
     *                       <br>
     *                       if false, we'll try it, and if it breaks, <br>
     *                       we'll remember not to use it again. <br>
     * @return
     */
    public List<Map<String, Object>> executeSqlToList(SqlHolder sqlHolder, boolean pmdKnownBroken) {
        QueryRunner runner = new QueryRunner(pmdKnownBroken);
        return executeSql(sqlHolder, runner,MAP_HANDLER );
    }

    public List<Object[]> executeSqlToObjectArray(SqlHolder sqlHolder){
        QueryRunner runner = new QueryRunner();
        return executeSql(sqlHolder, runner,OBJECT_ARRAY_HANDLER);
    }

    public List<Object[]> executeSqlToObjectArray(SqlHolder sqlHolder, boolean pmdKnownBroken){
        QueryRunner runner = new QueryRunner(pmdKnownBroken);
        return executeSql(sqlHolder, runner,OBJECT_ARRAY_HANDLER);
    }

    public <T> List<T> executeSqlToBean(SqlHolder sqlHolder,Class<T> beanCls){
        QueryRunner runner = new QueryRunner();
        ResultSetHandler<T> handler=new BeanHandler<>(beanCls);
        return executeSql(sqlHolder, runner, handler);
    }

    public <T> List<T> executeSqlToBean(SqlHolder sqlHolder,Class<T> beanCls,boolean pmdKnownBroken){
        QueryRunner runner = new QueryRunner(pmdKnownBroken);
        ResultSetHandler<T> handler=new BeanHandler<>(beanCls);
        return executeSql(sqlHolder, runner, handler);
    }

    private <T> List<T> executeSql(SqlHolder sqlHolder, QueryRunner runner,ResultSetHandler<T> handler) {
        Connection connection = null;
        AbstractTransactionManager transactionManager=getTransanctionManager();

        try {
            connection = getConnection(sqlHolder);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("连接获取失败", e);
        }

        try {
            List<T> result = runner.execute(connection, sqlHolder.getSql(), handler,
                    sqlHolder.getParams());
            transactionManager.commit();
            return result;
        } catch (Exception e) {
            try {
                transactionManager.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            throw new RuntimeException("SQL执行失败", e);
        } finally {
            transactionManager.closeSilently();
        }
    }

    protected abstract Connection getConnection(SqlHolder sqlHolder) throws SQLException;

    protected abstract AbstractTransactionManager getTransanctionManager();
}