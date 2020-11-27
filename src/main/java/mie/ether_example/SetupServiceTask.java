package mie.ether_example;


import java.nio.file.Files;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.toronto.dbservice.config.MIE354DBHelper;
import edu.toronto.dbservice.types.EtherAccount;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class SetupServiceTask implements JavaDelegate{
	// replace [username] with your onw ECF user name like following
	private static String testRpcCmdTemplate = "cmd /c start /MIN \"TestRpc\" C:\\Users\\simondie\\AppData\\Roaming\\npm\\testrpc.cmd -d --db %s";

	
	Connection dbCon = null;
	
	public static String createBlockchainDir() throws Exception {
		return Files.createTempDirectory("blockchain_").toAbsolutePath().toString();
	}
	
	public static void startTestRpc(String blockchainDirPath) throws Exception {
		String testRpcCmd = String.format(testRpcCmdTemplate, blockchainDirPath);
		Runtime.getRuntime().exec(testRpcCmd);
		Thread.sleep(4000); // sleep to allow server to start
	}

	public SetupServiceTask() {
		dbCon = MIE354DBHelper.getDBConnection();
	}
	
	@Override
	public void execute(DelegateExecution execution) {
		
		//run TestRpc
		String blockchainDir = null;
		try {
			blockchainDir = createBlockchainDir();
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		try {
			startTestRpc(blockchainDir);
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		System.out.println("TestRPC started");
		System.out.println("Blockchain Directory: " + blockchainDir);
		
		Statement statement = null;
		ResultSet resultSet = null;
		HashMap<Integer, EtherAccount> accounts = new HashMap<>();
		
		// load accounts from database to a HashMap and store it as a process variable
		try {
			statement = dbCon.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		try {
			resultSet = statement.executeQuery("SELECT * FROM Account");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		try {
			while (resultSet.next()) {
				Integer accountId = resultSet.getInt("accountId");
				String privateKey = resultSet.getString("privateKey");
				EtherAccount account = new EtherAccount(privateKey);
				accounts.put(accountId, account);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}

		execution.setVariable("accounts", accounts);
		
	}

}
