	@Override
	public ResultBody selectOrderDetail(HashMap<String, Object> param) throws Exception {
		
		ResultBody resultBody = new ResultBody();
		HashMap<String, Object> map = new HashMap<String,Object>();
		ObjectMapper mapper = new ObjectMapper();
		OrderGoodsVO goods = new OrderGoodsVO();
		
		try {
			
		CommonVO common = new CommonVO();
		common.setMallId(param.get("mallId").toString());
		String secretKey = commonDAO.selectApiSecretKey(common); 
		
		if( secretKey.equals("") ) {
			resultBody.setCode(ReturnMessage.NOSECRETKEY.getCode());
   			resultBody.setMessage(ReturnMessage.NOSECRETKEY.getMessage());
		
		} else {
			// data object 복호화
			String paramData = StringUtil.nvl(param.get("data"));
			String decryptString = Crypto.decrypt(paramData, secretKey);
			
			OrderVO order = mapper.readValue(decryptString, OrderVO.class);
			if(order.getOrdrIdx() == 0) {
				resultBody.setCode(ReturnMessage.NOIDX.getCode());
				resultBody.setMessage(ReturnMessage.NOIDX.getMessage());
			
			}else {
				
				HashMap<String, Object> maping = new HashMap<String,Object>();
				ArrayList<HashMap<String,Object>> OrderDetailAll= new ArrayList<HashMap<String,Object>>();
				
				OrderVO orderDetail = apiOrderDao.selectOrderDetail(order);
				
				goods.setOrdrIdx(order.getOrdrIdx());
				List<OrderGoodsVO> orderGoodsList = apiOrderDao.selectGoodsList(goods);
				
				if(orderDetail == null || orderGoodsList.size() == 0) {
//					HashMap<String, Object> maping = new HashMap<String,Object>();
//					ArrayList<HashMap<String,Object>> OrderDetailAll= new ArrayList<HashMap<String,Object>>();
					maping.put("orderDetail", orderDetail);
					OrderDetailAll.add(maping);
					
					maping = new HashMap<String, Object>();
					maping.put("orderGoodsList", orderGoodsList);
					OrderDetailAll.add(maping);
					
					JSONObject data = new JSONObject();
					data.put("contents", mapper.writeValueAsString(OrderDetailAll));
					String encryptString = Crypto.encrypt(data.toString(), secretKey);
					
					resultBody.setCode(ReturnMessage.NODATA.getCode());
					resultBody.setMessage(ReturnMessage.NODATA.getMessage());
					resultBody.setData(encryptString);
					
				}else {
//					map.put("orderDetail", mapper.writeValueAsString(orderDetail));
//					map.put("orderGoodsList", mapper.writeValueAsString(orderGoodsList));
					
//					HashMap<String, Object> maping = new HashMap<String,Object>();
//					ArrayList<HashMap<String,Object>> OrderDetailAll= new ArrayList<HashMap<String,Object>>();
					maping.put("orderDetail", orderDetail);
					OrderDetailAll.add(maping);
					
					maping = new HashMap<String, Object>();
					maping.put("orderGoodsList", orderGoodsList);
					OrderDetailAll.add(maping);
					
					JSONObject data = new JSONObject();
					data.put("contents", mapper.writeValueAsString(OrderDetailAll));
					
					String encryptString = Crypto.encrypt(data.toString(), secretKey);
					
					resultBody.setCode(ReturnMessage.SUCCESS.getCode());
		   			resultBody.setMessage(ReturnMessage.SUCCESS.getMessage());
					resultBody.setData(encryptString);
					
				}
				
			}
			
		}
	}catch(Exception e){
		log.error(e.getMessage());
		resultBody.setCode(ReturnMessage.FAILURE.getCode());
		resultBody.setMessage(ReturnMessage.FAILURE.getMessage());
	}
		return resultBody;
	}
