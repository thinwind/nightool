package win.shangyh.pros.dbutils;

import java.sql.Connection;
import java.sql.SQLException;

public class AccountedSqlExecutor extends AbstractSqlExecutor {

    private AccountedTransactionManager transactionManager;

    public AccountedSqlExecutor(AccountedTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    protected Connection getConnection(SqlHolder sqlHolder) throws SQLException {
        return transactionManager.getConnection(sqlHolder.getJdbcUrl(), sqlHolder.getAccount(), sqlHolder.getPassword());
    }

    @Override
	protected AbstractTransactionManager getTransanctionManager() {
		return transactionManager;
	}

}