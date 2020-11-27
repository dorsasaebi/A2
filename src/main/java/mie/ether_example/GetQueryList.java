package mie.ether_example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;
//import org.web3j.protocol.Web3j;
//import org.web3j.protocol.http.HttpService;
import edu.toronto.dbservice.config.MIE354DBHelper;
import edu.toronto.dbservice.types.ClientQuery;
//import edu.toronto.dbservice.types.EtherAccount;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class GetQueryList implements JavaDelegate{

	Connection dbCon = null;

	public GetQueryList() {
		dbCon = MIE354DBHelper.getDBConnection();
	}
	

	@Override
	public void execute(DelegateExecution execution) {
		
		
		// Selecting the registry request list from the data table
		Statement statement = null;
		ResultSet resultSet = null;
		List<ClientQuery> clientQueryList = new ArrayList<>();
		
		try {
			statement = dbCon.createStatement();
			resultSet = statement.executeQuery("SELECT * FROM Query");

			while (resultSet.next()) {
				Integer queryId = resultSet.getInt("id");
				Integer accountId = resultSet.getInt("account");
				String item = resultSet.getString("item");
				ClientQuery clientQuery = new ClientQuery(queryId, accountId, item);
				clientQueryList.add(clientQuery);
			}

			resultSet.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Saving the list of registry requests as a process variable
		execution.setVariable("clientQueryList", clientQueryList);
	}

}
