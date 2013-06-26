package servlets;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.JsonUtil;

import com.google.gson.Gson;
import com.healthbook.fitbit.SubscribeUpdatedResource;

/**
 * Servlet implementation class FitbitSubscribe
 */
@WebServlet("/FitbitSubscribe")
public class FitbitSubscribe extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FitbitSubscribe() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @throws IOException
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		System.out
				.println("**********************************************************************");
		Date now = new Date();
		System.out
				.println("*****************FitbitSubscribe Getting Something****"
						+ now.toString() + "****************");
		System.out
				.println("**********************************************************************");
		JsonUtil jutil = new JsonUtil();
		Gson gson = new Gson();
		String jsonInput = jutil.readJsonStrFromHttpRequest(request);
		System.out.println("jsonInputs:" + jsonInput);
		SubscribeUpdatedResource resouce = gson.fromJson(jsonInput,
				SubscribeUpdatedResource.class);
		System.out.println(resouce.updateList.size());
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		processRequest(request, response);
	}

}
