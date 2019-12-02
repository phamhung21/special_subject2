package vn.com.riceman.model;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.exceptions.ConstraintViolationException.Code;
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
import vn.com.riceman.model.reports.ReceiverByNameReport;
import vn.com.riceman.model.reports.RiceByNameReport;


/**
 * @overview the company who provide rice
 * 
 * @author 
 */
@DClass(schema = "riceman")
public class Receiver {

	public static final String A_name = "name";
	public static final String A_id = "id";
	public static final String A_address = "country";
	public static final String A_email = "email";
	public static final String A_phone = "phone";
	public static final String A_rptRecByName = "rptRecByName";

	// attribute
	@DAttr(name = A_id, id = true, auto = true, type = Type.String, length = 4, mutable = false, optional = false)
	private String id;
	private static int idCounter = 0;

	@DAttr(name = A_name, type = Type.String, length = 25, optional = false)
	private String name;
	
	@DAttr(name = A_email, type = Type.String, length = 25, optional = false)
	private String email;
	
	@DAttr(name = A_phone, type = Type.Integer, length = 11, optional = false)
	private Integer phone;
	
	@DAttr(name = A_address, type = Type.Domain, length = 20, optional = false)
	  @DAssoc(ascName="rice-has-country",role="rice",
	      ascType=AssocType.One2Many, endType=AssocEndType.One,
	  associate=@Associate(type=Country.class,cardMin=1,cardMax=MetaConstants.CARD_MORE))
	  private Country country;


	
	// v5.0: to realise link to report
	  @DAttr(name=A_rptRecByName,type=Type.Domain, serialisable=false, 
	      // IMPORTANT: set virtual=true to exclude this attribute from the object state
	      // (avoiding the view having to load this attribute's value from data source)
	      virtual=true)
	  private ReceiverByNameReport rptRecByName;

	// Constructors
	// Constructor with id and name

	// Constructor with name only
	@DOpt(type = DOpt.Type.RequiredConstructor)
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	public Receiver(
			@AttrRef("name") String name
			,	@AttrRef("email") String email
			, 	@AttrRef("phone") Integer phone
			) {
		this(null, name, email, phone, null
				);
	}
	
	 @DOpt(type = DOpt.Type.ObjectFormConstructor)
		public Receiver(
				@AttrRef("name") String name,
				@AttrRef("email") String email, 
				@AttrRef("phone") Integer phone,
				@AttrRef("country") Country country
				)
		throws ConstraintViolationException{
		 this(null, name, email, phone, country);
	 }

	
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public Receiver(@AttrRef("id") String id
			, @AttrRef("name") String name, 
			@AttrRef("email") String email,
			@AttrRef("phone") Integer phone,
			@AttrRef("country") Country country
			)
	throws ConstraintViolationException{
		this.id = nextID(id);
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.country = country;
			
	}

	// setter
	public void setName(String name) {
		this.name = name;
	}

	public void setCountry(Country country) throws ConstraintViolationException {
		this.country = country;
	}
	public void setEmail(String email) {
		if(validateEmail(email)==true) {
			this.email = email;
		}else {
			throw new ConstraintViolationException(Code.INVALID_VALUE_NOT_SPECIFIED_WHEN_REQUIRED);
		}
	}
	
	private boolean validateEmail(String email) {
		if(!email.contains("@gmail.com")) {
			return false;
		}else {
			return true;
		}
	}
	public void setPhone(Integer phone) {
		this.phone = phone;
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
	
	public String getEmail() throws ConstraintViolationException {
		if(validateEmail(email)==true) {
			return email;
		}
			throw new ConstraintViolationException(Code.INVALID_VALUE_NOT_SPECIFIED_WHEN_REQUIRED);
	}
	
	public Integer getPhone() {
		return phone;
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
			return "Receiver(" + id + "," + name + ")";
		else
			return "Receiver(" + id + ")";
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
		Receiver other = (Receiver) obj;
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