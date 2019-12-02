package vn.com.riceman.model.reports;

import java.util.Collection;
import java.util.Map;

import domainapp.basics.core.dodm.dsm.DSMBasic;
import domainapp.basics.core.dodm.qrm.QRM;
import domainapp.basics.exceptions.DataSourceException;
import domainapp.basics.exceptions.NotPossibleException;
import domainapp.basics.model.Oid;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.query.Expression.Op;
import domainapp.basics.model.query.Query;
import domainapp.basics.model.query.QueryToolKit;
import domainapp.basics.modules.report.model.meta.Output;
import vn.com.riceman.model.Supplier;

/**
 * @overview 
 * 	Represent the reports about rice provider by name
 * 
 * @author dmle
 *
 * @version 5.0
 */
@DClass(schema="courseman",serialisable=false)
public class SupplierByNameReport {
//	public static 
  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, optional = false, mutable = false)
  private int id;
  private static int idCounter = 0;

  /**input: student name */
  @DAttr(name = "name", type = Type.String, length = 30, optional = false)
  private String name;
  
  /**output: students whose names match {@link #name} */
  @DAttr(name="supplier",type=Type.Collection,optional=false, mutable=false,
      serialisable=false,filter=@Select(clazz=Supplier.class, 
      attributes={Supplier.A_id
    		  , Supplier.A_name
    		  , Supplier.A_address
    		  , Supplier.A_email
    		  , Supplier.A_phone,
          Supplier.A_rptSupByName})
      ,derivedFrom={"name"}
      ) 
  @DAssoc(ascName="supplier-by-name-report-has-supplier",role="report",
      ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(type=Supplier.class,cardMin=0,cardMax=MetaConstants.CARD_MORE
    ))
  @Output
  private Collection<Supplier> supplier;
  
  
  /**output: number of students found (if any), derived from {@link #students} */
  @DAttr(name = "numOfSupplier", type = Type.Integer, length = 20, auto=true, mutable=false)
  @Output
  private int numOfSupplier;
  
  /**
   * @effects 
   *  initialise this with <tt>name</tt> and use {@link QRM} to retrieve from data source 
   *  all {@link ManageSupplier} whose names match <tt>name</tt>.
   *  initialise {@link #students} with the result if any.
   *  
   *  <p>throws NotPossibleException if failed to generate data source query; 
   *  DataSourceException if fails to read from the data source
   * 
   */
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  @DOpt(type=DOpt.Type.RequiredConstructor)
  public SupplierByNameReport(@AttrRef("name") String name) throws NotPossibleException, DataSourceException {
    this.id=++idCounter;
    
    this.name = name;
    
    doReportQuery();

  }
  
  /**
   * @effects return name
   */
  public String getName() {
    return name;
  }

  /**
   * @effects <pre>
   *  set this.name = name
   *  if name is changed
   *    invoke {@link #doReportQuery()} to update the output attribute value
   *    throws NotPossibleException if failed to generate data source query; 
   *    DataSourceException if fails to read from the data source.
   *  </pre>
   */
  public void setName(String name) throws NotPossibleException, DataSourceException {
//    boolean doReportQuery = (name != null && !name.equals(this.name));
    
    this.name = name;
    
    // DONOT invoke this here if there are > 1 input attributes!
    doReportQuery();

  }

  /**
   * This method is invoked when the report input has be set by the user. 
   * 
   * @effects <pre>
   *   formulate the object query
   *   execute the query to retrieve from the data source the domain objects that satisfy it 
   *   update the output attributes accordingly.
   *  
   *  <p>throws NotPossibleException if failed to generate data source query; 
   *  DataSourceException if fails to read from the data source. </pre>
   */
  @DOpt(type=DOpt.Type.DerivedAttributeUpdater)
  @AttrRef(value="supplier")
  public void doReportQuery() throws NotPossibleException, DataSourceException {
    // the query manager instance
    
    QRM qrm = QRM.getInstance();
    
    // create a query to look up Student from the data source
    // and then populate the output attribute (students) with the result
    DSMBasic dsm = qrm.getDsm();
    
    //TODO: to conserve memory cache the query and only change the query parameter value(s)
    Query q = QueryToolKit.createSearchQuery(dsm, Supplier.class, 
        new String[] {Supplier.A_name}, 
        new Op[] {Op.MATCH}, 
        new Object[] {"%"+name+"%"});
    
    Map<Oid, Supplier> result = qrm.getDom().retrieveObjects(Supplier.class, q);
    
    if (result != null) {
      // update the main output data 
      supplier = result.values();
      
      // update other output (if any)
      numOfSupplier = supplier.size();
    } else {
      // no data found: reset output
      resetOutput();
    }
  }

  

  /**
   * @effects 
   *  reset all output attributes to their initial values
   */
  private void resetOutput() {
    supplier = null;
    numOfSupplier = 0;
  }

  /**
   * A link-adder method for {@link #students}, required for the object form to function.
   * However, this method is empty because students have already be recorded in the attribute {@link #students}.
   */
  @DOpt(type=DOpt.Type.LinkAdder)
  public boolean addSupplier(Collection<Supplier> supplier) {
    // do nothing
    return false;
  }
  

  /**
   * @effects return students
   */
  public Collection<Supplier> getSupplier() {
    return supplier;
  }
  

  
  /**
   * @effects return numStudents
   */
  public int getNumOfSupplier() {
    return numOfSupplier;
  }

  /**
   * @effects return id
   */
  public int getId() {
    return id;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SupplierByNameReport other = (SupplierByNameReport) obj;
    if (id != other.id)
      return false;
    return true;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String toString() {
    return "SuppliersByNameReport (" + id + ", " + name + ")";
  }

}
