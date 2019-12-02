package vn.com.riceman.model;

import java.util.ArrayList;
import java.util.Collection;

import domainapp.basics.exceptions.ConstraintViolationException;
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
import domainapp.basics.util.Tuple;

/**
 * Represents a student class.
 * 
 * @author dmle
 *
 */
@DClass(schema="courseman")
public class TypeOfRice {
	
	public static final String A_id = "id";
	public static final String A_name = "name";
	public static final String A_rptType = "rptType";
  @DAttr(name="id",id=true,auto=true,length=6,mutable=false, type=Type.Integer)
  private int id;
  
  private static int idCounter;
  
  @DAttr(name=A_name,length=20,type=Type.String,optional=false)
  private String name;
  
  @DAttr(name="rice",type=Type.Collection,
      serialisable=false,optional=false,
      filter=@Select(clazz=Rice.class))
  @DAssoc(ascName="type-has-rice",role="type",
      ascType=AssocType.One2Many,endType=AssocEndType.One,
      associate=@Associate(type=Rice.class,
      cardMin=1,cardMax=MetaConstants.CARD_MORE))  
  private Collection<Rice> rice;
  
  // derived attributes
  private int AmountOfRice;
  
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  @DOpt(type=DOpt.Type.RequiredConstructor)
  public TypeOfRice(@AttrRef("name") String name) {
    this(null, name);
  }

  // constructor to create objects from data source
  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public TypeOfRice(@AttrRef("id") Integer id,@AttrRef("name") String name) {
    this.id = nextID(id);
    this.name = name;
    
    rice = new ArrayList<>();
    AmountOfRice = 0;
  }

  @DOpt(type=DOpt.Type.Setter)
  public void setName(String name) {
    this.name = name;
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  //only need to do this for reflexive association: @MemberRef(name="rices")  
  public boolean addRice(Rice s) {
    if (!this.rice.contains(s)) {
      rice.add(s);
    }
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  public boolean addNewRice(Rice s) {
    rice.add(s);
    AmountOfRice++;
    
    // no other attributes changed
    return false; 
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  public boolean addRice(Collection<Rice> rice) {
    for (Rice s : rice) {
      if (!this.rice.contains(s)) {
        this.rice.add(s);
      }
    }
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  public boolean addNewRice(Collection<Rice> rice) {
    this.rice.addAll(rice);
    AmountOfRice += rice.size();

    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkRemover)
  //only need to do this for reflexive association: @MemberRef(name="rices")
  public boolean removeRice(Rice s) {
    boolean removed = rice.remove(s);
    
    if (removed) {
    	AmountOfRice--;
    }
    
    // no other attributes changed
    return false; 
  }
  
  @DOpt(type=DOpt.Type.Setter)
  public void setRice(Collection<Rice> rice) {
    this.rice = rice;
    
    AmountOfRice = rice.size();
  }
    
  /**
   * @effects 
   *  return <tt>ricesCount</tt>
   */
  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getRiceCount() {
    return AmountOfRice;
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setRiceCount(int count) {
	  AmountOfRice = count;
  }
  
  @DOpt(type=DOpt.Type.Getter)
  public String getName() {
    return name;
  }
  
  @DOpt(type=DOpt.Type.Getter)
  public Collection<Rice> getRice() {
    return rice;
  }
  
  @DOpt(type=DOpt.Type.Getter)
  public int getId() {
    return id;
  }
  
  @Override
  public String toString() {
    return "TypeOfRice("+getId()+","+getName()+")";
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
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
    TypeOfRice other = (TypeOfRice) obj;
    if (id != other.id)
      return false;
    return true;
  }

  private static int nextID(Integer currID) {
    if (currID == null) {
      idCounter++;
      return idCounter;
    } else {
      int num = currID.intValue();
      if (num > idCounter)
        idCounter = num;
      
      return currID;
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
      if (attrib.name().equals("id")) {
        int maxIdVal = (Integer) maxVal;
        if (maxIdVal > idCounter)  
          idCounter = maxIdVal;
      }
    }
  }
}
