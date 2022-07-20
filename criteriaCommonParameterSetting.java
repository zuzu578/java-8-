if(paramMap.get("sKey") != null && paramMap.get("sWords") != null) {
					condition1 = criteriaBuilder.like(root.get(paramMap.get("sKey").toString()), "%"+paramMap.get("sWords").toString()+"%");  
					criteriaQuery.where(condition1);
				}
				if(paramMap.get("delYn") != null) {
					delCondition = criteriaBuilder.equal(root.get("delYn"),paramMap.get("delYn").toString().charAt(0));
					criteriaQuery.where(delCondition);
				}
				
				if(paramMap.get("sKey") != null && paramMap.get("delYn") != null) {
					OrderConditions = criteriaBuilder.and(condition1, delCondition);
					criteriaQuery.where(OrderConditions);
				}
				
				// perPage 파라미터가 있을경우 
				if(paramMap.get("page") != null) {
					page =Integer.parseInt(paramMap.get("page").toString());
				}
				if(paramMap.get("perPage") != null) {
					perPage = Integer.parseInt(paramMap.get("perPage").toString());
				}
			
					
					perPage = Integer.parseInt(paramMap.get("perPage").toString());
					page = PageCalculator.calculatePage(paramMap);
					
					result = entityManager.createQuery(criteriaQuery)
							.setFirstResult(page)
							.setMaxResults(perPage)
							.getResultList();	
					
					pageNationMap.put("page", Integer.parseInt(paramMap.get("page").toString()));
			   	 	pageNationMap.put("totalCount",adminGoodsRepository.count());
			   	 	
			   	 	resultMap.put("pagination", pageNationMap);
			   	 	resultMap.put("contents", result);
					resultMap.put("message", Message.success);
					
					results.put("code", MessageCode.success);
			   	 	results.put("result", true);
					results.put("data", resultMap);
					
