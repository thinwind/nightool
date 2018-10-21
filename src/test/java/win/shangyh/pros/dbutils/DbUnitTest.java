package win.shangyh.pros.dbutils;
 

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.junit.Test;
public class DbUnitTest extends BaseDaoTest {
	
	/**
	 * 用户信息表表名
	 */
	private static final String TABLE_USER_INFO = "USER_INFO";
	
	@Test
	public void updateUserInfo() throws Exception {
        System.out.println("Hello Dbunit");
		try {
			//backup table from DB
			backupCustom(TABLE_USER_INFO);
			//被测试的方法
			// dbUnitTestService.updateUserInfo("AB2470AACFCA4E0BB6AF03B713DFDB99", "休息休息");
			
	        //get actual tableInfo from DB
	        IDataSet dbDataSet = getDBDataSet();
	        ITable dbTable = dbDataSet.getTable(TABLE_USER_INFO);
	        
	        //get expect Information from xml file
	        IDataSet xmlDataSet = getXmlDataSet("expect_user_info.xml");
	        ReplacementDataSet replacementDataSet = createReplacementDataSet(xmlDataSet);
	        ITable xmlTable = replacementDataSet.getTable(TABLE_USER_INFO);
	        
	        //exclude some columns which don't want to compare result
	        dbTable = DefaultColumnFilter.excludedColumnsTable(dbTable, new String[]{"ID"});
	        xmlTable = DefaultColumnFilter.excludedColumnsTable(xmlTable, new String[]{"ID"});
	        
//	        Assert.assertEquals(dbTable.getRowCount(), xmlTable.getRowCount());//比较行数
	        // Assertion.assertEquals(xmlTable, dbTable);
		} catch (Exception e) {
            e.printStackTrace();
		}finally{
			rollback();
		}
        
	}
 
}