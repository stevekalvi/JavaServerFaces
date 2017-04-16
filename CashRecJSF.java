/*
 * CashReceipts.java
 *
 * Created on March 9, 2006, 10:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package AR;
import javax.faces.model.SelectItem;
import javax.faces.el.ValueBinding;
import javax.faces.el.MethodBinding;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.net.*;
import java.text.FieldPosition;
import java.lang.Double;
import java.lang.StringBuffer;
import java.lang.Integer;

import java.lang.String;
import java.text.*;
import com.ibm.as400.access.*;
import javax.faces.application.FacesMessage;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.component.UIViewRoot;
import javax.faces.component.UIInput;
import javax.faces.component.EditableValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.*;
import javax.faces.component.*;
import javax.faces.event.ValueChangeEvent;


import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.component.html.HtmlSelectOneListbox;
import javax.faces.component.html.HtmlPanelGrid;
import java.sql.DriverManager;
import java.sql.*;



/**
 *
 * @author Stephen  Kalvi
 */
public class CashRecJSF {
 /**
  * Holds value of property customer.
  */
 public int cust;
 private String customer;
 private String quarters = null;
 private static SelectItem[] quarterItems = new SelectItem[] {
  new SelectItem(" "),
   new SelectItem("1"),
   new SelectItem("2"),
   new SelectItem("3"),
   new SelectItem("4")
 };
 private String message = null;
 private String custName = null;
 private String custRep = null;
 private String custAddr = null;
 private String custPhone = null;
 private String custRm = null;
 private String custLoc = null;
 private RowElement rows;
 private RowElement e;
 private ArrayList arr = null;
 private String temp_str = null;
 private String Prv_FY = null;
 private String Thr_FY = null;
 private String curhead = null;
 private String prvhead = null;
 private String thrhead = null;
 private Double d = 0.0;
 private Integer Customer_Number;
 private char[] destination;
 private String CUSTOMER_NUMBER$ = null;
 private AS400 sys = null;
 private boolean custlookup = false;
 private boolean viewsummary = false;
 private UISelectItems temp = null;
 private List customerItems = null;
 private String customers;
 private String findname = null;
 private HtmlPanelGrid formElements;
 private int counter = 0;
 private int formelementcount = 0;
 private String page1_action = "";
 private HtmlSelectOneListbox menu = null;
 private UIPanel panel = null;
 private UIViewRoot root = null;
 private HtmlSelectOneListbox DropDown = null;
 private boolean menuchanged = false;




 /** Creates a new instance of CashReceipts */
 public CashRecJSF() {}

 public String getMessage() {
  return this.message;
 }

 public ArrayList getRows() {
  return arr;
 }

 public String getPrvFiscal() {
  return this.Prv_FY;
 }

 /*
  *   Convert the decimal amounts on the summary page to a dollar format.  The converter allows us to define our
  *   own edit code.
  */

 public Converter getConvert() {
  return new Converter() {
   public Object getAsObject(FacesContext context, UIComponent component, String newValue) throws ConverterException {
    return null;
   }

   public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
    try {
     d = Double.parseDouble(value.toString());
    } catch (NumberFormatException e) {
     throw new ConverterException("Invalid decimal.....");
    }
    DecimalFormat dollar_format = new DecimalFormat("$#,###,###,##0.00");
    return dollar_format.format(d).toString();
   }

  };
 }

 /*
  *   Right justify the customer number using a field exit customer converter
  */

 public String field_exit(String newValue) {

  String CUSTOMER_NUMBER$ = newValue.toString();

  int Cust_length = CUSTOMER_NUMBER$.length();

  if (Cust_length < 5) {
   int start = 5 - Cust_length;
   char[] source = new char[Cust_length];
   source = CUSTOMER_NUMBER$.toCharArray();
   destination = new char[5];
   for (int i = 0; i < Cust_length; i++) {
    destination[i + start] = source[i];
   }
   for (int i = 0; i < start; i++) {
    destination[i] = '0';
   }

   CUSTOMER_NUMBER$ = "";
   for (int i = 0; i < 5; i++) {
    CUSTOMER_NUMBER$ += destination[i];

   }
  }
  return CUSTOMER_NUMBER$;
 }

 public void menuchange(ValueChangeEvent event) throws AbortProcessingException {

  PhaseId phaseId = event.getPhaseId();
  // if the phase is not update model values put the event on the queue....
  if (phaseId.equals(PhaseId.ANY_PHASE)) {
   event.setPhaseId(PhaseId.UPDATE_MODEL_VALUES);
   event.queue();
  }
  // else process the event
  else if (phaseId.equals(PhaseId.UPDATE_MODEL_VALUES)) {
   customer = event.getNewValue().toString();
   customer = this.field_exit(customer);
   menuchanged = true;
  }
 }

 public boolean getCallconverter() {
  return !menuchanged;
 }

 public boolean getDonotcallconverter() {
  return menuchanged;
 }

 public String getFindName() {
  return this.findname;
 }

 public void setFindName(String newValue) {
  this.findname = newValue;
 }

 /**
  * Getter for property customer.
  * @return Value of property customer.
  */
 public String getCustomer() {
  return this.customer;
 }

 public String getCustName() {
  return this.custName;
 }

 public String getCustRep() {
  return this.custRep;
 }

 public String getCustAddr() {
  return this.custAddr;
 }

 public String getCustPhone() {
  return this.custPhone;
 }

 public String getCustRm() {
  return this.custRm;
 }

 public String getCustLoc() {
  return this.custLoc;
 }

 /**
  * Setter for property customer.
  * @param customer New value of property customer.
  */
 public void setCustomer(String customer) {
  this.customer = customer;
 }

 public String getThrHead() {
  return this.thrhead;
 }

 public String getPrvHead() {
  return this.prvhead;
 }

 public String getCurHead() {
  return this.curhead;
 }

 public String test() {
  return null;
 }

 public String back1() {
  page1_action = "";
  custlookup = false;
  viewsummary = false;
  menuchanged = false;
  findname = "";
  customer = "";
  return "start1";
 }

 private void getinfo() {

  try {

   String KEY_RETURNED = "";

   // declare a new instance of your as400

   sys = new AS400("xx.xx.x.xxx");

   // Force the user to signon to get the userid 
   sys.connectService(AS400.COMMAND);
   String UserId = sys.getUserId();

   String Program_Name = "ARS033RI";

   QSYSObjectPathName name = new QSYSObjectPathName("SAKMOD", "ACCS_RCVQ", "DTAQ");

   KeyedDataQueue dq = new KeyedDataQueue(sys, name.getPath());

   // Create the data queue just in case this is the first time this
   // program has run.  The queue already exists exception is caught
   // and ignored.  The length of the key is twenty bytes, the length
   // of an entry is 1024 bytes.

   try {
    dq.create(20, 1024);
   } catch (Exception e) {};

   // Create AS400 Text objects for the different lengths
   // of parameters you are sending in.
   AS400Text txt5 = new AS400Text(5);
   AS400Text txt1 = new AS400Text(1);
   AS400Text txt_user = new AS400Text(10);
   AS400Text txt_pgm = new AS400Text(10);
   AS400Text txt_key = new AS400Text(20);

   // declare and instantiate  your parameter list.

   ProgramParameter[] parmList = new ProgramParameter[5];

   // assign values to your parameters using the AS400Text class to convert to bytes
   // the second parameter is an integer which sets the length of your parameter output
   parmList[0] = new ProgramParameter(txt5.toBytes(customer), 5);
   parmList[1] = new ProgramParameter(txt1.toBytes(quarters), 1);
   parmList[2] = new ProgramParameter(txt_user.toBytes(UserId), 10);
   parmList[3] = new ProgramParameter(txt_pgm.toBytes(Program_Name), 10);
   parmList[4] = new ProgramParameter(txt_key.toBytes(KEY_RETURNED), 20);

   // declare and instantiate the program.
   // if you don't know the exact program string, you can use: QSYSObjectPathName.toPath("MYLIBRARY","MYPROGRAM","PGM")
   ProgramCall pgm = new ProgramCall(sys, "/QSYS.LIB/SAKMOD.LIB/ARS033CL.PGM", parmList);

   if (pgm.run() != true) {
    AS400Message[] messageList = pgm.getMessageList();
   } else {
    CharacterFieldDescription Flat_File =
     new CharacterFieldDescription(new AS400Text(767, sys), "FLAT_FILE");

    // Build a record format for a flat file
    RecordFormat dataFormat = new RecordFormat();

    dataFormat.addFieldDescription(Flat_File);

    // Use the return parameter from ARS032CL as the key for the Data Queue

    String KEY1 = (String) txt_key.toObject(parmList[4].getOutputData());
    byte[] KEY1_BYTES = new byte[20];
    AS400Text temp1 = new AS400Text(20);
    KEY1_BYTES = temp1.toBytes(KEY1);

    // Create the keyed data queue object that represents the data queue on
    // the server.

    KeyedDataQueueEntry DQData = dq.read(KEY1_BYTES, 0, "EQ"); //Read Equal

    if (DQData == null) {
     System.out.println("<td> No data read....testing </td>");
     return;
    }

    Record data = dataFormat.getNewRecord(DQData.getData());
    String Flat_File$ = (String) data.getField("FLAT_FILE");

    String CSTNUM = Flat_File$.substring(0, 5);
    int Cust = Integer.parseInt(CSTNUM);

    custName = Flat_File$.substring(5, 35);
    System.out.println("cust is:" + Cust + " name is: " + custName);

    custAddr = Flat_File$.substring(35, 65);
    custPhone = Flat_File$.substring(145, 157);

    String Head1 = Flat_File$.substring(157, 169);
    String Cur_FY = Flat_File$.substring(169, 173);
    curhead = Head1 + "     " + Cur_FY;
    String Head2 = Flat_File$.substring(173, 185);
    Prv_FY = Flat_File$.substring(185, 190);
    prvhead = Head1 + "     " + Prv_FY;
    String Head3 = Flat_File$.substring(573, 585);
    Thr_FY = Flat_File$.substring(585, 590);
    thrhead = Head1 + "     " + Thr_FY;



    int cur_beg = 199;
    int cur_end = 211;
    int prv_beg = 211;
    int prv_end = 223;
    int thr_beg = 599;
    int thr_end = 611;
    arr = new ArrayList();
    for (int i = 0; i < 14; i++) {
     e = new RowElement();
     e.setCurrentString(Flat_File$.substring(cur_beg, cur_end));
     e.setCurrentDollar(Double.parseDouble(e.getCurrentString()));
     e.setCurrentDollar(e.getCurrentDollar() / 100);
     e.setPreviousString(Flat_File$.substring(prv_beg, prv_end));
     e.setPreviousDollar(Double.parseDouble(e.getPreviousString()));
     e.setPreviousDollar(e.getPreviousDollar() / 100);
     e.setThirdString(Flat_File$.substring(thr_beg, thr_end));
     e.setThirdDollar(Double.parseDouble(e.getThirdString()));
     e.setThirdDollar(e.getThirdDollar() / 100);
     switch (i) {
      case 0:
       e.setMonthString("October");
       break;
      case 1:
       e.setMonthString("November");
       break;
      case 2:
       e.setMonthString("December");
       break;
      case 3:
       e.setMonthString("January");
       break;
      case 4:
       e.setMonthString("February");
       break;
      case 5:
       e.setMonthString("March");
       break;
      case 6:
       e.setMonthString("April");
       break;
      case 7:
       e.setMonthString("May");
       break;
      case 8:
       e.setMonthString("June");
       break;
      case 9:
       e.setMonthString("July");
       break;
      case 10:
       e.setMonthString("August");
       break;
      case 11:
       e.setMonthString("September");
       break;
      case 12:
       e.setMonthString("YTD TOTALS");
       break;
      case 13:
       e.setMonthString("TOTALS");
       break;

     }
     arr.add(e);
     cur_beg += 24;
     cur_end += 24;
     prv_beg += 24;
     prv_end += 24;
     thr_beg += 12;
     thr_end += 12;
    }


    custRep = Flat_File$.substring(95, 119);
    custRm = Flat_File$.substring(120, 145);
    custLoc = Flat_File$.substring(65, 95);
   }


  } // try
  catch (Exception e) {
   System.out.println(e.toString());
  }


 }


 public void searchengine(String findit, List sel_items) {
   Connection connection = null;
   PreparedStatement stmt = null;
   ResultSet rs = null;

   /*
    *  Generate the arraylist to be used by the search engine....
    */

   if (sel_items == null) {
    sel_items = new ArrayList();
   } else {
    if (sel_items.size() > 0 && findit != null) { // Do not purge the list the first time through.
     Iterator iter = sel_items.iterator();
     while (iter.hasNext()) {
      iter.next();
      iter.remove();
     }
    }
   }

   /*
    *  Only search if the user enters a search string.  Do not run the search the first time through
    */

   if (findit != null) {
    if (findit.length() > 0) {

     try {
      /*
       * Load the IBM Toolbox for Java JDBC driver.
       */
      DriverManager.registerDriver(new com.ibm.as400.access.AS400JDBCDriver());

      /*
       * Get a connection to the database.  Since we do not
       * provide a user id or password, a prompt will appear.
       */

      connection = DriverManager.getConnection("jdbc:as400://12.12.1.123");
      /*
       *   
       * Execute the query.
       */

      Statement select = connection.createStatement();
      stmt = connection.prepareStatement("SELECT CSTNUM,CBNAME FROM SAKMOD.CST001PF WHERE CBNAME LIKE ?");
      stmt.setString(1, "%" + findit.toUpperCase() + "%");
      rs = stmt.executeQuery();
      int ix = 1;
      while (rs.next()) {
       String custnm = rs.getString("CBNAME");
       String custno = rs.getString("CSTNUM");
       UISelectItem items = new UISelectItem();
       items.setItemLabel(custno + " " + custnm);
       items.setItemValue(custno);
       sel_items.add(items);
       ix++;
      } // while
      this.setCustomerItems(sel_items);
      counter++;
      if (ix == 1) {
       UISelectItem items = new UISelectItem();
       items.setItemLabel("No customers were found matching the search criteria...");
       items.setItemValue("");
       sel_items.add(items);
       this.setCustomerItems(sel_items);
      }
     } catch (Exception e) {
      UISelectItem items = new UISelectItem();
      items.setItemLabel("Server connection not found..:" + e.toString());
      items.setItemValue("");
      sel_items.add(items);
      this.setCustomerItems(sel_items);

     } finally {

      /*
       * Clean up.
       */

      try {
       if (connection != null) {
        connection.close();
        stmt.close();
        rs.close();
       }
      } // try
      catch (Exception e) {} // catch
     } // finally
    } // findit.length > 0
   } // findit!=null
  } // Search Engine


 public boolean getCustLookup() {
  return custlookup;
 }

 public void setFormElements(UIPanel newValue) {
  this.panel = newValue;
 }

 public UIPanel getFormElements()

 {

  /*
   *  Get the root and find the form identifier....
   */

  UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
  UIComponent form = root.findComponent("cashreceipts");
  panel = new UIPanel();

  /*
   *  DROP DOWN BOX & value binding expression
   */

  DropDown = new HtmlSelectOneListbox();
  ValueBinding binding = FacesContext.getCurrentInstance().getApplication().createValueBinding("#{cash.customers}");
  DropDown.setValueBinding("value", binding);


  // DropDown.setOnclick("this.form.submit()");
  DropDown.setOnchange("checkChange()");
  DropDown.setId("Sel_One");

  /*
   * add the valueChangeListener & method binding expression
   */

  Class[] parms = new Class[] {
   ValueChangeEvent.class
  };
  FacesContext con_test = FacesContext.getCurrentInstance();
  Application app_test = con_test.getApplication();
  MethodBinding mb = app_test.createMethodBinding("#{cash.menuchange}", parms);
  DropDown.setValueChangeListener(mb);


  /*
   *  selectItems & value binding expression
   */

  UISelectItems sel_items = new UISelectItems();
  ValueBinding binditems = FacesContext.getCurrentInstance().getApplication().createValueBinding("#{cash.customerItems}");
  sel_items.setValueBinding("value", binditems);

  /*
   * add the individual items via the search.
   */

  this.searchengine(this.getFindName(), DropDown.getChildren());

  panel.getChildren().add(DropDown);
  panel.setRendered(custlookup);

  return panel;
 }

 public String findcust() {
  menuchanged = false;
  page1_action = "findcust";
  FacesContext context = FacesContext.getCurrentInstance();
  ValueBinding binding = context.getApplication().createValueBinding("#{cash.findName}");
  String find$ = (String) binding.getValue(context);
  this.setFindName(find$);
  if (find$.length() > 0) {
   custlookup = true;
  } else {
   custlookup = false;
  }
  return page1_action;
 }

 /*
  *   The customer number has already been validated via CustomerValidator.java.  So we just use the value to
  *   control the Dynamic Page Navigation.  We redisplay the screen if no customer number has been entered.
  */


 public String display() {
  FacesContext context = FacesContext.getCurrentInstance();
  ValueBinding binding = context.getApplication().createValueBinding("#{cash.customer}");
  String CUSTOMER_NUMBER$ = (String) binding.getValue(context);

  CUSTOMER_NUMBER$ = this.field_exit(CUSTOMER_NUMBER$);
  /*
   *  Control Dynamic Page Navigation.
   */

  Integer cust = Integer.valueOf(CUSTOMER_NUMBER$);
  customer = CUSTOMER_NUMBER$;

  if (cust > 0) {
   this.getinfo();
   menuchanged = false;
   custlookup = false;
   viewsummary = true;
   page1_action = "display";
   return page1_action; // view the cash summary
  } else {
   page1_action = null;
   return page1_action; // redisplay the screen
  }
 }

 /*
  * Do not make quarters an array if you are making the number
  * of items shown in the SelectOneListBox = 1 (size = 1)
  */

 public String getQuarters() {
  return this.quarters;
 }

 public void setQuarters(String newValue) {
  this.quarters = newValue;
 }

 public SelectItem[] getQuarterItems() {
  return this.quarterItems;
 }

 public String getCustomers() {
  return this.customers;
 }

 public void custparm(ValueChangeEvent event) {}

 public void setCustomers(String newValue) {
  this.customers = newValue;
 }

 public void setCustomerItems(List newValue) {
  this.customerItems = newValue;
 }

 public List getCustomerItems() {
  return this.customerItems;
 }


}