package ch.admin.bar.siard2.jdbc;

import java.io.*;
import java.math.*;
import java.text.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import javax.xml.datatype.*;
import static org.junit.Assert.*;
import org.junit.*;
import ch.admin.bar.siard2.jdbcx.*;
import ch.admin.bar.siard2.oracle.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;
import ch.enterag.utils.jdbc.*;
import ch.enterag.sqlparser.*;
import ch.enterag.sqlparser.identifier.*;

public class OracleResultSetTester 
  extends BaseResultSetTester 
{
	private static final ConnectionProperties _cp = new ConnectionProperties();
	private static final String _sDB_URL = OracleDriver.getUrl(_cp.getHost() + ":" + _cp.getPort() + ":" + _cp.getInstance());
  private static final String _sDBA_USER = _cp.getDbaUser();
  private static final String _sDBA_PASSWORD = _cp.getDbaPassword();
	private static final String _sDB_USER = _cp.getUser();
	private static final String _sDB_PASSWORD = _cp.getPassword();
  private static long _lMsTotalStart = 0;
  private static long _lMsTotal = 0;
  private static long _lMsConnect = 0;
  private static long _lMsExecute = 0;
  private static long _lMsTestDatabase = 0;
  private static final int iBUFSIZ = 8192;

  private static String getTableQuery(QualifiedId qiTable, List<TestColumnDefinition> listCd)
  {
    StringBuilder sbSql = new StringBuilder("SELECT\r\n  ");
    for (int iColumn = 0; iColumn < listCd.size(); iColumn++)
    {
      if (iColumn > 0)
        sbSql.append(",\r\n  ");
      TestColumnDefinition tcd = listCd.get(iColumn);
      sbSql.append(tcd.getName());
    }
    sbSql.append("\r\nFROM ");
    sbSql.append(qiTable.format());
    return sbSql.toString();
  } /* getTableQuery */
  
  private static String _sNativeQuerySimple = getTableQuery(TestOracleDatabase.getQualifiedSimpleTable(),TestOracleDatabase._listCdSimple);
  private static String _sNativeQueryComplex = getTableQuery(TestOracleDatabase.getQualifiedComplexTable(),TestOracleDatabase._listCdComplex);
  private static String _sSqlQuerySimple = getTableQuery(TestSqlDatabase.getQualifiedSimpleTable(),TestSqlDatabase._listCdSimple);
  private static String _sSqlQueryComplex = getTableQuery(TestSqlDatabase.getQualifiedComplexTable(),TestSqlDatabase._listCdComplex);

  @SuppressWarnings("deprecation")
  private static List<TestColumnDefinition> getListCdSimple()
  {
    List<TestColumnDefinition> listCdSimple = new ArrayList<TestColumnDefinition>();
    listCdSimple.add(new TestColumnDefinition("CCHAR_5","CHAR(5)","wxyZ"));
    listCdSimple.add(new TestColumnDefinition("CVARCHAR_255","VARCHAR(255)",TestUtils.getString(92)));
    listCdSimple.add(new TestColumnDefinition("CCLOB_2M","CLOB(2M)",TestUtils.getString(1000000)));
    listCdSimple.add(new TestColumnDefinition("CNCHAR_5","NCHAR(5)","Auää"));
    listCdSimple.add(new TestColumnDefinition("CNVARCHAR_127","NCHAR VARYING(127)",TestUtils.getNString(53)));
    listCdSimple.add(new TestColumnDefinition("CNCLOB_1M","NCLOB(1M)",TestUtils.getNString(500000)));
    listCdSimple.add(new TestColumnDefinition("CXML","XML","<a>foöäpwkfèégopàèwerkgv fviodsjv jdsjd idsjidsjsiudojiou operkv &lt; and &amp; ifjeifj</a>"));
    listCdSimple.add(new TestColumnDefinition("CBINARY_5","BINARY(5)",new byte[] {5,-4,3,-2} ));
    listCdSimple.add(new TestColumnDefinition("CVARBINARY_255","VARBINARY(255)",TestUtils.getBytes(76) ));
    listCdSimple.add(new TestColumnDefinition("CBLOB","BLOB",TestUtils.getBytes(500000)));
    listCdSimple.add(new TestColumnDefinition("CNUMERIC_31","NUMERIC(31)",BigInteger.valueOf(987654321098765432l)));
    listCdSimple.add(new TestColumnDefinition("CDECIMAL_15_5","DECIMAL(15,5)",new BigDecimal(BigInteger.valueOf(9876543210987l),5)));
    listCdSimple.add(new TestColumnDefinition("CSMALLINT","SMALLINT",Short.valueOf((short)23000)));
    listCdSimple.add(new TestColumnDefinition("CINTEGER","INTEGER",Integer.valueOf(987654321)));
    listCdSimple.add(new TestColumnDefinition("CBIGINT","BIGINT",Long.valueOf(-987654321098765432l)));
    listCdSimple.add(new TestColumnDefinition("CFLOAT_10","FLOAT(10)",Float.valueOf((float)Math.PI)));
    listCdSimple.add(new TestColumnDefinition("CREAL","REAL",Float.valueOf((float)Math.E)));
    listCdSimple.add(new TestColumnDefinition("CDOUBLE","DOUBLE PRECISION",Double.valueOf(Math.PI)));
    listCdSimple.add(new TestColumnDefinition("CBOOLEAN","BOOLEAN",Boolean.valueOf(false)));
    listCdSimple.add(new TestColumnDefinition("CDATE","DATE",new Date(2016-1900,12,2)));
    listCdSimple.add(new TestColumnDefinition("CTIME","TIME",new Time(14,24,12)));
    listCdSimple.add(new TestColumnDefinition("CTIMESTAMP","TIMESTAMP(9)",new Timestamp(2016-1900,12,2,14,24,12,987654321)));
    listCdSimple.add(new TestColumnDefinition("CINTERVAL_YEAR_3_MONTH","INTERVAL YEAR(3) TO MONTH",new Interval(1,3,6)));
    listCdSimple.add(new TestColumnDefinition("CINTERVAL_DAY_2_SECONDS_6","INTERVAL DAY(2) TO SECOND(6)",new Interval(1,0,17,54,23,123456000l)));
    return listCdSimple;
  }
  public static List<TestColumnDefinition> _listCdSimple = getListCdSimple();

  private static List<TestColumnDefinition> getListAdSimple()
  {
    List<TestColumnDefinition> listAdSimple = new ArrayList<TestColumnDefinition>();
    listAdSimple.add(new TestColumnDefinition("TABLEID","INTEGER",Integer.valueOf(3)));
    listAdSimple.add(new TestColumnDefinition("TRANSCRIPTION","CLOB",TestUtils.getString(1234567)));
    listAdSimple.add(new TestColumnDefinition("SOUND","BLOB",TestUtils.getBytes(1123456)));
    return listAdSimple;
  }
  public static List<TestColumnDefinition> _listAdSimple = getListAdSimple();
  
  private static List<TestColumnDefinition> getListAdComplex()
  {
    List<TestColumnDefinition> listAdComplex = new ArrayList<TestColumnDefinition>();
    listAdComplex.add(new TestColumnDefinition("ID","INTEGER",Integer.valueOf(3)));
    listAdComplex.add(new TestColumnDefinition("NESTED_ROW",TestSqlDatabase.getQualifiedSimpleType().format(),getListAdSimple()));
    return listAdComplex;
  }
  public static List<TestColumnDefinition> _listAdComplex = getListAdComplex();

  private static List<TestColumnDefinition> getListCdComplex()
  {
    List<TestColumnDefinition> listCdComplex = new ArrayList<TestColumnDefinition>();
    listCdComplex.add(new TestColumnDefinition("CID","INTEGER",Integer.valueOf(987654321)));
    listCdComplex.add(new TestColumnDefinition("CUDT",TestSqlDatabase.getQualifiedComplexType().format(),_listAdComplex));
    return listCdComplex;
  }
  public static List<TestColumnDefinition> _listCdComplex = getListCdComplex();
  
  private static List<TestColumnDefinition> getListCdArray()
  {
    List<TestColumnDefinition> listCd = new ArrayList<TestColumnDefinition>();
    listCd.add(new TestColumnDefinition("CARRAY_4[1]","CHARACTER(3)","USD"));
    listCd.add(new TestColumnDefinition("CARRAY_4[2]","CHARACTER(3)",null));
    listCd.add(new TestColumnDefinition("CARRAY_4[3]","CHARACTER(3)","CHF"));
    listCd.add(new TestColumnDefinition("CARRAY_4[4]","CHARACTER(3)","EUR"));
    return listCd;
  }
  public static List<TestColumnDefinition> _listCdArray = getListCdArray();

  private static List<TestColumnDefinition> getListAdObject()
  {
    List<TestColumnDefinition> listCd = new ArrayList<TestColumnDefinition>();
    listCd.add(new TestColumnDefinition("LABEL","VARCHAR(255)","E"));
    listCd.add(new TestColumnDefinition("VALUE","BINARY_DOUBLE",Double.valueOf(Math.E)));
    return listCd;
  }
  public static List<TestColumnDefinition> _listAdObject = getListAdObject();
  
  private static List<TestColumnDefinition> getListAdNativeComplex()
  {
    List<TestColumnDefinition> listCd = new ArrayList<TestColumnDefinition>();
    listCd.add(new TestColumnDefinition("LABEL","VARCHAR(255)","Native Complex"));
    listCd.add(new TestColumnDefinition("LARGE","BLOB",TestUtils.getBytes(1000)));
    listCd.add(new TestColumnDefinition("OBJ",TestOracleDatabase.getQualifiedObjectType().format(),_listAdObject));
    return listCd;
  }
  public static List<TestColumnDefinition> _listAdNativeComplex = getListAdNativeComplex();

  private static List<TestColumnDefinition> getListNativeComplex()
  {
    List<TestColumnDefinition> listCd = new ArrayList<TestColumnDefinition>();
    listCd.add(new TestColumnDefinition("CID","INTEGER",Integer.valueOf(123456789)));
    listCd.add(new TestColumnDefinition("CUDT_VARRAY","CHARACTER(3) ARRAY[4]",_listCdArray));
    listCd.add(new TestColumnDefinition("CUDT_OBJECT",TestOracleDatabase.getQualifiedObjectType().format(),_listAdObject));
    listCd.add(new TestColumnDefinition("CUDT_COMPLEX",TestOracleDatabase.getQualifiedComplexType().format(),_listAdNativeComplex));
    return listCd;
  }
  public static List<TestColumnDefinition> _listCdNativeComplex = getListNativeComplex();
  
	@BeforeClass
	public static void setUpClass() 
	{
    if (_lMsTotalStart == 0)
      _lMsTotalStart = System.currentTimeMillis(); 
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
      long lMsTestDatabaseStart = System.currentTimeMillis();
      new TestOracleDatabase(connOracle);
      TestOracleDatabase.grantSchema(connOracle, TestOracleDatabase._sTEST_SCHEMA, _sDB_USER);
      new TestSqlDatabase(connOracle);
      TestOracleDatabase.grantSchema(connOracle, TestSqlDatabase._sTEST_SCHEMA, _sDB_USER);
      _lMsTestDatabase = _lMsTestDatabase + System.currentTimeMillis() - lMsTestDatabaseStart;
      connOracle.close();
		}
		catch (SQLException se) { fail(se.getClass().getName() + ": " + se.getMessage()); }
	}

  @AfterClass
  public static void tearDownClass()
  {
    _lMsTotal = System.currentTimeMillis() - _lMsTotalStart;
    System.out.println("Total: "+String.valueOf(_lMsTotal)+", Connect: "+String.valueOf(_lMsConnect)+", Execute: "+String.valueOf(_lMsExecute)+", Test Database: "+String.valueOf(_lMsTestDatabase));
  }
  
  private Connection closeResultSet()
    throws SQLException
  {
    Connection conn = null;
    ResultSet rs = getResultSet();
    if (rs != null)
    {
      if (!rs.isClosed())
      {
        Statement stmt = rs.getStatement();
        rs.close();
        setResultSet(null);
        if (!stmt.isClosed())
        {
          conn = stmt.getConnection();
          stmt.close();
          conn.commit();
        }
      }
    }
    return conn;
  } /* closeResultSet */
  
  private void openResultSet(Connection conn, String sQuery)
    throws SQLException
  {
    closeResultSet();
    Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
    long lMsExecuteStart = System.currentTimeMillis();
    ResultSet rs = stmt.executeQuery(sQuery);
    _lMsExecute = _lMsExecute + System.currentTimeMillis() - lMsExecuteStart; 
    setResultSet(rs);
    rs.next();
  } /* openResultSet */
  
	@Before
	public void setUp() {
		OracleDataSource dsOracle;

		try 
		{
			dsOracle = new OracleDataSource();
			dsOracle.setUrl(_sDB_URL);
			dsOracle.setUser(_sDB_USER);
			dsOracle.setPassword(_sDB_PASSWORD);
      long lMsConnectStart = System.currentTimeMillis();
			Connection conn = dsOracle.getConnection();
      _lMsConnect = _lMsConnect + System.currentTimeMillis() - lMsConnectStart;
			conn.setAutoCommit(false);
      openResultSet(conn,_sSqlQuerySimple);
		}
		catch (SQLException se) { fail(se.getClass().getName() + ": " + se.getMessage()); }
	}
	
	@After
	public void tearDown()
	{
	  super.tearDown();
	}

  private TestColumnDefinition findColumnDefinition(List<TestColumnDefinition> listCd, String sName)
  {
    TestColumnDefinition tcd = null;
    for (Iterator<TestColumnDefinition> iterCd = listCd.iterator(); iterCd.hasNext(); )
    {
      TestColumnDefinition tcdTry = iterCd.next();
      if (sName.equals(tcdTry.getName()))
        tcd = tcdTry;
    }
    return tcd;
  } /* findColumnDefinition */
	
	@Test
	public void testClass() 
	{
    enter();
		assertEquals("Wrong result set class!", OracleResultSet.class, getResultSet().getClass());
	} /* testClass */

  @Override
  @Test
  public void testFindColumn()
  {
    enter();
    try { getResultSet().findColumn("CCHAR_5"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testFindColumn */
  
  @Override
  @Test
  public void testWasNull()
  {
    enter();
    try 
    {
      getResultSet().getObject(1);
      getResultSet().wasNull();
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testWasNull */

  @Override
  @Test
  public void testGetString()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CVARCHAR_255");
      String s = getResultSet().getString(tcd.getName());
      assertEquals("Invalid String!",(String)tcd.getValue(),s);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetString */
  
  @Override
  @Test
  public void testGetNString()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CNVARCHAR_127");
      String s = getResultSet().getNString(tcd.getName());
      assertEquals("Invalid String!",(String)tcd.getValue(),s);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetNString */
  
  @Override
  @Test
  public void testGetClob()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CCLOB_2M");
      Clob clob = getResultSet().getClob(tcd.getName());
      String s = clob.getSubString(1l,(int)clob.length());
      assertEquals("Invalid Clob!",(String)tcd.getValue(),s);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetClob */
  
  @Override
  @Test
  public void testGetNClob()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CNCLOB_1M");
      NClob nclob = getResultSet().getNClob(tcd.getName());
      String s = nclob.getSubString(1l,(int)nclob.length());
      assertEquals("Invalid NClob!",(String)tcd.getValue(),s);
    }
    catch(SQLFeatureNotSupportedException fnse) { System.out.println("testNClob: "+EU.getExceptionMessage(fnse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetNClob */
  
  @Override
  @Test
  public void testGetSqlXml()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CXML");
      SQLXML sqlxml = getResultSet().getSQLXML(tcd.getName());
      String s = sqlxml.getString().replaceAll("\\n\\s*","");
      assertEquals("Invalid SQLXML!",(String)tcd.getValue(),s);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetSqlXml */
  
  @Override
  @Test
  public void testGetBytes()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CVARBINARY_255");
      byte[] buf = getResultSet().getBytes(tcd.getName());
      assertTrue("Invalid byte array!",Arrays.equals((byte[])tcd.getValue(),buf));
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetBytes */
  
  @Override
  @Test
  public void testGetBlob()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CBLOB");
      Blob blob = getResultSet().getBlob(tcd.getName());
      byte[] buf = blob.getBytes(1l,(int)blob.length());
      assertTrue("Invalid Blob!",Arrays.equals((byte[])tcd.getValue(),buf));
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetBlob */
  
  @Override
  @Test
  public void testGetBigDecimal()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CDECIMAL_15_5");
      BigDecimal bd = getResultSet().getBigDecimal(tcd.getName());
      assertTrue("Invalid BigDecimal!",bd.compareTo((BigDecimal)tcd.getValue()) == 0);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetBigDecimal */
  
  @Override
  @Test
  @SuppressWarnings("deprecation")
  public void testGetBigDecimal_Int()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CDECIMAL_15_5");
      BigDecimal bd = getResultSet().getBigDecimal(tcd.getName(),3);
      assertEquals("Invalid BigDecimal!",((BigDecimal)tcd.getValue()).setScale(3,RoundingMode.DOWN),bd);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetBigDecimal_String_Int */
  
  @Override
  @Test
  public void testGetByte()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CBOOLEAN");
      byte by = getResultSet().getByte(tcd.getName());
      assertEquals("Invalid byte!",((Boolean)tcd.getValue()).booleanValue()?(byte)1:(byte)0,by);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetByte */

  @Override
  @Test
  public void testGetShort()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CSMALLINT");
      short sh = getResultSet().getShort(tcd.getName());
      assertEquals("Invalid short!",((Short)tcd.getValue()).shortValue(),sh);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetShort */
  
  @Override
  @Test
  public void testGetInt()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CINTEGER");
      int i = getResultSet().getInt(tcd.getName());
      assertEquals("Invalid int!",((Integer)tcd.getValue()).intValue(),i);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetInt */
  
  @Override
  @Test
  public void testGetLong()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CBIGINT");
      long l = getResultSet().getLong(tcd.getName());
      assertEquals("Invalid long!",((Long)tcd.getValue()).longValue(),l);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetLong */
  
  @Override
  @Test
  public void testGetFloat()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CREAL");
      float f = getResultSet().getFloat(tcd.getName());
      assertEquals("Invalid float!",(Float)tcd.getValue(),Float.valueOf(f));
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetFloat */
  
  @Override
  @Test
  public void testGetDouble()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CDOUBLE");
      double d = getResultSet().getDouble(tcd.getName());
      assertEquals("Invalid double!",(Double)tcd.getValue(),Double.valueOf(d));
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetDouble */
  
  @Override
  @Test
  public void testGetBoolean()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CBOOLEAN");
      boolean b = getResultSet().getBoolean(tcd.getName());
      assertEquals("Invalid boolean!",((Boolean)tcd.getValue()).booleanValue(),b);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetBoolean */
  
  @Override
  @Test
  public void testGetDate()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CDATE");
      Date date = getResultSet().getDate(tcd.getName());
      assertEquals("Invalid Date!",(Date)tcd.getValue(),date);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetDate */
  
  @Override
  @Test
  public void testGetDate_Calendar()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CDATE");
      Calendar cal = new GregorianCalendar();
      Date date = getResultSet().getDate(tcd.getName(),cal);
      assertEquals("Invalid Date!",(Date)tcd.getValue(),date);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetDate_Calendar */
  
  @Override
  @Test
  public void testGetTime()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CTIME");
      Time time = getResultSet().getTime(tcd.getName());
      assertEquals("Invalid Time!",(Time)tcd.getValue(),time);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetTime */
  
  @Override
  @Test
  public void testGetTime_Calendar()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CTIME");
      Calendar cal = new GregorianCalendar();
      Time time = getResultSet().getTime(tcd.getName(),cal);
      assertEquals("Invalid Time!",(Time)tcd.getValue(),time);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetTime_Calendar */
  
  @Override
  @Test
  public void testGetTimestamp()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CTIMESTAMP");
      Timestamp ts = getResultSet().getTimestamp(tcd.getName());
      assertEquals("Invalid Timestamp!",(Timestamp)tcd.getValue(),ts);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetTimestamp */

  @Override
  @Test
  public void testGetTimestamp_Calendar()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CTIMESTAMP");
      Calendar cal = new GregorianCalendar();
      Timestamp ts = getResultSet().getTimestamp(tcd.getName(),cal);
      assertEquals("Invalid Timestamp!",(Timestamp)tcd.getValue(),ts);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetTimestamp_Calendar */
  
  @Test
  public void testGetDuration()
  {
    enter();
    try 
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CINTERVAL_YEAR_3_MONTH");
      Duration duration = getBaseResultSet().getDuration(tcd.getName());
      assertEquals("Invalid Duration!",(Interval)tcd.getValue(),Interval.fromDuration(duration));
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  }
  
  @Override
  @Test
  public void testGetAsciiStream()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CVARCHAR_255");
      InputStream is = getResultSet().getAsciiStream(tcd.getName());
      byte[] buf = new byte[((String)tcd.getValue()).length()];
      is.read(buf);
      if (is.read() != -1)
        fail("Invalid length of ASCII stream!");
      is.close();
      String s = new String(buf);
      assertEquals("Invalid String!",(String)tcd.getValue(),s);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  } /* testGetAsciiStream */
  
  @Override
  @Test
  @SuppressWarnings("deprecation")
  public void testGetUnicodeStream()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CNVARCHAR_127");
      Reader rdr = new InputStreamReader(getResultSet().getUnicodeStream(tcd.getName()),"UTF-16");
      char[] cbuf = new char[((String)tcd.getValue()).length()];
      int iLength = 0;
      for (int iRead = rdr.read(cbuf); (iRead != -1) && (iLength < cbuf.length); iRead = rdr.read(cbuf,iLength,cbuf.length-iLength))
        iLength = iLength + iRead;
      rdr.close();
      assertEquals("Invalid length of character stream!",cbuf.length,iLength);
      String s = new String(cbuf);
      assertEquals("Invalid String!",(String)tcd.getValue(),s);
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  } /* testGetUnicodeStream */
  
  @Override
  @Test
  public void testGetCharacterStream()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CVARCHAR_255");
      Reader rdr = getResultSet().getCharacterStream(tcd.getName());
      char[] cbuf = new char[((String)tcd.getValue()).length()];
      rdr.read(cbuf);
      if (rdr.read() != -1)
        fail("Invalid length of character stream!");
      rdr.close();
      String s = new String(cbuf);
      assertEquals("Invalid String!",(String)tcd.getValue(),s);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  } /* testGetCharacterStream */
  
  @Override
  @Test
  public void testGetNCharacterStream()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CNVARCHAR_127");
      Reader rdr = getResultSet().getNCharacterStream(tcd.getName());
      char[] cbuf = new char[((String)tcd.getValue()).length()];
      rdr.read(cbuf);
      if (rdr.read() != -1)
        fail("Invalid length of character stream!");
      rdr.close();
      String s = new String(cbuf);
      assertEquals("Invalid String!",(String)tcd.getValue(),s);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  } /* testGetNCharacterStream */
  
  @Override
  @Test
  public void testGetBinaryStream()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CVARBINARY_255");
      InputStream is = getResultSet().getBinaryStream(tcd.getName());
      byte[] buf = new byte[((byte[])tcd.getValue()).length];
      is.read(buf);
      if (is.read() != -1)
        fail("Invalid length of binary stream!");
      is.close();
      assertTrue("Invalid byte array!",Arrays.equals((byte[])tcd.getValue(),buf));
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
  } /* testGetBinaryStream */
  
  @Override
  @Test
  public void testGetArray()
  {
    enter();
    // TODO: Add array to TestSqlDatabase
  } /* testGetArray */
  
  @Override
  @Test
  public void testGetRef()
  {
    enter();
  } /* testGetRef */
  
  @Override
  @Test
  public void testGetRowId()
  {
    enter();
  } /* testGetRowId */
  
  @Override
  @Test
  public void testGetUrl()
  {
    enter();
  } /* testGetUrl */
  
  @Override
  @Test
  public void testGetObject()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CINTERVAL_YEAR_3_MONTH");
      Object o = getResultSet().getObject(tcd.getName());
      assertEquals("Invalid Interval!",((Interval)tcd.getValue()).toDuration(),(Duration)o);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetObject */

  @Override
  @Test
  public void testGetObject_Class()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdSimple,"CDATE");
      Date date = getResultSet().getObject(tcd.getName(),Date.class);
      assertEquals("Invalid Date!",(Date)tcd.getValue(),date);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetObject_Class */
  
  @Override
  @Test
  public void testGetObject_Map()
  {
    enter();
    try
    {
      openResultSet(getResultSet().getStatement().getConnection(),_sSqlQueryComplex);
      TestColumnDefinition tcd = findColumnDefinition(
        TestSqlDatabase._listCdComplex,"CUDT");
      Map<String,Class<?>> map = new HashMap<String,Class<?>>();
      map.put(tcd.getType(), tcd.getValue().getClass());
      Object o = getResultSet().getObject(tcd.getName(),map);
      assertEquals("Invalid Udt!",tcd.getValue(),o);
    }
    catch(SQLFeatureNotSupportedException snse) { System.out.println("testGetObject_Map: "+EU.getExceptionMessage(snse)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetObject_Map */
  
  @Test
  public void testGetObjectSqlSimple()
  {
    enter();
    try
    {
      openResultSet(getResultSet().getStatement().getConnection(),_sSqlQuerySimple);
      for (int iColumn = 0; iColumn < TestSqlDatabase._listCdSimple.size(); iColumn++)
      {
        TestColumnDefinition tcd = TestSqlDatabase._listCdSimple.get(iColumn);
        Object o = getResultSet().getObject(tcd.getName());
        if (tcd.getName().equals("CCHAR_5") ||
            tcd.getName().equals("CNCHAR_5"))
        {
          if (o instanceof String)
          {
            String s = (String)o;
            s = s.substring(0,((String)tcd.getValue()).length());
            assertEquals("Invalid value for "+tcd.getType()+"!",tcd.getValue(),s);
          }
          else
            fail("Type String expected for "+tcd.getType()+"!");
        }
        else if (tcd.getName().equals("CVARCHAR_255") ||
          tcd.getName().equals("CNVARCHAR_127"))
        {
          if (o instanceof String)
          {
            String s = (String)o;
            assertEquals("Invalid value for "+tcd.getType()+"!",tcd.getValue(),s);
          }
          else
            fail("Type String expected for "+tcd.getType()+"!");
        }
        else if (tcd.getName().equals("CCLOB_2M") ||
          tcd.getName().equals("CNCLOB_1M"))
        {
          if (o instanceof Clob)
          {
            Clob clob = (Clob)o;
            assertEquals("Invalid value for "+tcd.getType()+"!",tcd.getValue(),clob.getSubString(1l,(int)clob.length()));
          }
          else
            fail("Type Clob expected for "+tcd.getType()+"!");
        }
        else if (tcd.getName().equals("CXML"))
        {
          if (o instanceof SQLXML)
          {
            SQLXML sqlxml = (SQLXML)o;
            String s = sqlxml.getString().trim();
            assertEquals("Invalid value for "+tcd.getType()+"!",(String)tcd.getValue(),s);
          }
          else
            fail("Type SQLXML expected for "+tcd.getType()+"!");
        }
        else if (tcd.getName().equals("CBINARY_5"))
        {
          if (o instanceof byte[])
          {
            byte[] buf = (byte[])o;
            buf = Arrays.copyOf(buf, ((byte[])tcd.getValue()).length);
            assertTrue("Invalid value for "+tcd.getType()+"!",Arrays.equals((byte[])tcd.getValue(), buf));
          }
          else
            fail("Type byte[] expected for "+tcd.getType()+"!");
        }
        else if (tcd.getName().equals("CVARBINARY_255"))
        {
          if (o instanceof byte[])
          {
            byte[] buf = (byte[])o;
            assertTrue("Invalid value for "+tcd.getType()+"!",Arrays.equals((byte[])tcd.getValue(), buf));
          }
          else
            fail("Type byte[] expected for "+tcd.getType()+"!");
        }
        else if (tcd.getName().equals("CBLOB"))
        {
          if ( o instanceof Blob)
          {
            Blob blob = (Blob)o;
            assertTrue("Invalid value for "+tcd.getType()+"!",Arrays.equals((byte[])tcd.getValue(),blob.getBytes(1l,(int)blob.length())));
          }
          else
            fail("Type Blob expected for "+tcd.getType()+"!");
        }
        else if (tcd.getName().equals("CDECIMAL_15_5") ||
          tcd.getName().equals("CNUMERIC_31"))
        {
          if (o instanceof BigDecimal)
          {
            BigDecimal bd = (BigDecimal)o;
            Object oExpected = tcd.getValue();
            if (oExpected instanceof BigDecimal)
            {
              BigDecimal bdExpected = (BigDecimal)o;
              assertEquals("Invalid value for "+tcd.getType()+"!",bdExpected,bd);
            }
            else if (oExpected instanceof BigInteger)
            {
              BigInteger biExpected = (BigInteger)oExpected;
              BigInteger bi = bd.toBigInteger();
              assertEquals("Invalid value for "+tcd.getType()+"!",biExpected,bi);
            }
          }
          else
            fail("Type BigDecimal expected for "+tcd.getType()+"!");
        }
        else if (tcd.getName().equals("CSMALLINT") ||
                 tcd.getName().equals("CINTEGER") ||
                 tcd.getName().equals("CBIGINT"))
        {
          if (o instanceof BigDecimal)
          {
            BigDecimal bd = (BigDecimal)o;
            BigDecimal bdExpected = null;
            if (tcd.getValue() instanceof Short)
            {
              Short sh = (Short)tcd.getValue();
              bdExpected = BigDecimal.valueOf(sh.longValue());
            }
            else if (tcd.getValue() instanceof Integer)
            {
              Integer i = (Integer)tcd.getValue();
              bdExpected = BigDecimal.valueOf(i.longValue());
            }
            else if (tcd.getValue() instanceof Long)
            {
              Long l = (Long)tcd.getValue();
              bdExpected = BigDecimal.valueOf(l.longValue());
            }
            assertEquals("Invalid value for "+tcd.getType()+"!",bdExpected,bd);
          }
          else
            fail("Type BigDecimal expected for "+tcd.getType()+"!");
        }
        else if (tcd.getName().equals("CREAL"))
        {
          if (o instanceof Float)
          {
            Float f = (Float)o;
            assertEquals("Invalid value for "+tcd.getType()+"!",(Float)tcd.getValue(),f);
          }
          else
            fail("Type Float expected for "+tcd.getType()+"!");
        }
        else if (tcd.getName().equals("CDOUBLE") ||
                 tcd.getName().equals("CFLOAT_10"))
        {
          if (o instanceof Double)
          {
            Double dExpected = Double.NaN;
            if (tcd.getValue() instanceof Float)
            {
              Float f = (Float)tcd.getValue();
              dExpected = Double.valueOf(f.doubleValue());
            }
            else if (tcd.getValue() instanceof Double)
            {
              dExpected = (Double)tcd.getValue();
            }
            Double d = (Double)o;
            assertEquals("Invalid value for "+tcd.getType()+"!",dExpected,d,0.00000000000001);
          }
          else
            fail("Type Double expected for "+tcd.getType()+"!");
        }
        else if (tcd.getName().equals("CBOOLEAN"))
        {
          if (o instanceof Short)
          {
            Short sh = (Short)o;
            assertEquals("Invalid value for "+tcd.getType()+"!",(Boolean)tcd.getValue(),Boolean.valueOf(sh != 0));
          }
        }
        else if (tcd.getName().equals("CDATE"))
        {
          if (o instanceof Timestamp)
          {
            Timestamp ts = (Timestamp)o;
            Date date = new Date(ts.getTime());
            assertEquals("Invalid value for "+tcd.getType()+"!",(Date)tcd.getValue(),date);
          }
          else
            fail("Type Date expected for "+tcd.getType()+"!");
        }
        else if (tcd.getName().equals("CTIME"))
        {
          if (o instanceof Timestamp)
          {
            Timestamp ts = (Timestamp)o;
            Time time = new Time(ts.getTime());
            assertEquals("Invalid value for "+tcd.getType()+"!",(Time)tcd.getValue(),time);
          }
          else
            fail("Type Time expected for "+tcd.getType()+"!");
        }
        else if (tcd.getName().equals("CTIMESTAMP"))
        {
          if (o instanceof Timestamp)
          {
            Timestamp ts = (Timestamp)o;
            assertEquals("Invalid value for "+tcd.getType()+"!",(Timestamp)tcd.getValue(),ts);
          }
          else
            fail("Type Timestamp expected for "+tcd.getType()+"!");
        }
        else if (tcd.getName().equals("CINTERVAL_YEAR_3_MONTH") ||
                 tcd.getName().equals("CINTERVAL_DAY_2_SECONDS_6"))
        {
          if (o instanceof Duration)
          {
            Duration duration = (Duration)o;
            assertEquals("Invalid value for "+tcd.getType()+"!",((Interval)tcd.getValue()).toDuration(),duration);
          }
          else
            fail("Type Duration expected for "+tcd.getType()+"!");
        }
        else
          fail("Unexpected column: "+tcd.getName()+"!");
      }
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetObjectSqlSimple */
  
  @Test
  public void testGetObjectNativeSimple()
  {
    enter();
    try
    {
      openResultSet(getResultSet().getStatement().getConnection(),
        _sNativeQuerySimple);
      for (int iColumn = 0; iColumn < TestOracleDatabase._listCdSimple.size(); iColumn++)
      {
        TestColumnDefinition tcd = TestOracleDatabase._listCdSimple
          .get(iColumn);
        Object o = getResultSet().getObject(tcd.getName());
        if (tcd.getName().equals("CURITYPE"))
        {
          if (o instanceof Struct)
          {
            Struct struct = (Struct) o;
            String s = "http://"+(String)struct.getAttributes()[0];
            assertEquals("Invalid value for " + tcd.getType() + "!",
              tcd.getValue(), s);
          }
          else
            fail("Type String expected for " + tcd.getType() + "!");
        }
        else if (tcd.getName().equals("CBFILE"))
        {
          if (o instanceof Blob)
          {
            Blob blob = (Blob) o;
            byte[] bufBlob = blob.getBytes(1l, (int) blob.length());
            byte[] buf = null;
            try
            {
              FileInputStream fis = new FileInputStream(
                TestOracleDatabase._sBFILE_SHARE + File.separator
                  + (String) tcd.getValue());
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              buf = new byte[iBUFSIZ];
              for (int iRead = fis.read(buf); iRead != -1; iRead = fis.read(buf))
                baos.write(buf, 0, iRead);
              baos.close();
              fis.close();
              buf = baos.toByteArray();
            }
            catch (IOException ie) { fail(EU.getExceptionMessage(ie)); }
            assertTrue(
              "Invalid value for " + tcd.getType() + "!",
              Arrays.equals(buf, bufBlob));
          }
          else
            fail("Type Blob expected for " + tcd.getType() + "!");
        }
        else if (tcd.getName().equals("CRAW_255"))
        {
          if (o instanceof byte[])
          {
            byte[] buf = (byte[]) o;
            assertTrue("Invalid value for " + tcd.getType() + "!",
              Arrays.equals((byte[]) tcd.getValue(), buf));
          }
          else
            fail("Type byte[] expected for " + tcd.getType() + "!");
        }
        else if (tcd.getName().equals("CCHAR_50")
          || tcd.getName().equals("CNCHAR_50"))
        {
          if (o instanceof String)
          {
            String s = (String) o;
            s = s.substring(0, ((String) tcd.getValue()).length());
            assertEquals("Invalid value for " + tcd.getType() + "!",
              tcd.getValue(), s);
          }
          else
            fail("Type String expected for " + tcd.getType() + "!");
        }
        else if (tcd.getName().equals("CVARCHAR2_255") || 
                 tcd.getName().equals("CNVARCHAR2_255")|| 
                 tcd.getName().equals("CLONG"))
        {
          if (o instanceof String)
          {
            String s = (String) o;
            assertEquals("Invalid value for " + tcd.getType() + "!",
              tcd.getValue(), s);
          }
          else
            fail("Type String expected for " + tcd.getType() + "!");
        }
        else if (tcd.getName().equals("CCLOB")
          || tcd.getName().equals("CNCLOB"))
        {
          if (o instanceof Clob)
          {
            Clob clob = (Clob) o;
            assertEquals("Invalid value for " + tcd.getType() + "!",
              tcd.getValue(),
              clob.getSubString(1l, (int) clob.length()));
          }
          else
            fail("Type Clob expected for " + tcd.getType() + "!");
        }
        else if (tcd.getName().equals("CXMLTYPE"))
        {
          if (o instanceof SQLXML)
          {
            SQLXML sqlxml = (SQLXML) o;
            String s = sqlxml.getString();
            s = s.replaceAll("\\n\\s*", "");
            assertEquals("Invalid value for " + tcd.getType() + "!",
              (String) tcd.getValue(), s);
          }
          else
            fail("Type SQLXML expected for " + tcd.getType() + "!");
        }
        else if (tcd.getName().equals("CBLOB"))
        {
          if (o instanceof Blob)
          {
            Blob blob = (Blob) o;
            byte[] buf = (byte[]) tcd.getValue();
            assertTrue(
              "Invalid value for " + tcd.getType() + "!",
              Arrays.equals(buf, blob.getBytes(1l, (int) blob.length())));
          }
          else
            fail("Type Blob expected for " + tcd.getType() + "!");
        }
        else if (tcd.getName().equals("CSMALLINT")
          || tcd.getName().equals("CINTEGER")
          || tcd.getName().equals("CBIGINT"))
        {
          if (o instanceof BigDecimal)
          {
            BigDecimal bd = (BigDecimal) o;
            BigDecimal bdExpected = null;
            if (tcd.getValue() instanceof Short)
            {
              Short sh = (Short) tcd.getValue();
              bdExpected = BigDecimal.valueOf(sh.longValue());
            }
            else if (tcd.getValue() instanceof Integer)
            {
              Integer i = (Integer) tcd.getValue();
              bdExpected = BigDecimal.valueOf(i.longValue());
            }
            else if (tcd.getValue() instanceof Long)
            {
              Long l = (Long) tcd.getValue();
              bdExpected = BigDecimal.valueOf(l.longValue());
            }
            assertEquals("Invalid value for " + tcd.getType() + "!",
              bdExpected, bd);
          }
          else
            fail("Type BigDecimal expected for " + tcd.getType() + "!");
        }
        else if (tcd.getName().equals("CNUMBER_15_5"))
        {
          if (o instanceof BigDecimal)
          {
            BigDecimal bd = (BigDecimal) o;
            assertEquals("Invalid value for " + tcd.getType() + "!",
              (BigDecimal) tcd.getValue(), bd);
          }
          else
            fail("Type BigDecimal expected for " + tcd.getType() + "!");
        }
        else if (tcd.getName().equals("CBOOLEAN"))
        {
          if (o instanceof BigDecimal)
          {
            BigDecimal bd = (BigDecimal) o;
            Boolean b = Boolean.TRUE;
            if (bd.equals(BigDecimal.ZERO))
              b = Boolean.FALSE;
            assertEquals("Invalid value for " + tcd.getType() + "!",
              (Boolean) tcd.getValue(), b);
          }
          else
            fail("Type BigDecimal expected for " + tcd.getType() + "!");
        }
        else if (tcd.getName().equals("CFLOAT"))
        {
          if (o instanceof BigDecimal)
          {
            BigDecimal bd = (BigDecimal) o;
            assertEquals("Invalid value for " + tcd.getType() + "!",
              (Double) tcd.getValue(), Double.valueOf(bd.doubleValue()), 0.0000000000001E-17);
          }
          else
            fail("Type Double expected for " + tcd.getType() + "!");
        }
        else if (tcd.getName().equals("CBINARY_FLOAT"))
        {
          if (o instanceof Float)
          {
            Float f = (Float) o;
            assertEquals("Invalid value for " + tcd.getType() + "!",
              (Float)tcd.getValue(), f, 0.00000001E-17);
          }
          else
            fail("Type Float expected for " + tcd.getType() + "!");
        }
        else if (tcd.getName().equals("CBINARY_DOUBLE"))
        {
          if (o instanceof Double)
          {
            Double d = (Double) o;
            assertEquals("Invalid value for " + tcd.getType() + "!",
              (Double)tcd.getValue(), d, 0.0000000000001E-17);
          }
          else
            fail("Type Double expected for " + tcd.getType() + "!");
        }
        else if (tcd.getName().equals("CDATE"))
        {
          if (o instanceof Timestamp)
          {
            Timestamp ts = (Timestamp) o;
            Date date = new Date(ts.getTime());
            assertEquals("Invalid value for " + tcd.getType() + "!",
              (Date) tcd.getValue(), date);
          }
          else
            fail("Type Date expected for " + tcd.getType() + "!");
        }
        else if (tcd.getName().equals("CTIMESTAMP")
          || tcd.getName().equals("CTIMESTAMP_TZ")
          || tcd.getName().equals("CTIMESTAMP_LOCAL_TZ"))
        {
          if (o instanceof Timestamp)
          {
            Timestamp ts = (Timestamp) o;
            assertEquals("Invalid value for " + tcd.getType() + "!",
              (Timestamp) tcd.getValue(), ts);
          }
          else
            fail("Type Timestamp expected for " + tcd.getType() + "!");
        }
        else if (tcd.getName().equals("CINTERVAL_YEAR_TO_MONTH")
          || tcd.getName().equals("CINTERVAL_DAY_TO_SECOND"))
        {
          if (o instanceof Duration)
          {
            Duration duration = (Duration) o;
            assertEquals("Invalid value for " + tcd.getType() + "!",
              ((Interval) tcd.getValue()).toDuration(), duration);
          }
          else
            fail("Type Duration expected for " + tcd.getType() + "!");
        }
        else
          fail("Unexpected column: " + tcd.getName() + "!");
      }
    }
    catch (SQLException se)
    {
      fail(EU.getExceptionMessage(se));
    }
  } /* testGetObjectNativeSimple */
  
  private boolean equalsStructValue(Struct struct,List<TestColumnDefinition> listAd)
    throws SQLException, ParseException
  {
    boolean bEqual = false;
    if (listAd.size() == struct.getAttributes().length)
    {
      bEqual = true;
      for (int iAttribute = 0; iAttribute < listAd.size(); iAttribute++)
      {
        TestColumnDefinition tad = listAd.get(iAttribute);
        Object o = struct.getAttributes()[iAttribute];
        if (o instanceof Clob)
        {
          Clob clob = (Clob)o;
          o = clob.getSubString(1l, (int)clob.length());
        }
        else if (o instanceof Blob)
        {
          Blob blob = (Blob)o;
          o = blob.getBytes(1l, (int)blob.length());
        }
        else if (o instanceof SQLXML)
        {
          SQLXML sqlxml = (SQLXML)o;
          o = sqlxml.getString();
        }
        if (o.getClass().equals(tad.getValue().getClass()))
        {
          if (o instanceof String)
          {
            String s = (String)o;
            String sExpected = (String)tad.getValue();
            o = s.substring(0,sExpected.length());
          }
          if (o instanceof byte[])
          {
            byte[] buf = (byte[])o;
            byte[] bufExpected = (byte[])tad.getValue();
            assertTrue("Invalid value for "+tad.getType()+"!",Arrays.equals(bufExpected,buf));
          }
          else
            assertEquals("Invalid value for "+tad.getType()+"!",tad.getValue(),o);
        }
        else if ((o instanceof BigDecimal) && (tad.getValue() instanceof Integer))
        {
          BigDecimal bd = (BigDecimal)o;
          assertEquals("Invalid value for "+tad.getType()+"!",(Integer)tad.getValue(),Integer.valueOf(bd.intValueExact()));
        }
        else if ((o instanceof Struct) && (tad.getValue() instanceof List<?>))
        {
          Struct structSub = (Struct)o;
          QualifiedId qiTypeExpected = new QualifiedId(tad.getType());
          QualifiedId qiType = new QualifiedId(structSub.getSQLTypeName());
          assertEquals("Invalid value for Struct!",qiTypeExpected,qiType);
          @SuppressWarnings("unchecked")
          List<TestColumnDefinition> listAdSub = (List<TestColumnDefinition>)tad.getValue();
          assertTrue("Invalid value for "+tad.getType()+"!",equalsStructValue(structSub,listAdSub));
        }
        else
          fail("Error: "+tad.getType()+": "+tad.getValue().getClass().getName()+"/"+o.getClass().getName());
      }
    }
    return bEqual;
  } /* equalsStructValue */
  
  private boolean equalsArrayValue(Array array,List<TestColumnDefinition> listCd)
    throws SQLException, ParseException
  {
    boolean bEqual = false;
    Object[] ao = (Object[])array.getArray();
    if (listCd.size() == ao.length)
    {
      bEqual = true;
      for (int iElement = 0; iElement < listCd.size(); iElement++)
      {
        TestColumnDefinition tcd = listCd.get(iElement);
        Object o = ao[iElement];
        if (o == null)
          assertTrue("Non-matching NULL-Value!",tcd.getValue() == null);
        else
        {
          if (o instanceof Clob)
          {
            Clob clob = (Clob)o;
            o = clob.getSubString(1l, (int)clob.length());
          }
          else if (o instanceof Blob)
          {
            Blob blob = (Blob)o;
            o = blob.getBytes(1l, (int)blob.length());
          }
          else if (o instanceof SQLXML)
          {
            SQLXML sqlxml = (SQLXML)o;
            o = sqlxml.getString();
          }
          if (o.getClass().equals(tcd.getValue().getClass()))
          {
            if (o instanceof String)
            {
              String s = (String)o;
              String sExpected = (String)tcd.getValue();
              o = s.substring(0,sExpected.length());
            }
            if (o instanceof byte[])
            {
              byte[] buf = (byte[])o;
              byte[] bufExpected = (byte[])tcd.getValue();
              assertTrue("Invalid value for "+tcd.getType()+"!",Arrays.equals(bufExpected,buf));
            }
            else
              assertEquals("Invalid value for "+tcd.getType()+"!",tcd.getValue(),o);
          }
          else if ((o instanceof BigDecimal) && (tcd.getValue() instanceof Integer))
          {
            BigDecimal bd = (BigDecimal)o;
            assertEquals("Invalid value for "+tcd.getType()+"!",(Integer)tcd.getValue(),Integer.valueOf(bd.intValueExact()));
          }
          else if ((o instanceof Struct) && (tcd.getValue() instanceof List<?>))
          {
            Struct structSub = (Struct)o;
            QualifiedId qiTypeExpected = new QualifiedId(tcd.getType());
            QualifiedId qiType = new QualifiedId(structSub.getSQLTypeName());
            assertEquals("Invalid value for Struct!",qiTypeExpected,qiType);
            @SuppressWarnings("unchecked")
            List<TestColumnDefinition> listAdSub = (List<TestColumnDefinition>)tcd.getValue();
            assertTrue("Invalid value for "+tcd.getType()+"!",equalsStructValue(structSub,listAdSub));
          }
          else
            fail("Error: "+tcd.getType()+": "+tcd.getValue().getClass().getName()+"/"+o.getClass().getName());
        }
      }
    }
    return bEqual;
  } /* equalsArrayValue */
  
  @Test
  public void testGetObjectSqlComplex()
  {
    enter();
    try
    {
      openResultSet(getResultSet().getStatement().getConnection(),_sSqlQueryComplex);
      for (int iColumn = 0; iColumn < TestSqlDatabase._listCdComplex.size(); iColumn++)
      {
        TestColumnDefinition tcd = TestSqlDatabase._listCdComplex.get(iColumn);
        Object o = getResultSet().getObject(tcd.getName());
        if (tcd.getName().equals("CID"))
        {
          if (o instanceof BigDecimal)
          {
            BigDecimal bd = (BigDecimal)o;
            assertEquals("Invalid value for "+tcd.getType()+"!",(Integer)tcd.getValue(),Integer.valueOf(bd.intValueExact()));
          }
          else
            fail("Type Integer expected for "+tcd.getType()+"!");
        }
        else if (tcd.getName().equals("CDISTINCT"))
        {
          if (o instanceof String)
          {
            String s = (String)o;
            s = s.substring(0,((String)tcd.getValue()).length());
            assertEquals("Invalid type for "+tcd.getType()+"!",(String)tcd.getValue(),s);
          }
          else
            fail("Type String expected for "+tcd.getType()+"!");
        }
        else if (tcd.getName().equals("CUDT"))
        {
          if (o instanceof Struct)
          {
            Struct struct = (Struct)o;
            QualifiedId qiTypeExpected = new QualifiedId(tcd.getType());
            QualifiedId qiType = new QualifiedId(struct.getSQLTypeName());
            assertEquals("Invalid value for Struct!",qiTypeExpected,qiType);
            @SuppressWarnings("unchecked")
            List<TestColumnDefinition> listAdComplex = (List<TestColumnDefinition>)tcd.getValue();
            assertTrue("Invalid value for "+tcd.getType()+"!",equalsStructValue(struct,listAdComplex));
          }
          else
            fail("Type Stuct expected for "+tcd.getType()+"!");
        }
        else
          fail("Unexpected column: "+tcd.getName()+"!");
      }
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ParseException pe) { fail(EU.getExceptionMessage(pe)); }
  } /* testGetObjectSqlComplex */
  
  @Test
  public void testGetObjectNativeComplex()
  {
    enter();
    try
    {
      openResultSet(getResultSet().getStatement().getConnection(),_sNativeQueryComplex);
      for (int iColumn = 0; iColumn < TestOracleDatabase._listCdComplex.size(); iColumn++)
      {
        TestColumnDefinition tcd = TestOracleDatabase._listCdComplex.get(iColumn);
        Object o = getResultSet().getObject(tcd.getName());
        if (tcd.getName().equals("CID"))
        {
          if (o instanceof BigDecimal)
          {
            BigDecimal bd = (BigDecimal)o;
            assertEquals("Invalid value for "+tcd.getType()+"!",(Integer)tcd.getValue(),Integer.valueOf(bd.intValueExact()));
          }
          else
            fail("Type Integer expected for "+tcd.getType()+"!");
        }
        else if (tcd.getName().equals("CUDT_OBJECT") ||
          tcd.getName().equals("CUDT_COMPLEX"))
        {
          if (o instanceof Struct)
          {
            Struct struct = (Struct)o;
            // System.out.println(((oracle.sql.STRUCT)struct).dump());
            QualifiedId qiTypeExpected = new QualifiedId(tcd.getType());
            QualifiedId qiType = new QualifiedId(struct.getSQLTypeName());
            assertEquals("Invalid value for Struct!",qiTypeExpected,qiType);
            @SuppressWarnings("unchecked")
            List<TestColumnDefinition> listAdComplex = (List<TestColumnDefinition>)tcd.getValue();
            assertTrue("Invalid value for "+tcd.getType()+"!",equalsStructValue(struct,listAdComplex));
          }
          else
            fail("Type Stuct expected for "+tcd.getType()+"!");
        }
        else if (tcd.getName().equals("CUDT_VARRAY"))
        {
          if (o instanceof Array)
          {
            Array array = (Array)o;
            String sBaseType = array.getBaseTypeName();
            @SuppressWarnings("unchecked")
            List<TestColumnDefinition> listCdArray = (List<TestColumnDefinition>)tcd.getValue();
            assertEquals("Invalid ARRAY type!",listCdArray.get(0).getType(),sBaseType);
            assertTrue("Invalid value for "+tcd.getType()+"!",equalsArrayValue(array,listCdArray));
          }
          else
            fail("Type Stuct expected for "+tcd.getType()+"!");
        }
        else
          fail("Unexpected column: "+tcd.getName()+"!");
      }
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ParseException pe) { fail(EU.getExceptionMessage(pe)); }
  } /* testGetObjectNativeComplex */

  @Override
  @Test
  public void testUpdateNull()
  {
    enter();
    try { getResultSet().updateNull("CCHAR_5"); }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNull */
  
  @Override
  @Test
  public void testUpdateString()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CVARCHAR_255");
      getResultSet().updateString(tcd.getName(),(String)tcd.getValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateString */
  
  @Override
  @Test
  public void testUpdateNString()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CNVARCHAR_127");
      getResultSet().updateNString(tcd.getName(),(String)tcd.getValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNString */
  
  @Override
  @Test
  public void testUpdateClob()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CCLOB_2M");
      Clob clob = getResultSet().getStatement().getConnection().createClob();
      clob.setString(1l,(String)tcd.getValue());
      getResultSet().updateClob(tcd.getName(),clob);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateClob */

  @Override
  @Test
  public void testUpdateClob_Reader()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CCLOB_2M");
      Reader rdr = new StringReader((String)tcd.getValue());
      getResultSet().updateClob(tcd.getName(),rdr);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateClob_Reader */
  
  @Override
  @Test
  public void testUpdateClob_Reader_Long()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CCLOB_2M");
      Reader rdr = new StringReader((String)tcd.getValue());
      getResultSet().updateClob(tcd.getName(),rdr,((String)tcd.getValue()).length());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateClob_Reader_Long */
  
  @Override
  @Test
  public void testUpdateNClob()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CNCLOB_1M");
      NClob nclob = getResultSet().getStatement().getConnection().createNClob();
      nclob.setString(1l, (String)tcd.getValue());
      getResultSet().updateNClob(tcd.getName(),nclob);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNClob */
  
  @Override
  @Test
  public void testUpdateNClob_Reader()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CNCLOB_1M");
      Reader rdr = new StringReader((String)tcd.getValue());
      getResultSet().updateNClob(tcd.getName(),rdr);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNClob_Reader */
  
  @Override
  @Test
  public void testUpdateNClob_Reader_Long()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CNCLOB_1M");
      Reader rdr = new StringReader((String)tcd.getValue());
      getResultSet().updateNClob(tcd.getName(),rdr,((String)tcd.getValue()).length());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNClob_Reader_Long */
  
  
  @Override
  @Test
  public void testUpdateSqlXml()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CXML");
      SQLXML sqlxml = getResultSet().getStatement().getConnection().createSQLXML();
      sqlxml.setString((String)tcd.getValue());
      getResultSet().updateSQLXML(tcd.getName(),sqlxml);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateSqlXml */
  
  @Override
  @Test
  public void testUpdateBytes()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CVARBINARY_255");
      getResultSet().updateBytes(tcd.getName(),(byte[])tcd.getValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBytes */
  
  @Override
  @Test
  public void testUpdateBlob()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CBLOB");
      Blob blob = getResultSet().getStatement().getConnection().createBlob();
      blob.setBytes(1, (byte[])tcd.getValue());
      getResultSet().updateBlob(tcd.getName(),blob);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBlob */

  @Override
  @Test
  public void testUpdateBlob_InputStream()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CBLOB");
      InputStream is = new ByteArrayInputStream((byte[])tcd.getValue());
      getResultSet().updateBlob(tcd.getName(),is);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBlob_InputStream */
  
  @Override
  @Test
  public void testUpdateBlob_InputStream_Long()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CBLOB");
      InputStream is = new ByteArrayInputStream((byte[])tcd.getValue());
      getResultSet().updateBlob(tcd.getName(),is,((byte[])tcd.getValue()).length);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBlob_String_InputStream_Long */
  
  @Override
  @Test
  public void testUpdateBigDecimal()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CDECIMAL_15_5");
      getResultSet().updateBigDecimal(tcd.getName(),(BigDecimal)tcd.getValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBigDecimal */
  
  @Override
  @Test
  public void testUpdateByte()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CBOOLEAN");
      getResultSet().updateByte(tcd.getName(),((Boolean)tcd.getValue()).booleanValue()?(byte)1:(byte)0);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateByte */
  
  @Override
  @Test
  public void testUpdateShort()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CSMALLINT");
      getResultSet().updateShort(tcd.getName(),((Short)tcd.getValue()).shortValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateShort */
  
  @Override
  @Test
  public void testUpdateInt()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CINTEGER");
      getResultSet().updateInt(tcd.getName(),((Integer)tcd.getValue()).intValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateInt */
  
  @Override
  @Test
  public void testUpdateLong()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CBIGINT");
      getResultSet().updateLong(tcd.getName(),((Long)tcd.getValue()).longValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateLong */
  
  @Override
  @Test
  public void testUpdateFloat()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CREAL");
      getResultSet().updateFloat(tcd.getName(),((Float)tcd.getValue()).floatValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateFloat */
  
  @Override
  @Test
  public void testUpdateDouble()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CDOUBLE");
      getResultSet().updateDouble(tcd.getName(),((Double)tcd.getValue()).doubleValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateDouble */
  
  @Override
  @Test
  public void testUpdateBoolean()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CBOOLEAN");
      getResultSet().updateBoolean(tcd.getName(),((Boolean)tcd.getValue()).booleanValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBoolean */
  
  @Override
  @Test
  public void testUpdateDate()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CDATE");
      getResultSet().updateDate(tcd.getName(),(Date)tcd.getValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateDate */
  
  @Override
  @Test
  public void testUpdateTime()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CTIME");
      getResultSet().updateTime(tcd.getName(),(Time)tcd.getValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testGetTime */
  
  @Override
  @Test
  public void testUpdateTimestamp()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CTIMESTAMP");
      getResultSet().updateTimestamp(tcd.getName(),(Timestamp)tcd.getValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateTimestamp */

  @Test
  public void testUpdateDuration()
  {
    enter();
    try 
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CINTERVAL_DAY_2_SECONDS_6");
      getBaseResultSet().updateDuration(tcd.getName(), ((Interval)tcd.getValue()).toDuration());
    }
    catch(SQLFeatureNotSupportedException sfnse) { printExceptionMessage(sfnse); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateDuration */
  
  @Override
  @Test
  public void testUpdateAsciiStream()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CVARCHAR_255");
      InputStream is = new ByteArrayInputStream(((String)tcd.getValue()).getBytes());
      getResultSet().updateAsciiStream(tcd.getName(),is);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateAsciiStream */
  
  @Override
  @Test
  public void testUpdateAsciiStream_Int()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CVARCHAR_255");
      InputStream is = new ByteArrayInputStream(((String)tcd.getValue()).getBytes());
      getResultSet().updateAsciiStream(tcd.getName(),is,((String)tcd.getValue()).length());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateAsciiStream_Int */
  
  @Override
  @Test
  public void testUpdateAsciiStream_Long()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CVARCHAR_255");
      InputStream is = new ByteArrayInputStream(((String)tcd.getValue()).getBytes());
      getResultSet().updateAsciiStream(tcd.getName(),is,(long)((String)tcd.getValue()).length());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateAsciiStream_Long */
  
  @Override
  @Test
  public void testUpdateCharacterStream()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CVARCHAR_255");
      Reader rdr = new StringReader((String)tcd.getValue());
      getResultSet().updateCharacterStream(tcd.getName(),rdr);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateCharacterStream */
  
  @Override
  @Test
  public void testUpdateCharacterStream_Int()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CVARCHAR_255");
      Reader rdr = new StringReader((String)tcd.getValue());
      getResultSet().updateCharacterStream(tcd.getName(),rdr,((String)tcd.getValue()).length());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateCharacterStream_Int */
  
  @Override
  @Test
  public void testUpdateCharacterStream_Long()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CVARCHAR_255");
      Reader rdr = new StringReader((String)tcd.getValue());
      getResultSet().updateCharacterStream(tcd.getName(),rdr,(long)((String)tcd.getValue()).length());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateCharacterStream_Long */
  
  @Override
  @Test
  public void testUpdateNCharacterStream()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CNVARCHAR_127");
      Reader rdr = new StringReader((String)tcd.getValue());
      getResultSet().updateCharacterStream(tcd.getName(),rdr);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNCharacterStream */
  
  @Override
  @Test
  public void testUpdateNCharacterStream_Int()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CNVARCHAR_127");
      Reader rdr = new StringReader((String)tcd.getValue());
      getResultSet().updateCharacterStream(tcd.getName(),rdr,((String)tcd.getValue()).length());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNCharacterStream_String_Int */
  
  @Override
  @Test
  public void testUpdateNCharacterStream_Long()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CNVARCHAR_127");
      Reader rdr = new StringReader((String)tcd.getValue());
      getResultSet().updateCharacterStream(tcd.getName(),rdr,(long)((String)tcd.getValue()).length());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateNCharacterStream_String_Long */

  @Override
  @Test
  public void testUpdateBinaryStream()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CVARBINARY_255");
      InputStream is = new ByteArrayInputStream((byte[])tcd.getValue());
      getResultSet().updateBinaryStream(tcd.getName(),is);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBinaryStream */
  
  @Override
  @Test
  public void testUpdateBinaryStream_Int()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CVARBINARY_255");
      InputStream is = new ByteArrayInputStream((byte[])tcd.getValue());
      getResultSet().updateBinaryStream(tcd.getName(),is,((byte[])tcd.getValue()).length);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBinaryStream_Int */
  
  @Override
  @Test
  public void testUpdateBinaryStream_Long()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CVARBINARY_255");
      InputStream is = new ByteArrayInputStream((byte[])tcd.getValue());
      getResultSet().updateBinaryStream(tcd.getName(),is,(long)((byte[])tcd.getValue()).length);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateBinaryStream_Long */
  
  @Override
  @Test
  public void testUpdateArray()
  {
    enter();
    // TODO: Add array to TestSqlDatabase
  } /* testUpdateArray */
  
  @Override
  @Test
  public void testUpdateRef()
  {
    enter();
  } /* testUpdateRef */
  
  @Override
  @Test
  public void testUpdateRowId()
  {
    enter();
  } /* testUpdateRowId */
  
  @Override
  @Test
  public void testUpdateObject()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CDATE");
      getResultSet().updateObject(tcd.getName(),tcd.getValue());
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateObject */
  
  @Override
  @Test
  public void testUpdateObject_Int()
  {
    enter();
    try
    {
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CDECIMAL_15_5");
      getResultSet().updateObject(tcd.getName(),tcd.getValue(),3);
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testUpdateObject_Int */
  
  @Override
  @Test
  public void testRefreshRow() throws SQLException
  {
    enter();
    try { getResultSet().refreshRow(); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* refreshRow */

  @Override
  @Test
  public void testDeleteRow() throws SQLException
  {
    enter();
    try 
    {
      // cannot delete row in simple table because of foreign key
      openResultSet(getResultSet().getStatement().getConnection(),_sSqlQueryComplex);
      getResultSet().deleteRow();
      // restore the database
      tearDown();
      setUpClass();
      setUp();
    }
    catch(Exception e) { fail(EU.getExceptionMessage(e)); }
  } /* testDeleteRow */
  
  @Override
  @Test
  public void testUpdateRow() throws SQLException
  {
    enter();
    try 
    { 
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CXML");
      openResultSet(getResultSet().getStatement().getConnection(),_sSqlQuerySimple);
      SQLXML sqlxml = getResultSet().getStatement().getConnection().createSQLXML();
      sqlxml.setString("<a>Konrad Zuse</a>");
      getResultSet().updateSQLXML(tcd.getName(), sqlxml);
      getResultSet().updateRow(); 
      sqlxml.free();
      // restore the database
      tearDown();
      setUpClass();
      setUp();
    }
    catch(Exception e) { fail(EU.getExceptionMessage(e)); }
  } /* testUpdateRow */

  @Override
  @Test
  public void testInsertRow() throws SQLException
  {
    enter();
    try 
    {
      getResultSet().moveToInsertRow();
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CINTEGER");
      getResultSet().updateInt(tcd.getName(),((Integer)tcd.getValue()).intValue());
      getResultSet().insertRow();
      // restore the database
      tearDown();
      setUpClass();
      setUp();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testInsertRow */
  
  @Test
  public void testInsertRowSimple() throws SQLException
  {
    enter();
    try 
    {
      openResultSet(getResultSet().getStatement().getConnection(),_sSqlQuerySimple);
      getResultSet().moveToInsertRow();
      TestColumnDefinition tcd = findColumnDefinition(_listCdSimple,"CCHAR_5");
      getResultSet().updateString(tcd.getName(),(String)tcd.getValue());
      tcd = findColumnDefinition(_listCdSimple,"CVARCHAR_255");
      getResultSet().updateString(tcd.getName(),(String)tcd.getValue());
      tcd = findColumnDefinition(_listCdSimple,"CCLOB_2M");
      Clob clob = getResultSet().getStatement().getConnection().createClob();
      clob.setString(1l, (String)tcd.getValue());
      getResultSet().updateClob(tcd.getName(), clob);
      tcd = findColumnDefinition(_listCdSimple,"CNCHAR_5");
      getResultSet().updateString(tcd.getName(),(String)tcd.getValue());
      tcd = findColumnDefinition(_listCdSimple,"CNVARCHAR_127");
      getResultSet().updateString(tcd.getName(),(String)tcd.getValue());
      tcd = findColumnDefinition(_listCdSimple,"CNCLOB_1M");
      NClob nclob = getResultSet().getStatement().getConnection().createNClob();
      nclob.setString(1l, (String)tcd.getValue());
      getResultSet().updateNClob(tcd.getName(), nclob);
      tcd = findColumnDefinition(_listCdSimple,"CXML");
      SQLXML sqlxml = getResultSet().getStatement().getConnection().createSQLXML();
      sqlxml.setString((String)tcd.getValue());
      getResultSet().updateSQLXML(tcd.getName(), sqlxml);
      tcd = findColumnDefinition(_listCdSimple,"CBINARY_5");
      getResultSet().updateBytes(tcd.getName(),(byte[])tcd.getValue());
      tcd = findColumnDefinition(_listCdSimple,"CVARBINARY_255");
      getResultSet().updateBytes(tcd.getName(),(byte[])tcd.getValue());
      tcd = findColumnDefinition(_listCdSimple,"CBLOB");
      Blob blob = getResultSet().getStatement().getConnection().createBlob();
      blob.setBytes(1l, (byte[])tcd.getValue());
      getResultSet().updateBlob(tcd.getName(), blob);
      tcd = findColumnDefinition(_listCdSimple,"CNUMERIC_31");
      getResultSet().updateBigDecimal(tcd.getName(),new BigDecimal((BigInteger)tcd.getValue()));
      tcd = findColumnDefinition(_listCdSimple,"CDECIMAL_15_5");
      getResultSet().updateBigDecimal(tcd.getName(),(BigDecimal)tcd.getValue());
      tcd = findColumnDefinition(_listCdSimple,"CSMALLINT");
      getResultSet().updateShort(tcd.getName(),((Short)tcd.getValue()).shortValue());
      tcd = findColumnDefinition(_listCdSimple,"CINTEGER");
      getResultSet().updateInt(tcd.getName(),((Integer)tcd.getValue()).intValue());
      tcd = findColumnDefinition(_listCdSimple,"CBIGINT");
      getResultSet().updateLong(tcd.getName(),((Long)tcd.getValue()).longValue());
      tcd = findColumnDefinition(_listCdSimple,"CFLOAT_10");
      getResultSet().updateDouble(tcd.getName(),((Float)tcd.getValue()).doubleValue());
      tcd = findColumnDefinition(_listCdSimple,"CREAL");
      getResultSet().updateFloat(tcd.getName(),((Float)tcd.getValue()).floatValue());
      tcd = findColumnDefinition(_listCdSimple,"CDOUBLE");
      getResultSet().updateDouble(tcd.getName(),((Double)tcd.getValue()).doubleValue());
      tcd = findColumnDefinition(_listCdSimple,"CBOOLEAN");
      getResultSet().updateBoolean(tcd.getName(),((Boolean)tcd.getValue()).booleanValue());
      tcd = findColumnDefinition(_listCdSimple,"CDATE");
      getResultSet().updateDate(tcd.getName(),(Date)tcd.getValue());
      tcd = findColumnDefinition(_listCdSimple,"CTIME");
      getResultSet().updateTime(tcd.getName(),(Time)tcd.getValue());
      tcd = findColumnDefinition(_listCdSimple,"CTIMESTAMP");
      getResultSet().updateTimestamp(tcd.getName(),(Timestamp)tcd.getValue());
      tcd = findColumnDefinition(_listCdSimple,"CINTERVAL_YEAR_3_MONTH");
      ((BaseResultSet)getResultSet()).updateDuration(tcd.getName(),((Interval)tcd.getValue()).toDuration());
      tcd = findColumnDefinition(_listCdSimple,"CINTERVAL_DAY_2_SECONDS_6");
      ((BaseResultSet)getResultSet()).updateDuration(tcd.getName(),((Interval)tcd.getValue()).toDuration());
      getResultSet().insertRow();
      getResultSet().moveToCurrentRow();
      
      openResultSet(getResultSet().getStatement().getConnection(),_sSqlQuerySimple);
      tcd = findColumnDefinition(
        _listCdSimple,"CINTEGER");
      while ((getResultSet().getInt(tcd.getName()) != ((Integer)tcd.getValue()).intValue()) && 
        getResultSet().next()) {}
      tcd = findColumnDefinition(_listCdSimple,"CCHAR_5");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (String)tcd.getValue(),
        (getResultSet().getString(tcd.getName())).substring(0,((String)tcd.getValue()).length()));
      tcd = findColumnDefinition(_listCdSimple,"CVARCHAR_255");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (String)tcd.getValue(),
        getResultSet().getString(tcd.getName()));
      tcd = findColumnDefinition(_listCdSimple,"CCLOB_2M");
      clob = getResultSet().getClob(tcd.getName());
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (String)tcd.getValue(),
        clob.getSubString(1l, (int)clob.length()));
      tcd = findColumnDefinition(_listCdSimple,"CNCHAR_5");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (String)tcd.getValue(),
        (getResultSet().getString(tcd.getName())).substring(0,((String)tcd.getValue()).length()));
      tcd = findColumnDefinition(_listCdSimple,"CNVARCHAR_127");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (String)tcd.getValue(),
        getResultSet().getString(tcd.getName()));
      tcd = findColumnDefinition(_listCdSimple,"CNCLOB_1M");
      nclob = getResultSet().getNClob(tcd.getName());
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (String)tcd.getValue(),
        nclob.getSubString(1l, (int)nclob.length()));
      tcd = findColumnDefinition(_listCdSimple,"CXML");
      sqlxml = getResultSet().getSQLXML(tcd.getName());
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (String)tcd.getValue(),
        sqlxml.getString().replaceAll("\\n\\s*",""));
      tcd = findColumnDefinition(_listCdSimple,"CBINARY_5");
      assertTrue("Insert of "+tcd.getType()+" failed!",
        Arrays.equals(
          (byte[])tcd.getValue(),
          Arrays.copyOf(
            getResultSet().getBytes(tcd.getName()),
            ((byte[])tcd.getValue()).length)));
      tcd = findColumnDefinition(_listCdSimple,"CVARBINARY_255");
      assertTrue("Insert of "+tcd.getType()+" failed!",
        Arrays.equals(
          (byte[])tcd.getValue(),
          getResultSet().getBytes(tcd.getName())));
      tcd = findColumnDefinition(_listCdSimple,"CBLOB");
      blob = getResultSet().getBlob(tcd.getName());
      assertTrue("Insert of "+tcd.getType()+" failed!",
        Arrays.equals(
          (byte[])tcd.getValue(),
          blob.getBytes(1l,(int)blob.length())));
      tcd = findColumnDefinition(_listCdSimple,"CNUMERIC_31");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (BigInteger)tcd.getValue(),
        getResultSet().getBigDecimal(tcd.getName()).toBigInteger());
      tcd = findColumnDefinition(_listCdSimple,"CDECIMAL_15_5");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (BigDecimal)tcd.getValue(),
        getResultSet().getBigDecimal(tcd.getName()));
      tcd = findColumnDefinition(_listCdSimple,"CSMALLINT");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        ((Short)tcd.getValue()).shortValue(),
        getResultSet().getShort(tcd.getName()));
      tcd = findColumnDefinition(_listCdSimple,"CINTEGER");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        ((Integer)tcd.getValue()).intValue(),
        getResultSet().getInt(tcd.getName()));
      tcd = findColumnDefinition(_listCdSimple,"CBIGINT");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        ((Long)tcd.getValue()).longValue(),
        getResultSet().getLong(tcd.getName()));
      tcd = findColumnDefinition(_listCdSimple,"CFLOAT_10");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (Float)tcd.getValue(),
        Float.valueOf(getResultSet().getFloat(tcd.getName())));
      tcd = findColumnDefinition(_listCdSimple,"CREAL");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (Float)tcd.getValue(),
        Float.valueOf(getResultSet().getFloat(tcd.getName())));
      tcd = findColumnDefinition(_listCdSimple,"CDOUBLE");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (Double)tcd.getValue(),
        Double.valueOf(getResultSet().getDouble(tcd.getName())));
      tcd = findColumnDefinition(_listCdSimple,"CBOOLEAN");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (Boolean)tcd.getValue(),
        Boolean.valueOf(getResultSet().getBoolean(tcd.getName())));
      tcd = findColumnDefinition(_listCdSimple,"CDATE");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (Date)tcd.getValue(),
        getResultSet().getDate(tcd.getName()));
      tcd = findColumnDefinition(_listCdSimple,"CTIME");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (Time)tcd.getValue(),
        getResultSet().getTime(tcd.getName()));
      tcd = findColumnDefinition(_listCdSimple,"CTIMESTAMP");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (Timestamp)tcd.getValue(),
        getResultSet().getTimestamp(tcd.getName()));
      tcd = findColumnDefinition(_listCdSimple,"CINTERVAL_YEAR_3_MONTH");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        (Interval)tcd.getValue(),
        Interval.fromDuration(((BaseResultSet)getResultSet()).getDuration(tcd.getName())));
      tcd = findColumnDefinition(_listCdSimple,"CINTERVAL_DAY_2_SECONDS_6");
      Date dateZero = new Date(0l);
      assertEquals("Insert of "+tcd.getType()+" failed!",
        ((Interval)tcd.getValue()).toDuration().getTimeInMillis(dateZero)/1000,
        ((BaseResultSet)getResultSet()).getDuration(tcd.getName()).getTimeInMillis(dateZero)/1000);
      // restore the database
      tearDown();
      setUpClass();
      setUp();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testInsertRow */
  
  private Struct createStruct(TestColumnDefinition tcd)
      throws SQLException
    {
      Struct struct = null;
      @SuppressWarnings("unchecked")
      List<TestColumnDefinition> listAttributes = (List<TestColumnDefinition>)tcd.getValue();
      Object[] aoAttribute = new Object[listAttributes.size()];
      for (int i = 0; i < listAttributes.size(); i++)
      {
        TestColumnDefinition tad = listAttributes.get(i);
        if (tad.getValue() instanceof List<?>)
          aoAttribute[i] = createStruct(tad);
        else
          aoAttribute[i] = tad.getValue();
      }
      struct = getResultSet().getStatement().getConnection().createStruct(tcd.getType(), aoAttribute);
      return struct;
    } /* createStruct */
    
  @Test
  public void testInsertRowSqlComplex() throws SQLException
  {
    enter();
    try 
    {
      // needed for foreign key constraint ...
      getResultSet().moveToInsertRow();
      TestColumnDefinition tcd = findColumnDefinition(
        _listCdSimple,"CINTEGER");
      getResultSet().updateInt(tcd.getName(),((Integer)tcd.getValue()).intValue());
      getResultSet().insertRow();
      getResultSet().moveToCurrentRow();

      openResultSet(getResultSet().getStatement().getConnection(),_sSqlQueryComplex);
      getResultSet().moveToInsertRow();
      tcd = findColumnDefinition(_listCdComplex,"CID");
      getResultSet().updateInt(tcd.getName(),((Integer)tcd.getValue()).intValue());

      tcd = findColumnDefinition(_listCdComplex,"CUDT");
      Struct struct = createStruct(tcd); 
      getResultSet().updateObject(tcd.getName(), struct);
      
      getResultSet().insertRow();
      getResultSet().moveToCurrentRow();
      
      openResultSet(getResultSet().getStatement().getConnection(),_sSqlQueryComplex);
      tcd = findColumnDefinition(_listCdComplex,"CID");
      while ((getResultSet().getInt(tcd.getName()) != ((Integer)tcd.getValue()).intValue()) && 
        getResultSet().next()) {}

      tcd = findColumnDefinition(_listCdComplex,"CID");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        ((Integer)tcd.getValue()).intValue(),
        getResultSet().getInt(tcd.getName()));
      
      tcd = findColumnDefinition(_listCdComplex,"CUDT");
      struct = (Struct)getResultSet().getObject(tcd.getName()); 
      assertTrue("Insert of "+tcd.getType()+" failed!",
        equalsStructValue(struct, _listAdComplex));
      // restore the database
      tearDown();
      setUpClass();
      setUp();
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ParseException pe) { fail(EU.getExceptionMessage(pe)); }
  } /* testInsertRowSqlComplex */

  private Array createArray(TestColumnDefinition tcd)
    throws SQLException
  {
    Array array = null;
    @SuppressWarnings("unchecked")
    List<TestColumnDefinition> listElements = (List<TestColumnDefinition>)tcd.getValue();
    String sTypeName = null;
    Object[] aoElement = new Object[listElements.size()];
    for (int i = 0; i < listElements.size(); i++)
    {
      TestColumnDefinition tcdElement = listElements.get(i);
      aoElement[i] = tcdElement.getValue();
      sTypeName = tcdElement.getType();
    }
    array = getResultSet().getStatement().getConnection().createArrayOf(sTypeName, aoElement);
    return array;
  }

  @Test
  public void testInsertRowNativeComplex()
  {
    enter();
    try
    {
      // no foreign key constraint ...
      openResultSet(getResultSet().getStatement().getConnection(),_sNativeQueryComplex);
      
      getResultSet().moveToInsertRow();
      TestColumnDefinition tcd = findColumnDefinition(_listCdNativeComplex,"CID");
      getResultSet().updateInt(tcd.getName(),((Integer)tcd.getValue()).intValue());
      
      tcd = findColumnDefinition(_listCdNativeComplex,"CUDT_VARRAY");
      Array array = createArray(tcd); 
      getResultSet().updateObject(tcd.getName(), array);
      
      tcd = findColumnDefinition(_listCdNativeComplex,"CUDT_OBJECT");
      Struct struct = createStruct(tcd); 
      getResultSet().updateObject(tcd.getName(), struct);

      /*** Stupid Oracle JDBC cannot handle recursive createStruct
      tcd = findColumnDefinition(_listCdNativeComplex,"CUDT_COMPLEX");
      struct = createStruct(tcd); 
      getResultSet().updateObject(tcd.getName(), struct);
      ***/
      
      getResultSet().insertRow();
      getResultSet().moveToCurrentRow();
      
      openResultSet(getResultSet().getStatement().getConnection(),_sNativeQueryComplex);
      tcd = findColumnDefinition(_listCdNativeComplex,"CID");
      while ((getResultSet().getInt(tcd.getName()) != ((Integer)tcd.getValue()).intValue()) && 
        getResultSet().next()) {}
      
      tcd = findColumnDefinition(_listCdNativeComplex,"CID");
      assertEquals("Insert of "+tcd.getType()+" failed!",
        ((Integer)tcd.getValue()).intValue(),
        getResultSet().getInt(tcd.getName()));
      
      tcd = findColumnDefinition(_listCdNativeComplex,"CUDT_VARRAY");
      array = (Array)getResultSet().getObject(tcd.getName()); 
      assertTrue("Insert of "+tcd.getType()+" failed!",
        equalsArrayValue(array, _listCdArray));

      tcd = findColumnDefinition(_listCdNativeComplex,"CUDT_OBJECT");
      struct = (Struct)getResultSet().getObject(tcd.getName()); 
      assertTrue("Insert of "+tcd.getType()+" failed!",
        equalsStructValue(struct, _listAdObject));

      /***
      tcd = findColumnDefinition(_listCdNativeComplex,"CUDT_COMPLEX");
      struct = (Struct)getResultSet().getObject(tcd.getName()); 
      assertTrue("Insert of "+tcd.getType()+" failed!",
        equalsStructValue(struct, _listAdNativeComplex));
      ***/
      // restore the database
      tearDown();
      setUpClass();
      setUp();
      
    }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ParseException pe) { fail(EU.getExceptionMessage(pe)); }
  } /* testGetObjectNativeComplex */
  
}
