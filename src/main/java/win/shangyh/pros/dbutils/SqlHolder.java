package win.shangyh.pros.dbutils;

import lombok.Data;

@Data
public class SqlHolder{
    private String sql;
    private Object[] params;
    private String jdbcUrl;
    private String account;
    private String password;
}
