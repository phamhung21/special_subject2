package vn.com.riceman.model;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;

/**
 * Represents a compulsory module (a subclass of Module)
 * 
 * @author dmle
 * 
 */
@DClass(schema="courseman")
public class DomesticImport extends ManageImport {

  // constructor method
  // the order of the arguments must be this: 
  // - super-class arguments first, then sub-class
//  @DOpt(type=DOpt.Type.ObjectFormConstructor)
//  public CompulsoryModule(@AttrRef("name") String name, 
//      @AttrRef("quantity") int quantity, @AttrRef("price") int price) {
//    this(null, null, name, quantity, price);
//  }

  // the order of the arguments must be this: 
  // - super-class arguments first, then sub-class
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public DomesticImport(@AttrRef("name") String name, 
      @AttrRef("quantity") Integer quantity, @AttrRef("price") Integer price, @AttrRef("supplier") Supplier supplier) {
    this(null
//    		, null
    		, name, quantity, price, supplier);
  }

  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public DomesticImport(String id
//		  , String code
		  , String name, Integer quantity, Integer price, Supplier supplier) 
    throws ConstraintViolationException {
    super(id
//    		, code
    		, name, quantity, price, supplier);
  }

}
