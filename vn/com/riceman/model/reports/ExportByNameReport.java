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
import vn.com.riceman.model.Employeer;
import vn.com.riceman.model.Export;

/**
 * @overview 
 * 	Represent the reports about exports by name.
 * 
 * @author dmle
 *
 * @version 5.0
 */
@DClass(schema="courseman",serialisable=false)
public class ExportByNameReport {
  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, optional = false, mutable = false)
  private int id;
  private static int idCounter = 0;

  /**input: employeer name */
  @DAttr(name = "name", type = Type.String, length = 30, optional = false)
  private String name;
  
  /**output: exports whose names match {@link #name} */
  @DAttr(name="exports",type=Type.Collection,optional=false, mutable=false,
      serialisable=false,filter=@Select(clazz=Export.class, 
      attributes={
    		  Export.A_id, 
    		  Export.A_name, 
    		  Export.A_exportTo,
    		  Export.AttributeName_Quantity, 
    		  Export.AttributeName_Price,
    		  Export.AttributeName_TotalPrice,
    		  Export.A_receiver, 
    		  Export.A_rptExportByName})
      ,derivedFrom={"name"}
      )
  @DAssoc(ascName="exports-by-name-report-has-exports",role="report",
      ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(type=Export.class,cardMin=0,cardMax=MetaConstants.CARD_MORE
    ))
  @Output
  private Collection<Export> exports;

  /**output: number of exports found (if any), derived from {@link #exports} */
  @DAttr(name = "numExports", type = Type.Integer, length = 20, auto=true, mutable=false)
  @Output
  private int numExports;
  
  /**
   * @effects 
   *  initialise this with <tt>name</tt> and use {@link QRM} to retrieve from data source 
   *  all {@link Export} whose names match <tt>name</tt>.
   *  initialise {@link #exports} with the result if any.
   *  
   *  <p>throws NotPossibleException if failed to generate data source query; 
   *  DataSourceException if fails to read from the data source
   * 
   */
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  @DOpt(type=DOpt.Type.RequiredConstructor)
  public ExportByNameReport(@AttrRef("name") String name) throws NotPossibleException, DataSourceException {
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
  @AttrRef(value="exports")
  public void doReportQuery() throws NotPossibleException, DataSourceException {
    // the query manager instance
    
    QRM qrm = QRM.getInstance();
    
    // create a query to look up Export from the data source
    // and then populate the output attribute (exports) with the result
    DSMBasic dsm = qrm.getDsm();
    
    //TODO: to conserve memory cache the query and only change the query parameter value(s)
    Query q = QueryToolKit.createSearchQuery(dsm, Export.class, 
        new String[] {Export.A_exportTo}, 
        new Op[] {Op.MATCH}, 
        new Object[] {"%"+name+"%"});
    
    Map<Oid, Export> result = qrm.getDom().retrieveObjects(Export.class, q);
    
    if (result != null) {
      // update the main output data 
      exports = result.values();
      
      // update other output (if any)
      numExports = exports.size();
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
    exports = null;
    numExports = 0;
  }

  /**
   * A link-adder method for {@link #exports}, required for the object form to function.
   * However, this method is empty because exports have already be recorded in the attribute {@link #exports}.
   */
  @DOpt(type=DOpt.Type.LinkAdder)
  public boolean addExport(Collection<Export> exports) {
    // do nothing
    return false;
  }
  
  /**
   * @effects return exports
   */
  public Collection<Export> getExports() {
    return exports;
  }
  
  /**
   * @effects return numExports
   */
  public int getNumExports() {
    return numExports;
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
    ExportByNameReport other = (ExportByNameReport) obj;
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
    return "ExportsByNameReport (" + id + ", " + name + ")";
  }

}
