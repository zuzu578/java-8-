private static final String SELECT_KEY = " SELECT IDENT_CURRENT('sendEmailHistory') as emailNo";
public int selectKey() {
		int emailNo = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet res = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(SELECT_KEY);
			res = pstmt.executeQuery();
			if(res.next()) {
				emailNo = res.getInt("emailNo");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(pstmt, conn);
		}
		
		return emailNo;
	 
	}
