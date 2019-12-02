package vn.com.riceman.software;





import domainapp.basics.exceptions.NotPossibleException;
import domainapp.basics.software.DomainAppToolSoftware;
import vn.com.riceman.model.Country;
import vn.com.riceman.model.Employeer;
import vn.com.riceman.model.Export;
import vn.com.riceman.model.ExportOrder;
import vn.com.riceman.model.Import;
import vn.com.riceman.model.ImportOrder;
import vn.com.riceman.model.Quality;
import vn.com.riceman.model.Receiver;
import vn.com.riceman.model.Rice;
import vn.com.riceman.model.Supplier;
import vn.com.riceman.model.TypeOfRice;
import vn.com.riceman.model.reports.ExportByNameReport;
import vn.com.riceman.model.reports.ImportByNameReport;
import vn.com.riceman.model.reports.ReceiverByNameReport;
import vn.com.riceman.model.reports.RiceByNameReport;
import vn.com.riceman.model.reports.RiceByTypeReport;
import vn.com.riceman.model.reports.SupplierByNameReport;

/**
 * @overview Encapsulate the basic functions for setting up and running a
 *           software given its domain model.
 * 
 * @author dmle
 *
 * @version
 */
public class CourseManSoftware extends DomainAppToolSoftware {

	// the domain model of software
	private static final Class[] model = {
			Export.class,
			Import.class,
			ImportOrder.class,
			ExportOrder.class,
			Receiver.class,
			Supplier.class,
			Rice.class, 
			Country.class, 
			Quality.class,
			Employeer.class,
			TypeOfRice.class,
			
			// reports
			ReceiverByNameReport.class,
			SupplierByNameReport.class,
			ExportByNameReport.class,
			ImportByNameReport.class,
			RiceByNameReport.class,
			RiceByTypeReport.class
			};

	/*
	 * (non-Javadoc)
	 * 
	 * @see vn.com.courseman.software.Software#getModel()
	 */
	/**
	 * @effects return {@link #model}.
	 */
	@Override
	protected Class[] getModel() {
		return model;
	}

	/**
	 * The main method
	 * 
	 * @effects run software with a command specified in args[0] and with the model
	 *          specified by {@link #getModel()}.
	 * 
	 *          <br>
	 *          Throws NotPossibleException if failed for some reasons.
	 */
	public static void main(String[] args) throws NotPossibleException {
		new CourseManSoftware().exec(args);
	}
}
