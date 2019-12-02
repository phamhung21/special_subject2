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
public class Quality {
	private static final String A_id = "id";
	@DAttr(name = A_id, id = true, auto = true, type = Type.String, length = 3, mutable = false, optional = false)
	private String id;
	private static int idCounter = 0;
  
  @DAttr(name="name",length=20,type=Type.String,optional=false)
  private String name;
  
  @DAttr(name="rices",type=Type.Collection,
      serialisable=false,optional=false,
      filter=@Select(clazz=Rice.class))
  @DAssoc(ascName="quality-has-rice",role="quality",
      ascType=AssocType.One2Many,endType=AssocEndType.One,
      associate=@Associate(type=Rice.class,
      cardMin=1,cardMax=MetaConstants.CARD_MORE))  
  private Collection<Rice> rices;
  
  // derived attributes
  private int riceCount;
  
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  @DOpt(type=DOpt.Type.RequiredConstructor)
  public Quality(@AttrRef("name") String name) {
    this(null, name);
  }

  // constructor to create objects from data source
  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public Quality(@AttrRef("id") String id,@AttrRef("name") String name) {
    this.id = nextID(id);
    this.name = name;
    
    rices = new ArrayList<>();
    riceCount = 0;
  }

  @DOpt(type=DOpt.Type.Setter)
  public void setName(String name) {
    this.name = name;
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  //only need to do this for reflexive association: @MemberRef(name="students")  
  public boolean addRice(Rice s) {
    if (!this.rices.contains(s)) {
      rices.add(s);
    }
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  public boolean addNewRice(Rice s) {
    rices.add(s);
    riceCount++;
    
    // no other attributes changed
    return false; 
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  public boolean addRice(Collection<Rice> rices) {
    for (Rice s : rices) {
      if (!this.rices.contains(s)) {
        this.rices.add(s);
      }
    }
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  public boolean addNewRice(Collection<Rice> rices) {
    this.rices.addAll(rices);
    riceCount += rices.size();

    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkRemover)
  //only need to do this for reflexive association: @MemberRef(name="students")
  public boolean removeRice(Rice s) {
    boolean removed = rices.remove(s);
    
    if (removed) {
      riceCount--;
    }
    
    // no other attributes changed
    return false; 
  }
  
  @DOpt(type=DOpt.Type.Setter)
  public void setRices(Collection<Rice> students) {
    this.rices = students;
    
    riceCount = students.size();
  }
    
  /**
   * @effects 
   *  return <tt>studentsCount</tt>
   */
  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getRicesCount() {
    return riceCount;
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setRicesCount(int count) {
    riceCount = count;
  }
  
  @DOpt(type=DOpt.Type.Getter)
  public String getName() {
    return name;
  }
  
  @DOpt(type=DOpt.Type.Getter)
  public Collection<Rice> getRices() {
    return rices;
  }
  
  @DOpt(type=DOpt.Type.Getter)
  public String getId() {
    return id;
  }
  
  @Override
  public String toString() {
    return "Quality("+getId()+","+getName()+")";
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
		Quality other = (Quality) obj;
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
				return "Q" + idCounter;
			} else {
				return "Q0" + idCounter;
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
