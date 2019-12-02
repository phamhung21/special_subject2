package vn.com.riceman.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.exceptions.ConstraintViolationException.Code;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.Select;
import domainapp.basics.util.Tuple;
//import vn.com.courseman.model.reports.HRByNameReport;

/**
 * Represents a student. The student ID is auto-incremented from the current
 * year.
 * 
 * @author dmle
 * @version 2.0
 */
@DClass(schema="courseman")
public class Employeer {
  public static final String A_name = "name";
  public static final String A_id = "id";
  public static final String A_dob = "dob";
  public static final String A_phone = "phone";
  public static final String A_address = "address";
  public static final String A_email = "email";
  public static final String A_salary = "salary";
  public static final String A_rptEmployeerByName = "rptEmployeerByName";

  // attributes of students
  @DAttr(name = A_id, id = true, type = Type.String, auto = true, length = 6, 
      mutable = false, optional = false)
  private String id;
  //static variable to keep track of student id
  private static int idCounter = 0;
 
  @DAttr(name = A_name, type = Type.String, length = 30, optional = false)
  private String name;
  
  @DAttr(name = A_dob, type = Type.String, length = 15, optional = false)
  private String dob;
  
  @DAttr(name = A_phone, type = Type.Integer, length = 11, optional = false)
  private Integer phone;
  
  @DAttr(name = A_address, type = Type.String, length = 20, optional = false)
  private String address;

  @DAttr(name = A_email, type = Type.String, length = 30, optional = false)
  private String email;

  @DAttr(name=A_salary,type=Type.Double,length = 10, optional = false)
  private double salary;


  
  
  // v5.0: to realise link to report
  @DAttr(name=A_rptEmployeerByName,type=Type.Domain, serialisable=false, 
      // IMPORTANT: set virtual=true to exclude this attribute from the object state
      // (avoiding the view having to load this attribute's value from data source)
      virtual=true)
//  private HRByNameReport rptEmployeerByName;
  
  // constructor methods
  // for creating in the application
  // without SClass
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  @DOpt(type=DOpt.Type.RequiredConstructor)
  public Employeer(@AttrRef("name") String name, 
      @AttrRef("dob") String dob, 
      @AttrRef("phone") Integer phone, 
      @AttrRef("address") String address, 
      @AttrRef("email") String email) {
    this(null, name, dob, phone, address, email, 0.0);
  }
  
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public Employeer(@AttrRef("name") String name, 
      @AttrRef("dob") String dob, 
      @AttrRef("phone") Integer phone, 
      @AttrRef("address") String address, 
      @AttrRef("email") String email, 
      @AttrRef("salary") double salary) {
    this(null, name, dob, phone, address, email, salary);
  }
  
  // a shared constructor that is invoked by other constructors
  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public Employeer(@AttrRef("id") String id, 
      @AttrRef("dob") String name,
      @AttrRef("dob") String dob,
      @AttrRef("phone") Integer phone,
      @AttrRef("address") String address, 
      @AttrRef("email") String email,
      @AttrRef("salary") double salary) 
  throws ConstraintViolationException {
    // generate an id
    this.id = nextID(id);

    // assign other values
    this.name = name;
    this.dob = dob;
    this.phone = phone;
    this.address = address;
    this.email = email;
    this.salary = salary;
  }

  // setter methods
  public void setName(String name) {
    this.name = name;
  }

  public void setDob(String dob) {
    this.dob = dob;
  }
  
  public void setPhone(Integer phone) {
	this.phone = phone;
}

  public void setAddress(String address) {
    this.address = address;
  }

  // v2.7.3
  public void setNewAddress(String address) {
    // change this invocation if need to perform other tasks (e.g. updating value of a derived attribtes)
    setAddress(address);
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

  public void setSalary(double cls) {
    this.salary = cls;
  }
  
  
  // getter methods
  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDob() {
    return dob;
  }
  
  public Integer getPhone() {
	return phone;
}

  public String getAddress() {
    return address;
  }

  public String getEmail() {
	  if(validateEmail(email)==true) {
			return email;
		}
			throw new ConstraintViolationException(Code.INVALID_VALUE_NOT_SPECIFIED_WHEN_REQUIRED);
	
  }

  public double getSalary() {
    return salary;
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
   * @effects returns <code>Employeer(id,name,dob,address,email)</code>.
   */
  public String toString(boolean full) {
    if (full)
      return "Employeer(" + id + "," + name + "," + dob + "," + address + ","
          + email 
          
          + ")";
    else
      return "Employeer(" + id + ")";
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
    Employeer other = (Employeer) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  // automatically generate the next student id
	private String nextID(String id) throws ConstraintViolationException {
		if (id == null) {
			// generate a new id
			idCounter++;
			if (idCounter >= 10) {
				return "H" + idCounter;
			} else {
				return "H0" + idCounter;
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
   * @requires 
   *  minVal != null /\ maxVal != null
   * @effects 
   *  update the auto-generated value of attribute <tt>attrib</tt>, specified for <tt>derivingValue</tt>, using <tt>minVal, maxVal</tt>
   */
  @DOpt(type=DOpt.Type.AutoAttributeValueSynchroniser)
  public static void updateAutoGeneratedValue(
      DAttr attrib,
      Tuple derivingValue, 
      Object minVal, 
      Object maxVal) throws ConstraintViolationException {
    
    if (minVal != null && maxVal != null) {
      //TODO: update this for the correct attribute if there are more than one auto attributes of this class 

      String maxId = (String) maxVal;
      
      try {
        int maxIdNum = Integer.parseInt(maxId.substring(1));
        
        if (maxIdNum > idCounter) // extra check
          idCounter = maxIdNum;
        
      } catch (RuntimeException e) {
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_VALUE, e, new Object[] {maxId});
      }
    }
  }
}
