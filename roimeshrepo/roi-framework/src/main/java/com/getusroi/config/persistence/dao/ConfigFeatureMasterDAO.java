package com.getusroi.config.persistence.dao;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class ConfigFeatureMasterDAO {
	
	final Logger logger = LoggerFactory.getLogger(ConfigFeatureMasterDAO.class);
	
	
	public static final String SELECTBYFEATURENAMEBYGROUPBYSITEID = "SELECT * FROM featureMaster WHERE featurename =? and featureGroup=? and siteId=?";

	/**
	 * check feature Exist featuremaster Table
	 * @param featureName
	 * @param featureGroup
	 * @param siteId
	 * @return masterNodeId;
	 * @throws IOException 
	 * @throws SQLException 
	 */

	public int getFeatureMasterIdByFeatureAndFeaturegroup(String featureName,String featureGroup,int siteId) throws SQLException, IOException{
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int masterNodeId=0;		
		try
		{
			conn = DataBaseUtil.getConnection();
			ps =  (PreparedStatement) conn.prepareStatement(SELECTBYFEATURENAMEBYGROUPBYSITEID);
			ps.setString(1, featureName);
			ps.setString(2, featureGroup);
			ps.setInt(3, siteId);

			rs = ps.executeQuery();
			
			if(rs.next()){
				masterNodeId=rs.getInt("masterNodeId");
			}
		}
		catch (ClassNotFoundException cnfe)
		{
			logger.error("Failed to Load the DB Driver",cnfe);
			//#TODO Exception Handling
			
		}
		finally {
			DataBaseUtil.dbCleanup(conn,ps,rs);
		}
		
		
		return masterNodeId;
		
	}
	


}
