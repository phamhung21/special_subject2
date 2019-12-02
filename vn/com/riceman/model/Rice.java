package vn.com.riceman.model;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.util.Tuple;
import vn.com.riceman.model.reports.RiceByNameReport;
import vn.com.riceman.model.reports.RiceByTypeReport;


/**
 * @overview represent a Seafood object
 * 
 * @author 
 */
@DClass(schema = "riceman")
public class Rice {

	public static final String A_name = "name";
	public static final String A_id = "id";
	public static final String A_address = "country";
	public static final String A_supplier = "supplier";
	public static final String A_type = "type";
	public static final String A_rptRiceByName = "rptRiceByName";
	public static final String A_rptRiceByType = "rptRiceByType";

	// attribute
	@DAttr(name = A_id, id = true, auto = true, type = Type.String, length = 6, mutable = false, optional = false)
	private String id;
	private static int idCounter = 0;

	@DAttr(name = A_name, type = Type.String, length = 25, optional = false)
	private String name;
	
	@DAttr(name = A_address, type = Type.Domain, length = 20, optional = false)
	  @DAssoc(ascName="rice-has-country",role="rice",
	      ascType=AssocType.One2Many, endType=AssocEndType.One,
	  associate=@Associate(type=Country.class,cardMin=1,cardMax=MetaConstants.CARD_MORE))
	  private Country country;

	@DAttr(name = "quality", type = Type.Domain, length = 6, optional = false)
	@DAssoc(ascName = "quality-has-rice", role = "rice", ascType = AssocType.One2One, endType = AssocEndType.One, associate = @Associate(type = Quality.class, cardMin = 1, cardMax = 1))
	private Quality quality;
	
	@DAttr(name = "supplier", type = Type.Domain, length = 6, optional = false)
	@DAssoc(ascName = "supllier-has-rice", role = "rice", ascType = AssocType.One2One, endType = AssocEndType.One, associate = @Associate(type = Quality.class, cardMin = 1, cardMax = 1))
	private Supplier supplier;
	
	 @DAttr(name=A_type,type=Type.Domain,length = 6, optional = false)
	  @DAssoc(ascName="type-has-rice",role="rice",
	      ascType=AssocType.One2Many,endType=AssocEndType.Many,
	      associate=@Associate(type=TypeOfRice.class,cardMin=1,cardMax=1))
	  private TypeOfRice type;
	
	// v5.0: to realise link to report
	  @DAttr(name=A_rptRiceByName,type=Type.Domain, serialisable=false, 
	      // IMPORTANT: set virtual=true to exclude this attribute from the object state
	      // (avoiding the view having to load this attribute's value from data source)
	      virtual=true)
	  private RiceByNameReport rptRiceByName;
	  
	  
	  @DAttr(name=A_rptRiceByType,type=Type.Domain, serialisable=false, 
		      // IMPORTANT: set virtual=true to exclude this attribute from the object state
		      // (avoiding the view having to load this attribute's value from data source)
		      virtual=true)
		  private RiceByTypeReport rptRiceByType;

	// Constructors
	// Constructor with id and name

	// Constructor with name only
	@DOpt(type = DOpt.Type.RequiredConstructor)
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	public Rice(
			@AttrRef("name") String name
			, @AttrRef("country") Country country
			, @AttrRef("quality") Quality quality
			
			) {
		this(null, name, country, quality, null, null
				);
	}

	 @DOpt(type=DOpt.Type.ObjectFormConstructor)
	  public Rice(@AttrRef("name") String name, 
	      @AttrRef("country") Country country, 
	      @AttrRef("quality") Quality quality, 
	      @AttrRef("supplier") Supplier supplier,
	      @AttrRef("type") TypeOfRice type) {
	    this(null, name, country, quality, supplier, type);
	  }
	 
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public Rice(
			@AttrRef("id") String id
			, @AttrRef("name") String name
			, @AttrRef("country") Country country
			, @AttrRef("quality") Quality quality
			, @AttrRef("supplier") Supplier supplier,
			@AttrRef("type") TypeOfRice type)
	throws ConstraintViolationException{
		this.id = nextID(id);
		this.name = name;
		this.country = country;
		this.quality = quality;
		this.supplier = supplier;
		this.type=type;
	
	}


	// setter
	public void setName(String name) {
		this.name = name;
	}

	public void setCountry(Country country) {
		this.country = country;
	}
	
	public void setQuality(Quality quality) {
		this.quality= quality;
	}
	
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	
	public void setType(TypeOfRice type) {
		this.type = type;
	}
	// getter
	public String getId() {
		return id;
	}

	public Country getCountry() {
		return country;
	}
	
	public String getName() {
		return name;
	}
	
	public Quality getQuality() {
		return quality;
	}
	public Supplier getSupplier() {
		return supplier;
	}
	public TypeOfRice getType() {
		return type;
	}

	// override toString
	/**
	 * @effects returns <code>this.id</code>
	 */
	@Override
	public String toString() {
		return toString(true);
	}

	/**
	 * @effects returns <code>Student(id,name,dob,address,email)</code>.
	 */
	public String toString(boolean full) {
		if (full)
			return "Rice(" + id + "," + name + ")";
		else
			return "Rice(" + id + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Rice other = (Rice) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	private String nextID(String id) throws ConstraintViolationException {
		if (id == null) {
			// generate a new id
			idCounter++;
			if (idCounter >= 10) {
				return "R" + idCounter;
			} else {
				return "R0" + idCounter;
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
}