package vn.com.riceman.model;

import java.util.LinkedHashMap;
import java.util.Map;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.util.Tuple;
import domainapp.basics.util.cache.StateHistory;
import vn.com.riceman.model.reports.ExportByNameReport;
import vn.com.riceman.model.reports.RiceByNameReport;

/**
 * @Overview Manage export to other
 * 
 * @author dmle
 * @version 2.0
 */
@DClass(schema = "courseman")
public class Export {

	public static final String A_id = "id";
	public static final String A_name = "rice";
	public static final String A_exportTo = "exportTo";
	public static final String AttributeName_Quantity = "quantity";
	public static final String AttributeName_Price = "price";
	public static final String AttributeName_TotalPrice = "totalPrice";
	public static final String A_rptExportByName = "rptExportByName";
	public static final String A_receiver = "receiver";
// attributes
	@DAttr(name = A_id, id = true, auto = true, type = Type.String, length = 6, mutable = false, optional = false)
	private String id;
	private static int idCounter = 0;


	@DAttr(name = A_exportTo, type = Type.String, length = 30, optional = false)
	private String exportTo;
	
	@DAttr(name = "rice", type = Type.Domain, length = 6, optional = false)
	@DAssoc(ascName = "export-has-rice", role = "rice", ascType = AssocType.One2One, endType = AssocEndType.One, associate = @Associate(type = Quality.class, cardMin = 1, cardMax = 1))
	private Rice rice;


	@DAttr(name = "receiver", type = Type.Domain, length = 6, optional = false)
	@DAssoc(ascName = "receiver-has-rice", role = "rice", ascType = AssocType.One2One, endType = AssocEndType.One, associate = @Associate(type = Quality.class, cardMin = 1, cardMax = 1))
	private Receiver receiver;

	@DAttr(name = A_rptExportByName, type = Type.Domain, serialisable = false,
			// IMPORTANT: set virtual=true to exclude this attribute from the object state
			// (avoiding the view having to load this attribute's value from data source)
			virtual = true)
	private ExportByNameReport rptExportByName;


	// overloading constructor to support object type values
	// @version 2.0
	
	 @DOpt(type=DOpt.Type.ObjectFormConstructor)
	  @DOpt(type=DOpt.Type.RequiredConstructor)
	  public Export (
			  @AttrRef("exportTo") String exportTo,
			  @AttrRef("rice") Rice rice,
//			  @AttrRef("quantity") Integer quantity,
//			  @AttrRef("price") Integer price,
	      @AttrRef("receiver") Receiver receiver) throws ConstraintViolationException {
	    this(null,exportTo, rice, 
//	    		0, 0, 
//	    		null,
	    		receiver);
	  }
	

	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public Export(String id,
		  
			String exportTo , Rice rice
			
			, Receiver receiver) throws ConstraintViolationException {
		this.id = nextID(id);
		this.exportTo=exportTo;
		// automatically generate a code
    

		// assign other values
		this.rice = rice;
	    
		this.receiver = receiver;
	}
	private String nextID(String id) throws ConstraintViolationException {
		if (id == null) {
			// generate a new id
			idCounter++;
			if (idCounter >= 10) {
				return "E" + idCounter;
			} else {
				return "E0" + idCounter;
			}
		} else {
			// update id
			int num;
			try {
				num = Integer.parseInt(id.substring(1));
			} catch (RuntimeException e) {
				throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e,
						new Object[] { id });
			}

			if (num > idCounter) {
				idCounter = num;
			}

			return id;
		}
	}

	public String getId() {
		return id;
	}
	
	public void setExportTo(String exportTo) {
		this.exportTo = exportTo;
	}

	// setter methods
	public void setRice(Rice rice) {
		this.rice = rice;
	}

	

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}
	

	public String getExportTo() {
		return exportTo;
	}
	
	public Rice getRice() {
		return rice;
	}


	public Receiver getReceiver() {
		return receiver;
	}
	// override toString
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "("
//  + getCode() 
				+ "," + getRice() + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result

		;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		return false;
	}

//   automatically generate a next module code

	/**
	 * @requires minVal != null /\ maxVal != null
	 * @effects update the auto-generated value of attribute <tt>attrib</tt>,
	 *          specified for <tt>derivingValue</tt>, using <tt>minVal, maxVal</tt>
	 */
	@DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
	public static void updateAutoGeneratedValue(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal)
			throws ConstraintViolationException {

		if (minVal != null && maxVal != null) {
			String maxId = (String) maxVal;
			try {
				int maxIdNum = Integer.parseInt(maxId.substring(1));

				if (maxIdNum > idCounter) // extra check
					idCounter = maxIdNum;

			} catch (RuntimeException e) {
				throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e,
						new Object[] { maxId });
			}
		}
	}
	
	public int compareTo(Object o) {
	    if (o == null || (!(o instanceof Export)))
	      return -1;

	    Export e = (Export) o;

	    return this.rice.getId().compareTo(e.rice.getId());
	  }
}
