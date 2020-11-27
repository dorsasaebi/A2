package mie.ether_example;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import edu.toronto.dbservice.config.MIE354DBHelper;
import edu.toronto.dbservice.types.EtherAccount;
import edu.toronto.dbservice.types.ClientQuery;


public class ClientQueryTask implements JavaDelegate{
	
	Connection dbCon = null;

	public ClientQueryTask() {
		dbCon = MIE354DBHelper.getDBConnection();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(DelegateExecution execution) {
		
		// get current Query request
		ClientQuery currentClientQuery = (ClientQuery) execution.getVariable("currentClientQuery");
		Integer queryId = currentClientQuery.getQueryID();
		Integer clientNum = currentClientQuery.getClientNum();
		String item = currentClientQuery.getItem();
		
		// connect to the blockchain and load the registry contract
		Web3j web3 = Web3j.build(new HttpService());
		String contractAddress = (String) execution.getVariable("contractAddress");
		HashMap<Integer, EtherAccount> accounts = (HashMap<Integer, EtherAccount>) execution.getVariable("accounts");
		Registry clientQuery = Registry.load(contractAddress, web3, accounts.get(clientNum).getCredentials(), EtherUtils.GAS_PRICE, EtherUtils.GAS_LIMIT_CONTRACT_TX);
		
		// encode the item key before registering with the contract
		Utf8String encodedItem = new Utf8String(item); 
		System.out.println(item + " : " + encodedItem.getValue());
		Address ownerAddress = null;
		try {
			ownerAddress = clientQuery.getOwner(encodedItem).get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
		}
		//Uint256 registryTime = null;
		//try {
			//registryTime = clientQuery.getTime(encodedItem).get();
		//} catch (InterruptedException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		//} catch (ExecutionException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		//}
		
		// decode owner address and time to strings
		String strOwnerAddress = ownerAddress.toString();
		//Date decodedTime = new Date(registryTime.getValue().intValue());
		//SimpleDateFormat timeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		//String strTime = timeFormatter.format(decodedTime);
		
		// save record on registration in Registered database table
		String query = "INSERT INTO Result (id, owner) VALUES (?, ?)";
		PreparedStatement preparedStmt = null;
		try {
			preparedStmt = dbCon.prepareStatement(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		try {
			preparedStmt.setInt (1, queryId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} // field 1 is an int
		try {
			preparedStmt.setString (2, strOwnerAddress);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} // field 2 is a string
		try {
			preparedStmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}

	}

}
