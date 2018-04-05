package ch.admin.bar.siard2.jdbc;

import java.sql.*;
import static org.junit.Assert.*;
import org.junit.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.sqlparser.SqlLiterals;
import ch.admin.bar.siard2.jdbcx.*;
import ch.admin.bar.siard2.oracle.*;

public class OracleStatementTester extends BaseStatementTester
{
  private static final ConnectionProperties _cp = new ConnectionProperties();   
  private static final String _sDB_URL = OracleDriver.getUrl(_cp.getHost()+":"+_cp.getPort()+":"+_cp.getInstance());
  private static final String _sDBA_USER = _cp.getDbaUser();
  private static final String _sDBA_PASSWORD = _cp.getDbaPassword();
  private static final String _sDB_USER = _cp.getUser();
  private static final String _sDB_PASSWORD = _cp.getPassword();
  private OracleStatement _stmtOracle = null;

  private static final String _sSQL_DDL = "CREATE TABLE TESTTABLE(CCHAR CHARACTER,\r\n" +
    "CVARCHAR VARCHAR(256),\r\n" +
    "CCLOB CLOB(4M),\r\n" +
    "CNCHAR NCHAR,\r\n" +
    "CNCHAR_VARYING NCHAR VARYING(256),\r\n" +
    "CNCLOB NCLOB(4G),\r\n" +
    "CXML XML,\r\n" +
    "CBINARY BINARY,\r\n" +
    "CVARBINARY VARBINARY(256),\r\n" +
    "CBLOB BLOB,\r\n" +
    "CNUMERIC NUMERIC(10, 3),\r\n" +
    "CDECIMAL DECIMAL,\r\n" +
    "CSMALLINT SMALLINT,\r\n" +
    "CINTEGER INTEGER,\r\n" +
    "CBIGINT BIGINT,\r\n" +
    "CFLOAT FLOAT(7),\r\n" +
    "CREAL REAL,\r\n" +
    "CDOUBLE DOUBLE PRECISION,\r\n" +
    "CBOOLEAN BOOLEAN,\r\n" +
    "CDATE DATE,\r\n" +
    "CTIME TIME(3),\r\n" +
    "CTIMESTAMP TIMESTAMP(9),\r\n" +
    "CINTERVALYEAR INTERVAL YEAR(2) TO MONTH,\r\n" +
    "CINTERVALDAY INTERVAL DAY TO MINUTE,\r\n" +
    "CINTERVALSECOND INTERVAL SECOND(2, 5),\r\n" +
    "PRIMARY KEY(CINTEGER),\r\n" +
    "UNIQUE(CCHAR))";
  private static final String _sSQL_CLEAN = "DROP TABLE TESTTABLE RESTRICT";
  private static final String _sSQL_QUERY = "SELECT * FROM SYS.USER_TABLES";

  @BeforeClass
  public static void setUpClass()
  {
    try 
    { 
      OracleDataSource dsOracle = new OracleDataSource();
      dsOracle.setUrl(_sDB_URL);
      dsOracle.setUser(_sDBA_USER);
      dsOracle.setPassword(_sDBA_PASSWORD);
      OracleConnection connOracle = (OracleConnection)dsOracle.getConnection();
      /** drop and create the test user
      try { TestOracleDatabase.dropUser(connOracle, _sDB_USER); }
      catch(SQLException se) {}
      TestOracleDatabase.createUser(connOracle, _sDB_USER, _sDB_PASSWORD);
      **/
      /* drop and create the test databases */
      new TestOracleDatabase(connOracle);
      TestOracleDatabase.grantSchema(connOracle, TestOracleDatabase._sTEST_SCHEMA, _sDB_USER);
      new TestSqlDatabase(connOracle);
      TestOracleDatabase.grantSchema(connOracle, TestSqlDatabase._sTEST_SCHEMA, _sDB_USER);
      connOracle.close();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* setUpClass */
  
  @Before
  public void setUp()
  {
    try 
    { 
      OracleDataSource dsOracle = new OracleDataSource();
      dsOracle.setUrl(_sDB_URL);
      dsOracle.setUser(_sDB_USER);
      dsOracle.setPassword(_sDB_PASSWORD);
      OracleConnection connOracle = (OracleConnection)dsOracle.getConnection();
      connOracle.setAutoCommit(false);
      _stmtOracle = (OracleStatement)connOracle.createStatement();
      setStatement(_stmtOracle);
    }
    catch(SQLException se) { fail(se.getClass().getName()+": "+se.getMessage()); }
  } /* setUp */

  @After
  public void tearDown()
  {
    super.tearDown();
  } /* tearDown */
  
  @Test
  public void testClass()
  {
    assertEquals("Wrong statement class!", OracleStatement.class, _stmtOracle.getClass());
  } /* testClass */

  @Test
  public void testExecute()
  {
    enter();
    try { _stmtOracle.execute(_sSQL_CLEAN); }
    catch(SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
    try { _stmtOracle.execute(_sSQL_DDL); }
    catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testExecute */
  
  @Test
  @Override
  public void testExecute_String_int()
  {
    enter();
    try { _stmtOracle.execute(_sSQL_CLEAN); }
    catch(SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
    try { _stmtOracle.execute(_sSQL_DDL, Statement.NO_GENERATED_KEYS); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testExecute_String_int */
  
  @Test
  @Override
  public void testExecute_String_AInt()
  {
    enter();
    try { _stmtOracle.execute(_sSQL_CLEAN); }
    catch(SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
    try { _stmtOracle.execute(_sSQL_DDL, new int[] {1,2}); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testExecute_String_AInt */
  
  @Test
  @Override
  public void testExecute_String_AString()
  {
    enter();
    try { _stmtOracle.execute(_sSQL_CLEAN); }
    catch(SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
    try { _stmtOracle.execute(_sSQL_DDL, new String[]{"COL_A", "COL_B"}); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testExecute_String_AString */
  
  @Test
  @Override
  public void testExecuteUpdate()
  {
    enter();
    try { _stmtOracle.executeUpdate(_sSQL_CLEAN); }
    catch(SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
    try { _stmtOracle.executeUpdate(_sSQL_DDL); }
    catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testExecuteUpdate */
  
  @Test
  @Override
  public void testExecuteQuery()
  {
    enter();
    try { _stmtOracle.executeQuery(_sSQL_QUERY); }
    catch(SQLTimeoutException ste) { fail(EU.getExceptionMessage(ste)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testExecuteQuery */
  
  @Test
  @Override
  public void testGetResultSet()
  {
    enter();
    try 
    {
      _stmtOracle.execute(_sSQL_QUERY);
      _stmtOracle.getResultSet(); 
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetResultSet */

  @Test
  public void testGetGeneratedKeys()
  {
    enter();
    try { _stmtOracle.getGeneratedKeys(); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
  } /* testGetGeneratedKeys */
  
  
  @Test
  @Override
  public void testSetCursorName()
  {
    enter();
    try { _stmtOracle.setCursorName("testCursor"); }
    catch(SQLFeatureNotSupportedException sfnse) { System.out.println(EU.getExceptionMessage(sfnse)); }
    catch(SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
  } /* testSetCursorName */
  
  @Test
  public void testExecuteSelectSizes()
  {
    StringBuilder sbSql = new StringBuilder("SELECT COUNT(*) AS RECORDS");
    for (int iColumn = 0; iColumn < TestOracleDatabase._listCdSimple.size(); iColumn++)
    {
      TestColumnDefinition tcd = TestOracleDatabase._listCdSimple.get(iColumn);
      if (tcd.getType().startsWith("BLOB") ||
        tcd.getType().startsWith("CLOB") ||
        tcd.getType().startsWith("NCLOB") ||
        tcd.getType().startsWith("BFILE"))
      {
        sbSql.append(",\r\n  SUM(OCTET_LENGTH(");
        sbSql.append(SqlLiterals.formatId(tcd.getName()));
        sbSql.append(")) AS ");
        sbSql.append(SqlLiterals.formatId(tcd.getName()+"_SIZE"));
      }
    }
    sbSql.append("\r\nFROM ");
    sbSql.append(TestOracleDatabase.getQualifiedSimpleTable().format());
    try
    {
      ResultSet rs = _stmtOracle.executeQuery(sbSql.toString());
      ResultSetMetaData rsmd = rs.getMetaData();
      while(rs.next())
      {
        for (int iColumn = 0; iColumn < rsmd.getColumnCount(); iColumn++)
        {
          String sColumnName = rsmd.getColumnLabel(iColumn+1);
          long lValue = rs.getLong(iColumn+1);
          System.out.println(sColumnName+": "+String.valueOf(lValue));
        }
      }
      rs.close();
    }
    catch(SQLException se) { System.out.println(EU.getExceptionMessage(se)); }
  } /* testExecuteSelectSizes */
  
} /* OracleStatementTester */
