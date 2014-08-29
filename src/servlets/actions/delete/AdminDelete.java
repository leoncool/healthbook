package servlets.actions.delete;

import health.database.DAO.DatastreamDAO;
import health.database.DAO.JobDAO;
import health.database.DAO.nosql.HBaseDatapointDAO;
import health.database.models.Datastream;
import health.database.models.DatastreamUnits;
import health.database.models.JobsTable;
import health.hbase.models.HBaseDataImport;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AdminDelete
 */
@WebServlet("/AdminDelete")
public class AdminDelete extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AdminDelete() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		try {
			String deletejobID = request.getParameter("jobid");

			JobDAO jobDao = new JobDAO();
			JobsTable job = jobDao.getJobByID(Integer.parseInt(deletejobID));
			System.out.println(job.getMethod());
			HBaseDatapointDAO dpDap = new HBaseDatapointDAO();
			DatastreamDAO dsDao = new DatastreamDAO();
			Datastream ds = dsDao.getDatastream(job.getTargetObjectID(), true,
					true);
			HashMap<String, String> dsUnitsList = new HashMap<>();
			for (DatastreamUnits unit : ds.getDatastreamUnitsList()) {
				dsUnitsList.put(unit.getShortUnitID(), unit.getShortUnitID());
			}
			HBaseDataImport dataexport = dpDap.exportDatapoints(
					job.getTargetObjectID(), (long) 0, Long.MAX_VALUE, null,
					dsUnitsList, null, null);
			if (dataexport!=null&&dataexport.getData_points() != null) {
				System.out.println("total datapoints for deletion:"
						+ dataexport.getData_points().size());
				out.println("total datapoints for deletion:"
						+ dataexport.getData_points().size());
				long no_deleted = dpDap.delete_range_Datapoint(
						job.getTargetObjectID(), 0, Long.MAX_VALUE);
				System.out.println("After Deletion:");
				out.println("After Deletion:");
				out.println("Total Deleted:" + no_deleted);
				System.out.println("Total Deleted:" + no_deleted);
			} else {
				out.println("no data points exist, no delete needed!");
			}
			dataexport = dpDap.exportDatapoints(job.getTargetObjectID(),
					(long) 0, Long.MAX_VALUE, null, dsUnitsList, null, null);
			if (dataexport!=null&&dataexport.getData_points() != null) {
				out.println("error!!!shouldn't reach here!");
				System.out.println("total datapoints for deletion:"
						+ dataexport.getData_points().size());
				out.println("total datapoints for deletion:"
						+ dataexport.getData_points().size());
			} else {
				out.println("deletion successful!");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
