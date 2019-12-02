package vn.com.riceman.model;

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
import domainapp.basics.util.Tuple;
import domainapp.basics.util.cache.StateHistory;

/**
 * Represents an enrolment
 * 
 * @author dmle
 * 
 */
@DClass(schema = "courseman")
public class ImportOrder implements Comparable {

  private static final String AttributeName_Quantity = "quantity";
  private static final String AttributeName_Price = "price";
  private static final String AttributeName_TotalPrice = "totalPrice";
  
  // attributes
  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, optional = false, mutable = false)
  private int id;
  private static int idCounter = 0;

  @DAttr(name = "riceID", type = Type.Domain, length = 5, optional = false)
  @DAssoc(ascName = "riceID-has-enrolments", role = "enrolment", 
    ascType = AssocType.One2Many, endType = AssocEndType.Many, 
    associate = @Associate(type = Rice.class, cardMin = 1, cardMax = 1), dependsOn = true)
  private Rice riceID;
  
  @DAttr(name = "supplierID", type = Type.Domain, length = 5, optional = false)
  @DAssoc(ascName = "supplierID-has-enrolments", role = "enrolment", 
    ascType = AssocType.One2Many, endType = AssocEndType.Many, 
    associate = @Associate(type = Receiver.class, cardMin = 1, cardMax = 1), dependsOn = true)
  private Supplier supplierID;

  @DAttr(name = "importID", type = Type.Domain, length = 5, optional = false)
  @DAssoc(ascName = "importID-has-enrolments", role = "enrolment", 
    ascType = AssocType.One2Many, endType = AssocEndType.Many, 
    associate = @Associate(type = Import.class, cardMin = 1, cardMax = 1), dependsOn = true)
  private Import importID;

  @DAttr(name = AttributeName_Quantity, type = Type.Integer, length = 6, optional = false, min = 0.0)
  private Integer quantity;
  
  @DAttr(name = AttributeName_Price, type = Type.Integer, length = 4, optional = false, min = 0.0)
  private Integer price;

      /* Note: no need to do this:
       derivedFrom={"quantity,price"}
       * because finalGrade and totalPrice are updated by the same method and this is already specified by totalPrice (below)
       */
//  )
  private char finalGrade;

  // v2.6.4.b derived from two attributes
  @DAttr(name = AttributeName_TotalPrice,type=Type.Integer,auto=true,mutable = false,optional = true,
      serialisable=false,
      derivedFrom={AttributeName_Quantity, AttributeName_Price})
  private Integer totalPrice;

  // v2.6.4.b
  private StateHistory<String, Object> stateHist;

  // constructor method
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  @DOpt(type=DOpt.Type.RequiredConstructor)
  public ImportOrder(@AttrRef("riceID") Rice s, 
      @AttrRef("importID") Import m) throws ConstraintViolationException {
    this(null, s, m, 0, 0, null);
  }

  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public ImportOrder(
		  @AttrRef("riceID") Rice s, 
		  @AttrRef("importID") Import m, 
		  @AttrRef("quantity") Integer quantity, 
      @AttrRef("price") Integer price)
      throws ConstraintViolationException {
    this(null, s, m, quantity, price, null);
  }

  // @version 2.0
  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public ImportOrder(Integer id, Rice s, Import m, Integer quantity,
      Integer price, 
      // v2.7.3: not used but needed to load data from source
      Character finalGrade) throws ConstraintViolationException {
    this.id = nextID(id);
    this.riceID = s;
    this.importID = m;
    this.quantity = (quantity != null) ? quantity.intValue()
        : null;
    this.price = (price != null) ? price.intValue() : null;

    // v2.6.4.b
    stateHist = new StateHistory<>();

    updateTotalPrice(); 
  }

  // setter methods
  public void setRiceID(Rice s) {
    this.riceID = s;
  }

  public void setImportID(Import m) {
    this.importID = m;
  }

  public void setQuantity(Integer mark) {
    setQuantity(mark, false);
  }

  public void setQuantity(Integer mark, boolean updateFinalGrade) {
    this.quantity = mark;
    if (updateFinalGrade)
      updateTotalPrice(); 
  }

  public void setPrice(Integer mark) {
    setPrice(mark, false);
  }
  
  public void setPrice(Integer mark, boolean updateFinalGrade) {
    this.price = mark;
    if (updateFinalGrade)
      updateTotalPrice(); 
  }

  @DOpt(type=DOpt.Type.DerivedAttributeUpdater)
  @AttrRef(value=AttributeName_TotalPrice)
  public void updateTotalPrice() {
    // updates both final mark and final grade
    if (quantity != null && price != null) {
      Integer finalPrice = quantity * price;
      
      totalPrice= finalPrice;
      
      // v2.6.4b: cache final mark
      stateHist.put(AttributeName_TotalPrice, totalPrice);

      // round the mark to the closest integer value
    }
  }
  
  // getter methods
  public Integer getId() {
    return id;
  }

  public Rice getRiceID() {
    return riceID;
  }

  public Import getImportID() {
    return importID;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public Integer getPrice() {
    return price;
  }


  // v2.6.4.b
  public Integer getTotalPrice() {
    return getTotalPrice(false);// totalPrice;
  }

  public Integer getTotalPrice(boolean cached) throws IllegalStateException {
    if (cached) {
      Object val = stateHist.get(AttributeName_TotalPrice);

      if (val == null)
        throw new IllegalStateException(
            "Order.getTotalPrice: cached value is null");

      return (Integer) val;
    } else {
      if (totalPrice != null)
        return totalPrice;
      else
        return 0;
    }

  }

  // override toString
  @Override
  public String toString() {
    return toString(false);
  }

  public String toString(boolean full) {
    if (full)
      return "Order(" + riceID + "," + importID + ")";
    else
      return "Order(" + getId() + "," + riceID.getId() + ","
          + 
          ")";
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
    ImportOrder other = (ImportOrder) obj;
    if (id != other.id)
      return false;
    return true;
  }

  private static int nextID(Integer currID) {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();

      if (num > idCounter) {
        idCounter = num;
      }
      return currID;
    }
  }

  /**
   * @requires minVal != null /\ maxVal != null
   * @effects update the auto-generated value of attribute <tt>attrib</tt>,
   *          specified for <tt>derivingValue</tt>, using
   *          <tt>minVal, maxVal</tt>
   */
  @DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
  public static void updateAutoGeneratedValue(DAttr attrib,
      Tuple derivingValue, Object minVal, Object maxVal)
      throws ConstraintViolationException {
    if (minVal != null && maxVal != null) {
      // check the right attribute
      if (attrib.name().equals("id")) {
        int maxIdVal = (Integer) maxVal;
        if (maxIdVal > idCounter)
          idCounter = maxIdVal;
      }
      // TODO add support for other attributes here
    }
  }


  // implements Comparable interface
  public int compareTo(Object o) {
    if (o == null || (!(o instanceof ImportOrder)))
      return -1;

    ImportOrder e = (ImportOrder) o;

    return this.riceID.getId().compareTo(e.riceID.getId());
  }
}
