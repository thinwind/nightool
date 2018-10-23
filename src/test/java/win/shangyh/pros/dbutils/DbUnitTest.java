package win.shangyh.pros.dbutils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DbUnitTest {

	protected AccountedTransactionManager transactionManager = new AccountedTransactionManager();

	protected String jdbcUrl = "jdbc:h2:file:~/tmp/dbutils/dbunit;AUTO_SERVER=TRUE";

	protected String user = "sa";

	protected String password = "";

	AccountedSqlExecutor sqlExecutor = new AccountedSqlExecutor(transactionManager);
	SqlHolder sqlHolder;

	@Before
	public void setup() throws Exception {
		transactionManager.startTransaction(jdbcUrl, user, password);
		String sql = "CREATE TABLE `test_user` (" + "`id` int(11) NOT NULL AUTO_INCREMENT,"
				+ "`name` varchar(45) DEFAULT NULL," + "`age` int(11) DEFAULT NULL,"
				+ "`gender` varchar(45) DEFAULT NULL," + "`intro` varchar(255) DEFAULT NULL," + "PRIMARY KEY (`id`)"
				+ ")";

		sqlHolder = new SqlHolder();
		sqlHolder.setAccount(user);
		sqlHolder.setPassword(password);
		sqlHolder.setJdbcUrl(this.jdbcUrl);

		sqlHolder.setSql("DROP TABLE `test_user` IF EXISTS");
		sqlExecutor.executeSql(sqlHolder);
		sqlHolder.setSql(sql);
		sqlExecutor.executeSql(sqlHolder);
		transactionManager.commit();
	}

	@Test
	public void insertAndSelectTest() throws Exception {
		String inserSql = "INSERT INTO `test_user` (`name`, `age`, `gender`) VALUES ('zhangsan', '19', 'girl')";
		sqlHolder.setSql(inserSql);
		sqlExecutor.executeSqlToObjectArray(sqlHolder);

		String selectSql = "SELECT * FROM test_user";
		sqlHolder.setSql(selectSql);
		List<Map<String, Object>> selectResult = sqlExecutor.executeSqlToList(sqlHolder);
		Assert.assertEquals(1, selectResult.size());
		Assert.assertEquals("zhangsan", selectResult.get(0).get("name"));
		Assert.assertEquals(19, selectResult.get(0).get("age"));
		Assert.assertEquals("girl", selectResult.get(0).get("gender"));
		Assert.assertNotEquals(-1, selectResult.get(0).get("id"));
	}

	@Test
	public void testUpdateTest() throws SQLException {
		String inserSql = "INSERT INTO `test_user` (`name`, `age`, `gender`) VALUES ('lisi', '20', 'boy')";
		sqlHolder.setSql(inserSql);
		sqlExecutor.executeSqlToObjectArray(sqlHolder);

		String updateSql = "UPDATE `test_user` SET `age` = '22' WHERE (`name` = 'lisi')";
		sqlHolder.setSql(updateSql);
		sqlExecutor.executeSqlToBean(sqlHolder, Integer.class);

		String selectSql = "SELECT * FROM test_user where name='lisi'";
		sqlHolder.setSql(selectSql);
		List<Map<String, Object>> selectResult = sqlExecutor.executeSqlToList(sqlHolder);
		Assert.assertEquals(1, selectResult.size());
		Assert.assertEquals(22, selectResult.get(0).get("age"));
	}

	@Test
	public void testRollBack() throws SQLException {
		String inserSql = "INSERT INTO `test_user` (`name`, `age`, `gender`) VALUES ('mawu', '30', 'boy')";
		sqlHolder.setSql(inserSql);
		sqlExecutor.executeSqlToObjectArray(sqlHolder);
		transactionManager.commit();
		String updateSql = "UPDATE `test_user` SET `age` = '22' WHERE (`name` = 'mawu')";
		sqlHolder.setSql(updateSql);
		sqlExecutor.executeSqlToBean(sqlHolder, Integer.class);
		transactionManager.rollback();
		String selectSql = "SELECT * FROM test_user where name='mawu'";
		sqlHolder.setSql(selectSql);
		List<Map<String, Object>> selectResult = sqlExecutor.executeSqlToList(sqlHolder);
		Assert.assertEquals(1, selectResult.size());
		Assert.assertEquals(30, selectResult.get(0).get("age"));
	}

	@Test
	public void testDelete() throws SQLException {
		String inserSql = "INSERT INTO `test_user` (id,`name`, `age`, `gender`) VALUES (99999,'wangliu', '35', 'boy')";
		sqlHolder.setSql(inserSql);
		sqlExecutor.executeSqlToObjectArray(sqlHolder);
		transactionManager.commit();

		String selectSql = "SELECT * FROM test_user where id=99999";
		sqlHolder.setSql(selectSql);
		List<Map<String, Object>> selectResult = sqlExecutor.executeSqlToList(sqlHolder);
		Assert.assertEquals(1, selectResult.size());
		Assert.assertEquals(35, selectResult.get(0).get("age"));

		String deleteSql="delete from test_user where id=99999";
		sqlHolder.setSql(deleteSql);
		sqlExecutor.executeSql(sqlHolder);
		transactionManager.commit();

		selectSql = "SELECT * FROM test_user where id=99999";
		sqlHolder.setSql(selectSql);
		selectResult = sqlExecutor.executeSqlToList(sqlHolder);
		// System.out.println(selectResult.get(0));
		Assert.assertNull(selectResult.get(0));
	}

	@After
	public void tearsDown() {
		// sqlHolder.setSql("DROP TABLE `test_user` IF EXISTS");
		// transactionManager.closeSilently();
	}

}