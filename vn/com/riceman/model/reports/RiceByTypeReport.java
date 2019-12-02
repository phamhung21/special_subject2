package vn.com.riceman.model.reports;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

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
import vn.com.riceman.model.ExportOrder;
import vn.com.riceman.model.Rice;
import vn.com.riceman.model.TypeOfRice;;

/**
 * @overview Represent the reports about receivers by name.
 * 
 * @author dmle
 *
 * @version 5.0
 */
@DClass(schema = "courseman", serialisable = false)
public class RiceByTypeReport {
	@DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, optional = false, mutable = false)
	private int id;
	private static int idCounter = 0;

	/** input: type name */
	@DAttr(name = "type", type = Type.String, length = 30, optional = false)
	private String type;

	/** output: receivers whose names match {@link #name} */
	@DAttr(name = "rice", type = Type.Collection, optional = false, mutable = false, serialisable = false, filter = @Select(clazz = Rice.class, attributes = {
			Rice.A_id, 
			Rice.A_name, 
			Rice.A_address, 
			Rice.A_supplier, 
			Rice.A_type, 
			Rice.A_rptRiceByName,
			Rice.A_rptRiceByType,
			TypeOfRice.A_rptType }), derivedFrom = { "name" })
	@DAssoc(ascName = "rice-by-type-report-has-rice", role = "report", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = Rice.class, cardMin = 0, cardMax = MetaConstants.CARD_MORE))
	@Output
	private Collection<Rice> rice;

	/**
	 * output: number of receivers found (if any), derived from {@link #receivers}
	 */
	@DAttr(name = "numRice", type = Type.Integer, length = 20, auto = true, mutable = false)
	@Output
	private int numRice;

	/**
	 * @effects initialise this with <tt>name</tt> and use {@link QRM} to retrieve
	 *          from data source all {@link ExportOrder} whose names match
	 *          <tt>name</tt>. initialise {@link #receivers} with the result if any.
	 * 
	 *          <p>
	 *          throws NotPossibleException if failed to generate data source query;
	 *          DataSourceException if fails to read from the data source
	 * 
	 */
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	@DOpt(type = DOpt.Type.RequiredConstructor)
	public RiceByTypeReport(@AttrRef("type") String type) throws NotPossibleException, DataSourceException {
		this.id = ++idCounter;

		this.type = type;

		doReportQuery();
	}

	/**
	 * @effects return name
	 */
	public String getType() {
		return type;
	}

	/**
	 * @effects
	 * 
	 *          <pre>
	 *  set this.name = name
	 *  if name is changed
	 *    invoke {@link #doReportQuery()} to update the output attribute value
	 *    throws NotPossibleException if failed to generate data source query; 
	 *    DataSourceException if fails to read from the data source.
	 *          </pre>
	 */
	public void setName(String type) throws NotPossibleException, DataSourceException {
//    boolean doReportQuery = (name != null && !name.equals(this.name));

		this.type = type;

		// DONOT invoke this here if there are > 1 input attributes!
		doReportQuery();
	}

	/**
	 * This method is invoked when the report input has be set by the user.
	 * 
	 * @effects
	 * 
	 *          <pre>
	 *   formulate the object query
	 *   execute the query to retrieve from the data source the domain objects that satisfy it 
	 *   update the output attributes accordingly.
	 *  
	 *  <p>throws NotPossibleException if failed to generate data source query; 
	 *  DataSourceException if fails to read from the data source.
	 *          </pre>
	 */
	@DOpt(type = DOpt.Type.DerivedAttributeUpdater)
	@AttrRef(value = "rice")
	public void doReportQuery() throws NotPossibleException, DataSourceException {
		// the query manager instance

		QRM qrm = QRM.getInstance();

		// create a query to look up ExportOrder from the data source
		// and then populate the output attribute (receivers) with the result
		DSMBasic dsm = qrm.getDsm();

		// TODO: to conserve memory cache the query and only change the query parameter
		// value(s)
		Query q = QueryToolKit.createSearchQuery(dsm, TypeOfRice.class, new String[] { TypeOfRice.A_name },
				new Op[] { Op.MATCH }, new Object[] { "%" + type + "%" });

		Map<Oid, TypeOfRice> result1 = qrm.getDom().retrieveObjects(TypeOfRice.class, q);
		
		TypeOfRice t = null;
		if (result1 != null) {
			for (Entry<Oid, TypeOfRice> e : result1.entrySet()) {
				t = e.getValue();
				break;
			}
		}
		
		Query q2 = QueryToolKit.createSearchQuery(dsm, Rice.class, new String[] { Rice.A_type },
				new Op[] { Op.EQ }, new Object[] { t });

		Map<Oid, Rice> result2 = qrm.getDom().retrieveObjects(Rice.class, q2);

		if (result2 != null) {
			// update the main output data
			rice = result2.values();

			// update other output (if any)
			numRice = rice.size();
		} else {
			// no data found: reset output
			resetOutput();
		}

	}

	/**
	 * @effects reset all output attributes to their initial values
	 */
	private void resetOutput() {
		rice = null;
		numRice = 0;
	}

	/**
	 * A link-adder method for {@link #receivers}, required for the object form to
	 * function. However, this method is empty because receivers have already be
	 * recorded in the attribute {@link #receivers}.
	 */
	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addType(Collection<Rice> receivers) {
		// do nothing
		return false;
	}

	/**
	 * @effects return receivers
	 */
	public Collection<Rice> getRice() {
		return rice;
	}

	/**
	 * @effects return numExportOrders
	 */
	public int getNumRice() {
		return numRice;
	}

	/**
	 * @effects return id
	 */
	public int getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
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
		RiceByTypeReport other = (RiceByTypeReport) obj;
		if (id != other.id)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	/**
	 * @effects
	 * 
	 * @version
	 */
	@Override
	public String toString() {
		return "RiceByTypeReport (" + id + ", " + type + ")";
	}

}
