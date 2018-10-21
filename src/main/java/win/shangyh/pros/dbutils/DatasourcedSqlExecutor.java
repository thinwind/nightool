package win.shangyh.pros.dbutils;

import java.sql.Connection;
import java.sql.SQLException;

public class DatasourcedSqlExecutor extends AbstractSqlExecutor {
    
    private DatasourcedTransactionManager transactionManager;

    public DatasourcedSqlExecutor(DatasourcedTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    protected Connection getConnection(SqlHolder sqlHolder) throws SQLException {
        return transactionManager.getConnection();
    }

    @Override
	protected AbstractTransactionManager getTransanctionManager() {
		return transactionManager;
	}
}